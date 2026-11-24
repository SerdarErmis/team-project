package app;

import entity.Commodity;
import interface_adapter.watchlist.WatchlistController;
import interface_adapter.watchlist.WatchlistPresenter;
import interface_adapter.watchlist.WatchlistViewModel;
import use_case.watchlist.WatchlistInteractor;
import view.BacktestView;
import view.GraphView;
import view.MainLayoutView;
import view.MapView;
import view.ViewManager;
import view.WatchlistView;
import view.WatchlistView.Candle;
import view.WatchlistView.CommodityRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class AppBuilder {
    private String ticker = "ZW=F";
    private String period = "1m";

    public MainLayoutView build() {
        List<CommodityRow> rows = List.of(
                row("Wheat", "ZW=F", "544.50", 1.2),
                row("Corn", "ZC=F", "428.25", 0.6),
                row("Soybeans", "ZS=F", "1090.75", -0.8),
                row("Oats", "ZO=F", "342.00", 0.4),
                row("Coffee", "KC=F", "315.60", 1.3),
                row("Sugar", "SB=F", "15.80", -1.1),
                row("Cotton", "CT=F", "65.40", 0.7)
        );

        Map<String, Commodity> samples = rows.stream().collect(Collectors.toMap(
                row -> row.ticker, row -> new Commodity(row.ticker, Double.parseDouble(row.price), "")));
        WatchlistViewModel model = new WatchlistViewModel();
        WatchlistPresenter presenter = new WatchlistPresenter(model);
        WatchlistInteractor selection = new WatchlistInteractor(samples, presenter);
        WatchlistView watchlist = new WatchlistView(model, new WatchlistController(selection));

        watchlist.setOnCommoditySelected(selected -> {
            ticker = selected;
            watchlist.setChartVisible(true);
            watchlist.setPriceHistory(sampleCandles(ticker, period));
        });
        watchlist.setOnTimeRangeSelected(selected -> {
            period = selected;
            watchlist.setPriceHistory(sampleCandles(ticker, period));
        });
        watchlist.setOnSearch(query -> {
            String text = query.trim().toLowerCase(Locale.ROOT);
            watchlist.setCommodities(rows.stream().filter(r -> r.name.toLowerCase(Locale.ROOT).contains(text)
                    || r.ticker.toLowerCase(Locale.ROOT).contains(text)).collect(Collectors.toList()));
            watchlist.setChartVisible(text.isEmpty());
            if (text.isEmpty()) {
                watchlist.selectCommodity(ticker);
            }
        });

        ViewManager manager = new ViewManager();
        manager.addView("Watchlist", watchlist);
        manager.addView("Graph", new GraphView());
        manager.addView("Map", new MapView());
        manager.addView("Backtest", new BacktestView());
        manager.showView("Watchlist");
        watchlist.setCommodities(rows);
        watchlist.selectCommodity(ticker);
        return new MainLayoutView(manager);
    }

    private static CommodityRow row(
            String name, String ticker,
            String price, double change) {

        double[] trend = {
                8, 11, 13, 10, 7, 9, 15, 18, 14, 10
        };

        return new CommodityRow(
                name, ticker, price, change, trend);
    }

    private static List<Candle> sampleCandles(
            String ticker, String range) {

        Random random = new Random(
                (ticker + range).hashCode());

        List<Candle> candles = new ArrayList<>();
        double price = 500;

        for (int i = 0; i < 45; i++) {
            double open = price;
            double close = open + random.nextDouble() * 30 - 15;
            double high = Math.max(open, close)
                    + random.nextDouble() * 10;
            double low = Math.min(open, close)
                    - random.nextDouble() * 10;

            candles.add(new Candle(
                    open, high, low, close));

            price = close;
        }

        return candles;
    }
}
