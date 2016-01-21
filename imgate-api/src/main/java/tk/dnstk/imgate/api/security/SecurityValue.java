package tk.dnstk.imgate.api.security;

/**
 * Created by zfeng on 1/20/2016.
 */
public enum SecurityValue {

    AccountId,

    AgentId,

    Token("X-Imgate-Token"),

    Role;

    private final String headerName;

    private SecurityValue(String headerName) {
        this.headerName = headerName;
    }

    private SecurityValue() {
        this.headerName = null;
    }

    public String getHeaderName() {
        return headerName;
    }
}
