package game.framework.input;


import javafx.scene.Node;
import javafx.scene.input.MouseButton;

public final class MouseZoomInputHandler extends AbstractMouseInputHandler implements InputHandler {
    private final Object lock = new Object();
    private double scale = 1.0;

    public MouseZoomInputHandler(Node component, MouseButton button) {
        super(component, button);
    }

    public boolean isActive() {
        return false;
    }

    protected void onMouseWheel(double rotation) {
        super.onMouseWheel(rotation);
        if (rotation != 0.0) {
            synchronized(this.lock) {
                if (rotation > 0.0) {
                    this.scale *= 0.8;
                } else {
                    this.scale *= 1.2;
                }

            }
        }
    }

    public double getScaleAndReset() {
        synchronized(this.lock) {
            double scale = this.scale;
            this.scale = 1.0;
            return scale;
        }
    }
}

