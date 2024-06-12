package game.framework.input;


import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.dyn4j.geometry.Vector2;

public final class MousePanningInputHandler extends AbstractMouseInputHandler {
    private final Object lock = new Object();
    private boolean panning = false;
    private Point2D start;
    private double x;
    private double y;

    public MousePanningInputHandler(Node component) {
        super(component, MouseButton.PRIMARY); // Modifica per specificare il pulsante del mouse
    }

    @Override
    protected void onMousePressed(Point2D point) {
        super.onMousePressed(point);
        this.handleMouseStart(point);
    }

    @Override
    protected void onMouseDrag(Point2D start, Point2D current) {
        super.onMouseDrag(start, current);
        this.handleMouseDrag(current);
    }

    @Override
    protected void onMouseRelease() {
        this.clearPanningState();
        super.onMouseRelease();
    }

    @Override
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        if (!flag) {
            this.clearPanningState();
        }
    }

    @Override
    public boolean isActive() {
        return this.panning;
    }

    @Override
    public void uninstall() {
        super.uninstall();
        this.clearPanningState();
    }

    private void handleMouseStart(Point2D start) {
        this.panning = true;
        this.start = start;
    }

    private void handleMouseDrag(Point2D current) {
        this.panning = true;
        double x = current.getX() - this.start.getX();
        double y = current.getY() - this.start.getY();
        synchronized (this.lock) {
            this.x += x;
            this.y -= y;
        }

        this.start = current;
    }

    private void clearPanningState() {
        this.panning = false;
        this.start = null;
    }

    public Vector2 getOffsetAndReset() {
        synchronized (this.lock) {
            Vector2 offset = new Vector2(this.x, this.y);
            this.x = 0.0;
            this.y = 0.0;
            return offset;
        }
    }
}
