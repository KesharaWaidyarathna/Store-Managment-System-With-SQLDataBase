package Controller;

import DTO.CustomerTM;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class CustomerManageController {


    public AnchorPane anpManage;
    public TableView<CustomerTM> tblCustomer;
    public JFXTextField txtId;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXButton btnSave;
    int count = 0;
    public Connection connection;

    public void initialize() throws ClassNotFoundException {




        //loadTable(String id,String name,String address)

        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        //loadCustomers();
        try {
            connection= DBConnection.getInstance().getConnection();
            ObservableList<CustomerTM> customer = tblCustomer.getItems();

            String sql = "SELECT * from customer";
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();
            while (rst.next()){
                System.out.println("Iitializer");
                customer.add(new CustomerTM(rst.getString(1),rst.getString(2),rst.getString(3)));
            }
            tblCustomer.setItems(customer);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tblCustomer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM newValue) {

                CustomerTM selectedItem = tblCustomer.getSelectionModel().getSelectedItem();

                try {
                    Connection connection = null;
                    try {
                        connection= DBConnection.getInstance().getConnection();
                        String sql="select * from customer where id=?";
                        PreparedStatement pstm = connection.prepareStatement(sql);
                        pstm.setString(1,selectedItem.getId());
                        ResultSet rst = pstm.executeQuery();

                        if(rst.next()){
                            txtId.setText(rst.getString(1));
                            txtName.setText(rst.getString(2));
                            txtAddress.setText(rst.getString(3));
                            txtId.setDisable(true);
                            btnSave.setText("UPDATE");
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

//    private void setID() {
//        String ID = "";
//        if (0 < DB.nextIndex && DB.nextIndex < 10) {
//            ID = "C00" + DB.nextIndex;
//        } else if (10 <= DB.nextIndex && DB.nextIndex < 100) {
//            ID = "C0" + DB.nextIndex;
//        } else if ((100 <= DB.nextIndex && DB.nextIndex < 1000)) {
//            ID = "C" + DB.nextIndex;
//        }
//        txtId.setText(ID);
//
//    }

    public void btnback_Action(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/View/DashboardCMS.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.anpManage.getScene().getWindow());
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }


    public void btnSave_Action(ActionEvent actionEvent) throws SQLException {

        connection= DBConnection.getInstance().getConnection();

        if(!txtName.getText().isEmpty() || !txtId.getText().isEmpty() || !txtAddress.getText().isEmpty()){

                String name =txtName.getText();

                if(name.matches("^\\b([A-Za-z.]+\\s?)+$") && txtAddress.getText().matches("^\\b[A-Za-z0-9/,\\s]+.$")){

                    if(btnSave.getText().equals("SAVE")){
                        String sql = "INSERT INTO customer VALUES(?,?,?)";
                        PreparedStatement pstm = connection.prepareStatement(sql);
                        pstm.setString(1,txtId.getText());
                        pstm.setString(2,txtName.getText());
                        pstm.setString(3,txtAddress.getText());
                        int affect = pstm.executeUpdate();

                        if(affect>0){
                            System.out.println("Done");
                        }

                    }else if(btnSave.getText().equals("UPDATE")){
                        System.out.println("UPDATE");
                        String sql = "UPDATE customer SET name=? , address=?  where id=?";
                        PreparedStatement pstm = connection.prepareStatement(sql);
                        pstm.setString(1,txtName.getText());
                        pstm.setString(2,txtAddress.getText());
                        pstm.setString(3,txtId.getText());


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
                            "Invalid inputs!",
                            ButtonType.OK);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                }

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "You have empty fields!",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();

        }
        tblCustomer.getItems().clear();
        try {
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void btnDelete_Action(ActionEvent actionEvent) throws SQLException {

        connection= DBConnection.getInstance().getConnection();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this customer?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {
            CustomerTM selectedItem = tblCustomer.getSelectionModel().getSelectedItem();

            String sql = "DELETE from customer where id=?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1,selectedItem.getId());
            int affected= pstm.executeUpdate();

            if (affected>0){
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION,
                        "Record deleted!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType1= alert1.showAndWait();
            }
            tblCustomer.getItems().clear();
            try {
                initialize();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }


    }

//    public void tableCustomer_OnClicked(MouseEvent mouseEvent) {
//        CustomerTM temp = (CustomerTM) tableCustomer.getSelectionModel().getSelectedItem();
//        txtID.setText(temp.getId());
//        txtName.setText(temp.getName());
//        txtAddress.setText(temp.getAddress());
//    }

    public void btnNew_Action(ActionEvent actionEvent) throws SQLException {
        txtId.clear();
        txtName.clear();
        txtName.clear();
        tblCustomer.getSelectionModel().clearSelection();
        txtName.setDisable(false);
        txtAddress.setDisable(false);
        txtName.requestFocus();
        btnSave.setDisable(false);

        // Generate a new id
        connection= DBConnection.getInstance().getConnection();
        String sql ="Select id from customer";
        PreparedStatement pstm =connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();

        String ids = null;
        int maxId = 0;

        while (rst.next()){
            ids=rst.getString(1);

            int id = Integer.parseInt(ids.replace("C", ""));
            if (id > maxId) {
                maxId = id;
            }
        }


        maxId = maxId + 1;
        String id = "";
        if (maxId < 10) {
            id = "C00" + maxId;
        } else if (maxId < 100) {
            id = "C0" + maxId;
        } else {
            id = "C" + maxId;
        }
        txtId.setText(id);
    }

    public void btnReports_OnAction(ActionEvent actionEvent) throws JRException {

        JasperDesign jasperDesign = JRXmlLoader.load(this.getClass().getResourceAsStream("/Reports/customer1.jrxml"));
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String,Object> params = new HashMap<>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params,new JRBeanCollectionDataSource(tblCustomer.getItems()));

        JasperViewer.viewReport(jasperPrint,false);


    }
}
