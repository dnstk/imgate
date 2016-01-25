package tk.dnstk.imgate.agent.remote;

import tk.dnstk.imgate.agent.com.eclipsesource.json.JsonObject;

class AgentAccess {

    private String accessId;

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

    public String toJSON() {
        JsonObject json = new JsonObject();
        json.add("accessId", getAccessId());
        json.add("accessSecret", getAccessSecret());
        json.add("accessType", "AGENT");
        return json.toString();
    }
}
