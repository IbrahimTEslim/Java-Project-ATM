package banksoftware.Classes;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.naming.NamingException;

interface MailHandling {
    public void sendMail(String to, String msgText, String msgSubject) throws MessagingException;

    public Message prepareMessage(Session session, String to, String from, String msgSubject,
                                  String msgText) throws MessagingException;

    public boolean isValid(String email) throws NotValidEmail, NamingException;

    public String codeGenerator();
}
