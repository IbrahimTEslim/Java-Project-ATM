/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banksoftware.Controller;

//import Account  ;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import banksoftware.Classes.Account;
import banksoftware.Classes.DatabaseDriver;
import banksoftware.Controller.LogInController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

/**
 * FXML Controller class
 *
 * @author jit
 */
public class OperationsController implements Initializable {
    private int accFirst[] = new int[2];
    static protected int pin;
    static protected String name;
    static protected int accountId;
    static protected int accLoanId ;
    DatabaseDriver driver = LogInController.driver;

    @FXML
    private Label screenContent;
    @FXML
    private BorderPane borderPane ;


    @FXML
    private void showCreateAcount() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create a New Account ");
            dialog.setHeaderText("Creating new account");
            dialog.setContentText("Please enter an initial amount :");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                driver.addAccount(pin, Double.parseDouble(result.get()));
            } else {
                Alert valueNotExisted = new Alert(AlertType.INFORMATION);
                valueNotExisted.setContentText("Hey, enter a value ");
                valueNotExisted.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Weird Error O-O !!");
            alert.show();
        }
        screenContent.setText(strAccount());

    } //Done

    @FXML
    private void deleteAccount() {
        try {
//            ArrayList<Account> account = driver.getCustomerAccounts(pin);
            TextInputDialog accID = new TextInputDialog();
            accID.setTitle("Text Input Dialog");
            accID.setHeaderText("delete an account");
            accID.setContentText("Please enter account ID :");
            Optional<String> result = accID.showAndWait();
            if (result.isPresent()) {
                //driver not defined for me  //Now it's defined
                int accId = Integer.parseInt(result.get());
                if (driver.isThisForThat(pin, accId))
                    if (canDelete(accId)) {
                        driver.deleteAccount(Integer.parseInt(result.get()));
                        screenContent.setText(strAccount());
                    } else {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("You can't remove the first two Account :(");
                        alert.show();
                    }
                else {
                    Dialog dialog = new Dialog();
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                    Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
                    closeButton.managedProperty().bind(closeButton.visibleProperty());
                    closeButton.setVisible(false);
                    dialog.setContentText("Obviously you can't delete an account you don't own :)");
                    dialog.showAndWait();
                }
            }
            //TODO still adding note that the account has deleted
        } catch (Exception e) {
            Dialog dialog = new Dialog();
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.managedProperty().bind(closeButton.visibleProperty());
            closeButton.setVisible(false);
            dialog.setContentText("Something wrong. Try again you may be lucky :)");
            dialog.show();
        }
    } //Done


    @FXML
    private void showOpenAcount(ActionEvent event) throws SQLException, IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Creating new account");
        dialog.setContentText("Please enter account ID :");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            accountId = Integer.parseInt(result.get());
            OpenTourAccountController.accId = accountId;
            if (!driver.isThisForThat(pin, accountId) ||
                    driver.checkAccountStatus(accountId) || !driver.checkAccount(pin, accountId)) {
                //Dialog dialog = new Dialog();
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
                closeButton.managedProperty().bind(closeButton.visibleProperty());
                closeButton.setVisible(false);
                dialog.setContentText("can't access this account :)");
                dialog.showAndWait();
            } else {
                loadPage(event, "OpenTourAccount");
            }
        }
    } //Done

    @FXML
    private void PaidLoan() throws IOException {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Open Loan ");
            dialog.setHeaderText("Open Your Loan");
            dialog.setContentText("Please enter an Account Id :");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                accLoanId = Integer.parseInt(result.get());
                if (driver.isThisForThat(pin,accLoanId)){
                    if (driver.checkAccountStatus(accLoanId)) {
                        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/PaidLoan.fxml"));
                        borderPane.setCenter(root);
                    }else{
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Information ");
                        alert.setContentText("You don't have a loan (^_^) ");
                        alert.show();
                    }
                }else{
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("You can't Open Loan Other People ");
                    alert.show();
                }
            } else {
                Alert valueNotExisted = new Alert(AlertType.INFORMATION);
                valueNotExisted.setContentText("Hey, enter a value ");
                valueNotExisted.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Weird Error O-O !!");
            alert.show();
        }

    }

    private void loadPage(ActionEvent event, String document) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/" + document + ".fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        loadPage(event, "LogInFxml");
    }

    @FXML
    private void showYourAccount(ActionEvent event) throws IOException {
        loadPage(event, "Operations");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        screenContent.setText(strAccount());

        // TODO
    }

    public String strAccount() {
        String strAccount = "";
        try {
            ArrayList<Account> account = driver.getCustomerAccounts(pin);
            for (int i = 0; i < account.size(); i++) {
                strAccount += (i + 1) + " ";
                switch (i) {
                    case 0:
                        strAccount += "Checking : ";
                        accFirst[0] = account.get(i).getAcc_id();
                        break;
                    case 1:
                        strAccount += "Savings : ";
                        accFirst[1] = account.get(i).getAcc_id();
                        break;
                    default:
                        strAccount += "Extra Account : ";

                }
                strAccount += account.get(i) + " \n\n";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strAccount;
    }

    private boolean canDelete(int accId) {
        return accId != accFirst[0] && accId != accFirst[1];
    }

}