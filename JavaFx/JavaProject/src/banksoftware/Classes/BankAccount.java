package banksoftware.Classes;

class BankAccount {

    private double balance = 0;

    public BankAccount(int balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    public void withdraw(double mount) {
        balance -= mount;
    }

    public void deposit(double mount) {
        balance += mount;
    }

}
