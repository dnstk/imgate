package tk.dnstk.imgate.api.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import tk.dnstk.imgate.api.model.AccessToken;

public interface ImgateAccessTokenRepository extends MongoRepository<AccessToken, String> {
}
