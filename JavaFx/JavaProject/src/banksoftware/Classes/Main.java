package banksoftware.Classes;

import javax.mail.*;
import javax.naming.NamingException;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.*;

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
         * @thr ows SQLException
         */

        PLaySound p = new PLaySound();
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
                        System.out.println("4-Paid Your Loan ");
                        System.out.println("5-Return to the previous page");
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
                                    if (!driver.checkAccount(pin, accId))
                                        throw new NullPointerException();
                                    driver.deleteAccount(accId);
                                    System.out.println("Account is deleted successfully :)");
                                } catch (NullPointerException ex) {
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
                                    if (!driver.isThisForThat(pin, accId)) break;
                                    if (!driver.checkAccount(pin, accId))
                                        throw new NullPointerException();
                                    if (driver.checkAccountStatus(accId)) {
                                        System.out.println("Paid Your amount before open , please :(");
                                        break;
                                    }
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
                                        System.out.println("8-Withdraw a loan ");
                                        System.out.println("9-Close Account");
                                        System.out.print("What do you want ? ");
                                        operate = input.nextInt();
                                        switch (operate) {
                                            case 1:
                                                System.out.print("Enter a typeAccount of transaction (d- deposit / w- withdrawal) : ");
                                                char type = input.next().charAt(0);
                                                System.out.print("Enter a amount : ");
                                                double amount = input.nextDouble();
                                                driver.addTransaction(accId, Character.toLowerCase(type), amount);
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
                                            case 2:
                                                int pageSize = 10;
                                                int numberOfPages = driver.getTransactionsNumber(accId) / pageSize + 1;
                                                transactionsPages:
                                                for (int i = 0; i < numberOfPages; i++) {
                                                    ArrayList page = driver.showPages(pageSize, i + 1, accId);
                                                    for (int j = 0; j < page.size(); j++) {
                                                        System.out.println((j + pageSize * i + 1) + "- " + page.get(j));
                                                    }
                                                    if (page.size() < pageSize) break transactionsPages;
                                                    System.out.print("Enter 'm' to continue or sth else to exit: ");
                                                    if (Character.toLowerCase(input.next().charAt(0)) != 'm')
                                                        break transactionsPages;
                                                }
                                                System.out.println("Finished...");
                                                break;
                                            case 3:
                                                System.out.print("Enter Id transacted : ");
                                                int idTrans = input.nextInt();
                                                if (driver.isIllegalDelete(idTrans, accId)) {
                                                    driver.deleteTransactionByID(idTrans);
                                                    System.out.println("Deleted successfully :)");
                                                } else {
                                                    System.out.println("heey you right there ! ");
                                                    System.out.println("this isn't Illegal, only delete this account transactions");
                                                }
                                                break;
                                            case 4:
                                                driver.deleteAllTransactions(accId);
                                            case 5:
                                                String message = "Dear " + name + "\n" + driver.getStringTransactions(accId) + "\n"
                                                        + "This is an unofficial copy of your transactions that is not held accountable by the law." +
                                                        " If you feel something wrong, contact us ";
                                                String email = driver.getEmail(pin);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            mail.sendMail(email, "Transactions your Account", message);
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
                                                driver.addTransaction(accId, 'w', Math.abs(amount2Send));
                                                driver.addTransaction(disAccount, 'd', Math.abs(amount2Send), accId);
                                                break;
                                            case 7:
                                                System.out.print("Enter last n rows: ");
                                                int n = input.nextInt();
                                                ArrayList ary = driver.getLastNRecords(accId, n);
                                                for (int i = 0; i < ary.size(); i++) {
                                                    System.out.println((i + 1) + " " + ary.get(i));
                                                }
                                                break;
                                            case 8:
                                                System.out.print("Enter the amount loan : ");
                                                int amountLoan = input.nextInt();
                                                System.out.println("Enter the number of years of payment : ");
                                                int numberOfYear = input.nextInt();
                                                System.out.println("Enter the account ID of the Patron1 : ");
                                                int patron1Id = input.nextInt();
                                                if (driver.getPin(patron1Id) == pin) {
                                                    System.out.println("You can't be a Patron to yourself");
                                                    break;
                                                }
                                                System.out.println("Enter the account ID of the Patron2 : ");
                                                int patron2Id = input.nextInt();
                                                if (driver.getPin(patron2Id) == pin) {
                                                    System.out.println("You can't be a Patron to yourself");
                                                    break;
                                                }
                                                if (driver.getPin(patron1Id) == driver.getPin(patron2Id)) {
                                                    System.out.println("Sorry, the Patron's number cannot be entered twice");
                                                    break;
                                                }
                                                String checkNumberPatron1 = mail.codeGenerator();
                                                String emailPatron1 = driver.getEmailFromAccid(patron1Id);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            mail.sendMail(emailPatron1, "this is Verifying Code for the Patron 1 ", checkNumberPatron1);
                                                        } catch (MessagingException e) {
                                                            System.out.println("This Error from inside, you are OK ☻♥");
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).start();
                                                String checkNumberPatron2 = mail.codeGenerator();
                                                String emailPatron2 = driver.getEmailFromAccid(patron1Id);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            mail.sendMail(emailPatron2, "this is Verifying Code for the Patron 2 ", checkNumberPatron2);
                                                        } catch (MessagingException e) {
                                                            System.out.println("This Error from inside, you are OK ☻♥");
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).start();
                                                System.out.print("Enter the verification number of the sending Patron1 on the e-mail : ");
                                                String checkedNumber1 = input.next();
                                                System.out.print("Enter the verification number of the sending Patron2 on the e-mail : ");
                                                String checkedNumber2 = input.next();
                                                if (checkNumberPatron1.equals(checkedNumber1) && checkNumberPatron2.equals(checkedNumber2)) {
                                                    driver.insertLoan(accId, patron1Id, patron2Id, amountLoan, numberOfYear);
                                                    driver.setAccountOpen(accId, false);
                                                }
                                                break;
                                        }
                                    } while (operate != 9);
                                } catch (NullPointerException e) {
                                    System.out.println("Account is not exist :( ");
                                }/*catch (Exception e){
                                    System.out.println("Oops :( , please try again ");
                                }*/
                                break;
                            case 4:
                                try {
                                    System.out.print("Enter the id account : ");
                                    int accId = input.nextInt();
                                    if (!driver.checkAccountStatus(accId)) {
                                        System.out.println("Sorry but you don't have a loan :)...");
                                        break;
                                    }

                                    double amountToPaid = driver.getLoanAmount(accId) - driver.getLoanPaid(accId);
                                    System.out.println("The amount your loan is : " + amountToPaid);
                                    System.out.print("Enter the number of the amount to be paid :");
                                    double amountPaid = input.nextInt();
                                    driver.addTransaction(accId, 'l', amountPaid);

                                    if (driver.getLoanAmount(accId) - driver.getLoanPaid(accId) <= 0) {
                                        driver.setAccountOpen(accId, true);
                                        driver.addTransaction(accId, 'd', amountPaid);
                                        driver.deleteLoan(accId);
                                    }

                                } catch (Exception e) {
                                    System.out.println("Sory please try again :(");
                                }


                                break;
                            case 5:
                                break;
                            default:
                                System.out.println("Choose a correct number ");
                        }
                        if (enter == 5)
                            break;
                    }
                    break;
                case 2:
                    System.out.println("--------- Sign in -------------");
                    boolean done = true;
                    while (done) {
                        System.out.println("-----------------------------");
                        System.out.print("Enter customer PIN: ");
                        int pinNew = input.nextInt();
                        if (driver.checkPin(pinNew)) {
                            System.out.printf("This number is reserved, choose other, suggestions : %s , %s \n", driver.suggestPin(), driver.suggestPin());
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
                        } catch (NamingException namingException) {
                            System.out.println("Can't find a domain such a " + namingException.getRemainingName() + " !");
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
                                    mail.sendMail(email, "Verifying Code  ", checkNumber);
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
                                done = false;
                                driver.insertClient(pinNew, nameNew.toLowerCase(), email);
                                System.out.println("A new user has been created ");
                                System.out.printf("Pin : %-2d , Name : %-2s , Email : %-2s\n", pinNew, nameNew, email);
                                break;
                            }
                            System.out.print("wrong number try again : ");
                            number = input.next();
                        }
                    }
                    break;
            }
            if (enter == 3)
                break;
        }
        System.out.println("Goodbye, see you later (^-^) ");
        driver.close();
    }
}
