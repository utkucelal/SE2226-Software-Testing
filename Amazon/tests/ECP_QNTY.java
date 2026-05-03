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
    @DisplayName("E1 When the correct quantity of products is added to the cart the system should reflect this accurately and function normally")
    void validQuantity() throws InterruptedException {
        bot.addItemToCart(quantity, product,0);
        assertEquals(Integer.parseInt(quantity), bot.fetchQuantity(1));
    }

    @Test
    @Order(2)
    @DisplayName("U1 When user set the quantity of product 0 system must delete product from cart")
    void zeroQuantity() throws InterruptedException{
        bot.addItemToCart(quantity, product,0);
        bot.setCartQuantity(0,1);
        assertFalse(bot.isCartEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("U2 when user try to set the quantity of product out of the stock limit system set quantity maximum acceptable amount and show warning")
    void maximumQuantity() throws InterruptedException{
        int overQ = 60;
        bot.addItemToCart(quantity, "B0FFBV27QL",0);
        bot.setCartQuantity(overQ,1);
        assertEquals("Out of Stock", bot.getError());
    }

    @Test
    @Order(4)
    @DisplayName("U3 When the user enters a negative amount, the system presents it as a positive number")
    void negativeAmount() throws InterruptedException{
        bot.addItemToCart("11", "B07116PMNM",0);
        bot.setCartQuantity(-15,1);
        assertEquals(15, bot.fetchQuantity(1));
    }

    @Test
    @Order(5)
    @DisplayName("U4 Input was blocked by the UI; special characters could not be typed")
    void invalidInput() throws InterruptedException{
        bot.addItemToCart("&", "B07116PMNM",0);
        assertEquals("not a num", bot.getError());
    }

}
