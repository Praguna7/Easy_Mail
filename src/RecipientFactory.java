/**
 * contains method to create recipient objects
 *
 * @author Praguna Chandrasekara
 */
public class RecipientFactory {
    public static Recipient getRecipient(String[] inputLine) {
        String recipientType = inputLine[0].toLowerCase();
        Recipient newRecipient = null;
        switch (recipientType) {
            case "official":
                newRecipient = new OfficialRecipient(inputLine[1], inputLine[2], inputLine[3]);
                break;

            case "office_friend":
                newRecipient = new OfficialFriend(inputLine[1], inputLine[2], inputLine[3], inputLine[4]);
                break;
            case "personal":
                newRecipient = new PersonalRecipient(inputLine[1], inputLine[2], inputLine[3], inputLine[4]);
                break;
        }
        return newRecipient;
    }
}
