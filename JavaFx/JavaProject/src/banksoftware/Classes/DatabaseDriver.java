package banksoftware.Classes;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseDriver implements DatabaseHandling {
    private Connection con;
    //    private Statement statement;
    //    private ResultSet resultSet;
     private String urlDatabase = "jdbc:oracle:oci:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orclpdb.mshome.net)))";
    private String username = "javaproject", password = "javaproject";



    /**
     * a Constructor which import jdbc driver and create a connection to the database
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public DatabaseDriver() throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection(urlDatabase, username, password);

    }

    /**
     * check this customer is exist or not
     *
     * @param pin
     * @param name
     * @return
     * @throws SQLException
     */
    public boolean checkCustomerExists(int pin, String name) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(*) from CUSTOMERS where PIN = "
                + pin + " and Name = '" + name.toLowerCase() + "'");
        resultSet.next();
        if (resultSet.getInt(1) == 1) {
            resultSet.close();
            return true;
        } else {
            resultSet.close();
            return false;
        }
    }

    /**
     * insert a new customer into Customer table
     *
     * @param pin
     * @param name
     * @param email
     * @return
     * @throws SQLException
     */
    public boolean insertClient(int pin, String name, String email) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("insert into Customers (PIN,Name,Email) Values (" + pin + ", '" + name + "', '" + email + "')");
        statement.executeQuery("insert into Accounts (PIN,isClosed) Values (" + pin + ", " + 0 + ")");
        statement.executeQuery("insert into Accounts (PIN,isClosed) Values (" + pin + ", " + 0 + ")");
        statement.executeQuery("commit");
        return checkCustomerExists(pin, name);
    }

    /**
     * delete a customer and automatically delete all his accounts and transactions and loans
     *
     * @param pin
     * @param name
     * @return
     * @throws SQLException
     */
    public boolean deleteCustomer(int pin, String name) throws SQLException {
        Statement statement = con.createStatement();
        if (!checkCustomerExists(pin, name)) {
            return true;
        }
        statement.executeQuery("DELETE FROM Customers WHERE pin = " + pin + " and Name = '" + name + "'");
        statement.executeQuery("commit");
        return (!checkCustomerExists(pin, name));
    }

    /**
     * return all Accounts this customer have as an ArrayList<Accounts>
     *
     * @param pin
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public ArrayList<Account> getCustomerAccounts(int pin) throws SQLException, IOException {
        Statement statement = con.createStatement();
        ArrayList<Account> accounts = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("Select * from Accounts where pin = "
                + pin + " ORDER BY id ASC NULLS LAST ");
        while (resultSet.next()) {
            int tempAccId = resultSet.getInt(1);
            boolean status = resultSet.getInt(3) == 1 ? true : false; // false = notClosed -- true = closed
            accounts.add(new Account(tempAccId, resultSet.getInt(2), getAccountBalance(tempAccId), status));
        }
        resultSet.close();
        return accounts;
    }

    /**
     * return the cusotmer email
     *
     * @param pin
     * @return
     * @throws SQLException
     */
    public String getEmail(int pin) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("Select Email from customers WHERE PIN = " + pin);
        resultSet.next();
        String email = resultSet.getString(1);
        resultSet.close();
        return email;
    }

    /**
     * Not in use anymore
     */
    public boolean addTransaction(int acc_id, char type, double amount) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Insert Into Transactions (AccID, Type, Amount) Values (" + acc_id + ", '" + type + "', " + Math.abs(amount) + ")");
        statement.executeQuery("commit");
        return true;
    }

    /**
     * add a transaction to Transactions table
     *
     * @param acc_id
     * @param type
     * @param amount
     * @param source
     * @return
     * @throws SQLException
     */
    public boolean addTransaction(int acc_id, char type, double amount, int source) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Insert Into Transactions (AccID, Type, Amount,Source) Values (" + acc_id + ", '" + type + "', " + amount + ", " + source + ")");
        statement.executeQuery("commit");
        return true;
    }

    /**
     * return how much transactions this account have
     *
     * @param accID
     * @return
     * @throws SQLException
     */
    public int getTransactionsNumber(int accID) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(*) from Transactions where accid = " + accID);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res;
    }

    /**
     * calculate the account balance from transaction table
     *
     * @param AccID
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public double getAccountBalance(int AccID) throws SQLException, IOException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("Select Type,Amount from Transactions where AccID = " + AccID);
        double balance = 0;
        while (resultSet.next()) {
            if (resultSet.getCharacterStream(1).read() == 'd') balance += resultSet.getDouble(2);
            if (resultSet.getCharacterStream(1).read() == 'w') balance -= resultSet.getDouble(2);
        }
        resultSet.close();
        return balance;
    }

    /**
     * return all PIN's from customer table
     *
     * @return
     * @throws SQLException
     */
    private ArrayList<Integer> getCustomerPin() throws SQLException {
        Statement statement = con.createStatement();
        ArrayList<Integer> customerId = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("SELECT PIN from customers ");
        while (resultSet.next()) {
            customerId.add(resultSet.getInt(1));
        }
        resultSet.close();
        return customerId;
    }

    /**
     * generate a random pin as a suggestion
     *
     * @return
     * @throws SQLException
     */
    public String suggestPin() throws SQLException {
        String pinNew = new MailSender().codeGenerator();
        while (checkPin(Integer.parseInt(pinNew))) {
            pinNew = new MailSender().codeGenerator();
        }
        return pinNew;
    }

    /**
     * check if this pin is in use or not
     *
     * @param pin
     * @return
     * @throws SQLException
     */
    public boolean checkPin(int pin) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(1) from customers where pin = " + pin);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res == 1;
    }

    public boolean checkAccount(int accId) throws SQLException{
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(1) from accounts where id = " + accId);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res == 1;
    }
    /**
     * remove an account from Accounts table
     *
     * @param accID
     * @throws SQLException
     */
    public void deleteAccount(int accID) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Delete from Accounts where ID = " + accID);
        statement.executeQuery("commit");
    }

    /**
     * delete a general transaction which match a conditions for specific account
     *
     * @param accID
     * @param type
     * @param amount
     * @throws SQLException
     */
    public void deleteTransaction(int accID, char type, double amount) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Delete from Transactions where TransID in (Select TransID from Transactions where AccID = " + accID + " and Type = '" +
                type + "' and Amount = " + amount + " FETCH FIRST 1 ROWS ONLY)");
        statement.executeQuery("commit");
    }

    /**
     * delete a specific transaction by id
     *
     * @param transID
     * @throws SQLException
     */
    public void deleteTransactionByID(int transID) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Delete from Transactions where TransID = " + transID);
        statement.executeQuery("commit");
    }

    /**
     * clean transactions for specific account
     *
     * @param accID
     * @throws SQLException
     */
    public void deleteAllTransactions(int accID) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Delete from Transactions where accid = " + accID);
        statement.executeQuery("commit");
    }

    /**
     * clean transactions for specific account and specific type
     *
     * @param accID
     * @throws SQLException
     */
    public void deleteAllTransactions(int accID,char type) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Delete from Transactions where accid = " + accID + " and type = '" + type+"'");
        statement.executeQuery("commit");
    }
    /**
     * get the customer email, the owner of this account
     *
     * @param accId
     * @return
     * @throws SQLException
     */
    public String getEmailFromAccid(int accId) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select email from customers where pin = (SELECT pin from accounts WHERE id = " + accId + ")");
        resultSet.next();
        String email = resultSet.getString(1);
        resultSet.close();
        return email;
    }

    public String getNameFromAccId(int accId) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select name from customers where pin = (SELECT pin from accounts WHERE id = " + accId + ")");
        resultSet.next();
        String name = resultSet.getString(1);
        resultSet.close();
        return name;
    }
    /**
     * return the pin for customer, the owner of this account
     *
     * @param accId
     * @return
     * @throws SQLException
     */
    public int getPin(int accId) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select pin from accounts where id = " + accId);
        resultSet.next();
        int pin;
        try{
            pin = resultSet.getInt(1);
        }catch (SQLException e) {
            return -1;
        }
        resultSet.close();
        return pin;
    }

    /**
     * return specific account transactions as an ArrayList<Transaction>
     *
     * @param accID
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public ArrayList<Transaction> getTransactions(int accID) throws SQLException, IOException {
        Statement statement = con.createStatement();
        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("select count(*) from Transactions where AccID = " + accID);
        resultSet.next();
        if (resultSet.getInt(1) == 0) return new ArrayList<>();
        resultSet = statement.executeQuery("select transID, AccID, Type, Amount from Transactions where AccID = " + accID + " ORDER BY transid ASC NULLS LAST");
        while (resultSet.next()) {
            transactions.add(new Transaction(resultSet.getInt(1), accID,
                    (char) resultSet.getCharacterStream(3).read(), resultSet.getInt(4)));
        }
        resultSet.close();
        return transactions;
    }

    /**
     * add an account with initial balance is zero
     *
     * @param pin
     * @throws SQLException
     */
    public void addAccount(int pin) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("insert into Accounts (PIN, isClosed) Values (" + pin + ", 0)");
        statement.executeQuery("commit");
    }

    /**
     * add an account with initial balance as an argument into Accounts table
     *
     * @param pin
     * @param balance
     * @throws SQLException
     */
    public void addAccount(int pin, double balance) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("insert into Accounts (PIN, isClosed) Values (" + pin + ", " + 0 + ")");
        addTransaction(getLastAccount(pin), 'd', balance);
        statement.executeQuery("commit");
    }

    /**
     * check if an account with specific id exist or not
     *
     * @param pin
     * @param accId
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public boolean checkAccount(int pin, int accId) throws SQLException, IOException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(1) from Accounts where id = " + accId);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res == 1;
    }

    /**
     * return transaction for specific account as a String
     *
     * @param accID
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public String getStringTransactions(int accID) throws IOException, SQLException {
        ArrayList<Transaction> transactions = getTransactions(accID);
        String s = "";
        for (int i = 0; i < transactions.size(); i++) {
            s += transactions.get(i).toString() + "\n";
        }
        return s;
    }

    /**
     * check if this deletion method is legal or not
     *
     * @param transID
     * @param accID
     * @return
     * @throws SQLException
     */
    public boolean isIllegalDelete(int transID, int accID) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(1) from Transactions where transID = " + transID + " and Accid = " + accID);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res == 1;
    }

    /**
     * return id for last account this custmer created
     *
     * @param pin
     * @return
     * @throws SQLException
     */
    private int getLastAccount(int pin) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("Select max(id) from accounts where pin = " + pin);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res;
    }

    /**
     * return if this account to this customer or not
     */
    public boolean isThisForThat(int pin, int accID) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(*) from accounts where pin = " + pin + " and id = " + accID);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res == 1;
    }

    /**
     * return the group of transaction as a page
     */
    public ArrayList<Transaction> showPages(int pageSize, int pageNumber, int accId) throws SQLException, IOException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from (select rownum rnum, data.*" +
                " from(select transid, accid, type, amount from transactions order by transid " +
                ") data where rownum <= " + (pageSize * pageNumber) + " and accid = " + accId + ") where rnum >"
                + (pageSize * (pageNumber - 1)));
        while (resultSet.next()) {
            transactions.add(new Transaction(resultSet.getInt(2), resultSet.getInt(3),
                    (char) resultSet.getCharacterStream(4).read(), resultSet.getDouble(5)));
        }
        return transactions;
    }

    /**
     * return the last n for the transactions
     */
    public ArrayList<Transaction> getLastNRecords(int accID, int n) throws SQLException, IOException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select transID, AccID, Type, Amount from Transactions where AccID = " + accID + " ORDER BY transid DESC fetch first " + n + " rows only");
        while (resultSet.next()) {
            transactions.add(new Transaction(resultSet.getInt(1), accID,
                    (char) resultSet.getCharacterStream(3).read(), resultSet.getInt(4)));
        }
        statement.close();
        resultSet.close();
        return transactions;
    }

    /**
     * insert row in the loan table
     */
    public void insertLoan(int accID, int patron1, int patron2, double amount, int years) throws SQLException {
        Statement statement = con.createStatement();
        Loan loan = new Loan(years, amount, accID, patron1, patron2);
        statement.executeQuery("insert into loans (accid, patron1, patron2,amount,datestart,dateend,withdrawn)" +
                "values (" + accID + ", " + patron1 + ", " + patron2 + ", " + loan.getTotalPayment() + "," +
                "to_date('" + loan.getStartDateStr() + "' ,'YYYY-MM-DD'), " +
                "to_date('" + loan.getLastDateStr() + "' ,'YYYY-MM-DD'), " + amount + ")");
        statement.executeQuery("commit");
    }

    /**
     * return the string of the loan withdrawal date from database
     */
    public String getStartDateLoan(int accid) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select to_char(dateStart, 'YYYY-MM-dd') from loans where accid = " + accid);
        resultSet.next();
        String date = resultSet.getString(1);
        resultSet.close();
        return date;
    }

    /**
     * return the last payment date as a string from database
     */
    public String getEndDateLoan(int accid) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select to_char(dateend, 'YYYY-MM-dd') from loans where accid = " + accid);
        resultSet.next();
        String date = resultSet.getString(1);
        resultSet.close();
        return date;
    }

    /**
     * Returns the amount to be repaid from the loan
     */
    public double getLoanAmount(int accId) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select amount from loans where accid = " + accId);
        resultSet.next();
        double res = resultSet.getDouble(1);
        resultSet.close();
        return res;
    }

    /**
     * set the account status
     */
    public void setAccountOpen(int accID, boolean open) throws SQLException {
        Statement statement = con.createStatement();
        if (open) {
            ResultSet resultSet = statement.executeQuery("update accounts set isclosed = 0 where id = " + accID);
        } else {
            ResultSet resultSet = statement.executeQuery("update accounts set isclosed = 1 where id = " + accID);
        }
    }

    /**
     * return The status of the account  if it is closed or not
     */
    public boolean checkAccountStatus(int accid) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select isclosed from accounts where id = " + accid);
        resultSet.next();
        boolean res = (resultSet.getInt(1) == 1 ? true : false);
        resultSet.close();
        return res;
    }

    /**
     * Returns the value the account paid
     */
    public double getLoanPaid(int accid) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select amount from transactions where accid = " + accid + " and type = 'l'");
        double paid = 0;
        while (resultSet.next()) {
            paid += resultSet.getDouble(1);
        }
        resultSet.close();
        return paid;
    }

    /**
     * return the patrons to this account
     */
    public int[] getPatrons(int accid) throws SQLException {
        int[] patrons = new int[2];
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select Patron1 , Patron2 from loans where accid = " + accid);
        while (resultSet.next()) {
            patrons[0] = resultSet.getInt(1);
            patrons[1] = resultSet.getInt(2);
        }
        return patrons;
    }

    /**
     * delete the loan in database
     */
    public void deleteLoan(int accId) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("DELETE FROM loans WHERE accid =" + accId);
        statement.executeQuery("commit");
    }

    /**
     * return the balance that the customer withdraw it in the loan
     */
    public double getWithdrawn(int accID) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select withdrawn from loans where accid = " + accID);
        resultSet.next();
        double res = resultSet.getDouble(1);
        resultSet.close();
        return res;
    }

    /**
     * return the loan object to account
     */
    public Loan getLoan(int accid) throws SQLException, ParseException {
        java.util.Date start = Loan.convertStringToDate(getStartDateLoan(accid));
        Date end = Loan.convertStringToDate(getEndDateLoan(accid));
        int numberOfYears = end.getYear() - start.getYear();

        double amount = getWithdrawn(accid);

        int[] patrons = getPatrons(accid);

        return new Loan(numberOfYears, amount, accid, patrons[0], patrons[1]);
    }

    /**
     * to close the connection with the database which we created it in the Constructor
     * this method should be implemented when exit from the project not before
     *
     * @throws SQLException
     */
    public void close() throws SQLException {
        con.close();
    }
}
