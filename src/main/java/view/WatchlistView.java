package view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class WatchlistView extends VBox {

    private final TextField searchBar;

    public WatchlistView() {

        setSpacing(20);
        setPadding(new Insets(30));

        // Title of tab
        Label title = new Label("Watchlist");
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;"
        );

        // Search bar
        searchBar = new TextField();
        searchBar.setPromptText("Search commodities...");
        searchBar.setPrefWidth(400);

        // Add components to the page
        getChildren().addAll(title, searchBar);
    }
}
