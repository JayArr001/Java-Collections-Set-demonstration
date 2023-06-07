enum productCategory {
    tv, computers, mobile, misc
}
//This enum could be anything for any categories, maybe a grocery store or some other outlet

public class Product
{
    //products are items held in carts
    private int sku;
    private String name;
    private String mfg;
    private productCategory category;
    private InventoryItem itemMetadata;

    //Constructor for the Product class. This constructor creates a new product instance.
    //product instance is required for many functions related in Cart.class and
    //InventoryItem.class, such as managing metadata related to products, sale prices etc.
    public Product(int inputSku, String inputName, String inputMfg, productCategory inputCat,
                   int inputQuantityAll, int inputReorderAmount, int inputThreshold, double inputPrice)
    {
        //if the master store list doesn't have this product yet
        if((Store.totalInventory.get(this) == null) && (Store.skuMap.get(inputSku) == null))
        {
            sku = inputSku; name = inputName; mfg = inputMfg; category = inputCat;
            itemMetadata = new InventoryItem(this, inputQuantityAll, inputReorderAmount,
                    inputThreshold, inputPrice);
            Store.totalInventory.put(this, itemMetadata);
            Store.skuMap.put(inputSku, this);
            //Store.totalInventory.put(inputSku, itemMetadata);
        }
        //Else the item is already on the list
        else
        {
            System.out.println("That item is already on the list. SKU: "
                    + inputSku + " - held by [" + Store.skuMap.get(inputSku).getName() + "]");
            System.out.println("You need to remove this product from the map before overriding the SKU");
        }
    }

    //Gets the paired InventoryItem object which holds metadata about the product
    //such as current stock, re-order thresholds, price etc
    //The product-metadata relationship didn't HAVE to be modeled this way, but it's simple and it works
    public InventoryItem getItemMetadata()
    {
        return itemMetadata;
    }

    //getters
    public int getSku() {return sku;}
    public String getName() {
        return name;
    }
    public String getMfg() {
        return mfg;
    }
    public productCategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "sku=" + sku +
                ", name='" + name + '\'' +
                ", mfg='" + mfg + '\'' +
                ", category=" + category +
                ",\nitemMetadata=" + itemMetadata +
                '}';
    }
}
