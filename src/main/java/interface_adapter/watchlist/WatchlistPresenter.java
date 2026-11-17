package interface_adapter.watchlist;

import use_case.watchlist.WatchlistOutputBoundary;
import use_case.watchlist.WatchlistOutputData;

public class WatchlistPresenter implements WatchlistOutputBoundary {
    private final WatchlistViewModel viewModel;

    public WatchlistPresenter(WatchlistViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(WatchlistOutputData data) {
        viewModel.setMessage(String.format("%s: %,.2f %s", data.getTicker(), data.getPrice(), data.getCurrency()));
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setMessage(errorMessage);
    }
}

