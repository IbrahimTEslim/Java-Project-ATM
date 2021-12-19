package OldMain;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;

public class Main {

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, MessagingException, NamingException, NotValidEmail, ParseException {

        /**
         * 1 insert loan Done Tested
         * 2 delete loan Done Tested
         * 3 get patrons Done Tested
         * 4 get startDate Done Tested
         * 5 get endDate Done Tested
         * 6 get loan Done Tested
         * 7 get amount (l) Done Tested
         * 8 get paid Done Tested
         * 9 open account(bool) Done Tested
         * 10 checkAccountStatus Done Tested
         * 11 Finish.....
         * @throws SQLException
         */

        PLaySound p=new PLaySound();
        ATMMachine atm = new ATMMachine();
        DatabaseDriver driver = new DatabaseDriver();
        MailSender mail = new MailSender();
        String[] typeAccount = {"Checking", "Savings", "Extra Account"};

        while (true) {
            System.out.println("---------------------------------------------------");
            System.out.println("            Welcome to my ATM machine              ");
            System.out.println("---------------------------------------------------");
            System.out.println("\n1-log in ?");
            System.out.println("2-Sign in ? ");
            System.out.println("3-close the machine :(");
            System.out.print("what do you want do ? ");
            int enter = input.nextInt();
            switch (enter) {
                case 1:
                    System.out.println("--------- log in -------------");
                    System.out.print("Enter customer PIN: ");
                    int pin = input.nextInt();
                    System.out.print("Enter Customer Name: ");
                    String name = input.next();

                    while (driver.checkCustomerExists(pin, name)) {
                        ArrayList<Account> account = driver.getCustomerAccounts(pin);
                        System.out.println("---------Your Accounts---------");
                        for (int i = 0; i < account.size(); i++) {
                            System.out.printf("%d-%s and Account Id is %d \n",
                                    (i + 1), (i < 2) ? typeAccount[i] : typeAccount[2], account.get(i).getAcc_id());
                        }
                        System.out.println("-------------------------------");

                        System.out.println("---------- Operations ----------");
                        System.out.println("1-Create a new account.      ");
                        System.out.println("2-Delete account.(else 1 & 2)");
                        System.out.println("3-Open your Account");
                        System.out.println("4-Return to the previous page");
                        System.out.print("What do you want ? ");
                        enter = input.nextInt();
                        switch (enter) {
                            case 1:
                                try {
                                    System.out.println("ok but what is the initial amount ? ");
                                    int amount = input.nextInt();
                                    driver.addAccount(pin, amount);
                                    System.out.println("Account is created successfully :)");
                                } catch (Exception e) {
                                    System.out.println("Oops try again :(");
                                }
                                break;
                            case 2:
                                try {
                                    System.out.print("ok :( , Enter the id Account " +
                                            "(Except for the first two accounts) : ");
                                    int accId = input.nextInt();
                                    if (accId == account.get(0).getAcc_id() || accId == account.get(1).getAcc_id())
                                        throw new Exception();
                                    if (!driver.checkAccount(pin,accId))
                                        throw new NullPointerException();
                                    driver.deleteAccount(accId);
                                    System.out.println("Account is deleted successfully :)");
                                }catch (NullPointerException ex){
                                    System.out.println("Account is not exist :( ");
                                } catch (Exception e) {
                                    System.out.println("Oops :( , please try again and choose correct account");
                                }
                                break;
                            case 3:
                                try {
                                    System.out.print("Enter Account ID : ");
                                    int accId = input.nextInt();
                                    int operate = 0;
                                    if(!driver.isThisForThat(pin,accId)) break;
                                    if (!driver.checkAccount(pin,accId))
                                        throw new NullPointerException();
                                    do {
                                        System.out.println("--------------------------------");
                                        System.out.println("-           Account             -");
                                        System.out.printf("-      Balance = %-2.2f        -\n", driver.getAccountBalance(accId));
                                        System.out.println("--------------------------------");
                                        System.out.println("1-Add transactions   ");
                                        System.out.println("2-Show transactions  ");
                                        System.out.println("3-Delete transaction ");
                                        System.out.println("4-Delete all transactions ");
                                        System.out.println("5-Send my transactions to your email ");
                                        System.out.println("6-Send to another account ");
                                        System.out.println("7-Show last n transaction  ");
                                        System.out.println("8-Close Account");
                                        System.out.print("What do you want ? ");
                                        operate = input.nextInt();
                                        switch (operate){
                                            case 1 :
                                                System.out.print("Enter a typeAccount of transaction (d- deposit / w- withdrawal) : ");
                                                char type = input.next().charAt(0);
                                                System.out.print("Enter a amount : ");
                                                double amount = input.nextDouble();
                                                driver.addTransaction(accId,Character.toLowerCase(type),amount);
                                                switch (Character.toLowerCase(type)) {
                                                    case 'w':
                                                        p.playWithdrawSound("withdraw.wav");
                                                        break;
                                                    case 'd':
                                                        p.playDepositSound("deposit.wav");
                                                        break;
                                                    default:
                                                }
                                                System.out.println("done :) ");
                                                break;
                                            case 2 :
                                                int pageSize = 10;
                                                int numberOfPages = driver.getTransactionsNumber(accId)/pageSize+1;
                                                transactionsPages: for (int i = 0; i < numberOfPages; i++) {
                                                    ArrayList page = driver.showPages(pageSize,i+1,accId);
                                                    for (int j = 0; j < page.size(); j++) {
                                                        System.out.println((j+pageSize*i+1)+"- " + page.get(j));
                                                    }
                                                    if(page.size()<pageSize)  break transactionsPages;
                                                    System.out.print("Enter 'm' to continue or sth else to exit: ");
                                                    if(Character.toLowerCase(input.next().charAt(0)) != 'm') break transactionsPages;
                                                }
                                                System.out.println("Finished...");
                                                break;
                                            case 3 :
                                                System.out.print("Enter Id transacted : ");
                                                int idTrans = input.nextInt();
                                                if(driver.isIllegalDelete(idTrans,accId)) {
                                                    driver.deleteTransactionByID(idTrans);
                                                    System.out.println("Deleted successfully :)");
                                                }
                                                else{
                                                    System.out.println("heey you right there ! ");
                                                    System.out.println("this isn't Illegal, only delete this account transactions");
                                                }
                                                break;
                                            case 4:
                                                driver.deleteAllTransactions(accId);
                                            case 5 :
                                                String message = "Dear " + name + "\n" + driver.getStringTransactions(accId) + "\n"
                                                        + "This is an unofficial copy of your transactions that is not held accountable by the law." +
                                                        " If you feel something wrong, contact us ";
                                                String email = driver.getEmail(pin);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            mail.sendMail(email,"Transactions your Account",message);
                                                        } catch (MessagingException e) {
                                                            System.out.println("This Error from inside, you are OK ☻♥");
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).start();
                                                break;
                                            case 6:
                                                System.out.print("Enter the account id you want to send to: ");
                                                int disAccount = input.nextInt();
                                                System.out.print("Enter the amount: ");
                                                double amount2Send = input.nextDouble();
                                                driver.addTransaction(accId,'w',Math.abs(amount2Send));
                                                driver.addTransaction(disAccount,'d',Math.abs(amount2Send),accId);
                                                break;
                                            case 7:
                                                System.out.print("Enter last n rows: ");
                                                int n = input.nextInt();
                                                ArrayList ary= driver.getLastNRecords(accId,n);
                                                for (int i = 0; i < ary.size(); i++) {
                                                    System.out.println((i+1)+" "+ary.get(i));
                                                }
                                        }
                                    } while (operate != 8);
                                }catch (NullPointerException e){
                                    System.out.println("Account is not exist :( ");
                                }/*catch (Exception e){
                                    System.out.println("Oops :( , please try again ");
                                }*/
                                break;
                            default:
                                System.out.println("Choose a correct number ");
                        }
                        if (enter==4)
                            break;
                    }
                    break;
                case 2:
                    System.out.println("--------- Sign in -------------");
                    boolean done = true ;
                    while (done){
                        System.out.println("-----------------------------");
                        System.out.print("Enter customer PIN: ");
                        int pinNew = input.nextInt();
                        if (driver.checkPin(pinNew)){
                            System.out.printf("This number is reserved, choose other, suggestions : %d , %d \n",driver.suggestPin(),driver.suggestPin());
                            continue;
                        }
                        System.out.print("Enter Customer Name: ");
                        String nameNew = input.next();
                        System.out.print("Enter your email: ");
                        String email = input.next();
                        try {
                            mail.isValid(email);
                        } catch (NotValidEmail notValidEmail) {
                            System.out.println(notValidEmail.getMessage());
                            continue;
                        }
                        catch (NamingException namingException) {
                            System.out.println("Can't find a domain such a "+namingException.getRemainingName()+" !");
                            continue;
                        }
//                        if(!mail.isValid(email)){
//                            try {
//                                throw new NotValidEmail("Not a Valid Email :(");
//                            } catch (NotValidEmail notValidEmail) {
//                                System.err.println(notValidEmail.getMessage());
//                                continue;
//                            }
//                        }
                        String checkNumber = mail.codeGenerator();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mail.sendMail(email,"Verifying Code  ",checkNumber);
                                } catch (MessagingException e) {
                                    System.out.println("This Error from inside, you are OK ☻♥");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        System.out.print("Open your email and enter the checked number : ");
                        String number = input.next();
                        for (int i = 0; i < 2; i++) {
                            if (number.equals(checkNumber)) {
                                done = false ;
                                driver.insertClient(pinNew,nameNew.toLowerCase(),email);
                                System.out.println("A new user has been created ");
                                System.out.printf("Pin : %-2d , Name : %-2s , Email : %-2s\n",pinNew,nameNew,email);
                                break;
                            }
                            System.out.print("wrong number try again : ");
                            number = input.next();
                        }
                    }
                    break;
            }
            if (enter == 3 )
                break;
        }
        System.out.println("Goodbye, see you later (^-^) ");
        driver.close();
    }
}

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

    public boolean checkAccount(int pin , int accId) throws SQLException, IOException;

    public String getStringTransactions(int accID) throws IOException, SQLException;
}

interface MailHandling {
    public void sendMail(String to, String msgText, String msgSubject) throws MessagingException;

    public Message prepareMessage(Session session, String to, String from, String msgSubject,
                                  String msgText) throws MessagingException;

    public boolean isValid(String email) throws NotValidEmail, NamingException;

    public String codeGenerator();
}

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

class Account {
    private int Acc_id, pin;
    private double balance;
    private boolean status;

    public Account(int acc_id, int pin, double balance,boolean status) {
        this.Acc_id = acc_id;
        this.pin = pin;
        this.balance = balance;
        this.status=status;
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
                "Acc_id=" + Acc_id +
                ", pin=" + pin +
                ", balance=" + balance +
                ", status=" + status +
                '}';
    }
}

class Transaction {
    private int transID, acc_id, amount;
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

    public Transaction(int transID, int acc_id, char type, int amount) {
        this.transID = transID;
        this.acc_id = acc_id;
        this.type = type;
        this.amount = amount;
    }
}

class DatabaseDriver implements DatabaseHandling {
    private Connection con;
//    private Statement statement;
//    private ResultSet resultSet;
    private String urlDatabase = "jdbc:oracle:oci:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orclpdb.mshome.net)))";
    private String username = "javaproject", password = "javaproject";

    public DatabaseDriver() throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection(urlDatabase, username, password);

    }

    public boolean checkCustomerExists(int pin, String name) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(*) from CUSTOMERS where PIN = "
                + pin + " and Name = '" + name.toLowerCase() + "'");
        resultSet.next();
        if (resultSet.getInt(1) == 1) {
            resultSet.close();
            return true;
        }
        else {
            resultSet.close();
            return false;
        }
    }

    public boolean insertClient(int pin, String name, String email) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("insert into Customers (PIN,Name,Email) Values (" + pin + ", '" + name + "', '" + email + "')");
        statement.executeQuery("insert into Accounts (PIN,isClosed) Values (" + pin + ", " + 0 + ")");
        statement.executeQuery("insert into Accounts (PIN,isClosed) Values (" + pin + ", " + 0 + ")");
        statement.executeQuery("commit");
        return checkCustomerExists(pin, name);
    }

    public boolean deleteCustomer(int pin, String name) throws SQLException {
        Statement statement = con.createStatement();
        if (!checkCustomerExists(pin, name)) {
            return true;
        }
        statement.executeQuery("DELETE FROM Customers WHERE pin = " + pin + " and Name = '" + name + "'");
        statement.executeQuery("commit");
        return (!checkCustomerExists(pin, name));
    }

    public ArrayList<Account> getCustomerAccounts(int pin) throws SQLException, IOException {
        Statement statement = con.createStatement();
        ArrayList<Account> accounts = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("Select * from Accounts where pin = "
                + pin + " ORDER BY id ASC NULLS LAST ");
        while (resultSet.next()) {
            int tempAccId = resultSet.getInt(1);
            boolean status = resultSet.getInt(3)==1?true:false; // false = notClosed -- true = closed
            accounts.add(new Account(tempAccId, resultSet.getInt(2),getAccountBalance(tempAccId),status));
        }
        resultSet.close();
        return accounts;
    }

    public String getEmail(int pin) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("Select Email from customers WHERE PIN = " + pin);
        resultSet.next();
        String email = resultSet.getString(1);
        resultSet.close();
        return email;
    }

    public boolean addTransaction(int acc_id, char type, double amount) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Insert Into Transactions (AccID, Type, Amount) Values (" + acc_id + ", '" + type + "', " + Math.abs(amount) + ")");
        statement.executeQuery("commit");
        return true;
    }

    public boolean addTransaction(int acc_id, char type, double amount,int source) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Insert Into Transactions (AccID, Type, Amount,Source) Values (" + acc_id + ", '" + type + "', " + amount +", "+source+ ")");
        statement.executeQuery("commit");
        return true;
    }

    public int getTransactionsNumber(int accID) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(*) from Transactions where accid = "+accID);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res;
    }

    public double getAccountBalance(int AccID) throws SQLException, IOException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("Select Type,Amount from Transactions where AccID = " + AccID);
        double balance = 0;
        while (resultSet.next()) {
            if (resultSet.getCharacterStream(1).read() == 'd') balance += resultSet.getInt(2);
            if (resultSet.getCharacterStream(1).read() == 'w') balance -= resultSet.getInt(2);
        }
        resultSet.close();
        return balance;
    }

    private ArrayList<Integer> getCustomerPin () throws SQLException {
        Statement statement = con.createStatement();
        ArrayList<Integer> customerId = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("SELECT PIN from customers ");
        while (resultSet.next()){
            customerId.add(resultSet.getInt(1));
        }
        resultSet.close();
        return customerId ;
    }

    public String suggestPin() throws SQLException {
        String pinNew = new MailSender().codeGenerator();
        while(checkPin(Integer.parseInt(pinNew))){
            pinNew = new MailSender().codeGenerator();
        }
        return pinNew;
    }

    public boolean checkPin(int pin) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(1) from customers where pin = "+pin);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res==1;
    }

    public void deleteAccount(int accID) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Delete from Accounts where ID = " + accID);
        statement.executeQuery("commit");
    }

    public void deleteTransaction(int accID, char type, double amount) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Delete from Transactions where TransID in (Select TransID from Transactions where AccID = " + accID + " and Type = '" + type + "' and Amount = " + amount + " FETCH FIRST 1 ROWS ONLY)");
        statement.executeQuery("commit");
    }

    public void deleteTransactionByID(int transID) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Delete from Transactions where TransID = " + transID);
        statement.executeQuery("commit");
    }

    public void deleteAllTransactions(int accID) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("Delete from Transactions where accid = "+accID);
        statement.executeQuery("commit");
    }

    public String getEmailFromAccid(int accId) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select email from customers where pin = (SELECT pin from accounts WHERE id = "+accId+")");
        resultSet.next();
        String email = resultSet.getString(1);
        resultSet.close();
        return email;
    }

    public ArrayList<Transaction> getTransactions(int accID) throws SQLException, IOException {
        Statement statement = con.createStatement();
        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("select count(*) from Transactions where AccID = " + accID);
        resultSet.next();
        if (resultSet.getInt(1) == 0) return new ArrayList<>();
        resultSet = statement.executeQuery("select transID, AccID, Type, Amount from Transactions where AccID = " + accID+" ORDER BY transid ASC NULLS LAST");
        while (resultSet.next()) {
            transactions.add(new Transaction(resultSet.getInt(1), accID,
                    (char) resultSet.getCharacterStream(3).read(), resultSet.getInt(4)));
        }
        resultSet.close();
        return transactions;
    }

    public void addAccount(int pin) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("insert into Accounts (PIN, isClosed) Values (" + pin + ", 0)");
        statement.executeQuery("commit");
    }

    public void addAccount(int pin, double balance) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("insert into Accounts (PIN, isClosed) Values (" + pin + ", " + 0 + ")");
        addTransaction(getLastAccount(pin),'d',balance);
        statement.executeQuery("commit");
    }

    public boolean checkAccount(int pin , int accId) throws SQLException, IOException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(1) from Accounts where id = "+accId);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res==1;
    }

    public String getStringTransactions(int accID) throws IOException, SQLException {
        ArrayList<Transaction> transactions = getTransactions(accID);
        String s = "";
        for (int i = 0; i < transactions.size(); i++) {
            s += transactions.get(i).toString()+ "\n" ;
        }
        return s ;
    }

    public boolean isIllegalDelete(int transID,int accID) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(1) from Transactions where transID = "+transID+" and Accid = "+accID);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res == 1;
    }

    private int getLastAccount(int pin) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("Select max(id) from accounts where pin = "+pin);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res;
    }

    public boolean isThisForThat (int pin, int accID) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(*) from accounts where pin = "+pin+" and id = "+accID);
        resultSet.next();
        int res = resultSet.getInt(1);
        resultSet.close();
        return res==1;
    }

    public ArrayList<Transaction> showPages(int pageSize, int pageNumber,int accId) throws SQLException, IOException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Statement statement=con.createStatement();
        ResultSet resultSet= statement.executeQuery("select * from (select rownum rnum, data.*" +
                " from(select transid, accid, type, amount from transactions order by transid " +
                ") data where rownum <= "+(pageSize * pageNumber)+" and accid = "+accId+") where rnum >"+(pageSize*(pageNumber-1)));
        while (resultSet.next()) {
            transactions.add(new Transaction(resultSet.getInt(2),resultSet.getInt(3),
                    (char) resultSet.getCharacterStream(4).read(),resultSet.getInt(5)));
        }
        return transactions;
    }

    public ArrayList<Transaction> getLastNRecords(int accID,int n) throws SQLException, IOException {
        ArrayList<Transaction> transactions= new ArrayList<>();
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select transID, AccID, Type, Amount from Transactions where AccID = " + accID+" ORDER BY transid DESC fetch first "+n+" rows only");
        while (resultSet.next()) {
            transactions.add(new Transaction(resultSet.getInt(1), accID,
                    (char) resultSet.getCharacterStream(3).read(), resultSet.getInt(4)));
        }
        statement.close();
        resultSet.close();
        return transactions;
    }

    public void insertLoan(int accID,int patron1, int patron2,int amount,int years) throws SQLException {
        Statement statement = con.createStatement();
        Loan loan = new Loan(years,amount,accID,patron1,patron2);
        statement.executeQuery("insert into loans (accid, patron1, patron2,amount,datestart,dateend,withdrawn)" +
                "values ("+accID+", "+patron1+", "+patron2+", "+loan.getTotalPayment()+","+
                "to_date('"+loan.getStartDateStr()+"' ,'YYYY-MM-DD'), "+
                "to_date('"+loan.getLastDateStr()+"' ,'YYYY-MM-DD'), "+amount+")");
        statement.executeQuery("commit");
    }

    public String getStartDateLoan(int accid) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select to_char(dateStart, 'YYYY-MM-dd') from loans where accid = "+accid);
        resultSet.next();
        String date = resultSet.getString(1);
        resultSet.close();
        return date;
    }

    public String getEndDateLoan(int accid) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select to_char(dateend, 'YYYY-MM-dd') from loans where accid = "+accid);
        resultSet.next();
        String date = resultSet.getString(1);
        resultSet.close();
        return date;
    }

    public double getLoanAmount(int accId) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select amount from loans where accid = "+accId);
        resultSet.next();
        double res = resultSet.getDouble(1);
        resultSet.close();
        return res;
    }

    public void setAccountOpen(int accID,boolean open) throws SQLException {
        Statement statement = con.createStatement();
        if(open) {
            ResultSet resultSet = statement.executeQuery("update accounts set isclosed = 0 where id = "+accID);
        }
        else {
            ResultSet resultSet = statement.executeQuery("update accounts set isclosed = 1 where id = " + accID);
        }
    }

    public boolean checkAccountStatus(int accid) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select isclosed from accounts where id = "+accid);
        resultSet.next();
        boolean res = (resultSet.getInt(1)==1 ?true:false);
        resultSet.close();
        return res;
    }

    public double getLoanPaid(int accid) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select amount from transactions where accid = "+accid+" and type = 'l'");
        double paid = 0;
        while (resultSet.next()) {
            paid+=resultSet.getDouble(1);
        }
        resultSet.close();
        return paid;
    }

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

    public void deleteLoan(int accId) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeQuery("DELETE FROM loans WHERE accid =" + accId);
        statement.executeQuery("commit");
    }

    public double getWithdrawn(int accID) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select withdrawn from loans where accid = "+accID);
        resultSet.next();
        double res= resultSet.getDouble(1);
        resultSet.close();
        return res;
    }

    public Loan getLoan(int accid) throws SQLException, ParseException {
        Date start = Loan.convertStringToDate(getStartDateLoan(accid));
        Date end = Loan.convertStringToDate(getEndDateLoan(accid));
        int numberOfYears = end.getYear() - start.getYear();

        double amount = getWithdrawn(accid);

        int[] patrons = getPatrons(accid);

        return new Loan(numberOfYears,amount,accid,patrons[0],patrons[1]);
    }

    /**
     * 1 insert loan Done
     * 2 delete loan Done
     * 3 get patrons Done
     * 4 get startDate Done
     * 5 get endDate Done
     * 6 get loan Done
     * 7 get amount (l) Done
     * 8 get paid Done
     * 9 open account(bool) Done
     * 10 checkAccountStatus Done
     * 11
     * @throws SQLException
     */
    public void close() throws SQLException {
//        resultSet.close();
//        statement.close();
        con.close();
    }
}

class MailSender implements MailHandling {
    private static String senderEmail = "iugjavaproject@gmail.com";
    private static String password = "java@project2";

    public MailSender() {
    }

    public MailSender(String username, String password) {
        senderEmail = username;
        this.password = password;
    }

    public void sendMail(String to, String msgSubject,String msgText) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });

        Message message = prepareMessage(session, to, senderEmail, msgText, msgSubject);
        Transport.send(message);
    }

    public Message prepareMessage(Session session, String to, String from, String msgSubject,
                                  String msgText) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

        message.setSubject(msgSubject);
        message.setText(msgText);

        return message;
    }

    private static ArrayList getMX(String hostName)
            throws NamingException {
        // Perform a DNS lookup for MX records in the domain
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial",
                "com.sun.jndi.dns.DnsContextFactory");
        DirContext ictx = new InitialDirContext(env);
        Attributes attrs = ictx.getAttributes
                (hostName, new String[]{"MX"});
        Attribute attr = attrs.get("MX");
        // if we don't have an MX record, try the machine itself
        if ((attr == null) || (attr.size() == 0)) {
            attrs = ictx.getAttributes(hostName, new String[]{"A"});
            attr = attrs.get("A");
            if (attr == null)
                throw new NamingException("No match for name '" + hostName + "'");
        }
        // Huzzah! we have machines to try. Return them as an array list
        // NOTE: We SHOULD take the preference into account to be absolutely
        //   correct. This is left as an exercise for anyone who cares.
        ArrayList res = new ArrayList();
        NamingEnumeration en = attr.getAll();
        while (en.hasMore()) {
            String x = (String) en.next();
            String f[] = x.split(" ");
            if (f[1].endsWith("."))
                f[1] = f[1].substring(0, (f[1].length() - 1));
            res.add(f[1]);
        }
        return res;
    }

    public boolean isValid(String email) throws NamingException, NotValidEmail {
        if (email == null) {
            throw new NotValidEmail("Not a Valid Email :(");
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        boolean goodSyntax = pat.matcher(email).matches();
        if(!goodSyntax) {
            throw new NotValidEmail("Not a Valid Email :(");
        }
        else{
            ArrayList mx = getMX(email.substring((email.indexOf('@'))+1));
            if(mx.size()==0) {
                throw new NamingException("No Domain Found !");
            }
        }
        return true;
    }

    @Override
    public String codeGenerator() {
        Random rnd = new Random();
        return String.format("%06d",rnd.nextInt(999999));
    }
}

class RunInThread implements Runnable {

    private MailSender mailSender;
    private String to,msgText,msgSubject;
    public RunInThread(MailSender mailSender,String to, String msgText,String msgSubject) {
        this.mailSender=mailSender;
        this.to=to;
        this.msgText=msgText;
        this.msgSubject=msgSubject;
    }
    @Override
    public void run() {
        try {
            mailSender.sendMail(to,msgText,msgSubject);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

class PLaySound {
    public void playWithdrawSound(String path) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    InputStream in = new FileInputStream(path);

                    // create an audiostream from the inputstream
                    AudioStream audioStream = new AudioStream(in);

                    // play the audio clip with the audioplayer class
                    AudioPlayer.player.start(audioStream);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    public void playDepositSound(String path) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    InputStream in = new FileInputStream(path);

                    // create an audiostream from the inputstream
                    AudioStream audioStream = new AudioStream(in);

                    // play the audio clip with the audioplayer class
                    AudioPlayer.player.start(audioStream);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();

    }
}

class NotValidEmail extends Exception{
    public NotValidEmail(String msg) {
       super(msg);
    }
    public NotValidEmail(String msg, Throwable err) {
        super(msg,err);
    }
}

class Loan {
    private final double annualInterestRate = 3.0;
    private int numberOfYears, accId, parton1 , parton2;
    private double loanAmount;
    private java.util.Date loanDate;
    private java.util.GregorianCalendar lastDateForPayments;


    /**
     * Construct a loan with specified annual interest rate,
     * number of years, and loan amount , and compute the last Date for Payment
     */
    public Loan(int numberOfYears, double loanAmount, int accId, int parton1, int parton2) {
        this.numberOfYears = numberOfYears;
        this.loanAmount = loanAmount;
        this.accId = accId;
        this.parton1 = parton1;
        this.parton2 = parton2;
        this.loanDate = new java.util.Date();
        lastDateForPayments = new GregorianCalendar();
        lastDateForPayments.set(GregorianCalendar.YEAR,
                (lastDateForPayments.get(GregorianCalendar.YEAR)) + numberOfYears);
    }

    public int getAccId() {
        return accId;
    }

    public int getParton1() {
        return parton1;
    }

    public int getParton2() {
        return parton2;
    }

    /**
     * Return numberOfYears
     */
    public int getNumberOfYears() {
        return numberOfYears;
    }

    /**
     * Set a new numberOfYears
     */
    public void setNumberOfYears(int numberOfYears) {
        this.numberOfYears = numberOfYears;
    }

    /**
     * Return annualInterestRate
     */
    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    /**
     * Return loanAmount
     */
    public double getLoanAmount() {
        return loanAmount;
    }

    /**
     * Set a new loan Amount
     */
    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    /**
     * Find monthly payment
     */
    public double getMonthlyPayment() {
        double monthlyInterestRate = annualInterestRate / 1200;
        double monthlyPayment = loanAmount * monthlyInterestRate / (1 -
                (1 / Math.pow(1 + monthlyInterestRate, numberOfYears * 12)));
        return monthlyPayment;
    }

    /**
     * Find total payment
     */
    public double getTotalPayment() {
        return getMonthlyPayment() * numberOfYears * 12;
    }

    /**
     * Return loan date
     */
    public java.util.Date getLoanDate() {
        return loanDate;
    }

    public String getStartDateStr() {
        GregorianCalendar temp = new GregorianCalendar();
        temp.setTime(loanDate);
        return temp.get(GregorianCalendar.YEAR)
                + "-" + (temp.get(GregorianCalendar.MONTH)+1)
                + "-" + temp.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public String getLastDate() {
        return "Year : " + lastDateForPayments.get(GregorianCalendar.YEAR)
                + " Month : " + lastDateForPayments.get(GregorianCalendar.MONTH)
                + " Day : " + lastDateForPayments.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public String getLastDateStr() {
        return lastDateForPayments.get(GregorianCalendar.YEAR)
                + "-" + (lastDateForPayments.get(GregorianCalendar.MONTH)+1)
                + "-" + lastDateForPayments.get(GregorianCalendar.DAY_OF_MONTH);
    }

    protected static Date convertStringToDate(String str) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
        return dateFormat.parse(str);
    }

    public GregorianCalendar getLastDateForPayments() {
        return lastDateForPayments;
    }

    public String toString() {
        return "Your Loan \n" + "amount Loan " + getLoanAmount()
                + "\nTotal Payment " + getTotalPayment() + "\nannualInterestRate : " + annualInterestRate
                + "\nLast Date For Payment " + getLastDate();
    }
}

