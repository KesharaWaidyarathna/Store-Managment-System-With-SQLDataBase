package DTO;

import com.jfoenix.controls.JFXButton;

public class ManageOrdersTM {
    private String itemCode;
    private int qty;
    private String itemDescription;
    private int unitPrice;
    private int total;
    private JFXButton delete;

    public ManageOrdersTM(String itemCode, String itemDescription, int qty, int unitPrice, int total, JFXButton delete) {
        this.itemCode = itemCode;
        this.itemDescription = itemDescription;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.total = total;
        this.delete = delete;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public JFXButton getDelete() {
        return delete;
    }

    public void setDelete(JFXButton delete) {
        this.delete = delete;
    }
}
