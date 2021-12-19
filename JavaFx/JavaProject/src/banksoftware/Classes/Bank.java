package banksoftware.Classes;

import java.util.ArrayList;
import java.util.Arrays;

class Bank {
    ArrayList<Customer> a = new ArrayList<>();
    ArrayList<Integer> id = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
    ArrayList<Integer> pin = new ArrayList<>(Arrays.asList(1234, 2341, 3412, 4123));

    public Bank() {
        insertData();
    }

    public void insertData() {
        for (int i = 0; i < id.size(); i++) {
            a.add(new Customer(pin.get(i), id.get(i)));
        }
    }

}
