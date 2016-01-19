package tk.dnstk.imgate.api.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import tk.dnstk.imgate.api.model.MailMessage;

import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 2016/1/19.
 */
public interface ImgateMessageRepository extends MongoRepository<MailMessage, String> {
    public Optional<MailMessage> findByMessageId(String messageId);

    public Optional<List<MailMessage>> findByAccountId(String accountId);

    public Optional<List<MailMessage>> findByAccountIdAndAgentId(String accountId, String agentId);

    public Optional<List<MailMessage>> findFirst50ByAccountIdOrderByCreatedDateDesc(String accountId);
}
