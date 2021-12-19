package banksoftware.Classes;

public class Transaction {
    private int transID, acc_id;
    double amount;
    private char type;

    @Override
    public String toString() {
        return "Transaction { " +
                " transID= " + transID +
                ", acc_id= " + acc_id +
                ", amount= " + amount +
                ", type= " + type +
                " }";
    }

    public Transaction(int transID, int acc_id, char type, double amount) {
        this.transID = transID;
        this.acc_id = acc_id;
        this.type = type;
        this.amount = amount;
    }
}
