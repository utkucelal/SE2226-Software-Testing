import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UC_FPDC {

    static final String TestURL = "https://www.amazon.com.tr/";
    private static Bot bot;


    @BeforeEach
    void setup() {
        bot = new Bot(TestURL,false); //önceden session başlat "C:\Program Files\Google\Chrome\Application\chrome.exe"  --remote-debugging-port=9222 --user-data-dir="C:\Temp\ChromeDebug"
        bot.connect();
    }

    @AfterEach
    void teardown() {
        bot.quit();
        bot.resetError();
    }

    @Test
    @DisplayName("System recommendations must respect the user filtering choices filter: Apple brand and ascending price order")
    void UC_FPDC_Test(){
        bot.goSmartPhone();
        bot.ascFilterProducts();
        assertTrue(bot.isCorrectFilter());
    }
}

