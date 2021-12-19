package banksoftware.Classes;

class ATMMachine extends Bank {

    public boolean checkAccount(int id, int pin) {
        return findAccount(id, pin) != -1;
    }

    public int findAccount(int id, int pin) {
        for (int i = 0; i < a.size(); i++) {
            if ((a.get(i).getNumber() == id) && (a.get(i).getPin() == pin)) {
                return i;
            }
        }
        return -1;
    }

}
