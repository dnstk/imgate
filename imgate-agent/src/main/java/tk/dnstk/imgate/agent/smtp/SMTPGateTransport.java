package tk.dnstk.imgate.agent.smtp;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.*;
import java.io.IOException;
import java.util.Arrays;

public class SMTPGateTransport extends SMTPTransport {

    public SMTPGateTransport(Session arg0, URLName arg1, String arg2, int arg3, boolean arg4) {
        super(arg0, arg1, arg2, arg3, arg4);
    }

    public SMTPGateTransport(Session arg0, URLName arg1) {
        super(arg0, arg1);
    }

    @Override
    protected boolean protocolConnect(String arg0, int arg1, String arg2, String arg3) throws MessagingException {
        return true;
    }

    @Override
    public synchronized void sendMessage(Message message, Address[] address)
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
                    buffer.append("ATTACH:\t" + attachments.substring(", ".length()) + "\n");
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
