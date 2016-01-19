package tk.dnstk.imgate.agent.smtp;

import com.sun.mail.smtp.SMTPTransport;
import tk.dnstk.imgate.agent.remote.AgentConfig;
import tk.dnstk.imgate.agent.remote.ImgateRemoteClient;
import tk.dnstk.imgate.agent.remote.SMTPMessage;

import javax.mail.*;
import java.io.IOException;
import java.util.Arrays;

public class SMTPGateTransport extends SMTPTransport {

    private ImgateRemoteClient client;

    public SMTPGateTransport(Session arg0, URLName arg1, String arg2, int arg3, boolean arg4) {
        super(arg0, arg1, arg2, arg3, arg4);
    }

    public SMTPGateTransport(Session arg0, URLName arg1) {
        super(arg0, arg1);
    }

    @Override
    protected boolean protocolConnect(String arg0, int arg1, String arg2, String arg3) throws MessagingException {
        AgentConfig config = new AgentConfig();
        // TODO
        config.setAgentId("agentId");
        config.setAgentKey("agentKey");
        config.setRemoteUri("http://localhost:8080/imgate/api/");
        this.client = new ImgateRemoteClient(config);
        return true;
    }

    @Override
    public synchronized void sendMessage(Message message, Address[] address)
            throws MessagingException {
        SMTPMessage msg = new SMTPMessage();
        msg.setSubject(message.getSubject());
        msg.setFromAddress(toStringArray(message.getFrom()));
        msg.setToAddress(toStringArray(message.getRecipients(Message.RecipientType.TO)));
        msg.setCcAddress(toStringArray(message.getRecipients(Message.RecipientType.CC)));
        msg.setBccAddress(toStringArray(message.getRecipients(Message.RecipientType.BCC)));
        try {
            Object content = message.getContent();
            Object bodyContent = content;
            if (content instanceof Multipart) {
                Multipart body = (Multipart) content;
                for (int i = 0; i < body.getCount(); i++) {
                    BodyPart bodyPart = body.getBodyPart(i);
                    if (!"attachment".equals(bodyPart.getDisposition()) && bodyPart.getFileName() == null) {
                        bodyContent = bodyPart.getContent();
                    }
                }
            }
            msg.setBody(String.valueOf(bodyContent));
        } catch (IOException e) {
            throw new SendFailedException(e.getMessage(), e);
        }
        msg.setCreatedDate(message.getSentDate());
        // TODO
        msg.setHtmlMessage(true);
        try {
            this.client.postSmtpMessage(msg);
        } catch (IOException e) {
            throw new SendFailedException("Cannot post smtp message to gate", e);
        }
    }

    private String[] toStringArray(Address[] address) {
        String[] array = new String[address.length];
        for (int i = 0; i < address.length; i++) {
            array[i] = address[i].toString();
        }
        return array;
    }

    private void printMessage(Message message, Address[] address)
            throws MessagingException {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\n");
        buffer.append("======================================================================\n");
        buffer.append("RE:\t").append(message.getSubject()).append("\n");
        buffer.append("FROM:\t").append(arrayToString(message.getFrom())).append("\n");
        buffer.append("TO:\t").append(arrayToString(message.getAllRecipients())).append("\n");
        buffer.append("DATE:\t").append(message.getSentDate()).append("\n");
        try {
            Object content = message.getContent();
            StringBuilder attachments = new StringBuilder();
            Object bodyContent = content;
            if (content instanceof Multipart) {
                Multipart body = (Multipart) content;
                for (int i = 0; i < body.getCount(); i++) {
                    BodyPart bodyPart = body.getBodyPart(i);
                    if ("attachment".equals(bodyPart.getDisposition())) {
                        attachments.append(", ").append(bodyPart.getFileName());
                    } else if (bodyPart.getFileName() == null) {
                        bodyContent = bodyPart.getContent();
                    }
                }
                if (attachments.length() > 0) {
                    buffer.append("ATTACH:\t").append(attachments.substring(", ".length())).append("\n");
                }
            }
            buffer.append("----------------------------------------------------------------------\n");
            buffer.append(bodyContent);
            buffer.append('\n');
        } catch (IOException e) {
            throw new SendFailedException(e.getMessage(), e);
        }
        buffer.append("======================================================================\n");
        System.out.print(buffer);
        System.out.flush();
    }

    private String arrayToString(Address[] list) {
        String s = Arrays.toString(list);
        return s.substring(1, s.length() - 1);
    }

}
