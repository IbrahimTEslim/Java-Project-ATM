/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banksoftware.Controller;

import banksoftware.Classes.DatabaseDriver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author jit
 */
public class SendToOtherAccountController implements Initializable {
    DatabaseDriver driver = LogInController.driver;

    @FXML
    private TextField disAcountID;
    @FXML
    private TextField amount2BeSent;

    @FXML
    private void sentToOtherAccount(ActionEvent event) throws SQLException, IOException {
        try{
            int disAccount = Integer.parseInt(disAcountID.getText());
            double amount = Double.parseDouble(amount2BeSent.getText());
            if (amount>0){
                driver.addTransaction(disAccount, 'd', amount, OpenTourAccountController.accId);
                driver.addTransaction(OpenTourAccountController.accId, 'w', amount);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Completed");
                alert.setContentText("The money has been transferred successfully..");
                alert.show();
                loadPage(event, "OpenTourAccount");

            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("not Completed");
                alert.setContentText("You can't insert a negative number :(, try Again ");
                alert.show();
            }
        }catch (NumberFormatException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("not Completed");
            alert.setContentText("There is a problem with the entry .Try again :(");
            alert.show();
        }catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("not Completed");
            alert.setContentText("There is a problem with the system, try another time :(");
            alert.show();
        }
        finally {
            disAcountID.setText("");
            amount2BeSent.setText("");
        }

    }
    private void loadPage(ActionEvent event, String document) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/" + document + ".fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
