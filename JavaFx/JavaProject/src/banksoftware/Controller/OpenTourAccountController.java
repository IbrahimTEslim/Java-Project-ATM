/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banksoftware.Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import banksoftware.Classes.Account;
import banksoftware.Classes.DatabaseDriver;
import banksoftware.Classes.MailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.mail.MessagingException;

/**
 * FXML Controller class
 *
 * @author jit
 */
public class OpenTourAccountController implements Initializable {

    DatabaseDriver driver = LogInController.driver;
    MailSender mailSender = LogInController.mailSender;
    public static int accId ;
    static Pagination pagination;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label data;
    @FXML
    protected Button balance;

    @FXML
    private void deleteTransaction(ActionEvent event) throws SQLException, IOException {
        showTransaction(event);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Dialog");
        dialog.setHeaderText("Deleting a Transaction");
        dialog.setContentText("Please enter Transaction ID :");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (driver.isIllegalDelete(Integer.parseInt(result.get()), accId)) {
                driver.deleteTransactionByID(Integer.parseInt(result.get()));
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setContentText("Deleted ☻");
                alert.show();
                show();
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("not Completed");
                alert.setContentText("Can't delete a transaction you don't own ☺");
                alert.show();
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("not Completed");
            alert.setContentText("Insert transaction Id :) ");
            alert.show();
        }
        balance.setText(driver.getAccountBalance(accId)+"");
    }

    private void show() {
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer param) {
                try {
                    return ShowTransactionsController.page(param);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    @FXML
    private void deleteAllTransactions(ActionEvent event) throws SQLException, IOException {
        showTransaction(event);
        Alert alert = new Alert(AlertType.CONFIRMATION, "are you sure you need to clear all the transactions ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            //driver is not defined  // Now Valid
            driver.deleteAllTransactions(accId);

            alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("All Deleted ☻");
            alert.show();
        }
        show();
        balance.setText(driver.getAccountBalance(accId)+"");
    }

    @FXML
    private void sendToAnotherAccount(ActionEvent event) throws IOException, SQLException {
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/SendToOtherAccount.fxml"));
        borderPane.setCenter(root);
        balance.setText(driver.getAccountBalance(accId)+"");
    }

    @FXML
    private void showTransaction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/showTransactions.fxml"));
        borderPane.setCenter(root);
    }

    @FXML
    private void sendEmail() throws IOException, SQLException {
        String message = "Dear " + OperationsController.name + "\n" + driver.getStringTransactions(accId) + "\n"
                + "This is an unofficial copy of your transactions that is not held accountable by the law."
                + " If you feel something wrong, contact us ";
        String email = driver.getEmail(OperationsController.pin);
        /*this block commented by ARY*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mailSender.sendMail(email, "Transactions your Account", message);
                } catch (MessagingException e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("This Error from inside, you are OK ☻♥");
                    alert.show();
                }
            }
        }).start();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mail");
        alert.setContentText("Done , Check your mail box :) ");
        alert.show();

    }

    @FXML
    private void addingtransactions() throws IOException, SQLException {
//
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/AddingTransaction.fxml"));
        borderPane.setCenter(root);
    }

    @FXML
    private void withdrawLoan(ActionEvent event) throws IOException {
        loadPage(event,"WithdrawLoanScreen");
    }

    @FXML
    private void cancel(ActionEvent event) throws IOException {
        loadPage(event, "OpenTourAccount");
    }

    @FXML
    private void exit(ActionEvent event) throws IOException {
        loadPage(event, "Operations");
    }

    private void loadPage(ActionEvent event, String document) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/" + document + ".fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }


    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        try {
            data.setText(yourData());
            balance.setText(String.format("%,.3f",driver.getAccountBalance(accId)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String yourData() throws Exception {
        return "name : " + driver.getNameFromAccId(accId) + "\n\n"
                + "balance : " + String.format("%,.3f",driver.getAccountBalance(accId)) + "\n\n"
                + "email : " + driver.getEmailFromAccid(accId) + "\n\n";
    }

}
