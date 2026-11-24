package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import view.WatchlistView.CommodityRow;

import java.util.List;
import java.util.Locale;

final class CommodityListCell extends ListCell<CommodityRow> {
    private final Label ticker = new Label();
    private final Label price = new Label();
    private final Label change = new Label();
    private final Canvas sparkline = new Canvas(84, 30);
    private final HBox row;

    CommodityListCell() {
        for (Label label : List.of(ticker, price, change)) {
            label.getStyleClass().add("row-label");
        }
        ticker.setMinWidth(88);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row = new HBox(5, ticker, price, spacer, change, sparkline);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 18, 4, 18));
    }

    @Override
    protected void updateItem(CommodityRow item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        ticker.setText(item.ticker);
        price.setText(item.price);
        double percent = item.changePercent;
        change.setText(Double.isFinite(percent) ? String.format(Locale.US, "%+.2f%%", percent) : "");
        row.getStyleClass().setAll(rowStyle(percent));
        drawSparkline(item.trend);
        setGraphic(row);
    }

    private static String rowStyle(double percent) {
        if (!Double.isFinite(percent) || percent == 0) {
            return "neutral-row";
        }
        return percent > 0 ? "positive-row" : "negative-row";
    }

    private void drawSparkline(double[] values) {
        GraphicsContext g = sparkline.getGraphicsContext2D();
        double width = sparkline.getWidth();
        double height = sparkline.getHeight();
        g.clearRect(0, 0, width, height);
        if (values.length < 2) {
            return;
        }

        double min = values[0];
        double max = values[0];
        for (double value : values) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        double scale = (height - 8) / Math.max(0.001, max - min);
        g.setStroke(Color.WHITE);
        g.setLineWidth(2);
        g.beginPath();
        g.moveTo(0, 4 + (max - values[0]) * scale);
        for (int i = 1; i < values.length; i++) {
            double x = i * width / (values.length - 1);
            g.lineTo(x, 4 + (max - values[i]) * scale);
        }
        g.stroke();
    }
}
