package banksoftware.Classes;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

public class MailSender implements MailHandling {
    private static String senderEmail = "iugjavaproject@gmail.com";
    private static String password = "java@project2";

    public MailSender() {
    }

    public MailSender(String username, String password) {
        senderEmail = username;
        this.password = password;
    }

    public void sendMail(String to, String msgSubject, String msgText) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });

        Message message = prepareMessage(session, to, senderEmail, msgSubject, msgText);
        Transport.send(message);
    }

    public Message prepareMessage(Session session, String to, String from, String msgSubject,
                                  String msgText) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

        message.setSubject(msgSubject);
        message.setText(msgText);

        return message;
    }

    private static ArrayList getMX(String hostName)
            throws NamingException {
        // Perform a DNS lookup for MX records in the domain
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial",
                "com.sun.jndi.dns.DnsContextFactory");
        DirContext ictx = new InitialDirContext(env);
        Attributes attrs = ictx.getAttributes
                (hostName, new String[]{"MX"});
        Attribute attr = attrs.get("MX");
        // if we don't have an MX record, try the machine itself
        if ((attr == null) || (attr.size() == 0)) {
            attrs = ictx.getAttributes(hostName, new String[]{"A"});
            attr = attrs.get("A");
            if (attr == null)
                throw new NamingException("No match for name '" + hostName + "'");
        }
        // Huzzah! we have machines to try. Return them as an array list
        // NOTE: We SHOULD take the preference into account to be absolutely
        //   correct. This is left as an exercise for anyone who cares.
        ArrayList res = new ArrayList();
        NamingEnumeration en = attr.getAll();
        while (en.hasMore()) {
            String x = (String) en.next();
            String f[] = x.split(" ");
            if (f[1].endsWith("."))
                f[1] = f[1].substring(0, (f[1].length() - 1));
            res.add(f[1]);
        }
        return res;
    }

    /**
     * Check if email is valid or not
     */
    public boolean isValid(String email) throws NamingException, NotValidEmail {
        if (email == null) {
            throw new NotValidEmail("Not a Valid Email :(");
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        boolean goodSyntax = pat.matcher(email).matches();
        if (!goodSyntax) {
            throw new NotValidEmail("Not a Valid Email :(");
        } else {
            ArrayList mx = getMX(email.substring((email.indexOf('@')) + 1));
            if (mx.size() == 0) {
                throw new NamingException("No Domain Found !");
            }
        }
        return true;
    }

    /**
     * Returns a random 6-digit number
     */
    @Override
    public String codeGenerator() {
        Random rnd = new Random();
        return String.format("%06d", rnd.nextInt(999999));
    }
}
