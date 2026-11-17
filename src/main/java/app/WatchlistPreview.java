
package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.WatchlistView;

public class WatchlistPreview extends Application {

    @Override
    public void start(Stage stage) {

        WatchlistView view = new WatchlistView();

        Scene scene = new Scene(view, 1100, 700);
        stage.setTitle("Agriberg Terminal");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
