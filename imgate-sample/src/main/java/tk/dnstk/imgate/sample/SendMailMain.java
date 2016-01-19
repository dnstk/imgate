package tk.dnstk.imgate.sample;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class SendMailMain {

    public static void main(String[] args) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.example.com");
        email.setFrom("from@example.com", "From User");
        email.addTo("to@example.com", "To User");
        email.addTo("to2@example.com", "To 2 User");
        email.addCc("cc@example.com", "CC User");
        email.addBcc("bcc@example.com", "BCC User");
        email.setSubject("Sample Email Subject");
        email.setMsg("<p>This is body content.</p>");
        email.send();
    }
}
