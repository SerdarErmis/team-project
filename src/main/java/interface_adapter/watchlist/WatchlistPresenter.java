package interface_adapter.watchlist;

import java.util.Locale;
import use_case.watchlist.WatchlistOutputBoundary;
import use_case.watchlist.WatchlistOutputData;

public class WatchlistPresenter implements WatchlistOutputBoundary {
    private final WatchlistViewModel model;

    public WatchlistPresenter(WatchlistViewModel model) {
        this.model = model;
    }

    @Override
    public void prepareSuccessView(WatchlistOutputData data) {
        String price = String.format(Locale.US, "%.2f", data.getPrice());
        model.setState(new WatchlistState(data.getTicker(), price, data.getCurrency(), ""));
    }

    @Override
    public void prepareFailView(String ticker, String error) {
        model.setState(new WatchlistState(ticker, "", "", error));
    }
}
