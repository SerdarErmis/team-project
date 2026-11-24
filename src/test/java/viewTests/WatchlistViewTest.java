package viewTests;

import app.AppBuilder;
import entity.Commodity;
import interface_adapter.watchlist.*;
import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.junit.BeforeClass;
import org.junit.Test;
import use_case.watchlist.*;
import view.*;
import view.WatchlistView.Candle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

public class WatchlistViewTest {
    @BeforeClass
    public static void startToolkit() {
        Platform.startup(() -> Platform.setImplicitExit(false));
    }

    private static void onFx(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void navigationDisplaysRegisteredPages() throws Exception {
        onFx(() -> {
            MainLayoutView layout = new AppBuilder().build();
            ViewManager manager = (ViewManager) layout.getCenter();
            assertTrue(manager.getChildren().get(0) instanceof WatchlistView);
            assertEquals(4, layout.lookupAll(".nav-tab").size());
            for (var node : layout.lookupAll(".nav-tab")) {
                ToggleButton tab = (ToggleButton) node;
                tab.fire();
                assertTrue(tab.isSelected());
                assertEquals(tab.getText() + "View", manager.getChildren().get(0).getClass().getSimpleName());
            }
            assertThrows(IllegalArgumentException.class, () -> manager.showView("Missing"));
        });
    }

    @Test
    public void previewUsesLocalQuotesAndCandles() throws Exception {
        onFx(() -> {
            ViewManager manager = (ViewManager) new AppBuilder().build().getCenter();
            WatchlistView page = (WatchlistView) manager.getChildren().get(0);
            @SuppressWarnings("unchecked")
            ListView<WatchlistView.CommodityRow> list = (ListView<WatchlistView.CommodityRow>) page.lookup(".commodity-list");
            assertEquals(7, list.getItems().size());
            WatchlistView.CommodityRow wheat = list.getSelectionModel().getSelectedItem();
            assertEquals("ZW=F", wheat.ticker);
            assertEquals("544.50", wheat.price);
            assertEquals(1.2, wheat.changePercent, 0.0);
            TextField search = (TextField) page.lookup(".search-bar");
            search.setText("Corn");
            assertEquals(1, list.getItems().size());
            page.selectCommodity("ZC=F");
            search.clear();
            assertEquals("ZC=F", list.getSelectionModel().getSelectedItem().ticker);
            assertTrue(page.lookupAll(".label").stream()
                    .anyMatch(n -> "428.25".equals(((Label) n).getText())));
            Canvas chart = (Canvas) page.lookup(".chart-panel").lookup("Canvas");
            assertTrue(containsColor(chart, Color.web("#50c600")));
            assertTrue(containsColor(chart, Color.web("#ff1616")));
        });
    }

    @Test
    public void selectionDispatchesTickerAndRendersBackgroundState() throws Exception {
        WatchlistViewModel model = new WatchlistViewModel();
        AtomicReference<WatchlistView> page = new AtomicReference<>();
        onFx(() -> {
            List<String> calls = new ArrayList<>();
            WatchlistView view = new WatchlistView(model,
                    new WatchlistController(input -> calls.add("details " + input.getTicker())));
            view.setOnCommoditySelected(ticker -> calls.add("selected " + ticker));
            page.set(view);
            view.setCommodities(List.of(new WatchlistView.CommodityRow("Corn", "ZC=F", "", 0, new double[0])));
            view.selectCommodity("ZC=F");
            assertEquals(List.of("selected ZC=F", "details ZC=F"), calls);
        });
        WatchlistPresenter presenter = new WatchlistPresenter(model);
        presenter.prepareSuccessView(new WatchlistOutputData("ZC=F", 456.5, "USD"));
        onFx(() -> assertTrue(page.get().lookupAll(".label").stream()
                .anyMatch(n -> ((Label)n).getText().equals("456.50 USD"))));
    }

    @Test
    public void chartRespondsToRangeAndSearchCallbacks() throws Exception {
        onFx(() -> {
            WatchlistView page = new WatchlistView();
            var panel = page.lookup(".chart-panel");
            page.setOnSearch(query -> page.setChartVisible(query.isEmpty()));
            TextField search = (TextField) page.lookup(".search-bar");
            search.setText("Corn");
            assertFalse(panel.isManaged());
            assertFalse(panel.isVisible());
            search.clear();
            assertTrue(panel.isManaged());
            assertTrue(panel.isVisible());

            page.setOnTimeRangeSelected(range -> {
                assertEquals("1d", range);
                page.setPriceHistory(List.of(new Candle(10, 14, 9, 13), new Candle(13, 15, 9, 10)));
            });
            ToggleButton day = page.lookupAll(".range-button").stream().map(ToggleButton.class::cast)
                    .filter(button -> button.getText().equals("1d")).findFirst().orElseThrow();
            day.fire();
            Canvas chart = (Canvas) panel.lookup("Canvas");
            assertTrue(containsColor(chart, Color.web("#50c600")));
            assertTrue(containsColor(chart, Color.web("#ff1616")));
        });
    }

    @Test
    public void reusedCellClearsOldTrendForUnavailableQuote() throws Exception {
        onFx(() -> {
            WatchlistView page = new WatchlistView();
            page.setCommodities(List.of(
                    new WatchlistView.CommodityRow("Wheat", "ZW=F", "500", 1, new double[]{2, 4, 3}),
                    new WatchlistView.CommodityRow("Corn", "ZC=F", "Unavailable", Double.NaN, new double[0])));
            @SuppressWarnings("unchecked")
            ListView<WatchlistView.CommodityRow> list = (ListView<WatchlistView.CommodityRow>) page.lookup(".commodity-list");
            ListCell<WatchlistView.CommodityRow> cell = list.getCellFactory().call(list);
            cell.updateListView(list);
            cell.updateIndex(0);
            Canvas sparkline = (Canvas) cell.getGraphic().lookup("Canvas");
            assertTrue(containsColor(sparkline, Color.WHITE));
            cell.updateIndex(1);
            assertSame(sparkline, cell.getGraphic().lookup("Canvas"));
            assertTrue(cell.getGraphic().getStyleClass().contains("neutral-row"));
            assertFalse(containsColor(sparkline, Color.WHITE));
        });
    }

    private static boolean containsColor(Canvas canvas, Color color) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = canvas.snapshot(params, null);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (color.equals(image.getPixelReader().getColor(x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Test
    public void useCasePublishesStructuredStates() {
        WatchlistViewModel model = new WatchlistViewModel();
        List<WatchlistState> events = new ArrayList<>();
        java.beans.PropertyChangeListener listener = e -> events.add((WatchlistState)e.getNewValue());
        model.addPropertyChangeListener(listener);
        WatchlistPresenter presenter = new WatchlistPresenter(model);
        WatchlistInteractor interactor = new WatchlistInteractor(
                Map.of("ZW=F", new Commodity("ZW=F", 12.5, "USD")), presenter);
        interactor.execute(new WatchlistInputData("ZW=F"));
        interactor.execute(new WatchlistInputData("ZC=F"));
        interactor.execute(new WatchlistInputData("  "));
        assertEquals("12.50", events.get(0).formattedPrice);
        assertEquals("ZC=F", events.get(1).ticker);
        assertEquals("No commodity exists with ticker ZC=F", events.get(1).errorMessage);
        assertEquals("No ticker provided.", model.getState().errorMessage);
        model.removePropertyChangeListener(listener);
        interactor.execute(new WatchlistInputData("ZW=F"));
        assertEquals(3, events.size());
    }
}
