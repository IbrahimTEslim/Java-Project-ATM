package banksoftware.Classes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Loan {
    private final double annualInterestRate = 3.0;
    private int numberOfYears, accId, parton1, parton2;
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

    /**
     * return the id Account
     */
    public int getAccId() {
        return accId;
    }

    /**
     * return the id Account for the Patron 1
     */
    public int getParton1() {
        return parton1;
    }

    /**
     * return the id Account for the Patron 2
     */
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

    /**
     * return the string of the loan withdrawal date
     */
    public String getStartDateStr() {
        GregorianCalendar temp = new GregorianCalendar();
        temp.setTime(loanDate);
        return temp.get(GregorianCalendar.YEAR)
                + "-" + (temp.get(GregorianCalendar.MONTH) + 1)
                + "-" + temp.get(GregorianCalendar.DAY_OF_MONTH);
    }

    /**
     * return the last payment date as a string
     */
    public String getLastDateStr() {
        return lastDateForPayments.get(GregorianCalendar.YEAR)
                + "-" + (lastDateForPayments.get(GregorianCalendar.MONTH) + 1)
                + "-" + lastDateForPayments.get(GregorianCalendar.DAY_OF_MONTH);
    }

    /**
     * Converting string into date format
     */
    protected static Date convertStringToDate(String str) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormat.parse(str);
    }

    /**
     * get last date for Payment
     */
    public GregorianCalendar getLastDateForPayments() {
        return lastDateForPayments;
    }

    /**
     * return the string of the data about the Object
     */
    @Override
    public String toString() {
        return    "\namount Loan " + String.format("%,.3f",getLoanAmount())
                + "\nTotal Payment " + String.format("%,.3f",getTotalPayment()) + "\nannualInterestRate : " + annualInterestRate
                + "\nLast Date For Payment " + getLastDateStr();
    }
}
