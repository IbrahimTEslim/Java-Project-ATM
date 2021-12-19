package banksoftware.Classes;

public class NotValidEmail extends Exception {
    public NotValidEmail(String msg) {
        super(msg);
    }

    public NotValidEmail(String msg, Throwable err) {
        super(msg, err);
    }
}
