package banksoftware.Classes;

public class Account {
    private int Acc_id, pin;
    private double balance;
    private boolean status;

    public Account(int acc_id, int pin, double balance, boolean status) {
        this.Acc_id = acc_id;
        this.pin = pin;
        this.balance = balance;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public int getAcc_id() {
        return Acc_id;
    }

    @Override
    public String toString() {
        return "Account{" +
                " Acc_id= " + Acc_id +
                ", pin= " + pin +
                ", balance= " + String.format("%,.3f",balance) +
                ", status : "+
                ((status)? "closed" : "open")+
                " }";
    }
}
