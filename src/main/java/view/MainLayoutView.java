
package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;

public class MainLayoutView extends BorderPane {

    private final ViewManager viewManager;

    public MainLayoutView(ViewManager viewManager) {
        this.viewManager = viewManager;

        getStyleClass().add("watchlist-root");

        setTop(createHeader());
        setCenter(viewManager);
    }

    private HBox createHeader() {

        Node logo = createLogo();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ToggleGroup navigationGroup = new ToggleGroup();

        HBox tabs = new HBox(6);

        for (String page : new String[]{
                "Graph", "Map", "Backtest", "Watchlist"
        }) {
            ToggleButton button = new ToggleButton(page);

            button.getStyleClass().add("nav-tab");
            button.setToggleGroup(navigationGroup);
            button.setPrefSize(140, 42);

            // JavaFX action listener for navigation
            button.setOnAction(event -> {
                navigationGroup.selectToggle(button);
                viewManager.showView(page);
            });

            tabs.getChildren().add(button);

            if (page.equals("Watchlist")) {
                navigationGroup.selectToggle(button);
            }
        }

        Label profile = new Label("A");
        profile.getStyleClass().add("profile");

        HBox header = new HBox(
                14, logo, spacer, tabs, profile);

        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 12, 16));
        header.getStyleClass().add("header");

        return header;
    }

    private Node createLogo() {

        URL resource = getClass().getResource(
                "/images/agriberg-logo.png");

        if (resource == null) {
            Label label = new Label("Agriberg");
            label.getStyleClass().add("brand");
            return label;
        }

        ImageView logo = new ImageView(
                new Image(resource.toExternalForm()));

        logo.setFitWidth(165);
        logo.setPreserveRatio(true);

        StackPane logoBox = new StackPane(logo);
        logoBox.getStyleClass().add("logo-box");

        return logoBox;
    }
}
