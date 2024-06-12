package game.framework;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Polygon;
import game.framework.FXRenderer;

public class SimulationBody extends Body {
    protected Color color;

    public SimulationBody() {
        this.color = getRandomColor();
    }

    public SimulationBody(Color color) {
        this.color = color;
    }

    public void render(GraphicsContext gc, double scale) {
        this.render(gc, scale, this.color);
    }

    public void render(GraphicsContext gc, double scale, Color color) {
        Affine ot = gc.getTransform();
        Affine lt = new Affine();
        lt.appendTranslation(this.transform.getTranslationX() * scale, this.transform.getTranslationY() * scale);
        lt.appendRotation(Math.toDegrees(this.transform.getRotationAngle()));
        gc.setTransform(lt);

        for (BodyFixture fixture : this.fixtures) {
            this.renderFixture(gc, scale, fixture, color);
        }

        gc.setFill(Color.WHITE);
        gc.fillOval(this.getLocalCenter().x * scale - 2.0, this.getLocalCenter().y * scale - 2.0, 4.0, 4.0);
        gc.setStroke(Color.DARKGRAY);
        gc.strokeOval(this.getLocalCenter().x * scale - 2.0, this.getLocalCenter().y * scale - 2.0, 4.0, 4.0);

        gc.setTransform(ot);
    }

    protected void renderFixture(GraphicsContext gc, double scale, BodyFixture fixture, Color color) {
        Convex convex = fixture.getShape();
        if (this.isAtRest()) {
            color = color.brighter();
        }
        FXRenderer.render(gc, convex, scale, color);
    }
//
//    private void renderShape(GraphicsContext gc, Convex convex, double scale, Color color) {
//        gc.setFill(color);
//        gc.setStroke(color);
//
//        if (convex instanceof Circle) {
//            Circle circle = (Circle) convex;
//            double radius = circle.getRadius() * scale;
//            double x = circle.getCenter().x * scale - radius;
//            double y = circle.getCenter().y * scale - radius;
//            gc.fillOval(x, y, radius * 2, radius * 2);
//            gc.strokeOval(x, y, radius * 2, radius * 2);
//        } else if (convex instanceof Polygon) {
//            Polygon polygon = (Polygon) convex;
//            double[] xPoints = new double[polygon.getVertices().length];
//            double[] yPoints = new double[polygon.getVertices().length];
//            for (int i = 0; i < polygon.getVertices().length; i++) {
//                xPoints[i] = polygon.getVertices()[i].x * scale;
//                yPoints[i] = polygon.getVertices()[i].y * scale;
//            }
//            gc.fillPolygon(xPoints, yPoints, polygon.getVertices().length);
//            gc.strokePolygon(xPoints, yPoints, polygon.getVertices().length);
//        }
//        // Handle other shape types similarly
//    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private static Color getRandomColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }
}
