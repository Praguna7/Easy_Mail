# Command-Line Email Client

This is a Java-based command-line email client that allows you to send birthday greetings and maintain an email list. The program uses object-oriented principles and design pattern best practices to provide a flexible and user-friendly email client.

## Features

This email client offers the following features:

1. **Recipient Management**: You can add, edit, and store recipient details, including official recipients, official friends, and personal recipients, in a text file.

2. **Birthday Greetings**: The program can send birthday greetings to recipients on their special day. Different greetings are sent to official friends and personal recipients.

3. **Email Sending**: You can send regular emails with specified recipients, subject, and content.

4. **Email Logging**: All sent emails are saved as objects on the hard disk using object serialization.


## Usage

1. **Adding a New Recipient**:
   - Use the command-line option to add a new recipient.
   - Provide recipient details in the format: `Recipient Type: Name, Email, [Designation|Nickname], [Birthday]`.
   - Valid recipient types are "Official," "Office_friend," and "Personal."

2. **Sending an Email**:
   - Use the command-line option to send an email.
   - Enter the recipient's email address, subject, and email content.

3. **Printing Recipients with Birthdays**:
   - Use the command-line option to print recipients who have birthdays on the current date.

4. **Printing Email Details**:
   - Use the command-line option to print details of all the emails sent on a specified date.

5. **Printing Recipient Count**:
   - Use the command-line option to print the number of recipient objects in the application.

## Conclution

- Object serialization is used to save sent emails.
- Recipient details are stored in a text file, and recipient objects are created based on the provided data.
- The email client uses JavaMail API for sending emails.

