package Observables;

import Observers.MyObserver;
import java.util.ArrayList;
import java.util.List;

public abstract class MyObservable {
    private boolean changed;
    private final List<MyObserver> observers;

    public MyObservable() {
        changed = false;
        observers = new ArrayList<>();
    }

    public void setChanged() {
        changed = true;
    }

    public void addObserver(MyObserver ob) {
        if (ob == null) return;
        if (!observers.contains(ob)) observers.add(ob);
    }

    public void removeObserver(MyObserver ob) {
        if (ob == null) return;
        observers.remove(ob);
    }

    public void notifyObservers() {
        if (!changed) return;
        List<MyObserver> snapshot = new ArrayList<>(observers);
        for (MyObserver ob : snapshot) {
            try {
                ob.update();
            } catch (Exception ex) {
                // ignore exceptions to keep notification chain alive
            }
        }
        changed = false;
    }
}