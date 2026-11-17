package use_case.watchlist;
import entity.Commodity;
import java.util.Optional;

public interface WatchlistCommodityDataAccessInterface {
    Optional<Commodity> getCommodity(String ticker);
}

