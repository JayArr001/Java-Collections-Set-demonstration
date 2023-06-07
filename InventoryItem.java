public class InventoryItem {
    //InventoryItems are back-end objects for managing their price, stock level etc

    //product object for reference, and what the meta-information revolves around
    private Product metaProduct;

    //total quantity held in the store and held in carts
    private int quantityAll;

    //quantity reserved for whatever reason eg layaway or adding to cart
    private int quantityReserved;

    //quantity that will be reordered with the threshold is reached
    private int quantityReorderAmount;

    //quantity threshold that, when reached, re-orders the amount in quantityReorderAmount
    private int reorderThreshold;

    //price that the product is set to
    private double salesPrice;

    //Constructor for the metadata object which is paired with the actual product
    //This object contains information about the product itself, such as stock remaining
    //pricing, re-order threshold etc.
    //The Product-InventoryItem relationship didn't HAVE to be modeled this way, in fact
    //this class might be considered redundant and maybe it could all be in the Product class
    public InventoryItem(Product inputProduct, int inputQuantityAll, int inputReorderAmount,
                         int inputThreshold, double inputPrice)
    {
        metaProduct = inputProduct;
        quantityAll = inputQuantityAll;
        quantityReserved = 0; //set this to 0 upon init
        reorderThreshold = inputThreshold;
        quantityReorderAmount = inputReorderAmount;
        salesPrice = inputPrice;
    }

    //reserves a number of items, returns false if it's not possible
    public boolean reserveItem(int quantityToReserve) {
        //if there is sufficient quantity
        if ((quantityAll - quantityReserved - quantityToReserve) > 0) {
            quantityReserved += quantityToReserve;
            return true;
        }
        //return false if it's not possible
        return false;
    }

    //releases a number of items from reserved list. returns false if the number of
    //reserved items was less than the quantity to release
    public boolean releaseItem(int quantityToRelease) {
        quantityReserved -= quantityToRelease;
        if (quantityReserved < 0) {
            quantityReserved = 0;
            return false;
        }
        return true;
    }

    //tries to sell items and remove them from reserve list. All items must enter the reserve list
    //before being sold. The order goes stock/shelf -> cart (reserved) -> sale
    //returns false if it's not possible for some reason
    public boolean sellItem(int quantityToSell) {
        //If there's not enough stock, do nothing
        if (((quantityReserved - quantityToSell) < 0) || ((quantityAll - quantityToSell) < 0)) {
            return false;
        }
        quantityReserved -= quantityToSell;
        quantityAll -= quantityToSell;
        if(quantityAll <= reorderThreshold)
        {
            reorderStock();
        }
        return true;
    }

    //Method called when threshold was reached and it's time to "order" more stock
    public void reorderStock()
    {
        //"""order""" more
        quantityAll += quantityReorderAmount;
    }

    //Gets the unreserved quantity of an item
    public int getUnreservedQuantity()
    { return quantityAll - quantityReserved;}

    //getters
    public int getQuantityAll() {
        return quantityAll;
    }

    public int getQuantityReserved() {
        return quantityReserved;
    }

    public int getQuantityReorderAmount() {
        return quantityReorderAmount;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public Product getRealProduct()
    {
        return metaProduct;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                ", quantityAll=" + quantityAll +
                ", quantityReserved=" + quantityReserved +
                ", quantityReorderAmount=" + quantityReorderAmount +
                ", reorderThreshold=" + reorderThreshold +
                ", salesPrice=" + salesPrice +
                '}';
    }
}
