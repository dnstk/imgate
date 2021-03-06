package tk.dnstk.imgate.api.model;

import javax.validation.constraints.NotNull;

public class AccessRequest {

    @NotNull
    private String accessId;

    @NotNull
    private AccessToken.AccessType accessType;

    @NotNull
    private String accessSecret;

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public AccessToken.AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessToken.AccessType accessType) {
        this.accessType = accessType;
    }
}
