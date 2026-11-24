package use_case.watchlist;

import entity.Commodity;

import java.util.Map;

public class WatchlistInteractor implements WatchlistInputBoundary {
    private final Map<String, Commodity> commodities;
    private final WatchlistOutputBoundary presenter;

    public WatchlistInteractor(Map<String, Commodity> commodities, WatchlistOutputBoundary presenter) {
        this.commodities = Map.copyOf(commodities);
        this.presenter = presenter;
    }

    @Override
    public void execute(WatchlistInputData input) {
        String ticker = input == null ? null : input.getTicker();
        if (ticker == null || ticker.isBlank()) {
            presenter.prepareFailView(ticker, "No ticker provided.");
            return;
        }
        Commodity commodity = commodities.get(ticker.strip());
        if (commodity == null) {
            presenter.prepareFailView(ticker, "No commodity exists with ticker " + ticker);
            return;
        }
        presenter.prepareSuccessView(new WatchlistOutputData(
                commodity.getTicker(), commodity.getPrice(), commodity.getCurrency()));
    }
}
