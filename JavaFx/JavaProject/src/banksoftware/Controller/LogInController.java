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
import javafx.scene.control.PasswordField;
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
public class LogInController implements Initializable {

    @FXML
    private PasswordField pin;
    @FXML
    private TextField name;
    @FXML
    private Button login;


    protected static MailSender mailSender;
    protected static DatabaseDriver driver;

    @FXML
    private void goToOperations(ActionEvent event) {
        try {
            if (driver.checkCustomerExists(Integer.parseInt(pin.getText()), name.getText())) {
                OperationsController.pin = Integer.parseInt(pin.getText());
                OperationsController.name = name.getText();
                loadPage(event, "Operations");
            } else {
                Alert wrong = new Alert(Alert.AlertType.ERROR);
                wrong.setTitle("Wrong Insert");
                wrong.setContentText("Please Enter A Pin and Name ( ♥ _ ♥ )");
                wrong.showAndWait();
            }
        } catch (IOException | SQLException ex) {
            Alert wrong = new Alert(Alert.AlertType.ERROR);
            wrong.setTitle("Process Error");
            wrong.setContentText("Please Try again :) ");
            wrong.showAndWait();
        }
        catch (NumberFormatException ex){
            Alert wrong = new Alert(Alert.AlertType.ERROR);
            wrong.setTitle("Error");
            wrong.setContentText("Please insert a Correct Pin :) ");
            wrong.showAndWait();
        }
    }
    @FXML
    private void goToSignIn(ActionEvent event) throws IOException {
        loadPage(event,"newSignup");
    }


    private void loadPage(ActionEvent event, String document) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/"+document + ".fxml"));
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
        login.setDefaultButton(true);
        mailSender = new MailSender();
        try {
            driver = new DatabaseDriver();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
