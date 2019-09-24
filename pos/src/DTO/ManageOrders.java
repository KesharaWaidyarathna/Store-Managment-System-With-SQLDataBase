package DTO;

public class ManageOrders {
    private String itemCode;
    private String itemDescription;
    private String qtyOnHand;
    private String unitPrice;
    private String total;

    public ManageOrders(){}

    public ManageOrders(String itemCode, String itemDescription, String qtyOnHand, String unitPrice, String total) {
        this.itemCode = itemCode;
        this.itemDescription = itemDescription;
        this.qtyOnHand = qtyOnHand;
        this.unitPrice = unitPrice;
        this.total = total;
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

    public String getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(String qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
