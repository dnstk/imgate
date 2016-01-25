package tk.dnstk.imgate.api.security;

public enum SecurityValue {

    AccountId,

    AgentId,

    Token,

    Role;

    public static final String TOKEN_HEADER = "X-Imgate-Token";

    public String getHeaderName() {
        switch (this) {
            case Token:
                return TOKEN_HEADER;
            default:
                return null;
        }

    }
}
