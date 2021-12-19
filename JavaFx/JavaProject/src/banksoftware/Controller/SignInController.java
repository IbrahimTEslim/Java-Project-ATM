/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banksoftware.Controller;

import banksoftware.Classes.DatabaseDriver;
import banksoftware.Classes.MailSender;
import banksoftware.Classes.NotValidEmail;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import javax.naming.NamingException;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author jit
 */
public class SignInController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    TextField pinStr,nameStr,emailStr;
    @FXML
    Label checkPin,checkName,checkEmail;
    @FXML
    Button btnSignIn;
    private static DatabaseDriver driver;

    static {
        try {
            driver = new DatabaseDriver();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static MailSender sender = LogInController.mailSender;
    private Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @FXML
    private void goBack(ActionEvent event) throws IOException{
        loadPage(event, "LogInFxml");
    }

    @FXML void signUp(ActionEvent event) throws SQLException, NotValidEmail, NamingException, MessagingException, IOException {
        if(pinStr.getText().isEmpty() || nameStr.getText().isEmpty() || emailStr.getText().isEmpty()) {
            if (pinStr.getText().isEmpty()) {
                checkPin.setText("Required");
                checkPin.setVisible(true);
            }
            if (nameStr.getText().isEmpty()) {
                checkName.setText("Required");
                checkName.setVisible(true);
            }
            if (emailStr.getText().isEmpty()) {
                checkEmail.setText("Required");
                checkEmail.setVisible(true);
            }
            return;
        }
        if(driver.checkPin(Integer.parseInt(pinStr.getText()))){
            String suggestion = "(" +driver.suggestPin()+""+")";
            checkPin.setText("Already Reserved Pin \\ Suggestion: "+suggestion);
            checkPin.setVisible(true);
            return;
        }
        if(nameStr.getText().length()<2) {
            checkName.setText("too short name");
            checkName.setVisible(true);
            return;
        }
        try {
            sender.isValid(emailStr.getText());
        }catch (NotValidEmail notValidEmail) {
            checkEmail.setText("Not Valid Email");
            checkEmail.setVisible(true);
            return;
        }
        int tries = 0;
        confirmation: while (tries < 3) {
            String code = sender.codeGenerator();
            int finalTries = tries;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd :: hh:mm a");
            LocalDateTime now = LocalDateTime.now();
            String msgText = "Welcome dear "+nameStr.getText()+"\n";
            msgText+="Your Registration Pin: "+pinStr.getText()+"\n";
            msgText+="Your Registration code: "+code+"\n";
            msgText+="Your Registration time: "+ dtf.format(now);
            String finalMsgText = msgText;
            new Thread(()->{
                try {
                    sender.sendMail(emailStr.getText(), "Registration Code - try: "+(finalTries +1)+" / 3", finalMsgText);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }).start();
            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setHeaderText("Registration Code - try: "+(finalTries +1)+" / 3");
            textInputDialog.setContentText("we send a code to your email\nEnter it to confirm your registration: ");
            textInputDialog.showAndWait();
            String clientCode = textInputDialog.getEditor().getText();
            if (!clientCode.equals(code)) {
                tries++;
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Code not matching !!\n"+(3-tries)+" left.");
            }
            else break confirmation;
        }
        if(tries >= 3){ loadPage(event,"LogInFxml"); return;}
        driver.insertClient(Integer.parseInt(pinStr.getText()),nameStr.getText().toLowerCase(),emailStr.getText());
        alert .setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Registration");
        alert.setContentText("Registration Done.\nOpen your account ?");
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(okButton, noButton);
        alert.showAndWait().ifPresent(type -> {
            if (type == okButton) {
                try {
                    OperationsController.pin = Integer.parseInt(pinStr.getText());
                    OperationsController.name = nameStr.getText();
                    loadPage(event, "Operations");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else  {
                try {
                    loadPage(event,"LogInFxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @FXML private void pinChange(KeyEvent event) {
        checkPin.setVisible(false);
    }
    @FXML private void nameChange(KeyEvent event) {
        checkName.setVisible(false);
    }
    @FXML private void emailChange(KeyEvent event) {
        checkEmail.setVisible(false);
    }


    @FXML private void backButton(ActionEvent event) throws IOException {
        loadPage(event,"LogInFxml");
    }

     private void loadPage(ActionEvent event , String document ) throws IOException{
         Parent root = FXMLLoader.load(getClass().getResource("../Fxml/"+document + ".fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnSignIn.setDefaultButton(true);
        // TODO
    }    
    
}
