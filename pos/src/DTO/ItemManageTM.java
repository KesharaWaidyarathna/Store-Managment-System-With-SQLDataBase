package DTO;

public class ItemManageTM {
    private String itemCode;
    private String itemDescription;
    private int qtyOnHand;
    private int unitPrice;

    public ItemManageTM(String itemCode, String itemDescription, int qtyOnHand, int unitPrice) {
        this.itemCode = itemCode;
        this.itemDescription = itemDescription;
        this.qtyOnHand = qtyOnHand;
        this.unitPrice = unitPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "ItemManageTM{" +
                "itemCode='" + itemCode + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", qtyOnHand=" + qtyOnHand +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
