package tk.dnstk.imgate.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;
import tk.dnstk.imgate.api.ObjectNotFoundException;
import tk.dnstk.imgate.api.data.ImgateAgentRepository;
import tk.dnstk.imgate.api.model.Agent;
import tk.dnstk.imgate.api.security.AuthorizedOn;

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

    @RequestMapping(method = RequestMethod.GET, path = "")
    public Resource<List<Agent>> getAgentsByAccountId(@RequestParam("accountId") String accountId) {
        List<Agent> list = agentRepo.findByAccountId(accountId);
        return new Resource<>(list);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{agentId}")
    public Resource<Agent> getAgent(@AuthorizedOn(AgentId) @PathVariable("agentId") String agentId) {
        Agent agent = agentRepo.findOne(agentId);
        if (agent == null) {
            throw new ObjectNotFoundException(agentId);
        }
        return new Resource<>(agent);
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public Resource<Agent> createAgentByAccount(@AuthorizedOn(AccountId) @RequestParam("accountId") String accountId) {
        Agent agent = new Agent();
        agent.setAccountId(accountId);
        agent.setAgentId(UUID.randomUUID().toString());
        agent.setCreateDate(new Date());
        agentRepo.save(agent);
        return new Resource<>(agent);
    }
}
