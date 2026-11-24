package view;

import interface_adapter.watchlist.WatchlistController;
import interface_adapter.watchlist.WatchlistState;
import interface_adapter.watchlist.WatchlistViewModel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.function.Consumer;

public class WatchlistView extends BorderPane implements PropertyChangeListener {
    public static class CommodityRow {
        public final String name;
        public final String ticker;
        public final String price;
        public final double changePercent;
        public final double[] trend;

        public CommodityRow(String name, String ticker,
                            String price, double changePercent,
                            double[] trend) {
            this.name = name;
            this.ticker = ticker;
            this.price = price;
            this.changePercent = changePercent;
            this.trend = trend.clone();
        }
    }

    public static class Candle {
        public final double open;
        public final double high;
        public final double low;
        public final double close;

        public Candle(double open, double high,
                      double low, double close) {
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
        }
    }

    private final ListView<CommodityRow> commodityList = new ListView<>();
    private final PriceHistoryPane history = new PriceHistoryPane();
    private Consumer<String> searchHandler = query -> {};
    private Consumer<String> selectionHandler = ticker -> {};

    public WatchlistView() {
        this(ticker -> {});
    }

    public WatchlistView(WatchlistViewModel model, WatchlistController controller) {
        this(controller::execute);
        model.addPropertyChangeListener(this);
    }

    private WatchlistView(Consumer<String> requestDetails) {
        getStyleClass().add("watchlist-root");
        HBox content = new HBox(20, createSidebar(), history);
        content.setPadding(new Insets(10, 16, 16, 16));
        HBox.setHgrow(history, Priority.ALWAYS);
        setCenter(content);

        commodityList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                history.setHeading(selected.name + " / " + selected.ticker);
                selectionHandler.accept(selected.ticker);
                requestDetails.accept(selected.ticker);
            }
        });
    }

    private VBox createSidebar() {
        commodityList.setCellFactory(list -> new CommodityListCell());
        commodityList.setFixedCellSize(44);
        commodityList.getStyleClass().add("commodity-list");
        commodityList.setPlaceholder(new Label("No matching commodities"));
        VBox sidebar = new VBox(8, createSearchBox(), commodityList);
        sidebar.setMinWidth(320);
        sidebar.setPrefWidth(485);
        sidebar.setMaxWidth(485);
        VBox.setVgrow(commodityList, Priority.ALWAYS);
        return sidebar;
    }

    private StackPane createSearchBox() {
        TextField search = new TextField();
        search.setPromptText("Enter Commodity Name");
        search.getStyleClass().add("search-bar");
        search.textProperty().addListener((obs, old, query) -> searchHandler.accept(query));

        Label icon = new Label("⌕");
        icon.getStyleClass().add("search-icon");
        icon.setMouseTransparent(true);
        StackPane.setAlignment(icon, Pos.CENTER_RIGHT);
        StackPane.setMargin(icon, new Insets(0, 12, 0, 0));
        return new StackPane(search, icon);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (!"state".equals(event.getPropertyName())) {
            return;
        }
        WatchlistState state = (WatchlistState) event.getNewValue();
        if (Platform.isFxApplicationThread()) {
            updateDetails(state);
        } else {
            Platform.runLater(() -> updateDetails(state));
        }
    }

    private void updateDetails(WatchlistState state) {
        CommodityRow selected = commodityList.getSelectionModel().getSelectedItem();
        if (selected != null && selected.ticker.equals(state.ticker)) {
            history.showDetails(state);
        }
    }

    public void setCommodities(List<CommodityRow> rows) {
        commodityList.getItems().setAll(rows);
    }

    public void selectCommodity(String ticker) {
        for (CommodityRow row : commodityList.getItems()) {
            if (row.ticker.equals(ticker)) {
                commodityList.getSelectionModel().select(row);
                return;
            }
        }
    }

    public void setPriceHistory(List<Candle> candles) {
        history.setCandles(candles);
    }

    public void setChartVisible(boolean visible) {
        history.setVisible(visible);
        history.setManaged(visible);
    }

    public void setOnSearch(Consumer<String> handler) {
        searchHandler = handler;
    }

    public void setOnCommoditySelected(Consumer<String> handler) {
        selectionHandler = handler;
    }

    public void setOnTimeRangeSelected(Consumer<String> handler) {
        history.setOnRangeSelected(handler);
    }
}
