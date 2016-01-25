package tk.dnstk.imgate.agent.remote;

import tk.dnstk.imgate.agent.com.eclipsesource.json.JsonObject;

class AgentToken {

    private String tokenId;

    private String accountId;

    public static AgentToken fromJSON(String jsonString) {
        JsonObject json = JsonObject.readFrom(jsonString);
        AgentToken token = new AgentToken();
        token.setTokenId(json.get("tokenId").asString());
        token.setAccountId(json.get("accountId").asString());
        return token;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
