import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UC_CMOC {
    static final String TestURL = "https://www.amazon.com.tr/";
    private static Bot bot;
    String quantity = "1";
    String productOne = "B09NHMC6J6";
    String productTwo = "B07116PMNM";

    @BeforeEach
    void setup() {
        bot = new Bot(TestURL,true);
        bot.connect();
    }

    @AfterEach
    void teardown() {
        bot.quit();
        bot.resetError();
    }

    @Test
    @DisplayName("System must able to be add and manage multiple products")
    void UC_CMOCTest() throws InterruptedException{
        bot.addItemToCart(quantity,productOne,0);
        bot.addItemToCart(quantity,productTwo,0);
        bot.setCartQuantity(2,1);
        bot.setCartQuantity(0,2);
        Thread.sleep(5000);
        bot.checkout();
        bot.fuckGoBack();
    }

}
