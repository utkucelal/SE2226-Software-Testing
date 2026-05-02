public class Main {
    public static void main(String[] args) throws InterruptedException {
        String URL = "https://www.amazon.com.tr/";
        Bot bot = new Bot(URL);
        bot.addItemToCart(4);
       System.out.println(bot.getError());
    }
}
