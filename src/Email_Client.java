/**
 * Program to send birthday mails and maintain email list
 * @author Praguna Chandrasekara-200084L
 */
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.SocketException;
import java.util.Properties;
import javax.mail.internet.AddressException;
import java.io.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Email_Client {

    //stores sent mails as outgoingMail objects
    private static List<outgoingMail> sentMails = new ArrayList<>();
    private static boolean sentFromFile=false;

    /**
     * used to split comma separated string
     * @param line comma separated string
     * @return array of strings splitted and trimmed by comma
     */
    private static String[] stringSplit(String line){

        String[] returnArr=line.trim().split("[:,]");
        for(int i=0;i< returnArr.length;i++){
            returnArr[i]=returnArr[i].trim();
        }
        return returnArr;
    }

    //to store recipients with birthdays as recipient objects
    static List<Recipient> recipientsWithBD = new ArrayList<>();

    //to store official recipients as recipient objects
    static List<Recipient> officialRecipients = new ArrayList<>();

    /**
     * send birthday wishing emails
     * @param r a recipient object who has birthday today
     */
    private static void sendBirthdayMail(Recipient r ){

        String email=r.email;
        String sub = "Happy Birthday!";
        String body = Recipient.getBDText(r);
        outgoingMail ml = new outgoingMail(email,sub,body,true); //create mail object

//only send if not send a wishing mail in same day
        if(!isSendBDMailAlready(ml)) {
            if(!sentFromFile){
                System.out.println("Sending B'Day mail(s)! Please wait...");
                sentFromFile=true;
            }
            SendEmail.mail(ml);
            sentMails.add(ml); //store sent mail as mail object
        }
    }

    /**
     * add record of a given recipient
     * @param dataLine comma separated string containing recipient's data
     * ex:-Personal: sunil,<nick-name>,sunil@gmail.com,2000/10/10
     * ex:-Official: nimal,nimal@gmail.com,ceo
     * ex:-Office_friend: kamal,kamal@gmail.com,clerk,2000/12/12
     * @return true if recipient was added succesfully
     */
    private static boolean addRecipient(String dataLine,boolean fromCmdLine){
        String[] inputArray =stringSplit(dataLine);
        if(validator.recordValidate(inputArray)) { //check if the input in valid
            Recipient newResObj = RecipientFactory.getRecipient(inputArray);
            if (newResObj.birthday == null) {
//add officialRecipients
                officialRecipients.add(newResObj);
            } else {
//add official friends and personal recipients
                recipientsWithBD.add(newResObj);
                if (isBirthDay(newResObj)) {
//birthdayPeople.add(newResObj);
                    if(fromCmdLine){
                        System.out.println("Today is "+newResObj.name+"'s birthday!");
                        System.out.println("Sending Birthday Wishes...");
                    }
                    sendBirthdayMail(newResObj);
                }
            }
            return true;
        }
        else return false;
    }

    /**
     * check whether birthday of a recipient is today
     * @param res recipient object
     * @return if recipient having birthday today return true
     */
    private static boolean isBirthDay(Recipient res) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
//convert birth date into LocalDate object
        LocalDate birthDate = LocalDate.parse(res.birthday,formatter);
        LocalDate today = LocalDate.now();
        if(birthDate.getMonthValue()==today.getMonthValue() &&
                birthDate.getDayOfMonth() == today.getDayOfMonth() ){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * check whether birthday of a recipient is in given date
     * @param res recipient object
     * @return if recipient having birthday in given date return true
     */
    private static boolean isBirthDay(Recipient res,String date)throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
        LocalDate givenDate;
        givenDate = LocalDate.parse(date,formatter);

//convert birth date into LocalDate object
        LocalDate birthDate = LocalDate.parse(res.birthday,formatter);

        if(birthDate.getMonthValue()==givenDate.getMonthValue() &&
                birthDate.getDayOfMonth() == givenDate.getDayOfMonth() ){
            return true;
        }
        else{
            return false;
        }
    }


    /**
     * write given string to given file
     * @param line a string
     * @param f a file
     */
    private static void write(String line,File f){
        try {
            FileWriter fw = new FileWriter(f,true);
            fw.write(line+"\n");

            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * check whether birthday mail for a particular recipient is sent already
     * @param mail mail object
     * @return return true if already sent
     */
    private static boolean isSendBDMailAlready(outgoingMail mail){
        LocalDate today =LocalDate.now();
        for (outgoingMail sentMail:sentMails) {
            if(mail.emailAddress.equalsIgnoreCase(sentMail.emailAddress) && sentMail.isBDMail){
                if(mail.sentDate.isEqual(today)){
                    return true;
                }
            }
        }
        return false;
    }

    private static String getInput(){
        Scanner scn1 =new Scanner(System.in);
        String input="";
        while (true){
            input = scn1.nextLine();
            input=input.trim();
            if(!input.equalsIgnoreCase("") ) break;
        }
        return input;
    }

    /**
     * main method which initiates the program
     */
    public static void main(String[] args) throws IOException {

        try {
            EmailClient();
        } catch (IOException e) {
            System.out.println(e);
        }


    }
    private static void EmailClient() throws IOException {
//name of file which is used to store serialized mail objects
        final String serEmailFileName = "sentEmails.ser";
//name of text file which used to store recipient records
        final String clientListFileName = "clientList.txt";

// Load recipients from clientfile and create objects
        File clientFile = new File(clientListFileName);
        clientFile.createNewFile();
        Scanner scn = new Scanner(clientFile);

//De-serialize stored outgoingMail objects
        File temp =new File(serEmailFileName);
        temp.createNewFile();
        FileInputStream fis = new FileInputStream(serEmailFileName);
        ObjectInputStream ois=null;
        try {
            ois =new ObjectInputStream(fis);
        }catch (EOFException aa){;}

        while (true && ois!=null) {
            try {
                sentMails.add((outgoingMail) ois.readObject());

            } catch (OptionalDataException | ClassNotFoundException e) {
                break;
            } catch (EOFException a) {
                break;
            }
        }
        if (ois != null) {
//when serializing file is empty
            ois.close();
        }
        fis.close();


        int i =1;
//traverse through clientFile and create objects
        while(scn.hasNextLine()){
            String next= scn.nextLine();
            if(next.trim().equalsIgnoreCase("") && scn.hasNextLine()) {
                next= scn.nextLine();
                i++;
            }
            if(next.trim().equalsIgnoreCase("") && !scn.hasNextLine()) {
                break;
            }
            boolean isSuccess =addRecipient(next,false);
            if (!isSuccess){
//if invalid record is found, notify user which line caused the error
                System.out.println("Invalid Record Found in Line : "+i+" of "+clientFile.getName());
                System.out.println("Correct it and try again");
                System.exit(0);
            }
            i++;
        }


        System.out.println("Enter option type: \n" +
                " 0 - Exit\n" +
                " 1 - Adding a new recipient\n" +
                " 2 - Sending an email\n" +
                " 3 - Printing out all the recipients who have birthdays\n" +
                " 4 - Printing out details of all the emails sent\n" +
                " 5 - Printing out the number of recipient objects in the application");


        int option;
        try{
            option = Integer.parseInt(getInput());
        } catch (NumberFormatException e) {
//when user input is invalid
//option=-1 triggers default case
            option=-1;
        }

        while (true) {
            switch (option) {
                case 0:
//Serialize mail objects and save
                    FileOutputStream fos =new FileOutputStream(serEmailFileName);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    for (outgoingMail sentMail : sentMails) {
                        oos.writeObject(sentMail);
                    }

                    oos.close();
                    fos.close();
//when option is zero
                    System.out.println("Thank You For Using..!");
//Terminate the program
                    System.exit(0);
                    break;
                case 1:
// input format - Official: nimal,nimal@gmail.com,ceo
// Use a single input to g
// et all the details of a recipient
// code to add a new recipient
// store details in clientList.txt file
// Hint: use methods for reading and writing files
                    String input=getInput();

                    if(!addRecipient(input,true)){
                        System.out.println("Invalid input");
                        break;
                    }
//write record to the clientFile
                    write(input, clientFile);
                    System.out.println("Record added successfully");
                    break;

                case 2:
// input format - email, subject, content
// code to send an email
                    System.out.println("Enter email address, subject, body as comma separated String : ");
                    String emailInput=getInput();
                    String[] splittedEmail = emailInput.split(",");

                    if(!validator.emailValidate(splittedEmail[0])){
                        System.out.println("Invalid E-mail Address!");
                    }
                    else if(splittedEmail.length==1){
                        System.out.println("No subject and body found!");
                        break;
                    }
                    else if(splittedEmail.length==2){
                        System.out.println("No email body found!");
                        break;
                    }
                    else{
                        outgoingMail m = new outgoingMail(splittedEmail[0],
                                splittedEmail[1],splittedEmail[2],false);

                        System.out.println("Sending email....");
                        System.out.println("Please wait...");
                        if(SendEmail.mail(m)){
                            sentMails.add(m);
                            System.out.println("Email was sent successfully!");
                        }
                        else {
                            System.out.println("Sending failed!");
                        }
                    }
                    break;

                case 3:
// input format - yyyy/MM/dd (ex: 2018/09/17)
// code to print recipients who have birthdays on the given date
                    System.out.println("Enter date in format of yyyy/mm/dd : ");
                    String givenDate = getInput();
                    boolean noBDs=true;
//traverse through recipients list to find if
// anyone having birthday on given date
                    for (Recipient recipient : recipientsWithBD) {
                        if(validator.dateValidate(givenDate)){ //validate date
                            if (isBirthDay(recipient, givenDate)) {
                                System.out.println(recipient.name);
                                noBDs=false;
                            }
                        }
                        else {
//if user input is invalid
                            System.out.println("Invalid Date : (Accepted format : yyyy/MM/dd)");
                            noBDs=false;
                            break;
                        }
                    }
                    if(noBDs){
                        System.out.println("No one is having birthdays on "+givenDate);
                    }
                    break;

                case 4:
// input format - yyyy/MM/dd (ex: 2018/09/17)
// code to print the details of all the emails sent on the input date
                    System.out.println("Enter date in format of yyyy/mm/dd : ");
                    String date = getInput();
                    if(validator.dateValidate(date)) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
                        LocalDate reqestedDate = LocalDate.parse(date,formatter);

                        int k = 1; //keep track on number of emails
                        boolean sendAtLeastOne = false;
                        for (outgoingMail mail : sentMails) {
                            if (mail.sentDate.isEqual(reqestedDate)) {
                                System.out.println("Email " + k + " ===========" +
                                        "======================================");
                                System.out.println("Email Address : " + mail.emailAddress);
                                System.out.println("Subject : " + mail.subject);
                                System.out.println("Email Body : " + mail.body);
                                System.out.println("Date sent : "+mail.sentDate);
                                System.out.println("=========================" +
                                        "================================\n");
                                sendAtLeastOne = true;
                                k++;
                            }
                        }
                        if(!sendAtLeastOne){
                            System.out.println("No emails were sent on "+date);
                        }
                    }
                    else {
                        System.out.println("Invalid Date : (Accepted format : yyyy/MM/dd)");
                    }
                    break;

                case 5:
// code to print the number of recipient objects in the application
                    System.out.println("Number of E-mail Recipients : " + Recipient.count);
                    break;

                default:
//when user input is invalid
                    System.out.println("Invalid Input : Please Try Again...!");
                    break;
            }
            System.out.println("Enter option type:");
            try{
                option = Integer.parseInt(getInput());
            } catch (NumberFormatException e) {
//if user input is invalid
//by making option = -1 default case is triggered in next iteration
                option=-1;
            }
        }
    }
}


/**
 * Contain methods to validate user inputs
 * @author Praguna Chandrasekara
 */
public class validator{

    /**
     * to validate an email address
     * @param email email as a string
     * @return true if email is valid
     */
    public static boolean emailValidate(String email){
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        }
        catch (AddressException e) {
            return false;
        }
        return true;
    }

    /**
     * to validate a date
     * @param date string containing date
     * @return true if date is valid
     */
    public static boolean dateValidate(String date){
//accepted date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
        try{
            LocalDate ld = LocalDate.parse(date,formatter);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * to validate a data record stored in clientFile
     * @param line a string of record
     * @return true if valid
     */
    public static boolean recordValidate(String[] line){
//if recipient is official
        if(line[0].equalsIgnoreCase("official")){
            if(line.length!=4) return false;
            if(!validator.emailValidate(line[2])) return false;
        }

//if recipient is office_friend
        else if(line[0].equalsIgnoreCase("office_friend")){
            if(line.length!=5) return false;
            if(!emailValidate(line[2])) return false;
            if(!validator.dateValidate(line[4])) return false;
        }
//if recipient is personal
        else if(line[0].equalsIgnoreCase("personal")){
            if(line.length!=5) return false;
            if(!emailValidate(line[3])) return false;
            if(!validator.dateValidate(line[4])) return false;
        }
        else return false;
        return true;
    }
}

/**
 * contain method to send email
 * @author MK
 */
class SendEmail {

    public static boolean mail(outgoingMail m) {

        String receiversEmail=m.emailAddress;
        String subject=m.subject;
        String content=m.body;

        final String username = "email.clientoop@gmail.com";
        final String password = "dnuituczqebjfqcq";


        Properties prop = new Properties();
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(receiversEmail)
            );
            message.setSubject(subject);
            message.setText(content);

            try{Transport.send(message);}
            catch (MessagingException a3){
                return false;
            }

            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;

        }
    }
}


