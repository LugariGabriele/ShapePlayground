package game.framework;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;

public final class FXRenderer {
    public FXRenderer() {
    }

    public static final void render(GraphicsContext gc, Shape shape, double scale, Color color) {
        if (shape != null) {
            if (color == null) {
                color = Color.ORANGE;
            }

            if (shape instanceof Circle) {
                render(gc, (Circle) shape, scale, color);
            } else if (shape instanceof Segment) {
                render(gc, (Segment) shape, scale, color);
            }
            // Handle other shape types similarly
        }
    }

    public static final void render(GraphicsContext gc, Circle circle, double scale, Color color) {
        double radius = circle.getRadius();
        Vector2 center = circle.getCenter();
        double radius2 = 2.0 * radius;
        gc.setFill(color);
        gc.fillOval((center.x - radius) * scale, (center.y - radius) * scale, radius2 * scale, radius2 * scale);
        gc.setStroke(getOutlineColor(color));
        gc.strokeOval((center.x - radius) * scale, (center.y - radius) * scale, radius2 * scale, radius2 * scale);
        gc.strokeLine(center.x * scale, center.y * scale, (center.x + radius) * scale, center.y * scale);
    }

    public static final void render(GraphicsContext gc, Segment segment, double scale, Color color) {
        Vector2[] vertices = segment.getVertices();
        gc.setStroke(getOutlineColor(color));
        gc.strokeLine(vertices[0].x * scale, vertices[0].y * scale, vertices[1].x * scale, vertices[1].y * scale);
    }

    private static final Color getOutlineColor(Color color) {
        Color oc = color.darker();
        return new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), color.getOpacity());
    }

    public static final Color getRandomColor() {
        return new Color(Math.random() * 0.5 + 0.5, Math.random() * 0.5 + 0.5, Math.random() * 0.5 + 0.5, 1.0);
    }
}