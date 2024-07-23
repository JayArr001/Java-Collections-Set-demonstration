import java.time.LocalDateTime;

//This application simulates common e-commerce operations, including:
//creating virtual shopping carts, checking out/timing out carts after inactivity
//inventory management including moving units from stock to cart (thus reserving them), plus vice versa
//metadata storage/handling related to items, such as SKU, naming, prices and other thresholds

//This main file is a sample provided to showcase example operations of the program
public class Main
{
    //Simple calls to test and demonstrate functionality of the classes
    public static void main(String[] args)
    {
        //from a design-perspective, it makes sense for the store object to be adding products
        //productCategory is an enum because product categories should be fairly stable/unchanging
        Store store = new Store("Electronics Store");
        store.addNewProduct(1, "Apple TV", "Apple",
                productCategory.tv, 1000, 500,
                500, 999.99);
        store.addNewProduct(2, "Samsung TV", "Samsung",
                productCategory.tv, 2000, 100,
                500, 1099.99);
        store.addNewProduct(3, "Samsung Galaxy", "Samsung",
                productCategory.mobile, 1500, 500,
                500, 1099.99);
        store.addNewProduct(1, "Samsung Galaxy", "Samsung",
                productCategory.mobile, 1500, 500,
                500, 1099.99);
        store.addNewProduct(4, "Google Pixel", "Google",
                productCategory.tv, 1700, 500,
                500, 1299.99);

        LocalDateTime oldDate = LocalDateTime.now().minusDays(3);

        //the saleCart simulates a successfully completed sale and will call checkOutCart()
        //information about the cart and its contents will be printed
        Cart saleCart = store.newShoppingCart(1);

        //abandonedCart will simulate a customer adding items to the cart, but will not check-out
        //this cart will need to be timed out and items returned to stock from reserved status
        Cart abandonedCart = store.newShoppingCart(2, oldDate);

        saleCart.addItem(1, 600);
        saleCart.addItem(2, 10);

        store.checkOutCart(1);

        //reprint stock now that we may have reserved some stuff
        store.listProductsByCategory();

        abandonedCart.addItem(2, 10);
        abandonedCart.addItem(3, 100);

        //reprint again that item counts have changed
        store.listProductsByCategory();

        //timeoutAgedCarts(x) will time out carts that have existed for X days or longer
        //for this example, it will expire the abandonedCart object
        store.timeoutAgedCarts(2);
        store.printAllCarts();
        store.listProductsByCategory();
        store.delistProduct(1);
        store.listProductsByCategory();
        saleCart.addItem(1, 600);
    }

}
