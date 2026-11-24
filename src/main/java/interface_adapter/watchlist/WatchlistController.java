package interface_adapter.watchlist;

import use_case.watchlist.WatchlistInputBoundary;
import use_case.watchlist.WatchlistInputData;

public class WatchlistController {
    private final WatchlistInputBoundary interactor;

    public WatchlistController(WatchlistInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String ticker) {
        interactor.execute(new WatchlistInputData(ticker));
    }
}

