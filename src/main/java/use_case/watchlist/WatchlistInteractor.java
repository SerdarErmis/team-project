package use_case.watchlist;
import entity.Commodity;

import java.util.Optional;

public class WatchlistInteractor implements WatchlistInputBoundary {
    private final WatchlistCommodityDataAccessInterface commodityDAO;
    private final WatchlistOutputBoundary presenter;


    public WatchlistInteractor(WatchlistCommodityDataAccessInterface commodityDAO,
                               WatchlistOutputBoundary presenter) {
        this.commodityDAO = commodityDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(WatchlistInputData input) {
        String ticker = input.getTicker();
        if (ticker.isEmpty()) {
            presenter.prepareFailView("No ticker provided.");
            return;
        }
        Optional<Commodity> maybeComm = commodityDAO.getCommodity(ticker);
        if (maybeComm.isPresent()) {
            Commodity comm = maybeComm.get();
            WatchlistOutputData output = new WatchlistOutputData(comm.getTicker(), comm.getPrice(), comm.getCurrency());
            presenter.prepareSuccessView(output);
        } else {
            presenter.prepareFailView("No commodity exists with ticker " + ticker);
        }
        return;
    }
}
