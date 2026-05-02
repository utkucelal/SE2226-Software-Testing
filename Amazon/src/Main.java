public class Main {
    public static void main(String[] args) throws InterruptedException {
        String URL = "https://www.amazon.com.tr/";
        String quantity = "1";
        String productOne = "B08YK28ZMD";
        String productTwo = "B07116PMNM";
        Bot bot = new Bot(URL,false);
        bot.connect();
        bot.addItemToCart(quantity,productOne,2);
        bot.addItemToCart(quantity,productTwo,0);
        bot.setCartQuantity(3,1);
        bot.setCartQuantity(0,2);
        //bot.quit();

       System.out.println(bot.getError());
    }
}
