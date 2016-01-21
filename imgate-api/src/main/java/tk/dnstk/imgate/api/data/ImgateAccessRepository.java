package tk.dnstk.imgate.api.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import tk.dnstk.imgate.api.model.Access;

import java.util.Optional;

public interface ImgateAccessRepository extends MongoRepository<Access, String> {

    public Optional<Access> findByAccessIdAndAccessType(String accessId, Access.AccessType accessType);
}
