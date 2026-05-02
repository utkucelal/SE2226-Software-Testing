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
        bot = new Bot(TestURL);
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
    void zeroQuantity(){
        bot.addItemToCart(quantity, product);
        bot.setCartQuantity(0);
        assertFalse(bot.isCartEmpty(),"Item was removed from the shopping cart."); //Faillması lazım
    }

    @Test
    @Order(2)
    @DisplayName("product must successfully added to cart.")
    void minoneProductToCart(){
        bot.addItemToCart(quantity, product);
        bot.setCartQuantity(1);
        assertEquals(1,bot.fetchQuantity(),"Item quantity updated successfully");
    }

    @Test
    @Order(3)
    @DisplayName("product must successfully added to cart.")
    void maxoneProductToCart(){
        bot.addItemToCart(quantity, product);
        bot.setCartQuantity(11);
        bot.pagerefresh();
        assertEquals(11,bot.fetchQuantity());
    }

    @Test
    @Order(4)
    @DisplayName("product must successfully added to cart.")
    void maxProductToCart(){
        int overQ = 12;
        bot.addItemToCart(quantity, "B0FFBV27QL");
        bot.setCartQuantity(overQ);
        assertEquals("Out of Stock", bot.getError());
    }
}
