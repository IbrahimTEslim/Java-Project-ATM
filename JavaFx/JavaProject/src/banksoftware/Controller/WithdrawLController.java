/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banksoftware.Controller;

import banksoftware.Classes.DatabaseDriver;
import banksoftware.Classes.MailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author jit
 */
public class WithdrawLController implements Initializable {

    @FXML
    private BorderPane borderPane;

    DatabaseDriver driver = LogInController.driver;
    MailSender mail = new MailSender();


    double amount;
    int numberOfYear;
    int patron1;
    int patron2;
    int pin = OperationsController.pin;
    int accId = OperationsController.accountId;
    @FXML
    private Button next;
    @FXML
    private ProgressBar bar;
    String checkNumberPatron1;
    String checkNumberPatron2;

    static boolean canNext = true;
    String[] now = {"SubWithdraw1.fxml", "SubWithdraw2.fxml", "SubWithdraw3.fxml"};
    int x = -1;

    FXMLLoader loader;
    Parent root;

    @FXML
    private void Next(ActionEvent event) throws IOException, SQLException {

        if (x == 0) {
            SubWithdraw1Controller subWithdraw1Controller = loader.getController();
            subWithdraw1Controller.getText();

            String loanAmount = subWithdraw1Controller.amountStr, loanYears = subWithdraw1Controller.yearStr;

            checkSub1(loanAmount, loanYears);
        } else if (x == 1) {
            SubWithdraw2Controller sub2 = loader.getController();
            if (checkSub2(sub2.patron1IdT, sub2.patron2IdT)) {
                sendCheckNumber();
                next.setText("Completed");
            }
        }
        if (x != now.length - 1 && canNext) {
            loader = new FXMLLoader(getClass().getResource("../Fxml/" + now[++x]));
            root = loader.load();
            borderPane.setCenter(root);
            canNext = false;
        } else if (x == now.length - 1) {
            SubWithdraw3Controller sub3 = loader.getController();
            if (checkSub3(sub3.checkPatron1, sub3.checkPatron2)) {
                driver.insertLoan(accId, patron1, patron2, amount, numberOfYear);
                driver.setAccountOpen(accId, false);
                exit(event);
            }
        }

        bar.setProgress((x + 1.0) / (now.length + 1.0));
    }


    @FXML
    private void back(ActionEvent event) throws IOException {
        if (x != 0) {
            loader = new FXMLLoader(getClass().getResource("../Fxml/" + now[--x]));
            root = loader.load();
            borderPane.setCenter(root);
        } else
            cancel(event);

        next.setText("Next");
        bar.setProgress((x + 1.0) / (now.length + 1.0));
    }

    @FXML
    private void cancel(ActionEvent event) throws IOException {
        canNext = true;
        loadPage(event, "OpenTourAccount");
    }

    private void loadPage(ActionEvent event, String document) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/" + document + ".fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }




    private void checkSub1(String loanAmount, String loanYears) {
        if (!(loanAmount.isEmpty()) && !(loanYears.isEmpty())) {
            amount = Double.parseDouble(loanAmount);
            numberOfYear = Integer.parseInt(loanYears);
            if (amount <= 0 || numberOfYear <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Not Negative or Zeros Values");
                alert.show();
            } else
                canNext = true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Empty Values");
            alert.show();
        }
    }

    private boolean checkSub2(TextField patron1IdT, TextField patron2IdT) throws SQLException {
        if (!(patron1IdT.getText().isEmpty()) && !(patron2IdT.getText().isEmpty())) {
//            driver.getPin(patron1Id) == pin
            patron1 = Integer.parseInt(patron1IdT.getText());
            patron2 = Integer.parseInt(patron2IdT.getText());
            if (driver.checkAccount(patron1) && driver.checkAccount(patron2)) {

                // boolean goodPatron = (driver.getPin(patron1)==pin) && (driver.getPin(patron2)==pin);
                if ((driver.getPin(patron1) == pin) || (driver.getPin(patron2) == pin)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("You can't be a Patron to yourself");
                    alert.show();
                    return false;
                }
                if ((driver.getPin(patron1) == (driver.getPin(patron2)))) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Sorry, the Patron's number cannot be entered twice");
                    alert.show();
                    return false;
                }
                canNext = true;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Sorry, wrong accounts id's");
                alert.show();
                return false;
            }
        }
        return true;
    }

    private boolean checkSub3(TextField code1, TextField code2) {
        boolean res = code1.getText().equals(checkNumberPatron1) && code2.getText().equals(checkNumberPatron2);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (res) {
            alert.setTitle("Done");
            alert.setContentText("Done...");
        } else {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Wrong Codes");
        }
        alert.show();
        return res;
    }

    public void sendCheckNumber() throws SQLException {
        checkNumberPatron1 = mail.codeGenerator();
        String emailPatron1 = driver.getEmailFromAccid(patron1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mail.sendMail(emailPatron1, "this is Verifying Code for the Patron 1 ", checkNumberPatron1);
                } catch (MessagingException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("This Error from inside, you are OK ☻♥");
                    alert.show();
                }
            }
        }).start();
        checkNumberPatron2 = mail.codeGenerator();
        String emailPatron2 = driver.getEmailFromAccid(patron2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mail.sendMail(emailPatron2, "this is Verifying Code for the Patron 2 ", checkNumberPatron2);
                } catch (MessagingException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("This Error from inside, you are OK ☻♥");
                    alert.show();
                }
            }
        }).start();
        System.out.println(checkNumberPatron1);
        System.out.println(checkNumberPatron2);
    }

    private void exit(ActionEvent event) throws IOException {
        loadPage(event, "Operations");
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

//        nextSubPane = "SubWithdraw1.fxml";
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("../Fxml/SubWithdraw1.fxml"));
//            borderPane.setCenter(root);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}

