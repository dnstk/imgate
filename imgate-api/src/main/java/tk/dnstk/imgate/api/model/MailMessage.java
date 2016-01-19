package tk.dnstk.imgate.api.model;

public class MailMessage {

    private String agentId;

    private String subject;

    private String body;

    private boolean htmlMessage;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isHtmlMessage() {
        return htmlMessage;
    }

    public void setHtmlMessage(boolean htmlMessage) {
        this.htmlMessage = htmlMessage;
    }

    public String[] getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String[] fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String[] getToAddress() {
        return toAddress;
    }

    public void setToAddress(String[] toAddress) {
        this.toAddress = toAddress;
    }

    public String[] getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(String[] ccAddress) {
        this.ccAddress = ccAddress;
    }

    public String[] getBccAddress() {
        return bccAddress;
    }

    public void setBccAddress(String[] bccAddress) {
        this.bccAddress = bccAddress;
    }

    private String[] fromAddress;

    private String[] toAddress;

    private String[] ccAddress;

    private String[] bccAddress;

}
