import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store
{
    public static Map<Product, InventoryItem> totalInventory;
    //this is pretty much just to stop skus doubling up
    public static HashMap<Integer, Product> skuMap;
    private HashMap<Integer, Cart> cartsList;
    private String storeName;
    private static int cartsCounter = 1;

    //Constructor for the main store class
    public Store(String newName)
    {
        storeName = newName;
        totalInventory = new HashMap<>();
        skuMap = new HashMap<>();
        cartsList = new HashMap<>();
        System.out.println("Grand opening of " + storeName);
    }

    //Loops through the list of carts and prints out their contents
    public void printAllCarts()
    {
        System.out.println("======================================");
        System.out.println("Carts");
        for(var e: cartsList.keySet())
        {
            System.out.println(cartsList.get(e).toString());
        }
    }

    //Times out all carts that are older than n days.
    //Releases all their contents and allows them to be placed into another cart
    public void timeoutAgedCarts(int daysToExpire)
    {
        System.out.println("Beginning timeout process on all aged cards older than "
                + daysToExpire + " days");
        for(int e: cartsList.keySet())
        {
            Cart cartPointer = cartsList.get(e);
            LocalDateTime date1 = cartPointer.getDate();
            LocalDateTime date2 = LocalDateTime.now().minusDays(daysToExpire);

            //Date 1 occurs BEFORE Date 2
            if(date1.compareTo(date2) < 0)
            {
                System.out.println("Card ID " + e + " has been timed out due to inactivity");
                cartPointer.clearCart();
            }
        }
    }

    //Checks out a cart and completes a sale, also prints the receipt for the cart's items
    //Subtracts total stock from the respective items sold
    //checks if the remaining stock triggers threshold to automatically re-order more stock
    public void checkOutCart(int cartId)
    {
        if(cartsList.get(cartId) == null)
        {
            System.out.println("Cart ID not found");
            return;
        }
        Cart targetCart = cartsList.get(cartId);
        targetCart.checkoutAndPrint();
        System.out.println("Cart ID " + cartsList.get(cartId) + " has been checked out");
    }

    //Gets all items in inventory and prints them by category.
    //This could be applied to print the entire inventory sorted another way
    //Eg: sorted by manufacturer
    public void listProductsByCategory()
    {
        System.out.println("================================================");
        System.out.println("Starting print of all items listed by category");
        Map<productCategory, List<Product>> inventoryCategories = new HashMap<>();
        //populates the big list to print
        for(var e : totalInventory.keySet())
        {
            Product productPointer = totalInventory.get(e).getRealProduct();
            productCategory getProductCat = productPointer.getCategory();
            inventoryCategories.computeIfAbsent(getProductCat, k -> new ArrayList<>()).add(productPointer);
            //The below is logically the same thing but written out and more costly
            /*List<Product> newList = inventoryCategories.get(getProductCat);
            if(newList == null)
            {
                newList = new ArrayList<Product>();
                inventoryCategories.put(getProductCat, newList);
            }
            newList.add(productPointer);*/
        }
        for(productCategory e : inventoryCategories.keySet())
        {
            System.out.println("---------------");
            System.out.println("Category: " + e.name().toUpperCase());
            String formattedLine = inventoryCategories.get(e).toString().replaceAll("},", "}\n");
            System.out.println(formattedLine);
        }
    }

    //Adds new product to the store and corresponding maps
    //The SKU is provided in the method argument for simplicity, but it could
    //be generated sequentially using a static field in this store class
    public void addNewProduct(int inputSku, String inputName, String inputMfg, productCategory inputCat,
           int inputQuantityAll, int inputReorderAmount, int inputThreshold, double inputPrice)
    {
        System.out.println("Attempting to add new product: " + inputName);
        Product productToAdd = new Product(inputSku, inputName, inputMfg, inputCat,
                inputQuantityAll, inputReorderAmount, inputThreshold, inputPrice);
        if(productToAdd.getName() == null)
        {
            System.out.println("Failed to add product");
        }
        else
        {
            System.out.println("Successfully added product. Details: ");
            System.out.println(productToAdd.toString());
        }
    }

    //Removes ALL of a product from stock, opens SKU number back up
    //Overloaded method that allows passing a SKU instead of a Product for identical functionality
    public void delistProduct(int targetSKU)
    {
        Product p = skuMap.get(targetSKU);
        if(p == null)
        {
            System.out.println("SKU not found");
        }
        else
        {
            delistProduct(p);
        }
    }

    //Removes ALL of a product from stock, opens SKU number back up
    public void delistProduct(Product targetProduct)
    {
        //Target item doesn't exist or not found
        if(totalInventory.get(targetProduct) == null)
        {
            System.out.println("Can't remove what we dont have");
            return;
        }
        else
        {
            for(int e: cartsList.keySet())
            {
                Cart cartPointer = cartsList.get(e);
                if(cartPointer.getProductsInCart().containsKey(targetProduct))
                {
                    cartPointer.removeAllOfItem(targetProduct);
                }
            }
            totalInventory.remove(targetProduct);
            skuMap.remove(targetProduct.getSku());
        }
    }

    //Overloaded method that allows omission of a date/time and just uses the current time
    public Cart newShoppingCart(int inputId)
    { return newShoppingCart(inputId, LocalDateTime.now());}

    //Initializes a new shopping cart which can Add/remove items, check out etc
    public Cart newShoppingCart(int inputId, LocalDateTime inputDate)
    {
        if(cartsList.containsKey(inputId))
        {
            System.out.println("Cart ID " + inputId + " is already in use");
            return null;
        }
        Cart c = new Cart(inputId, cartType.online, inputDate);
        cartsList.put(inputId, c);
        return c;
    }
}
