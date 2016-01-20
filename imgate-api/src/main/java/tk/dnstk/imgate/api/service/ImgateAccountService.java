package tk.dnstk.imgate.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.dnstk.imgate.api.ObjectNotFoundException;
import tk.dnstk.imgate.api.data.ImgateAccountRepository;
import tk.dnstk.imgate.api.model.Account;
import tk.dnstk.imgate.api.model.MailMessage;
import tk.dnstk.imgate.api.model.Role;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@ControllerAdvice
public class ImgateAccountService {

    @Autowired
    private ImgateAccountRepository accountRepo;

    @RequestMapping(method = RequestMethod.POST, path="/add")
    public Resource<Account> addAccount(@Validated @RequestBody Account account) {
        account.setCreatedDate(new Date());
        account.setAccountId(null);
        Account result = accountRepo.save(account);
        return new Resource<>(result);
    }

    @RequestMapping(method = RequestMethod.GET, path="/{accountId}")
    public Resource<Account> getAccount(@PathVariable("accountId") String accountId) {
        Account result = accountRepo.findByAccountId(accountId).orElseThrow(() -> new ObjectNotFoundException("accountId"));
        return new Resource<>(result);
    }

    @RequestMapping(method = RequestMethod.GET, path="/role/{role}")
    public Resources<Account> getAccountsWithRole(@PathVariable("role") String role) {
        Role r = Role.valueOf(role);
        List<Account> result = accountRepo.findByRoleOrderByCreatedDateDesc(r).orElseThrow(() -> new ObjectNotFoundException("role"));
        return new Resources<>(result);
    }
}
