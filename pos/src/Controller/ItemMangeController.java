package Controller;

import DTO.ItemManageTM;
import DTO.Items;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemMangeController {
    public AnchorPane anpManageItems;
    public JFXTextField txtItemCode;
    public JFXTextField txtItemDiscription;
    public JFXTextField txtHOQ;
    public JFXTextField txtUnitPrice;
    public JFXButton btnSave;
    public TableView<ItemManageTM> tblItem;
    public Connection connection;



    public void initialize() {

        tblItem.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblItem.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("itemDescription"));
        tblItem.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblItem.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));


        try {
            connection= DBConnection.getInstance().getConnection();
            ObservableList<ItemManageTM> items = tblItem.getItems();

            String sql = "SELECT * from item";
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();
            while (rst.next()){
                System.out.println("Iitializer");
                items.add(new ItemManageTM(rst.getString(1),rst.getString(2),Integer.parseInt(rst.getString(3)),Integer.parseInt(rst.getString(4))));
            }
            tblItem.setItems(items);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tblItem.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemManageTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemManageTM> observable, ItemManageTM oldValue, ItemManageTM newValue) {

                ItemManageTM selectedItem = tblItem.getSelectionModel().getSelectedItem();

                try {
                    Connection connection = null;
                    try {
                        connection= DBConnection.getInstance().getConnection();
                        String sql="select * from item where itemCode=?";
                        PreparedStatement pstm = connection.prepareStatement(sql);
                        pstm.setString(1,selectedItem.getItemCode());
                        ResultSet rst = pstm.executeQuery();

                        if(rst.next()){
                            txtItemCode.setText(rst.getString(1));
                            txtItemDiscription.setText(rst.getString(2));
                            txtHOQ.setText(rst.getString(3));
                            txtUnitPrice.setText(rst.getString(4));
                            txtItemCode.setDisable(true);
                            btnSave.setText("Update");
                        }




                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }catch (NullPointerException n){
                    return;
                }
            }
        });


    }

    public void btnSave_Action(ActionEvent actionEvent) throws SQLException {

        connection= DBConnection.getInstance().getConnection();


        if(txtItemDiscription.getText().isEmpty() || txtHOQ.getText().isEmpty() || txtUnitPrice.getText().isEmpty()){

            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "You have empty fields!",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();

            return;
        }else{

            if(txtItemDiscription.getText().matches("^\\b([A-Za-z]+\\s*)+$") && txtUnitPrice.getText().matches("^\\d+$") && txtHOQ.getText().matches("^\\d+$")){

                if(btnSave.getText().equals("Save")){

                    System.out.println("Save");
                    String sql = "INSERT INTO item VALUES(?,?,?,?)";
                    PreparedStatement pstm = connection.prepareStatement(sql);
                    pstm.setString(1,txtItemCode.getText());
                    pstm.setString(2,txtItemDiscription.getText());
                    pstm.setString(3,txtHOQ.getText());
                    pstm.setString(4,txtUnitPrice.getText());
                    int affect = pstm.executeUpdate();

                    if(affect>0){
                        System.out.println("Done");
                    }

                }else if(btnSave.getText().equals("Update")){
                    System.out.println("Update");
                    String sql = "UPDATE item SET description=? , qtyOnHand=?, unitPrice=?  where itemCode=?";
                    PreparedStatement pstm = connection.prepareStatement(sql);
                    pstm.setString(1,txtItemDiscription.getText());
                    pstm.setString(2,txtHOQ.getText());
                    pstm.setString(3,txtUnitPrice.getText());
                    pstm.setString(4,txtItemCode.getText());


                    int affected= pstm.executeUpdate();

                    if (affected>0) {

                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                "Record updated!",
                                ButtonType.OK);
                        Optional<ButtonType> buttonType = alert.showAndWait();

                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Update error!",
                                ButtonType.OK);
                        Optional<ButtonType> buttonType = alert.showAndWait();

                    }

                    btnSave.setText("Save");

                }


            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Invalid input!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
            }
        }
        tblItem.getItems().clear();

            initialize();
    }

    public void btnDelete_Action(ActionEvent actionEvent) throws SQLException {

        connection= DBConnection.getInstance().getConnection();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this customer?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {

            ItemManageTM selectedItem = tblItem.getSelectionModel().getSelectedItem();

            String sql = "DELETE from item where itemCode=?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1,selectedItem.getItemCode());
            int affected= pstm.executeUpdate();

            if (affected>0){
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION,
                        "Record deleted!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType1= alert1.showAndWait();
            }
            tblItem.getItems().clear();
                initialize();


        }


    }

    public void btnBack_Action(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/View/DashboardCMS.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.anpManageItems.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    public void btnNew_Action(ActionEvent actionEvent) throws SQLException {
        txtItemCode.clear();
        txtItemDiscription.clear();
        txtHOQ.clear();
        txtUnitPrice.clear();
        tblItem.getSelectionModel().clearSelection();
        txtItemCode.setDisable(false);
        txtItemDiscription.setDisable(false);
        txtHOQ.setDisable(false);
        txtUnitPrice.setDisable(false);
        txtItemDiscription.requestFocus();
        btnSave.setDisable(false);

        // Generate a new id

        String sql ="Select itemCode from item";
        PreparedStatement pstm =connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();

        String ids = null;
        int maxId = 0;

        while (rst.next()){
            ids=rst.getString(1);

            int id = Integer.parseInt(ids.replace("I", ""));
            if (id > maxId) {
                maxId = id;
            }
        }


        maxId = maxId + 1;
        String id = "";
        if (maxId < 10) {
            id = "I00" + maxId;
        } else if (maxId < 100) {
            id = "I0" + maxId;
        } else {
            id = "I" + maxId;
        }
        txtItemCode.setText(id);
    }


    public void btnReports_OnAction(ActionEvent actionEvent) throws JRException {

        JasperDesign jasperDesign = JRXmlLoader.load(this.getClass().getResourceAsStream("/Reports/items.jrxml"));
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

//        ObservableList<ItemManageTM> table = tblItem.getItems();
//        ObservableList<Items> items = FXCollections.observableArrayList();

//        for (int i = 0; i <table.size() ; i++) {
//            items.add(new Items(table.get(i).getItemCode(),table.get(i).getItemDescription(),Integer.toString(table.get(i).getQtyOnHand()),Integer.toString(table.get(i).getUnitPrice())));
//        }
        Map<String,Object> params = new HashMap<>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params,DBConnection.getInstance().getConnection());

        JasperViewer.viewReport(jasperPrint,false);


    }
}
