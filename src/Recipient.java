/**
 * used to create recipient objects
 */
public class Recipient {
    static int count = 0;
    private final static String myName = "Praguna";
    String name;
    String email;
    String birthday, nickName, designation = null;

    public Recipient(String name, String email) {
        this.name = name;
        this.email = email;
        count++;
    }

    public static String getBDText(Recipient r) {
        if (r.getClass().equals(PersonalRecipient.class)) {
            return "hugs and love on your birthday. " + myName + ".";
        } else if (r.getClass().equals(OfficialFriend.class)) {
            return "Wish you a Happy Birthday. " + myName + ".";
        } else return null;
    }
}
