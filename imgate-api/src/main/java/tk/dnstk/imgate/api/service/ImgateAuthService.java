package tk.dnstk.imgate.api.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.dnstk.imgate.api.InvalidAccessException;
import tk.dnstk.imgate.api.InvalidArgumentException;
import tk.dnstk.imgate.api.ObjectNotFoundException;
import tk.dnstk.imgate.api.data.ImgateAccessRepository;
import tk.dnstk.imgate.api.data.ImgateAccessTokenRepository;
import tk.dnstk.imgate.api.model.Access;
import tk.dnstk.imgate.api.model.Access.AccessType;
import tk.dnstk.imgate.api.model.AccessRequest;
import tk.dnstk.imgate.api.model.AccessToken;
import tk.dnstk.imgate.api.model.Role;
import tk.dnstk.imgate.api.security.SecurityContext;
import tk.dnstk.imgate.api.security.SecurityValue;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@ControllerAdvice
@Order(80)
public class ImgateAuthService implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImgateAuthService.class);

    private static final long EXPIRE_TIME = 30 * 60 * 1000L;

    @Autowired
    private ImgateAccessTokenRepository tokenRepo;

    @Autowired
    private ImgateAccessRepository accessRepo;

    @Autowired
    private ImgateAccountService accountService;

    @Autowired
    private ImgateAgentService agentService;

    @Autowired
    private Environment environment;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @RequestMapping(method = RequestMethod.POST, path = "/tokens")
    public AccessToken createToken(@RequestParam("type") AccessType accessType,
                                   @Validated @RequestBody AccessRequest accessRequest) {
        String accountId = validateAccess(accessRequest.getAccessId(), accessType, accessRequest.getAccessSecret());
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessId(accessRequest.getAccessId());
        // without '-' in token id
        accessToken.setTokenId(UUID.randomUUID().toString().replace("-",""));
        accessToken.setCreateDate(new Date());
        accessToken.setExpireDate(new Date(System.currentTimeMillis() + EXPIRE_TIME));
        accessToken.setAccountId(accountId);
        return tokenRepo.save(accessToken);
    }

    private String validateAccess(String accessId, AccessType accessType, String accessSecret) {
        Access access = accessRepo.findByAccessIdAndAccessType(accessId, accessType)
                .orElseThrow(() -> new InvalidAccessException("No access found"));
        if (!passwordEncoder.matches(accessSecret, access.getAccessSecret())) {
            throw new InvalidAccessException("Invalid access");
        }
        switch (accessType) {
            case ACCOUNT:
                return access.getAccessId();
            case AGENT:
                return agentService.getAgent(access.getAccessId()).getContent().getAccountId();
            default:
                throw new InvalidArgumentException("invalid access type: " + accessType);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/tokens")
    public AccessToken getToken() {
        String tokenId = SecurityContext.currentValue(SecurityValue.Token);
        AccessToken accessToken = tokenRepo.findOne(tokenId);
        return accessToken;
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/tokens")
    public void revokeToken() {
        String tokenId = SecurityContext.currentValue(SecurityValue.Token);
        tokenRepo.delete(tokenId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/access")
    public void setAccess(@Validated @RequestBody Access access) {
        // valid grant permission
        String currentAccountId = SecurityContext.currentValue(SecurityValue.AccountId);
        String accessId = access.getAccessId();
        AccessType accessType = access.getAccessType();
        String roleStr = SecurityContext.currentValue(SecurityValue.Role);
        Role role = Role.valueOf(roleStr);
        switch (role) {
            case ADMIN:
                // admin can set any access (include self)
                break;
            case USER:
                // user can only set their managed access (include self)
                validateOwnership(currentAccountId, accessType, accessId);
                break;
        }
        // update grant access id
        access.setGrantAccessId(currentAccountId);
        setAccess0(access);
    }

    private void setAccess0(@Validated @RequestBody Access access) {
        // encrypt password
        access.setAccessSecret(passwordEncoder.encode(access.getAccessSecret()));
        // save access object
        access = accessRepo.save(access);
        LOGGER.info("Save access successful for {}({}) by account {}", access.getAccessId(),
                access.getAccessType(), access.getGrantAccessId());
    }

    // assume this is check account with agent
    private void validateOwnership(String currentId, AccessType accessType, String accessId) {
        switch (accessType) {
            case AGENT:
                boolean hasOwnership = agentService.getAgentsByAccountId(currentId).getContent().stream()
                        .filter(agent -> agent.getAccountId().equals(accessId))
                        .findAny()
                        .isPresent();
                if (hasOwnership) {
                    return;
                }
        }
        throw new InvalidAccessException("No ownership for '" + accessType + "' on " + accessId);
    }


    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    VndErrors invalidAccessExceptionHandler(InvalidAccessException ex) {
        // TODO "error" to be error code or request id for diagnostic
        return new VndErrors("error", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors invalidArgumentExceptionHandler(InvalidArgumentException ex) {
        // TODO "error" to be error code or request id for diagnostic
        return new VndErrors("error", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors objectNotFoundExceptionHandler(ObjectNotFoundException ex) {
        // TODO "error" to be error code or request id for diagnostic
        return new VndErrors("error", ex.getMessage());
    }

    @Override
    public void run(String... args) throws Exception {
        if (!Boolean.parseBoolean(environment.getProperty("imgate.admin.skip", "false"))) {
            setupAdminAccess(environment.getProperty("imgate.admin.id", "admin"),
                    environment.getProperty("imgate.admin.secret", "imgate"));
        }
    }

    private void setupAdminAccess(String id, String secret) {
        Access adminAccess = new Access();
        adminAccess.setAccessId(id);
        adminAccess.setAccessType(Access.AccessType.ACCOUNT);
        adminAccess.setAccessSecret(secret);
        adminAccess.setGrantAccessId(id);
        setAccess0(adminAccess);
    }
}
