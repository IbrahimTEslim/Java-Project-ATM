package banksoftware.Classes;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

interface DatabaseHandling {
    public boolean checkCustomerExists(int pin, String name) throws SQLException;

    public boolean insertClient(int pin, String name, String email) throws SQLException;

    public boolean deleteCustomer(int pin, String name) throws SQLException;

    public ArrayList<Account> getCustomerAccounts(int pin) throws SQLException, IOException;

    public String getEmail(int pin) throws SQLException;

    public boolean addTransaction(int acc_id, char type, double amount) throws SQLException;

    public double getAccountBalance(int AccID) throws SQLException, IOException;

    public void deleteAccount(int accID) throws SQLException;

    public void deleteTransaction(int accID, char type, double amount) throws SQLException;

    public void deleteTransactionByID(int transID) throws SQLException;

    public ArrayList<Transaction> getTransactions(int accID) throws SQLException, IOException;

    public void addAccount(int pin) throws SQLException;

    public void addAccount(int pin, double balance) throws SQLException;

    public boolean checkAccount(int pin, int accId) throws SQLException, IOException;

    public boolean checkAccount(int accId) throws SQLException, IOException;

    public String getStringTransactions(int accID) throws IOException, SQLException;

    public boolean addTransaction(int acc_id, char type, double amount, int source) throws SQLException;

    public int getTransactionsNumber(int accID) throws SQLException;

    public String suggestPin() throws SQLException;

    public boolean checkPin(int pin) throws SQLException;

    public void deleteAllTransactions(int accID) throws SQLException;

    public String getEmailFromAccid(int accId) throws SQLException;

    public int getPin(int accId) throws SQLException;

    public boolean isIllegalDelete(int transID, int accID) throws SQLException;

    public boolean isThisForThat(int pin, int accID) throws SQLException;

    public ArrayList<Transaction> showPages(int pageSize, int pageNumber, int accId) throws SQLException, IOException;

    public ArrayList<Transaction> getLastNRecords(int accID, int n) throws SQLException, IOException;

    public void insertLoan(int accID, int patron1, int patron2, double amount, int years) throws SQLException;

    public String getStartDateLoan(int accid) throws SQLException;

    public String getEndDateLoan(int accid) throws SQLException;

    public double getLoanAmount(int accId) throws SQLException;

    public void setAccountOpen(int accID, boolean open) throws SQLException;

    public boolean checkAccountStatus(int accid) throws SQLException;

    public double getLoanPaid(int accid) throws SQLException;

    public int[] getPatrons(int accid) throws SQLException;

    public void deleteLoan(int accId) throws SQLException;

    public double getWithdrawn(int accID) throws SQLException;

    public Loan getLoan(int accid) throws SQLException, ParseException;

    public void close() throws SQLException;
}
