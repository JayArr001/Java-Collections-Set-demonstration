import java.time.LocalDateTime;
import java.util.HashMap;

enum cartType {physical, online}
public class Cart
{
    private int cartId;
    private LocalDateTime date;
    private cartType type;
    private HashMap<Product, Integer> productsInCart;

    //Overloaded constructor which allows for a call without passing a date/time
    public Cart(int inputId, cartType inputType)
    {
        this(inputId, inputType, LocalDateTime.now());
        //assign cart's date for aging to "now" upon instantiating if date isnt given
    }

    //Cart constructor for the cart object
    public Cart(int inputId, cartType inputType, LocalDateTime inputDate)
    {
        cartId = inputId;
        date = inputDate;
        type = inputType;
        productsInCart = new HashMap<>();
    }

    //Gets the date/time that a cart object is holding
    //either when it was initialized, or for the sake of demonstrating time-outs, when a date was passed
    public LocalDateTime getDate() {return date;}

    //Overloaded method which allows passing of SKU instead of a Product object
    public void addItem(int productSku, int quantityToAdd)
    {
        if(Store.skuMap.get(productSku) == null)
        {
            System.out.println("SKU " + productSku + " not found");
            return;
        }
        addItem(Store.skuMap.get(productSku), quantityToAdd);
    }

    //Adds an item to the cart, and reserves that much of the item for the cart
    public void addItem(Product productToAdd, int quantityToAdd)
    {
        //if there is less net stock available than what is being asked for, break out
        int stockAvailable = Store.totalInventory.get(productToAdd).getUnreservedQuantity();
        if(stockAvailable < quantityToAdd)
        {
            System.out.println("Not enough stock of " + productToAdd.getName());
            System.out.println("Requested:  " + quantityToAdd + " - available: " + stockAvailable);
            return;
        }
        //if the cart doesn't currently have the item
        // or in other words, a new unique item is being added
        if(productsInCart.get(productToAdd) == null)
        {
            productsInCart.put(productToAdd, quantityToAdd);
        }
        else
        {
            //else the item already exists in the cart
            int currentAmount = productsInCart.get(productToAdd);
            currentAmount += quantityToAdd;
            productsInCart.put(productToAdd, currentAmount);
        }
        Store.totalInventory.get(productToAdd).reserveItem(quantityToAdd);
    }

    //Removes an item from the cart and releases it back, making it available again for other carts
    public void removeItem(Product productToRemove, int quantityToRemove)
    {
        if(productsInCart.get(productToRemove) == null)
        {
            System.out.println("Item not found in cart");
            return;
        }
        //else the item already exists in the cart
        int currentAmount = productsInCart.get(productToRemove);
        if((currentAmount - quantityToRemove) < 0) //wants to remove the whole amount
        {
            productsInCart.remove(productToRemove);
            Store.totalInventory.get(productToRemove).releaseItem(currentAmount);
            return;
        }
        else
        {
            int remainingAmount = currentAmount - quantityToRemove;
            Store.totalInventory.get(productToRemove).releaseItem(quantityToRemove);
            productsInCart.put(productToRemove, remainingAmount);
        }
    }

    //Overloaded method which allows passing of SKU to remove an item from cart instead of Product
    public void removeItem(int skuRemove, int quantityToRemove)
    {
        Product targetProduct = Store.totalInventory.get(skuRemove).getRealProduct();
        removeItem(targetProduct, quantityToRemove);
    }

    //Helper method which does the same thing as removeItem but doesn't require passing a quantity
    //It will remove all of that item from the cart
    public void removeAllOfItem(Product productToRemove)
    {
        if(productsInCart.get(productToRemove) == null)
        {
            System.out.println("Item not found in cart");
            return;
        }
        //else the item already exists in the cart
        int currentAmount = productsInCart.get(productToRemove);
        productsInCart.remove(productToRemove);
        Store.totalInventory.get(productToRemove).releaseItem(currentAmount);
    }

    //This is not the checkout process, used to empty a cart that is timed out
    //Releases the items back for general availability
    public void clearCart()
    {
        for(Product e : productsInCart.keySet())
        {
            int amount = productsInCart.get(e);
            //productsInCart.remove(e, amount);
            Store.totalInventory.get(e).releaseItem(amount);
        }
        productsInCart.clear();
    }

    //Checks out the cart and prints out a corresponding receipt
    public void checkoutAndPrint()
    {
        System.out.println("Name\tQuantity\tPrice/unit");
        double receiptTotal = 0.0;
        for(Product e : productsInCart.keySet())
        {
            int saleQuantity = productsInCart.get(e);
            double itemPrice = Store.totalInventory.get(e).getSalesPrice();
            double itemTotal = (itemPrice * (double) saleQuantity);
            receiptTotal += itemTotal;
            Store.totalInventory.get(e).sellItem(saleQuantity);
            System.out.println(e.getName() + "\t" + saleQuantity + "\t" + itemPrice);
        }
        System.out.println("\t\t" + receiptTotal);
        productsInCart.clear();
    }

    //Returns the hashmap for the products in cart
    //This return is mutable but a defensive copy could be returned instead if mutability was a concern
    public HashMap<Product, Integer> getProductsInCart()
    {
        return productsInCart;
    }

    //Overriding the toString method to be more descriptive than the implicit toString() given
    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", date=" + date +
                ", type=" + type +
                ", productsInCart=" + productsInCart +
                '}';
    }
}
