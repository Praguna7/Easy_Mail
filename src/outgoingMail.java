import java.io.Serializable;
import java.time.LocalDate;

/**
 * creates outgoingMail objects
 *
 * @author Praguna Chandrasekara
 */
public class outgoingMail implements Serializable {
    String emailAddress;
    String subject;
    String body;
    LocalDate sentDate;
    boolean isBDMail;

    public outgoingMail(String emailAddress, String subject, String body, boolean isBDMail) {
        this.emailAddress = emailAddress;
        this.subject = subject;
        this.body = body;
        this.sentDate = LocalDate.now();
        this.isBDMail = isBDMail;

    }
}
