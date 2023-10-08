public class OfficialFriend extends OfficialRecipient {
    public OfficialFriend(String name, String email, String designation, String birthday) {
        super(name, email, designation);
        this.birthday = birthday;
    }
}
