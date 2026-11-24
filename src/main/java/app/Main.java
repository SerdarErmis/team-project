package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new AppBuilder().build(), 1100, 740);
        scene.getStylesheets().add(Main.class.getResource("/styles/watchlist.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Agriberg Terminal");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
