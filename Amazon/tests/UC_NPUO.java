import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UC_NPUO {

    static final String TestURL = "https://www.amazon.com.tr/";
    private static Bot bot;
    String quantity = "1";
    String product = "B00KC8N098";

    @BeforeEach
    void setup() {
        bot = new Bot(TestURL,true); //önceden session başlat "C:\Program Files\Google\Chrome\Application\chrome.exe"  --remote-debugging-port=9222 --user-data-dir="C:\Temp\ChromeDebug"
        bot.connect();
    }

    @AfterEach
    void teardown() {
        bot.quit();
        bot.resetError();
    }

    @Test
    @DisplayName("If the user has a Prime membership, the system always offers free shipping on all orders")
    void UC_NPUO_Test() throws InterruptedException{
        bot.openProduct(product);
        assertFalse(bot.isPrime(),"product have prime feature");
        bot.addItemToCart(quantity,product,2);
        bot.checkout();
        //Thread.sleep(1000);
        bot.confirmAddress();
        assertFalse(bot.isPrimeCargoFree(),"product have free shipping feature");
    }

}
