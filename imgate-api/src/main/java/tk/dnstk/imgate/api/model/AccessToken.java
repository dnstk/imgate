package tk.dnstk.imgate.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class AccessToken {

    @Id
    private String tokenId;

    private Date createDate;

    private Date expireDate;

    private String accountId;

    private String accessId;

    private AccessType accessType;

    private Role accessRole;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

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

    public Role getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(Role accessRole) {
        this.accessRole = accessRole;
    }

    public enum AccessType {
        ACCOUNT, AGENT
    }
}
