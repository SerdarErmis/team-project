package interface_adapter.watchlist;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class WatchlistViewModel {
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private volatile WatchlistState state = new WatchlistState("", "", "", "");

    public WatchlistState getState() {
        return state;
    }

    public synchronized void setState(WatchlistState next) {
        WatchlistState old = state;
        state = next;
        changes.firePropertyChange("state", old, next);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
}
