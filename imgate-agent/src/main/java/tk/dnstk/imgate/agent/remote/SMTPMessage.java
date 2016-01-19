package tk.dnstk.imgate.agent.remote;

import tk.dnstk.imgate.agent.com.eclipsesource.json.JsonArray;
import tk.dnstk.imgate.agent.com.eclipsesource.json.JsonObject;

import java.util.Date;

public class SMTPMessage {

    private String subject;

    private String body;

    private boolean htmlMessage;

    private String[] fromAddress;

    private String[] toAddress;

    private String[] ccAddress;

    private String[] bccAddress;

    private Date createdDate;

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String toJSON() {
        JsonObject json = new JsonObject();
        json.add("subject", getSubject());
        json.add("body", getBody());
        addAddress(json, "fromAddress", getFromAddress());
        addAddress(json, "toAddress", getToAddress());
        addAddress(json, "ccAddress", getCcAddress());
        addAddress(json, "bccAddress", getBccAddress());
        json.add("htmlMessage", isHtmlMessage());
        Date date = getCreatedDate();
        if (date==null) {
            date = new Date();
        }
        json.add("createdDate", date.getTime());
        return json.toString();
    }

    private void addAddress(JsonObject json, String name, String[] address) {
        if (address == null) {
            return;
        }
        JsonArray array = new JsonArray();
        for(String as:address) {
            array.add(as);
        }
        json.add(name, array);
    }

}
