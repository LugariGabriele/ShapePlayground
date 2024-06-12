package game.framework.input;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.Node;

public final class BooleanStateMouseInputHandler extends AbstractMouseInputHandler {
    private final Object lock;
    private boolean active;
    private Point2D location;
    private boolean hasBeenHandled;

    public BooleanStateMouseInputHandler(Node component, MouseButton button) {
        super(component, button);
        this.lock=new Object();
    }

    protected void onMousePressed(Point2D point) {
        super.onMousePressed(point);
        synchronized(this.lock) {
            boolean active = this.active;
            this.active = true;
            this.location = point;
            if (!active) {
                this.hasBeenHandled = false;
            }

        }
    }

    protected void onMouseRelease() {
        this.active = false;
        super.onMouseRelease();
    }

    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        if (!flag) {
            this.clearState();
        }

    }

    public void uninstall() {
        super.uninstall();
        this.clearState();
    }

    private void clearState() {
        this.active = false;
        this.location = null;
        this.hasBeenHandled = false;
    }

    public Point2D getMouseLocation() {
        synchronized(this.lock) {
            return this.location;
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isActiveButNotHandled() {
        if (this.hasBeenHandled)
            return false;

        return this.active;
    }

    public void setHasBeenHandled(boolean hasBeenHandled) {
        this.hasBeenHandled = hasBeenHandled;
    }
}

