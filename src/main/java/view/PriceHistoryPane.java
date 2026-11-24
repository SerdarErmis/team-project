package view;

import interface_adapter.watchlist.WatchlistState;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import view.WatchlistView.Candle;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

final class PriceHistoryPane extends VBox {
    private static final Color RISING = Color.web("#50c600");
    private static final Color FALLING = Color.web("#ff1616");
    private final Label heading = new Label("COMMODITY PRICE HISTORY");
    private final Label detail = new Label();
    private final Canvas canvas = new Canvas(460, 480);
    private final StackPane plot = new StackPane(canvas);
    private List<Candle> candles = List.of();
    private Consumer<String> rangeHandler = range -> {};

    PriceHistoryPane() {
        super(10);
        getStyleClass().add("chart-panel");
        heading.getStyleClass().add("chart-heading");
        plot.setMinSize(0, 0);
        VBox.setVgrow(plot, Priority.ALWAYS);
        getChildren().addAll(heading, detail, plot, createRangeButtons());
        plot.widthProperty().addListener(observable -> redraw());
        plot.heightProperty().addListener(observable -> redraw());
    }

    private HBox createRangeButtons() {
        HBox buttons = new HBox(5);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        ToggleGroup group = new ToggleGroup();
        for (String range : List.of("1d", "1w", "1m", "1y")) {
            ToggleButton button = new ToggleButton(range);
            button.getStyleClass().add("range-button");
            button.setToggleGroup(group);
            button.setSelected(range.equals("1m"));
            button.setOnAction(event -> rangeHandler.accept(range));
            buttons.getChildren().add(button);
        }
        return buttons;
    }

    void setOnRangeSelected(Consumer<String> handler) {
        rangeHandler = handler;
    }

    void setHeading(String text) {
        heading.setText(text);
    }

    void showDetails(WatchlistState state) {
        if (!state.errorMessage.isEmpty()) {
            detail.setText(state.errorMessage);
        } else {
            detail.setText((state.formattedPrice + " " + state.currency).trim());
        }
    }

    void setCandles(List<Candle> history) {
        candles = List.copyOf(history);
        redraw();
    }

    private void redraw() {
        double width = Math.max(100, plot.getWidth());
        double height = Math.max(100, plot.getHeight());
        canvas.setWidth(width);
        canvas.setHeight(height);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, width, height);
        if (candles.isEmpty()) {
            g.setFill(Color.GRAY);
            g.fillText("Select a commodity", 25, 40);
            return;
        }

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (Candle candle : candles) {
            min = Math.min(min, candle.low);
            max = Math.max(max, candle.high);
        }
        double padding = Math.max(1, (max - min) * 0.1);
        min -= padding;
        max += padding;
        double top = 24;
        double plotHeight = height - 34 - top;
        double right = width - 70;
        double priceRange = max - min;

        g.setStroke(Color.web("#e9edf1"));
        g.setFill(Color.web("#435064"));
        g.setLineWidth(1);
        for (int i = 0; i <= 5; i++) {
            double y = top + i * plotHeight / 5;
            double value = max - i * priceRange / 5;
            g.strokeLine(8, y, right, y);
            g.fillText(String.format(Locale.US, "%.2f", value), right + 7, y + 4);
        }

        double scale = plotHeight / priceRange;
        double step = (right - 16) / candles.size();
        double bodyWidth = Math.max(2, Math.min(11, step * 0.6));
        g.setLineWidth(1.5);
        for (int i = 0; i < candles.size(); i++) {
            Candle candle = candles.get(i);
            double x = 10 + (i + 0.5) * step;
            double openY = top + (max - candle.open) * scale;
            double closeY = top + (max - candle.close) * scale;
            double highY = top + (max - candle.high) * scale;
            double lowY = top + (max - candle.low) * scale;
            Color color = candle.close >= candle.open ? RISING : FALLING;
            g.setStroke(color);
            g.setFill(color);
            g.strokeLine(x, highY, x, lowY);
            g.fillRect(x - bodyWidth / 2, Math.min(openY, closeY),
                    bodyWidth, Math.max(2, Math.abs(closeY - openY)));
        }
    }
}
