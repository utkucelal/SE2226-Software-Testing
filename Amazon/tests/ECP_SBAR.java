import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ECP_SBAR {
    static final String TestURL = "https://www.amazon.com.tr/";
    private static Bot bot;

    @BeforeEach
    void setup() {
        bot = new Bot(TestURL,false);
        bot.connect();
    }

    @AfterEach
    void teardown() {
        bot.quit();
    }

    @Test
    @Order(1)
    @DisplayName("E1 A valid Alphabetical search query must redirect to search results pages")
    public void alphabeticalSearchTest() {
        String query = "laptop";
        bot.makeSearch(query);
        assertEquals("Amazon.com.tr : "+query, bot.getTabtitle());
    }

    @Test
    @Order(2)
    @DisplayName("E2 A valid Alphanumerical search query must redirect to search results pages")
    public void alphanumaricalSearchTest() {
        String query = "RTX 4060";
        bot.makeSearch(query);
        assertEquals("Amazon.com.tr : "+query, bot.getTabtitle());
    }

    @Test
    @Order(3)
    @DisplayName("E3 Search queries with logical expressions return bundled products")
    public void bundleSearchTest() {
        String query = "mouse & keyboard";
        bot.makeSearch(query);
        assertEquals("Amazon.com.tr : "+query, bot.getTabtitle());
    }

    @Test
    @Order(5)
    @DisplayName("U1 System should display a 'Arama sorgunuz için sonuç bulunamadı.' message (FAIL)")
    public void symbolSearchTest() {
        String query = "@@@";
        bot.makeSearch(query);
        assertNotEquals("Amazon.com.tr : "+query, bot.getTabtitle());
    }

    @Test
    @Order(6)
    @DisplayName("U2 null search queries returned site to main page")
    public void emptySearchTest() {
        String query = "";
        bot.makeSearch(query);
        assertEquals("Amazon.com.tr: Elektronik, bilgisayar, akıllı telefon, kitap, oyuncak, yapı market, ev, mutfak, oyun konsolları ürünleri ve daha fazlası için internet alışveriş sitesi", bot.getTabtitle());
    }

    @Test
    @Order(7)
    @DisplayName("U3 very long Search query cause to 414 error")
    public void LongSearchTest() {
        StringBuilder query = new StringBuilder();
        query.repeat("a", 8079); //8040
        bot.makeSearch(query.toString());
        assertEquals("ERROR: The request could not be satisfied", bot.getTabtitle());
    }

    @Test
    @Order(8)
    @DisplayName("U4 injection queries count as plain text")
    public void injectionSearchTest() {
        String query = "SELECT * FROM products";
        bot.makeSearch(query);
        assertEquals("Amazon.com.tr : "+query, bot.getTabtitle());
    }


}
