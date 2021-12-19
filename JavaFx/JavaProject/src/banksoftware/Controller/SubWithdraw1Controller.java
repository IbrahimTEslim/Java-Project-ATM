package banksoftware.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class SubWithdraw1Controller implements Initializable {
    @FXML
    public TextField amountT;
    @FXML
    public TextField numberOfYearT;

    public String amountStr,yearStr;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onChange(KeyEvent keyEvent) {
    }

    public void getText() {
        amountStr = amountT.getText();
        yearStr = numberOfYearT.getText();
    }
}
