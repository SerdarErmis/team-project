
package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.WatchlistView;
import view.WatchlistView.CommodityRow;
import view.WatchlistView.Candle;

import java.util.*;
import java.util.stream.Collectors;

public class WatchlistPreview extends Application {

    @Override
    public void start(Stage stage) {
        WatchlistView view = new WatchlistView();

        List<CommodityRow> all = Arrays.asList(
                row("Wheat", "ZW=F", "544.50", 1.2),
                row("Corn", "ZC=F", "428.25", 0.6),
                row("Soybeans", "ZS=F", "1090.75", -0.8),
                row("Oats", "ZO=F", "342.00", 0.4),
                row("Coffee", "KC=F", "315.60", 1.3),
                row("Sugar", "SB=F", "15.80", -1.1),
                row("Cotton", "CT=F", "65.40", 0.7)
        );

        final String[] currentTicker = {"ZW=F"};
        final String[] currentRange = {"1m"};

        view.setOnCommoditySelected(ticker -> {
            currentTicker[0] = ticker;
            view.setPriceHistory(
                    sampleCandles(ticker, currentRange[0]));
            view.setChartVisible(true);
        });

        view.setOnTimeRangeSelected(range -> {
            currentRange[0] = range;
            view.setPriceHistory(
                    sampleCandles(currentTicker[0], range));
        });

        view.setOnSearch(query -> {
            String text = query.toLowerCase().trim();

            List<CommodityRow> filtered = all.stream()
                    .filter(c ->
                            c.name.toLowerCase().contains(text)
                                    || c.ticker.toLowerCase().contains(text))
                    .collect(Collectors.toList());

            view.setCommodities(filtered);

            // Page 5: hide chart during search
            view.setChartVisible(text.isEmpty());

            if (text.isEmpty()) {
                view.selectCommodity(currentTicker[0]);
            }
        });

        view.setCommodities(all);
        view.selectCommodity("ZW=F");

        Scene scene = new Scene(view, 1100, 740);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                                getClass().getResource("/styles/watchlist.css"))
                        .toExternalForm()
        );

        stage.setTitle("Agriberg Terminal — Watchlist");
        stage.setScene(scene);
        stage.show();
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
