package banksoftware.Controller;

import banksoftware.Classes.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ShowTransactionsController implements Initializable {
    protected static DatabaseDriver driver = LogInController.driver;
    protected static int accId = OperationsController.accountId;
    @FXML private Pagination pagination ;
    @FXML private AnchorPane anchorPane;

    protected static VBox page(int pageIndex) throws IOException, SQLException {
        VBox vBox = new VBox();
        GridPane gridPane = new GridPane();
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
//        Label transId = new Label("trans id"),accIdStr= new Label("acc id"),type= new Label("type"),amount= new Label("amount ($)");
//        gridPane.addRow(0,transId,accIdStr,type,amount);
        ArrayList<Transaction> transPage = driver.showPages(10,pageIndex+1,accId);
        for (int i = 0; i < transPage.size(); i++) {
            gridPane.addRow(i,new Label( String.valueOf((pageIndex)*10+(i+1))),new Label(transPage.get(i).toString()));
        }
        vBox.getChildren().add(gridPane);
        return vBox;
    }

    @FXML private void show()
     {
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer param) {
                try {
                    return page(param);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        try {
            accId = OperationsController.accountId;
            pagination.setPageCount((driver.getTransactionsNumber(accId)/11)+1);
            OpenTourAccountController.pagination = pagination ;
            show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
