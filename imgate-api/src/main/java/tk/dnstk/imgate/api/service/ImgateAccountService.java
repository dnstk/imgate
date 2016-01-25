package tk.dnstk.imgate.api.service;


import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.dnstk.imgate.api.InvalidAccessException;
import tk.dnstk.imgate.api.InvalidArgumentException;
import tk.dnstk.imgate.api.ObjectNotFoundException;
import tk.dnstk.imgate.api.data.ImgateAccountRepository;
import tk.dnstk.imgate.api.model.Account;
import tk.dnstk.imgate.api.model.Role;
import tk.dnstk.imgate.api.security.AuthorizedOn;
import tk.dnstk.imgate.api.security.SecurityContext;
import tk.dnstk.imgate.api.security.SecurityValue;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@ControllerAdvice
public class ImgateAccountService {

    @Autowired
    private ImgateAccountRepository accountRepo;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.POST, path = "/add")
    public Resource<Account> addAccount(@Validated @RequestBody Account account) {
        String createdAccountId = SecurityContext.currentValue(SecurityValue.AccountId);
        Optional<Account> createdAccount = accountRepo.findByAccountId(createdAccountId);
        if (createdAccount.map(Account::getRole).orElse(Role.ADMIN) != Role.ADMIN) {
            throw new InvalidAccessException("Require admin role to add account");
        }
        if (account.getAccountId() == null) {
            account.setAccountId(UUID.randomUUID().toString());
        }
        Account ac = accountRepo.findOne(account.getAccountId());
        if (ac != null) {
            throw new InvalidArgumentException("Duplicate account id: " + account.getAccountId());
        }
        account.setCreatedDate(new Date());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setCreatedAccountId(createdAccount.map(Account::getAccountId).orElse(account.getAccountId()));
        Account result = accountRepo.save(account);
        return new Resource<>(result);
    }

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.GET, path = "/{accountId}")
    public Resource<Account> getAccount(@AuthorizedOn(SecurityValue.AccountId) @PathVariable("accountId") String accountId) {
        Account result = accountRepo.findByAccountId(accountId).orElseThrow(() -> new ObjectNotFoundException(accountId));
        return new Resource<>(result);
    }

    @ApiImplicitParam(name = SecurityValue.TOKEN_HEADER, paramType = "header", required = true)
    @RequestMapping(method = RequestMethod.GET, path = "/roles/{role}")
    public Resources<Account> getAccountsWithRole(@PathVariable("role") String role) {
        Role r = Role.valueOf(role);
        List<Account> result = accountRepo.findByRoleOrderByCreatedDateDesc(r).orElseThrow(() -> new ObjectNotFoundException("role"));
        return new Resources<>(result);
    }
}
