package tk.dnstk.imgate.api.service;


import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.dnstk.imgate.api.ObjectNotFoundException;
import tk.dnstk.imgate.api.data.ImgateMessageRepository;
import tk.dnstk.imgate.api.model.MailMessage;
import tk.dnstk.imgate.api.security.AuthorizedOn;
import tk.dnstk.imgate.api.security.SecurityContext;
import tk.dnstk.imgate.api.security.SecurityValue;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/accounts/{accountId}/messages")
@ControllerAdvice
public class ImgateMessageService {

    @Autowired
    private ImgateMessageRepository messageRepo;

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.GET, path = "/{messageId}")
    public Resource<MailMessage> getMessage(@AuthorizedOn(SecurityValue.AccountId)
                                            @PathVariable("accountId") String accountId,
                                            @PathVariable("messageId") String messageId) {
        MailMessage message = messageRepo.findByMessageId(messageId).orElseThrow(() -> new ObjectNotFoundException(messageId));
        return new Resource<>(message);
    }

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.GET, path = "")
    public Resources<MailMessage> getMessages(@AuthorizedOn(SecurityValue.AccountId)
                                              @PathVariable("accountId") String accountId) {
        List<MailMessage> messages = messageRepo.findByAccountId(accountId).orElseThrow(() -> new ObjectNotFoundException(accountId));
        return new Resources<>(messages);
    }

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.GET, path = "/recent50")
    public Resources<MailMessage> getRecent50Messages(@AuthorizedOn(SecurityValue.AccountId)
                                                      @PathVariable("accountId") String accountId) {
        List<MailMessage> messages = messageRepo.findFirst50ByAccountIdOrderByCreatedDateDesc(accountId).orElseThrow(() -> new ObjectNotFoundException(accountId));
        return new Resources<>(messages);
    }

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.POST, path = "")
    public Resource<MailMessage> addMessage(@AuthorizedOn(SecurityValue.AccountId)
                                            @PathVariable("accountId") String accountId,
                                            @Validated @RequestBody MailMessage message) {
        message.setAccountId(accountId);
        message.setAgentId(SecurityContext.currentValue(SecurityValue.AgentId));
        message.setMessageId(null);
        message.setCreatedDate(new Date());
        MailMessage result = messageRepo.save(message);
        return new Resource<>(result);
    }
}
