import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Decision_Table {
    /*
    Prime account session "C:\Program Files\Google\Chrome\Application\chrome.exe"  --remote-debugging-port=9222 --user-data-dir="C:\Chromes\\utku-prime"
    Non-Prime account session "C:\Program Files\Google\Chrome\Application\chrome.exe"  --remote-debugging-port=9222 --user-data-dir="C:\Chromes\alp-no-prime"
    */
    String productBelow350 = "B0F7HBF9CS";
    String productOver350 = "B0FDW39WBK";
    String productOver500 = "B0F4QHY97M";
    int annonceBelow350 = 0;
    int annonceOver350 = 0;
    int annonceOver500 = 0;
    static final String TestURL = "https://www.amazon.com.tr/";
    private static Bot bot;

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
    @DisplayName("Rule 1-2: Conditions: PrimeMember= true,ProductOver350= true Actions: FreeShipping MakeDiscount ")
    @Order(1)
    void ruleOneTwo() throws InterruptedException {
        bot.addItemToCart("1",productOver350, annonceOver350);
        bot.checkout();
        assertTrue(bot.isPrimeCargoFree());
        assertTrue(bot.isDiscounted());
    }

    @Test
    @DisplayName("Rule 3-4: Conditions: PrimeMember= true,ProductOver350= False Actions: FreeShipping")
    @Order(2)
    void ruleThreeFour() throws InterruptedException {
        bot.addItemToCart("1",productBelow350, annonceBelow350);
        bot.checkout();
        assertTrue(bot.isPrimeCargoFree());
        assertFalse(bot.isDiscounted());
    }

    @Test
    @DisplayName("Rule 5-6: Conditions: PrimeMember = False,ProductOver500=True Actions: FreeShipping")
    @Order(3)
    void ruleFivesSix() throws InterruptedException {
        bot.addItemToCart("1",productOver500, annonceOver500);
        bot.checkout();
        bot.confirmAddress();
        assertTrue(bot.isCargoFree());
        assertFalse(bot.isDiscounted());
    }

    @Test
    @DisplayName("Rule 7-8: Conditions: PrimeMember= False, ProductOver500=False Actions: None")
    @Order(4)
    void ruleSevenEight() throws InterruptedException {
        bot.addItemToCart("1",productBelow350, annonceBelow350);
        bot.checkout();
        bot.confirmAddress();
        assertFalse(bot.isCargoFree());
        assertFalse(bot.isDiscounted());
    }


}
