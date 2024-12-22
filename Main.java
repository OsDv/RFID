public class Main {
    public static void main(String[] args) {
        ManageurDesTravailleurs m = new ManageurDesTravailleurs();
        if (!m.setUplistDesTravailleurs())return;
        m.start();
    }

}