package tk.dnstk.imgate.api.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import tk.dnstk.imgate.api.model.Account;
import tk.dnstk.imgate.api.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 2016/1/20.
 */
public interface ImgateAccountRepository extends MongoRepository<Account, String> {
    public Optional<Account> findByAccountId(String accountId);

    public Optional<List<Account>> findByRoleOrderByCreatedDateDesc(Role role);
}
