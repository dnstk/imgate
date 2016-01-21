package tk.dnstk.imgate.api.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import tk.dnstk.imgate.api.model.Agent;

import java.util.List;

public interface ImgateAgentRepository extends MongoRepository<Agent, String> {

    public List<Agent> findByAccountId(String accountId);

}
