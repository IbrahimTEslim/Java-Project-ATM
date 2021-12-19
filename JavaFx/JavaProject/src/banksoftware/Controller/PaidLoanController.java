package banksoftware.Controller;

import banksoftware.Classes.DatabaseDriver;
import banksoftware.Classes.Loan;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

public class PaidLoanController implements Initializable {

    DatabaseDriver driver = LogInController.driver;
    private final int accLoan = OperationsController.accLoanId;

    @FXML
    private Label data ;

    @FXML
    private TextField amount ;

    @FXML
    private void paid(ActionEvent event) throws SQLException, ParseException, IOException {
        double mount = Double.parseDouble(amount.getText());
        driver.addTransaction(accLoan, 'l', mount);
        data.setText(dataOfLoan());
        Alert alert ;
        amount.setText("");

        if (driver.getLoanAmount(accLoan) - driver.getLoanPaid(accLoan) <= 0) {
            double extraAMount = driver.getLoanPaid(accLoan) - driver.getLoanAmount(accLoan);
            driver.setAccountOpen(accLoan, true);
            driver.addTransaction(accLoan, 'd', extraAMount);
            driver.deleteLoan(accLoan);
            driver.deleteAllTransactions(accLoan,'l');

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Done ");
            alert.setContentText("You are Paid Your loan (^_^) ");
            alert.show();
            loadPage(event, "Operations");
            return ;
        }
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText("Done . Process Completed (^_^) ");
        alert.show();
    }
    private void loadPage(ActionEvent event, String document) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../Fxml/" + document + ".fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    private String dataOfLoan() throws SQLException, ParseException {
        Loan loan = driver.getLoan(accLoan);
        double amountToPaid = driver.getLoanAmount(accLoan) - driver.getLoanPaid(accLoan);
        return  "The amount your loan is : " + String.format("%,.3f",amountToPaid)  + loan.toString();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            data.setText(dataOfLoan());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
