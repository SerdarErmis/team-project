package entity;

public class Commodity {
    private final String ticker;
    private final double price;
    private final String currency;

    public Commodity(String ticker, double price, String currency) {
        this.ticker = ticker;
        this.price = price;
        this.currency = currency;
    }

    public String getTicker() { return ticker; }
    public double getPrice() { return price; }
    public String getCurrency() { return currency; }
}