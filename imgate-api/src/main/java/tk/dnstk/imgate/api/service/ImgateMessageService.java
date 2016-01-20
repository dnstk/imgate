package tk.dnstk.imgate.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.dnstk.imgate.api.ObjectNotFoundException;
import tk.dnstk.imgate.api.data.ImgateMessageRepository;
import tk.dnstk.imgate.api.model.MailMessage;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/accounts/{accountId}/messages")
@ControllerAdvice
public class ImgateMessageService {

    @Autowired
    private ImgateMessageRepository messageRepo;

    @RequestMapping(method = RequestMethod.GET, path="/{messageId}")
    public Resource<MailMessage> getMessage(@PathVariable("accountId") String accountId,
                                            @PathVariable("messageId") String messageId) {
        MailMessage message = messageRepo.findByMessageId(messageId).orElseThrow(() -> new ObjectNotFoundException(messageId));
        return new Resource<>(message);
    }

    @RequestMapping(method = RequestMethod.GET, path="")
    public Resources<MailMessage> getmessages(@PathVariable("accountId") String accountId) {
        List<MailMessage> messages = messageRepo.findByAccountId(accountId).orElseThrow(() -> new ObjectNotFoundException(accountId));
        return new Resources<>(messages);
    }

    @RequestMapping(method = RequestMethod.GET, path="/pop50")
    public Resources<MailMessage> getPop50messages(@PathVariable("accountId") String accountId) {
        List<MailMessage> messages = messageRepo.findFirst50ByAccountIdOrderByCreatedDateDesc(accountId).orElseThrow(() -> new ObjectNotFoundException(accountId));
        return new Resources<>(messages);
    }

    @RequestMapping(method = RequestMethod.POST, path="")
    public Resource<MailMessage> addMailMessage(@PathVariable("accountId") String accountId,
                                                @Validated @RequestBody MailMessage message) {
        message.setAccountId(accountId);
        message.setMessageId(null);
        message.setCreatedDate(new Date());
        MailMessage result = messageRepo.save(message);
        return new Resource<>(result);
    }
}
