package DTO;

public class SearchTM {
    private String orderID;
    private String date;
    private int total;
    private String customerID;
    private String customerName;

    public SearchTM(String orderID, String date, int total, String customerID, String customerName) {
        this.orderID = orderID;
        this.date = date;
        this.total = total;
        this.customerID = customerID;
        this.customerName = customerName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
