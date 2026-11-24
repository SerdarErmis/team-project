
package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class WatchlistView extends BorderPane {

    // Display-ready data supplied by the presenter
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

    private final TextField searchBar = new TextField();

    private final ObservableList<CommodityRow> items =
            FXCollections.observableArrayList();

    private final ListView<CommodityRow> commodityList =
            new ListView<>(items);

    private final VBox chartPanel = new VBox(10);
    private final StackPane chartArea = new StackPane();
    private final Canvas chartCanvas = new Canvas(460, 480);
    private final Label selectedCommodity = new Label();

    private List<Candle> candles = new ArrayList<>();

    private Consumer<String> searchHandler = query -> {};
    private Consumer<String> selectionHandler = ticker -> {};
    private Consumer<String> rangeHandler = range -> {};

    public WatchlistView() {
        getStyleClass().add("watchlist-root");

        setTop(createHeader());
        setCenter(createContent());

        configureEvents();
    }

    // HEADER

    private HBox createHeader() {
        Label logo = new Label("Agriberg");
        logo.getStyleClass().add("brand");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Group ensures only one tab is selected
        ToggleGroup navigationGroup = new ToggleGroup();

        ToggleButton graph = createTab("Graph", navigationGroup);
        ToggleButton network = createTab("Network", navigationGroup);
        ToggleButton calendar = createTab("Calendar", navigationGroup);
        ToggleButton watchlist = createTab("Watchlist", navigationGroup);

        // Watchlist is selected initially because this is its page
        navigationGroup.selectToggle(watchlist);

        HBox tabs = new HBox(6);
        tabs.getChildren().addAll(
                graph, network, calendar, watchlist
        );

        Label profile = new Label("A");
        profile.getStyleClass().add("profile");

        HBox header = new HBox(14,
                logo, spacer, tabs, profile);

        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 12, 16));
        header.getStyleClass().add("header");

        return header;
    }


    private ToggleButton createTab(String name, ToggleGroup group) {
        ToggleButton tab = new ToggleButton(name);

        tab.getStyleClass().add("nav-tab");
        tab.setToggleGroup(group);
        tab.setAlignment(Pos.CENTER);
        tab.setPrefSize(140, 42);

        return tab;
    }

    // MAIN PAGE
    private HBox createContent() {
        searchBar.setPromptText("Enter Commodity Name");
        searchBar.getStyleClass().add("search-bar");

        Label searchIcon = new Label("⌕");
        searchIcon.getStyleClass().add("search-icon");
        searchIcon.setMouseTransparent(true);

        StackPane searchBox =
                new StackPane(searchBar, searchIcon);

        StackPane.setAlignment(
                searchIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(
                searchIcon, new Insets(0, 12, 0, 0));

        commodityList.setCellFactory(
                list -> createCommodityCell());

        commodityList.setFixedCellSize(44);
        commodityList.getStyleClass().add("commodity-list");
        commodityList.setPlaceholder(
                new Label("No matching commodities"));

        VBox left = new VBox(8, searchBox, commodityList);
        left.setPrefWidth(485);
        left.setMinWidth(320);
        left.setMaxWidth(485);
        VBox.setVgrow(commodityList, Priority.ALWAYS);

        createChartPanel();

        HBox content = new HBox(20, left, chartPanel);
        content.setPadding(new Insets(10, 16, 16, 16));
        HBox.setHgrow(chartPanel, Priority.ALWAYS);

        return content;
    }

    // CUSTOM COMMODITY ROW
    private ListCell<CommodityRow> createCommodityCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(
                    CommodityRow item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label ticker = new Label(item.ticker);
                ticker.getStyleClass().add("row-label");
                ticker.setMinWidth(88);

                Label price = new Label(item.price);
                price.getStyleClass().add("row-label");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Canvas sparkline = new Canvas(84, 30);
                drawSparkline(sparkline, item.trend);

                HBox row = new HBox(5,
                        ticker, price, spacer, sparkline);

                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(4, 18, 4, 18));

                row.getStyleClass().add(
                        item.changePercent >= 0
                                ? "positive-row"
                                : "negative-row"
                );

                setText(null);
                setGraphic(row);
            }
        };
    }

    private void drawSparkline(Canvas canvas,
                               double[] values) {
        if (values.length < 2) return;

        GraphicsContext g = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        double min = values[0];
        double max = values[0];

        for (double value : values) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        double range = Math.max(0.001, max - min);

        g.setStroke(Color.WHITE);
        g.setLineWidth(2);

        g.beginPath();
        for (int i = 0; i < values.length; i++) {
            double x = i * w / (values.length - 1);
            double y = 4 + (max - values[i])
                    / range * (h - 8);

            if (i == 0) g.moveTo(x, y);
            else g.lineTo(x, y);
        }
        g.stroke();
    }

    // LARGE CANDLESTICK CHART
    private void createChartPanel() {
        chartPanel.getStyleClass().add("chart-panel");

        selectedCommodity.setText("COMMODITY PRICE HISTORY");
        selectedCommodity.getStyleClass().add("chart-heading");

        chartArea.getChildren().add(chartCanvas);
        chartArea.setMinSize(0, 0);
        VBox.setVgrow(chartArea, Priority.ALWAYS);

        HBox ranges = new HBox(5);
        ranges.setAlignment(Pos.CENTER_RIGHT);

        ToggleGroup group = new ToggleGroup();

        for (String range : new String[]{"1d", "1w", "1m", "1y"}) {
            ToggleButton button = new ToggleButton(range);
            button.getStyleClass().add("range-button");
            button.setToggleGroup(group);

            if (range.equals("1m")) {
                button.setSelected(true);
            }

            button.setOnAction(event ->
                    rangeHandler.accept(range));

            ranges.getChildren().add(button);
        }

        chartPanel.getChildren().addAll(
                selectedCommodity, chartArea, ranges);

        chartArea.widthProperty().addListener(
                (obs, oldValue, newValue) -> redrawChart());
        chartArea.heightProperty().addListener(
                (obs, oldValue, newValue) -> redrawChart());
    }

    private void redrawChart() {
        double w = Math.max(100, chartArea.getWidth());
        double h = Math.max(100, chartArea.getHeight());

        chartCanvas.setWidth(w);
        chartCanvas.setHeight(h);

        GraphicsContext g = chartCanvas.getGraphicsContext2D();

        g.setFill(Color.WHITE);
        g.fillRect(0, 0, w, h);

        if (candles.isEmpty()) {
            g.setFill(Color.GRAY);
            g.fillText("Select a commodity", 25, 40);
            return;
        }

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        for (Candle candle : candles) {
            min = Math.min(min, candle.low);
            max = Math.max(max, candle.high);
        }

        double padding = Math.max(1, (max - min) * 0.1);
        min -= padding;
        max += padding;

        double top = 24;
        double bottom = h - 34;
        double right = w - 70;
        double plotHeight = bottom - top;
        double priceRange = max - min;

        // Horizontal grid and price labels
        for (int i = 0; i <= 5; i++) {
            double y = top + i * plotHeight / 5;
            double value = max - i * priceRange / 5;

            g.setStroke(Color.web("#e9edf1"));
            g.setLineWidth(1);
            g.strokeLine(8, y, right, y);

            g.setFill(Color.web("#435064"));
            g.fillText(
                    String.format(Locale.US, "%.2f", value),
                    right + 7, y + 4
            );
        }

        double step = (right - 16) / candles.size();
        double width = Math.max(2, Math.min(11, step * 0.6));

        for (int i = 0; i < candles.size(); i++) {
            Candle c = candles.get(i);

            double x = 10 + (i + 0.5) * step;
            double openY = top + (max - c.open)
                    / priceRange * plotHeight;
            double closeY = top + (max - c.close)
                    / priceRange * plotHeight;
            double highY = top + (max - c.high)
                    / priceRange * plotHeight;
            double lowY = top + (max - c.low)
                    / priceRange * plotHeight;

            Color color = Color.web(
                    c.close >= c.open ? "#50c600" : "#ff1616"
            );
            g.setStroke(color);
            g.setFill(color);
            g.setLineWidth(1.5);

            // Wick
            g.strokeLine(x, highY, x, lowY);

            // Candle body
            g.fillRect(
                    x - width / 2,
                    Math.min(openY, closeY),
                    width,
                    Math.max(2, Math.abs(closeY - openY))
            );
        }
    }

    // UI EVENTS
    private void configureEvents() {
        searchBar.textProperty().addListener(
                (obs, oldValue, newValue) ->
                        searchHandler.accept(newValue)
        );

        commodityList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedCommodity.setText(
                                newValue.name + " / "
                                        + newValue.ticker);
                        selectionHandler.accept(newValue.ticker);
                    }
                });
    }

    // PUBLIC METHODS FOR THE CONTROLLER / PRESENTER
    public void setCommodities(List<CommodityRow> newItems) {
        items.setAll(newItems);
    }

    public void selectCommodity(String ticker) {
        for (CommodityRow item : items) {
            if (item.ticker.equals(ticker)) {
                commodityList.getSelectionModel().select(item);
                return;
            }
        }
    }

    public void setPriceHistory(List<Candle> history) {
        candles = new ArrayList<>(history);
        redrawChart();
    }

    public void setChartVisible(boolean visible) {
        chartPanel.setVisible(visible);
        chartPanel.setManaged(visible);
    }

    public void setOnSearch(Consumer<String> handler) {
        searchHandler = handler;
    }

    public void setOnCommoditySelected(Consumer<String> handler) {
        selectionHandler = handler;
    }

    public void setOnTimeRangeSelected(Consumer<String> handler) {
        rangeHandler = handler;
    }
}
