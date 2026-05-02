import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ECP_QNTY {
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
    @DisplayName("E1 When the correct quantity of products is added to the cart the system should reflect this accurately and function normally")
    void validQuantity() {
        bot.addItemToCart(quantity, product);
        assertEquals(Integer.parseInt(quantity), bot.fetchQuantity());
    }

    @Test
    @Order(2)
    @DisplayName("U1 When user set the quantity of product 0 system must delete product from cart")
    void zeroQuantity(){
        bot.addItemToCart(quantity, product);
        bot.setCartQuantity(0);
        assertTrue(bot.isCartEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("U2 when user try to set the quantity of product out of the stock limit system set quantity maximum acceptable amount and show warning")
    void maximumQuantity(){
        int overQ = 60;
        bot.addItemToCart(quantity, "B0FFBV27QL");
        bot.setCartQuantity(overQ);
        assertEquals("Out of Stock", bot.getError());
    }

    @Test
    @Order(4)
    @DisplayName("U3 When the user enters a negative amount, the system presents it as a positive number")
    void negativeAmount() {
        bot.addItemToCart("11", "B07116PMNM");
        bot.setCartQuantity(-15);
        assertEquals(15, bot.fetchQuantity());
    }

    @Test
    @Order(5)
    @DisplayName("U4 Input was blocked by the UI; special characters could not be typed")
    void invalidInput() {
        bot.addItemToCart("&", "B07116PMNM");
        assertEquals("not a num", bot.getError());
    }

}
