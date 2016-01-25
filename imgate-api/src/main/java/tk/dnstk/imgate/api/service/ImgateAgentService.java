package tk.dnstk.imgate.api.service;


import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tk.dnstk.imgate.api.InvalidAccessException;
import tk.dnstk.imgate.api.InvalidArgumentException;
import tk.dnstk.imgate.api.ObjectNotFoundException;
import tk.dnstk.imgate.api.data.ImgateAgentRepository;
import tk.dnstk.imgate.api.model.Agent;
import tk.dnstk.imgate.api.security.AuthorizedOn;
import tk.dnstk.imgate.api.security.SecurityContext;
import tk.dnstk.imgate.api.security.SecurityValue;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static tk.dnstk.imgate.api.security.SecurityValue.AccountId;
import static tk.dnstk.imgate.api.security.SecurityValue.AgentId;

@RestController
@RequestMapping("/agents")
@ControllerAdvice
public class ImgateAgentService {

    @Autowired
    private ImgateAgentRepository agentRepo;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.GET, path = "")
    public Resources<Agent> getAgentsByAccountId(@AuthorizedOn(SecurityValue.AccountId) @RequestParam("accountId") String accountId) {
        List<Agent> list = agentRepo.findByAccountId(accountId);
        return new Resources<>(list);
    }

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.GET, path = "/{agentId}")
    public Resource<Agent> getAgent(@AuthorizedOn(AgentId) @PathVariable("agentId") String agentId) {
        Agent agent = agentRepo.findOne(agentId);
        if (agent == null) {
            throw new ObjectNotFoundException(agentId);
        }
        return new Resource<>(agent);
    }

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.POST, path = "")
    public Resource<Agent> createAgentByAccount(@AuthorizedOn(AccountId) @RequestParam("accountId") String accountId,
                                                @RequestParam(name = "agentId", required = false) String agentId) {
        if (agentId != null) {
            if (agentRepo.findOne(agentId) != null) {
                throw new InvalidArgumentException("Duplicate agent id: " + agentId);
            }
        } else {
            agentId = UUID.randomUUID().toString();
        }
        Agent agent = new Agent();
        agent.setAccountId(accountId);
        agent.setAgentId(agentId);
        agent.setCreateDate(new Date());
        // default password
        agent.setPassword(passwordEncoder.encode(""));
        agentRepo.save(agent);
        return new Resource<>(agent);
    }


    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.PUT, path = "/{agentId}/password")
    public void setAgentPassword(@PathVariable("agentId") String agentId,
                                 @RequestBody String password) {
        String accountId = SecurityContext.currentValue(SecurityValue.AccountId);
        List<Agent> agents = agentRepo.findByAccountId(accountId);
        for (Agent agent : agents) {
            if (agent.getAgentId().equals(agentId)) {
                agent.setPassword(passwordEncoder.encode(password));
                agentRepo.save(agent);
                return;
            }
        }
        throw new InvalidAccessException(agentId);
    }
}
