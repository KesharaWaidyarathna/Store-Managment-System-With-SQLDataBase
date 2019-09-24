package Controller;

import DB.DB;
import DTO.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ManageOrdersController {
    public JFXButton btnPlaceOrder;
    public AnchorPane apnPlaceOrder;
    public JFXComboBox<String> cmbCustomerID;
    public JFXTextField txtName;
    public JFXButton btnNewOrder;
    public JFXButton btnHome;
    public JFXTextField txtDescription;
    public JFXComboBox<String> cmbItemCode;
    public JFXTextField txtUnitePrice;
    public JFXTextField txtQOH;
    public JFXTextField txtQuantity;
    public TableView<ManageOrdersTM> tblOrders;
    public Label lblTotal;
    public JFXButton btnSave;
    public JFXTextField txtDate;
    public JFXTextField txtOrderID;

    public int total = 0;
    public Connection connection;
    public ArrayList<ItemManageTM> items = new ArrayList<>();



    public void initialize() {
        System.out.println("Initialize");
        tblOrders.getItems().clear();
        tblOrders.refresh();

        //Date
        SimpleDateFormat fomatter = new SimpleDateFormat("dd/MM/YYYY");
        Date date = new Date();
        txtDate.setText(fomatter.format(date));
        txtDate.setDisable(true);

        if (txtOrderID.getText().isEmpty()) {
            try {
                orderID();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //load items
        items.clear();
        try {
            connection= DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("select * from item");
            ResultSet rst = pstm.executeQuery();

            while (rst.next()){
                items.add(new ItemManageTM(rst.getString(1),rst.getString(2),rst.getInt(3),rst.getInt(4)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        cmbCustomerID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                try {
                    connection= DBConnection.getInstance().getConnection();

                    if (cmbCustomerID.getSelectionModel().getSelectedItem() != null) {
                        Object selectedItem = cmbCustomerID.getSelectionModel().getSelectedItem();

                        PreparedStatement pstm = connection.prepareStatement("select name from customer where id=?");
                        pstm.setString(1,selectedItem.toString());
                        ResultSet rst = pstm.executeQuery();

                        if(rst.next()){
                            txtName.setText(rst.getString(1));
                            txtName.setDisable(true);
                            cmbCustomerID.setDisable(true);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                try {
                    connection= DBConnection.getInstance().getConnection();

                    Object selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
                    //ObservableList<ItemManageTM> items = FXCollections.observableList(DB.items);
                    String ID = (String) selectedItem;
                    if (cmbItemCode.getSelectionModel().isEmpty()) {
                        return;
                    } else {

                        for (int i = 0; i < items.size(); i++) {
                            if(items.get(i).getItemCode().equals(selectedItem)){
                                if(items.get(i).getQtyOnHand()>0){
                                    txtQOH.setText(String.valueOf(items.get(i).getQtyOnHand()));
                                    txtQOH.setDisable(true);
                                }else{
                                    Alert alert = new Alert(Alert.AlertType.ERROR,
                                            "Out of stock!",
                                            ButtonType.OK);
                                    Optional<ButtonType> buttonType = alert.showAndWait();
                                    return;
                                }
                            }
                        }

                        PreparedStatement pstm = connection.prepareStatement("select * from item where itemCode=?");
                        pstm.setString(1,selectedItem.toString());
                        ResultSet rst = pstm.executeQuery();

                        if(rst.next()){

                                txtDescription.setText(rst.getString(2));
                                txtUnitePrice.setText(rst.getString(4));
                                txtDescription.setDisable(true);
                                txtUnitePrice.setDisable(true);
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }



            }
        });
        tblOrders.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ManageOrdersTM>() {
            @Override
            public void changed(ObservableValue<? extends ManageOrdersTM> observable, ManageOrdersTM oldValue, ManageOrdersTM newValue) {
                if (tblOrders.getSelectionModel().isEmpty()) {
                    return;
                }

                btnSave.setText("Update");
                ManageOrdersTM selectedItem = tblOrders.getSelectionModel().getSelectedItem();
                //   ObservableList<ManageOrdersTM> orders = tableOrderDetails.getItems();
                ObservableList<ItemManageTM> items = FXCollections.observableList(DB.items);
                cmbItemCode.getSelectionModel().select(selectedItem.getItemCode());
                txtDescription.setText(selectedItem.getItemDescription());
                for (int i = 0; i < items.size(); i++) {
                    if (selectedItem.getItemCode().equals(items.get(i).getItemCode())) {
                        txtQOH.setText(Integer.toString(items.get(i).getQtyOnHand()));
                    }
                }
                txtUnitePrice.setText(Integer.toString(selectedItem.getUnitPrice()));
                txtQuantity.setText(Integer.toString(selectedItem.getQty()));
                txtDescription.setDisable(true);
                txtUnitePrice.setDisable(true);
                txtQOH.setDisable(true);
            }
        });


        //set table columns
        tblOrders.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrders.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("itemDescription"));
        tblOrders.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrders.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrders.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));
        tblOrders.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("delete"));

        ObservableList<ManageOrdersTM> orders = FXCollections.observableList(DB.orders);
        tblOrders.setItems(orders);

        //customers
        cmbCustomerID.getItems().clear();
        ObservableList custID = cmbCustomerID.getItems();
        try {
            PreparedStatement pstm = connection.prepareStatement("select id from customer");
            ResultSet rst = pstm.executeQuery();

            while(rst.next()){
                custID.add(rst.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //items
        cmbItemCode.getItems().clear();
        ObservableList itemID = cmbItemCode.getItems();
        for (int i = 0; i < items.size(); i++) {
            itemID.add(items.get(i).getItemCode());
        }
    }



    public void btnPlaceOrder_Action(ActionEvent actionEvent) throws SQLException, JRException {

        connection= DBConnection.getInstance().getConnection();



        ObservableList<ManageOrdersTM> orders = tblOrders.getItems();

       PreparedStatement pstm =connection.prepareStatement("INSERT INTO orders VALUES (?,?,?)");
       pstm.setString(1,txtOrderID.getText());
       pstm.setString(2,txtDate.getText());
       pstm.setString(3, (String) cmbCustomerID.getSelectionModel().getSelectedItem());
       int affected = pstm.executeUpdate();

       if(affected>0){
           System.out.println("Order inserted");
       }


        for (int i = 0; i < orders.size(); i++) {

            PreparedStatement pstm1 = connection.prepareStatement("INSERT INTO orderDetail values (?,?,?,?)");
            pstm1.setString(1,txtOrderID.getText());
            pstm1.setString(2,orders.get(i).getItemCode());
            pstm1.setString(3, String.valueOf(orders.get(i).getQty()));
            pstm1.setString(4, String.valueOf(orders.get(i).getUnitPrice()));
            int affected1 = pstm1.executeUpdate();

            if(affected1>0){
                System.out.println("Order detail inseted");
            }

            PreparedStatement pstm2 = connection.prepareStatement("select qtyOnHand from item where itemCode=?");
            pstm2.setString(1,orders.get(i).getItemCode());
            ResultSet rst = pstm2.executeQuery();

            if(rst.next()){

                //int newQty = rst.getInt(1)-Integer.parseInt(txtQty.getText());
                int newQty=0;
                for (int j = 0; j <items.size() ; j++) {
                    if(items.get(j).getItemCode().equals(orders.get(i).getItemCode())){
                        newQty=items.get(j).getQtyOnHand();
                    }
                }

                PreparedStatement pstm3 = connection.prepareStatement("UPDATE item set qtyOnHand=? where itemCode=?");
                pstm3.setString(1, String.valueOf(newQty));
                pstm3.setString(2, orders.get(i).getItemCode());
                int affected2 = pstm3.executeUpdate();

                if(affected2>0){
                    System.out.println("Qty on hand updated!");
                }
            }
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "You have successfully placed the order.",
                ButtonType.OK);
        Optional<ButtonType> buttonType = alert.showAndWait();

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream("/Reports/placeOrder.jasper"));
        JasperReport jasperSubReport = (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream("/Reports/orderSub.jasper"));


        Map<String, Object> params = new HashMap<>();
        params.put("orderID", txtOrderID.getText());
        params.put("subReport",jasperSubReport );

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, DBConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint, false);


        orderID();
        initialize();
        cmbCustomerID.setDisable(false);
       clearALL();
        return;
    }

    private void orderID() throws SQLException {
        // Generate a new id
        connection= DBConnection.getInstance().getConnection();
        String sql ="Select orderId from orders";
        PreparedStatement pstm =connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();

        String ids = null;
        int maxId = 0;

        while (rst.next()){
            ids=rst.getString(1);

            int id = Integer.parseInt(ids.replace("OD", ""));
            if (id > maxId) {
                maxId = id;
            }
        }


        maxId = maxId + 1;
        String id = "";
        if (maxId < 10) {
            id = "OD00" + maxId;
        } else if (maxId < 100) {
            id = "OD0" + maxId;
        } else {
            id = "OD" + maxId;
        }
        txtOrderID.setText(id);
    }

    void clearALL() {
        btnNewOrder_Action(null);
        txtName.clear();
        tblOrders.getItems().clear();
        lblTotal.setText("0");
        cmbCustomerID.getSelectionModel().clearSelection();
    }

    public void btnNewOrder_Action(ActionEvent actionEvent) {

        tblOrders.getSelectionModel().clearSelection();
        btnSave.setText("Add");
        txtDescription.clear();
        txtQuantity.clear();
        txtQOH.clear();
        txtUnitePrice.clear();
        cmbItemCode.getSelectionModel().clearSelection();
        cmbCustomerID.requestFocus();
    }

    public void btnSave_Action(ActionEvent actionEvent) throws SQLException {

        connection= DBConnection.getInstance().getConnection();
        ObservableList<ManageOrdersTM> orders = tblOrders.getItems();
        //ObservableList<ItemManageTM> items = FXCollections.observableList(DB.items);

        if (btnSave.getText().equals("Add")) {

            int qtyOnHand = Integer.parseInt(txtQOH.getText());
            String qtyy = txtQuantity.getText();

            if (txtQuantity.getText().isEmpty() || txtQuantity.getText().equals("0") || !txtQuantity.getText().matches("^\\d+$")) {

                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Please add valid quontity!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
                return;

            } else if (cmbItemCode.getSelectionModel().isEmpty() || cmbCustomerID.getSelectionModel().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Please select the customer ID and item ID",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
                return;
            } else if (qtyOnHand < Integer.parseInt(qtyy)) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Out of stock!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
                return;
            } else {
                //ObservableList<ManageOrdersTM> orders=tableOrderDetails.getItems();
                int qty = Integer.parseInt(txtQuantity.getText());
                int uniPrice = Integer.parseInt(txtUnitePrice.getText());


                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i).getItemCode().equals(cmbItemCode.getSelectionModel().getSelectedItem())) {
                        int newQty = orders.get(i).getQty() + Integer.parseInt(txtQuantity.getText());
                        orders.get(i).setQty(newQty);
                        int unitPrice = Integer.parseInt(txtUnitePrice.getText());
                        int newTotal = newQty * unitPrice;
                        orders.get(i).setTotal(newTotal);
                        tblOrders.refresh();
                        System.out.println("Done editing.");
                        int finalTotal = 0;
                        for (int j = 0; j < orders.size(); j++) {
                            finalTotal += orders.get(i).getTotal();
                        }
                        lblTotal.setText(Integer.toString(finalTotal));
                        for (int j = 0; j < items.size(); j++) {
                            if (items.get(j).getItemCode().equals(cmbItemCode.getSelectionModel().getSelectedItem())) {
                                int qtyOH = items.get(j).getQtyOnHand();
                                items.get(j).setQtyOnHand(qtyOH - newQty);

                            }
                        }
                        return;

                    }
                }

                total = (qty * uniPrice);
                JFXButton button = new JFXButton("Delete");
                String itemCode = (String) cmbItemCode.getSelectionModel().getSelectedItem();
                ManageOrdersTM object = new ManageOrdersTM((String) cmbItemCode.getSelectionModel().getSelectedItem(), txtDescription.getText(), Integer.parseInt(txtQuantity.getText()), Integer.parseInt(txtUnitePrice.getText()), total, button);
                orders.add(object);

                button.setOnAction(event -> {
                    btnTableDelete_OnAction(object);
                });
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).getItemCode().equals(cmbItemCode.getSelectionModel().getSelectedItem())) {
                        int qtyOH = items.get(j).getQtyOnHand();
                        items.get(j).setQtyOnHand(qtyOH - qty);
                    }
                }

                int finalTotal = 0;
                for (int i = 0; i < orders.size(); i++) {
                    finalTotal += orders.get(i).getTotal();
                }
                lblTotal.setText(Integer.toString(finalTotal));
            }


        } else {
            System.out.println("Update");
            ManageOrdersTM item = tblOrders.getSelectionModel().getSelectedItem();
            String ID = (String) cmbItemCode.getSelectionModel().getSelectedItem();
            ObservableList<ManageOrdersTM> table = tblOrders.getItems();

            for (int i = 0; i < table.size(); i++) {
                if (item.getItemCode().equals(orders.get(i).getItemCode())) {
                    table.get(i).setQty(Integer.parseInt(txtQuantity.getText()));
                    int qty = table.get(i).getQty();
                    int unitPrice = table.get(i).getUnitPrice();
                    table.get(i).setTotal(qty * unitPrice);

                    int finalTotal = 0;
                    for (int j = 0; j < orders.size(); j++) {
                        finalTotal += table.get(i).getTotal();
                    }
                    lblTotal.setText(Integer.toString(finalTotal));


                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Successfully updated the data..",
                            ButtonType.OK);
                    Optional<ButtonType> buttonType = alert.showAndWait();

                    for (int k = 0; k < items.size(); k++) {
                        if (items.get(k).getItemCode().equals(item.getItemCode())) {
                            int qtyOH = items.get(k).getQtyOnHand() + item.getQty();
                            int QTY = Integer.parseInt(txtQuantity.getText());
                            items.get(k).setQtyOnHand(qtyOH - QTY);
                        }
                    }
                    tblOrders.refresh();
                    return;
                }
            }

        }
    }

    public void btnTableDelete_OnAction(ManageOrdersTM object) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this customer?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        ObservableList<ItemManageTM> items = FXCollections.observableList(DB.items);
        if (buttonType.get() == ButtonType.YES) {
            int total = object.getTotal();
            int finalTotal = Integer.parseInt(lblTotal.getText());
            tblOrders.getItems().remove(object);
            lblTotal.setText(Integer.toString(finalTotal - total));


            for (int j = 0; j < items.size(); j++) {
                if (items.get(j).getItemCode().equals(object.getItemCode())) {
                    int qOH = items.get(j).getQtyOnHand();
                    int qty = object.getQty();

                    items.get(j).setQtyOnHand(qOH + qty);
                }
            }
        }
    }

    public void initializeForSearchOrder(String orderID) {

        connection = DBConnection.getInstance().getConnection();
        tblOrders.getItems().clear();
        ObservableList<ManageOrdersTM> table = tblOrders.getItems();
        txtOrderID.setText(orderID);

        try {
            PreparedStatement pstm = connection.prepareStatement("select o.itemCode , i.description, o.qty,o.unitPrice,(o.unitPrice*o.qty) as total from orderDetail o,item i where (i.itemCode=o.itemCode) AND o.orderId=?");
            pstm.setString(1,orderID);
            ResultSet rst = pstm.executeQuery();

            while(rst.next()){
                JFXButton button = new JFXButton("Delete");
                ManageOrdersTM mng =  new ManageOrdersTM(rst.getString(1),rst.getString(2),rst.getInt(3),rst.getInt(4),rst.getInt(5),button);
                table.add(mng);

                tblOrders.setDisable(true);
                cmbCustomerID.setEditable(false);
                txtName.setEditable(false);
                cmbItemCode.setEditable(false);
                txtDescription.setEditable(false);
                txtQuantity.setEditable(false);
                txtQOH.setEditable(false);
                txtUnitePrice.setEditable(false);
                txtOrderID.setEditable(false);
                btnSave.setDisable(true);
                btnPlaceOrder.setDisable(true);

            }
            int finalTotal = 0;
            for (int i = 0; i < table.size(); i++) {
                finalTotal += table.get(i).getTotal();
            }
            lblTotal.setText(Integer.toString(finalTotal));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try{
            String sql="SELECT orderDate,customerId FROM orders WHERE orderId=?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1 ,orderID);
            ResultSet rst = pst.executeQuery();

            if (rst.next()){
                txtDate.setText(rst.getString(1));
                cmbCustomerID.setValue(rst.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tblOrders.setItems(table);

    }

    public void btnHome_Action(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/View/DashboardCMS.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.apnPlaceOrder.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }


    public void btnSave_KeyAction(KeyEvent keyEvent) {
    }
}
