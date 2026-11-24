package interface_adapter.watchlist;

public final class WatchlistState {
    public final String ticker;
    public final String formattedPrice;
    public final String currency;
    public final String errorMessage;

    public WatchlistState(String ticker, String formattedPrice, String currency, String errorMessage) {
        this.ticker = ticker;
        this.formattedPrice = formattedPrice;
        this.currency = currency;
        this.errorMessage = errorMessage;
    }
}
