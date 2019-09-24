package DTO;

import java.util.ArrayList;

public class Orders {
    String date;
    String customerID;
    String orderID;
    ArrayList<OrderDetails> orderDetails = new ArrayList<>();

    public Orders(String date, String customerID, String orderID, ArrayList<OrderDetails> orderDetails) {
        this.date = date;
        this.customerID = customerID;
        this.orderID = orderID;
        this.orderDetails = orderDetails;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public ArrayList<OrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(ArrayList<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "date='" + date + '\'' +
                ", customerID='" + customerID + '\'' +
                ", orderID='" + orderID + '\'' +
                ", orderDetails=" + orderDetails +
                '}';
    }
}
