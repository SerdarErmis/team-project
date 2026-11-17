package interface_adapter.watchlist;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class WatchlistViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String message = "";

    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        String old = this.message;
        this.message = newMessage;
        support.firePropertyChange("message", old, newMessage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}

