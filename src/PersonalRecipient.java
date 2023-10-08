public class PersonalRecipient extends Recipient {
    public PersonalRecipient(String name, String nickName, String email, String birthday) {
        super(name, email);
        this.nickName = nickName;
        this.birthday = birthday;
    }
}
