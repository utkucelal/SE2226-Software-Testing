public class Main {
    public static void main(String[] args) throws InterruptedException {
        String URL = "https://www.amazon.com.tr/";
        Bot bot = new Bot(URL);
        bot.connect();
        bot.addItemToCart("11", "B07116PMNM");
        bot.setCartQuantity(-15);
        System.out.println(bot.fetchQuantity());
        bot.quit();

       System.out.println(bot.getError());
    }
}
