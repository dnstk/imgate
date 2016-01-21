package tk.dnstk.imgate.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document
public class Access {

    public enum AccessType {
        ACCOUNT, AGENT;
    }

    @Id
    private String accessId;

    @NotNull
    private AccessType accessType;

    @NotNull
    private String accessSecret;

    // who grant this access
    private String grantAccessId;

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getGrantAccessId() {
        return grantAccessId;
    }

    public void setGrantAccessId(String grantAccessId) {
        this.grantAccessId = grantAccessId;
    }
}
