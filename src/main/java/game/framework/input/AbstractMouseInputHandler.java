package game.framework.input;


import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.Node;
import javafx.geometry.Point2D;

public abstract class AbstractMouseInputHandler extends AbstractInputHandler implements InputHandler {
    protected final Node component;
    protected final MouseButton button;
    private Point2D dragCurrent;
    private Point2D dragStart;

    public AbstractMouseInputHandler(Node component, MouseButton button) {
        this.component = component;
        this.button = button;

        install();
    }

    public void install() {
        this.component.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        this.component.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        this.component.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        this.component.addEventHandler(ScrollEvent.SCROLL, this::handleMouseWheel);
    }

    public void uninstall() {
        this.component.removeEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        this.component.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        this.component.removeEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        this.component.removeEventHandler(ScrollEvent.SCROLL, this::handleMouseWheel);
    }

    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        if (!flag) {
            this.dragCurrent = null;
            this.dragStart = null;
        }
    }

    protected void onMousePressed(Point2D point) {
    }

    protected void onMouseDrag(Point2D start, Point2D current) {
    }

    protected void onMouseRelease() {
    }

    protected void onMouseWheel(double rotation) {
    }

    private void handleMousePressed(MouseEvent e) {
        if (!e.isConsumed()) {
            if (e.getButton() == this.button) {
                this.dragCurrent = new Point2D(e.getX(), e.getY());
                this.dragStart = this.dragCurrent;
                if (this.isEnabled() && !this.isDependentBehaviorActive()) {
                    this.onMousePressed(this.dragStart);
                }
            }
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (!e.isConsumed()) {
            this.dragCurrent = new Point2D(e.getX(), e.getY());
            if (this.isEnabled() && !this.isDependentBehaviorActive() && this.dragStart != null) {
                this.onMouseDrag(this.dragStart, this.dragCurrent);
            }
        }
    }

    private void handleMouseReleased(MouseEvent e) {
        if (!e.isConsumed()) {
            if (e.getButton() == this.button) {
                this.dragCurrent = null;
                this.dragStart = null;
                if (this.isEnabled() && !this.isDependentBehaviorActive()) {
                    this.onMouseRelease();
                }
            }
        }
    }

    private void handleMouseWheel(ScrollEvent e) {
        if (!e.isConsumed()) {
            double wheelRotation = e.getDeltaY();
            if (this.isEnabled() && !this.isDependentBehaviorActive()) {
                this.onMouseWheel(wheelRotation);
            }
        }
    }
}


