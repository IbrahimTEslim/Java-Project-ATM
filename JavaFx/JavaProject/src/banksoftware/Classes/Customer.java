package banksoftware.Classes;

class Customer {
    BankAccount checking = new BankAccount(0);
    BankAccount savings = new BankAccount(0);
    private int pin;
    private int number;

    public Customer(int pin, int number) {
        this.pin = pin;
        this.number = number;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getPin() {
        return pin;
    }

    public double getNumber() {
        return number;
    }
}
