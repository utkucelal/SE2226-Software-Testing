import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BVA_QNTY {
    static final String TestURL = "https://www.amazon.com.tr/";
    private static Bot bot;
    String quantity = "8";
    String product = "B07116PMNM";

    @BeforeEach
    void setup() {
        bot = new Bot(TestURL,false);
        bot.connect();
    }

    @AfterEach
    void teardown() {
        bot.quit();
        bot.resetError();
    }
    @Test
    @Order(1)
    @DisplayName("B1 System should display error or invalid input message.")
    void zeroQuantity() throws InterruptedException {
        bot.addItemToCart("11", product,0);
        bot.setCartQuantity(0,1);
        assertFalse(bot.isCartEmpty(),"Item was removed from the shopping cart.");
    }

    @Test
    @Order(2)
    @DisplayName("B2 product must successfully added to cart.")
    void minoneProductToCart() throws InterruptedException{
        bot.addItemToCart(quantity, product,0);
        bot.setCartQuantity(1,1);
        assertEquals(1,bot.fetchQuantity(1),"Item quantity updated successfully");
    }

    @Test
    @Order(3)
    @DisplayName("B3 product must successfully added to cart.")
    void maxoneProductToCart() throws InterruptedException {
        bot.addItemToCart(quantity, product,0);
        bot.setCartQuantity(11,1);
        Thread.sleep(2000);
        bot.pagerefresh();
        assertEquals(11,bot.fetchQuantity(1));
    }

    @Test
    @Order(4)
    @DisplayName("B4 System gives out of stock error and set the quantity to max stock")
    void maxProductToCart() throws InterruptedException{
        int overQ = 12;
        bot.addItemToCart(quantity, "B0FFBV27QL",0);
        bot.setCartQuantity(overQ,1);
        assertEquals("Out of Stock", bot.getError());
    }
}
