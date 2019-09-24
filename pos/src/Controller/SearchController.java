package Controller;

import DTO.ManageOrders;
import DTO.ManageOrdersTM;
import DTO.SearchTM;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SearchController {

    public JFXTextField txtSearch;
    public Connection connection;
    public AnchorPane anpSearchOrders;
    public TableView<SearchTM> tblSearch;

    public void initialize() {

        tblSearch.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderID"));
        tblSearch.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("date"));
        tblSearch.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("total"));
        tblSearch.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("customerID"));
        tblSearch.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("customerName"));

        tblSearch.getItems().clear();
        ObservableList<SearchTM> search = tblSearch.getItems();
        try {
            connection= DBConnection.getInstance().getConnection();

            PreparedStatement pstm = connection.prepareStatement("select orders.orderId,orders.orderDate,sum((orderDetail.qty)*(orderDetail.unitPrice)) as total,orders.customerId,customer.name from ((orders \n" +
                    "INNER JOIN orderDetail ON orders.orderId = orderDetail.orderId)\n" +
                    "INNER JOIN customer ON orders.customerId = customer.id) group by orders.orderId");

            ResultSet rst = pstm.executeQuery();

            while(rst.next()){
                search.add(new SearchTM(rst.getString(1),rst.getString(2),rst.getInt(3),rst.getString(4),rst.getString(5)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        tblSearch.setItems(search);

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String searchText = txtSearch.getText();
                ObservableList<SearchTM> search1 = tblSearch.getItems();
                tblSearch.getItems().clear();

                if(searchText.equals(null)){
                    initialize();
                    return;
                }

                try {
                    connection= DBConnection.getInstance().getConnection();
                    String like = "%"+searchText+"%";
                    PreparedStatement pstm = connection.prepareStatement("select orders.orderId,orders.orderDate,sum((orderDetail.qty)*(orderDetail.unitPrice)) as total,orders.customerId,customer.name from ((orders \n" +
                            "INNER JOIN orderDetail ON orders.orderId = orderDetail.orderId)\n" +
                            "INNER JOIN customer ON orders.customerId = customer.id) group by orders.orderId having orders.orderId like ? OR orders.orderDate like ? OR orders.customerId like ? OR customer.name like ? OR sum((orderDetail.qty)*(orderDetail.unitPrice)) like ?;");

                    pstm.setString(1,like);
                    pstm.setString(2,like);
                    pstm.setString(3,like);
                    pstm.setString(4,like);
                    pstm.setString(5,like);

                    ResultSet rst = pstm.executeQuery();


                    while(rst.next()){

                        search1.add(new SearchTM(rst.getString(1),rst.getString(2),rst.getInt(3),rst.getString(4),rst.getString(5)));
                    }
                    tblSearch.setItems(search1);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void tblSearch_Action(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2) {

            URL resource = this.getClass().getResource("/View/PlaceOrder.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Parent root = fxmlLoader.load();
            Scene placeOrderScene = new Scene(root);
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(placeOrderScene);
            secondaryStage.centerOnScreen();
            secondaryStage.setTitle("View Order");
            secondaryStage.setResizable(false);

            ManageOrdersController ctrl = fxmlLoader.getController();
            SearchTM selectedOrder = tblSearch.getSelectionModel().getSelectedItem();
            ctrl.initializeForSearchOrder(selectedOrder.getOrderID());


            secondaryStage.showAndWait();
            ctrl.tblOrders.getItems().clear();
        }
    }

    public void btnHome_Action(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/View/DashboardCMS.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.anpSearchOrders.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    public void btnReports_OnAction(ActionEvent actionEvent) throws JRException {

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream("/Reports/search.jasper"));
        Map<String, Object> params = new HashMap<>();
        String temp=txtSearch.getText();
        params.put("searchText","%"+temp+"%");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, DBConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint, false);

    }
}
