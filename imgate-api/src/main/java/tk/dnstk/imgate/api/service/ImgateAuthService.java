package tk.dnstk.imgate.api.service;


import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.dnstk.imgate.api.InvalidAccessException;
import tk.dnstk.imgate.api.InvalidArgumentException;
import tk.dnstk.imgate.api.ObjectNotFoundException;
import tk.dnstk.imgate.api.RequireAccessException;
import tk.dnstk.imgate.api.data.ImgateAccessTokenRepository;
import tk.dnstk.imgate.api.model.*;
import tk.dnstk.imgate.api.model.AccessToken.AccessType;
import tk.dnstk.imgate.api.security.SecurityContext;
import tk.dnstk.imgate.api.security.SecurityContextInitializer;
import tk.dnstk.imgate.api.security.SecurityValue;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@ControllerAdvice
@Order(80)
public class ImgateAuthService implements CommandLineRunner, SecurityContextInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImgateAuthService.class);

    private static final long EXPIRE_TIME = 30 * 60 * 1000L;

    @Autowired
    private ImgateAccessTokenRepository tokenRepo;

    @Autowired
    private ImgateAccountService accountService;

    @Autowired
    private ImgateAgentService agentService;

    @Autowired
    private Environment environment;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @RequestMapping(method = RequestMethod.POST, path = "/tokens")
    public AccessToken createToken(@Validated @RequestBody AccessRequest accessRequest) {
        String accountId = validateAccess(accessRequest.getAccessId(), accessRequest.getAccessType(),
                accessRequest.getAccessSecret());
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessId(accessRequest.getAccessId());
        // without '-' in token id
        accessToken.setTokenId(UUID.randomUUID().toString().replace("-", ""));
        accessToken.setCreateDate(new Date());
        accessToken.setExpireDate(new Date(System.currentTimeMillis() + EXPIRE_TIME));
        accessToken.setAccountId(accountId);
        accessToken.setAccessType(accessRequest.getAccessType());
        accessToken.setAccessRole(accountService.getAccount(accountId).getContent().getRole());
        return tokenRepo.save(accessToken);
    }

    private String validateAccess(String accessId, AccessType accessType, String accessSecret) {
        switch (accessType) {
            case ACCOUNT:
                try {
                    Account account = accountService.getAccount(accessId).getContent();
                    if (!passwordEncoder.matches(accessSecret, account.getPassword())) {
                        throw new InvalidAccessException("Invalid access");
                    }
                    return account.getAccountId();
                } catch (ObjectNotFoundException e) {
                    throw new InvalidAccessException("No access found");
                }
            case AGENT:
                try {
                    Agent agent = agentService.getAgent(accessId).getContent();
                    if (!passwordEncoder.matches(accessSecret, agent.getPassword())) {
                        throw new InvalidAccessException("Invalid access");
                    }
                    return agent.getAccountId();
                } catch (ObjectNotFoundException e) {
                    throw new InvalidAccessException("No access found");
                }
            default:
                throw new InvalidArgumentException("invalid access type: " + accessType);
        }
    }

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.GET, path = "/tokens")
    public AccessToken getToken() {
        String tokenId = SecurityContext.currentValue(SecurityValue.Token);
        return tokenRepo.findOne(tokenId);
    }

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.DELETE, path = "/tokens")
    public void revokeToken() {
        String tokenId = SecurityContext.currentValue(SecurityValue.Token);
        tokenRepo.delete(tokenId);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!Boolean.parseBoolean(environment.getProperty("imgate.admin.skip", "false"))) {
            SecurityContext.runInContext(() -> {
                setupAdminAccess(environment.getProperty("imgate.admin.id", "admin"),
                        environment.getProperty("imgate.admin.secret", "imgate"));
                setupDemoAgent(environment.getProperty("imgate.agent.id", "demo"),
                        environment.getProperty("imgate.agent.secret", "demo"));
            });
        }
    }

    private void setupAdminAccess(String id, String secret) {
        SecurityContext.getContext().set(SecurityValue.AccountId, id);
        Account adminAccount = new Account();
        adminAccount.setAccountId(id);
        adminAccount.setPassword(secret);
        adminAccount.setRole(Role.ADMIN);
        accountService.addAccount(adminAccount);
        LOGGER.info("Save account successful for {}", id);
    }

    private void setupDemoAgent(String id, String secret) {
        agentService.createAgentByAccount(SecurityContext.currentValue(SecurityValue.AccountId), id);
        agentService.setAgentPassword(id, secret);
        LOGGER.info("Save agent successful for {}", id);
    }

    @Override
    public void initializeContext(SecurityContext context, HttpServletRequest request) {
        String tokenId = context.get(SecurityValue.Token);
        if (tokenId == null) {
            return;
        }
        AccessToken accessToken = tokenRepo.findOne(tokenId);
        if (accessToken == null) {
            throw new RequireAccessException("Invalid access token");
        }
        if (accessToken.getExpireDate() != null && accessToken.getExpireDate().before(new Date())) {
            throw new RequireAccessException("Expired access token, try request new access");
        }
        context.set(SecurityValue.AccountId, accessToken.getAccountId());
        switch (accessToken.getAccessType()) {
            case ACCOUNT:
                context.set(SecurityValue.Role, String.valueOf(accessToken.getAccessRole()));
                break;
            case AGENT:
                context.set(SecurityValue.AgentId, accessToken.getAccessId());
                break;
        }

    }
}
