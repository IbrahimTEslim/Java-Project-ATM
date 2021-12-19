package banksoftware.Controller;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import banksoftware.Classes.DatabaseDriver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jit
 */

public class AddingTransactionController implements Initializable {
    DatabaseDriver driver = LogInController.driver;
    /**
     * Initializes the controller class.
     */
    @FXML
    private RadioButton deposit;
    @FXML
    private RadioButton withdraw;
    @FXML
    private TextField title;

    @FXML
    private void addTransaction(ActionEvent event) throws SQLException, IOException {
        double amount;
        try{
             amount = Double.parseDouble(title.getText());
            if (amount>0){
                char type = (deposit.isSelected()) ? 'd' : 'w';
                driver.addTransaction(OperationsController.accountId, type, amount);
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
            title.setText("");
        }

    }
    private void loadPage(ActionEvent event, String document) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/" + document + ".fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
