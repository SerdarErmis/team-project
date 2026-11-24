
package view;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class ViewManager extends StackPane {

    private final Map<String, Node> views = new HashMap<>();

    // Register a page under a name
    public void addView(String name, Node view) {
        views.put(name, view);
    }

    // Change the currently displayed page
    public void showView(String name) {
        Node view = views.get(name);

        if (view == null) {
            throw new IllegalArgumentException(
                    "Unknown view: " + name);
        }

        getChildren().setAll(view);
    }
}
