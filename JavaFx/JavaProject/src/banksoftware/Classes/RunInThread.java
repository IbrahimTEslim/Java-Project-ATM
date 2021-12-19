package banksoftware.Classes;

import javax.mail.MessagingException;

class RunInThread implements Runnable {

    private MailSender mailSender;
    private String to, msgText, msgSubject;

    public RunInThread(MailSender mailSender, String to, String msgText, String msgSubject) {
        this.mailSender = mailSender;
        this.to = to;
        this.msgText = msgText;
        this.msgSubject = msgSubject;
    }

    @Override
    public void run() {
        try {
            mailSender.sendMail(to, msgText, msgSubject);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
