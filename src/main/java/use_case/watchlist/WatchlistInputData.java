package use_case.watchlist;

public class WatchlistInputData {
    private final String ticker;

    public WatchlistInputData(String ticker) {
        this.ticker = ticker;
    }
    public String getTicker() { return ticker; }
}