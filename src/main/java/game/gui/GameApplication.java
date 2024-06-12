
package game.gui;

import game.framework.Camera;
import game.framework.SimulationBody;
import game.framework.input.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.world.World;
import org.dyn4j.geometry.*;
import org.dyn4j.world.WorldCollisionData;

import java.util.Iterator;

public class GameApplication extends Application {
    protected Canvas canvas;
    protected final World<SimulationBody> world = new World();
    protected final Camera camera = new Camera();
    protected SimulationBody ball;
    protected boolean stopped;
    protected long lastUpdate = 0;
    protected SimulationBody ground;

    protected ToggleStateKeyboardInputHandler paused;
    protected ToggleStateKeyboardInputHandler step;
    protected BooleanStateKeyboardInputHandler reset;
    protected BooleanStateKeyboardInputHandler resetCamera;
    protected MousePickingInputHandler picking;
    protected MousePanningInputHandler panning;
    protected MouseZoomInputHandler zoom;
    protected ToggleStateKeyboardInputHandler renderContacts;
    protected   ToggleStateKeyboardInputHandler renderBodyAABBs;
    protected ToggleStateKeyboardInputHandler renderBodyRotationRadius;
    protected ToggleStateKeyboardInputHandler renderFixtureAABBs;
    protected ToggleStateKeyboardInputHandler renderFixtureRotationRadius;
    protected ToggleStateKeyboardInputHandler renderBounds;
    protected ToggleStateKeyboardInputHandler printStepNumber;
    protected ToggleStateKeyboardInputHandler printSimulation;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulation");

        // Creazione di un Canvas
        canvas = new Canvas(800, 600);

        // Impostazione del layout
        AnchorPane root = new AnchorPane();
        root.getChildren().add(canvas);
        // Creazione e impostazione della scena
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        //inizializazzione degli input
        this.picking = new MousePickingInputHandler(this.canvas, this.camera, this.world) {
            public void onPickingStart(SimulationBody body) {
                super.onPickingStart(body);
                GameApplication.this.onBodyMousePickingStart(body);
            }
            public void onPickingEnd(SimulationBody body) {
                super.onPickingEnd(body);
                GameApplication.this.onBodyMousePickingEnd(body);
            }
        };
        this.picking.install();
        this.panning = new MousePanningInputHandler(this.canvas);
        this.panning.install();
        this.picking.getDependentBehaviors().add(this.panning);
        this.panning.getDependentBehaviors().add(this.picking);
        this.zoom = new MouseZoomInputHandler(this.canvas, MouseButton.PRIMARY);
        this.zoom.install();
        this.paused = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{32});
        this.step = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{10});
        this.reset = new BooleanStateKeyboardInputHandler(this.canvas, new int[]{82});
        this.resetCamera = new BooleanStateKeyboardInputHandler(this.canvas, new int[]{72});
        this.renderContacts = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{67});
        this.renderBodyAABBs = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{66});
        this.renderBodyRotationRadius = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{66});
        this.renderFixtureAABBs = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{70});
        this.renderFixtureRotationRadius = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{70});
        this.renderBounds = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{90});
        this.paused.install();
        this.step.install();
        this.step.setDependentBehaviorsAdditive(true);
        this.step.getDependentBehaviors().add(this.paused);
        this.reset.install();
        this.resetCamera.install();
        this.renderContacts.install();
        this.renderBodyAABBs.install();
        this.renderBodyRotationRadius.install();
        this.renderFixtureAABBs.install();
        this.renderFixtureRotationRadius.install();
        this.renderBounds.install();
        this.printSimulation = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{96, 48});
        this.printStepNumber = new ToggleStateKeyboardInputHandler(this.canvas, new int[]{97, 49});
        this.printSimulation.install();
        this.printStepNumber.install();
        this.printControls();


        // Inizializza il mondo della fisica
        initPhysics();

        //Inizializzo la camera
        initializeCamera(this.camera);

        // Inizializza il rendering della simulazione
        startAnimation();
    }

    protected void initializeCamera(Camera camera) {
        camera.scale = 46.0;
        camera.offsetX = 0.0;
        camera.offsetY = 0.0;
    }

    private void initPhysics() {

        // Creazione del corpo dinamico (palla)
        ball = new SimulationBody();
        ball.addFixture(Geometry.createCircle(1.0)); // cerchio con raggio 1.0
        ball.setMass(MassType.NORMAL);
        ball.translate(0, 5); // posizionamento iniziale
        world.addBody(ball);

        // Creazione del corpo statico (terra)
        ground = new SimulationBody();
        ground.addFixture(Geometry.createRectangle(canvas.getWidth(), 1)); // rettangolo largo 20 e alto 1
        ground.setMass(MassType.INFINITE);
        ground.translate(0, -6); // posizionamento sotto la palla
        world.addBody(ground);
    }

    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (lastUpdate > 0 && !isPaused()) {
                    double elapsedTime = (now - lastUpdate) / 1_000_000_000.0;
                    updatePhysics(elapsedTime);
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gameLoop(gc, elapsedTime);
                }
                lastUpdate = now;
            }
        };
        timer.start();
    }

    private void updatePhysics(double elapsedTime) {
        // Aggiorna il mondo fisico
        world.update(elapsedTime);
    }

    private void gameLoop(GraphicsContext gc, double elapsedTime) {
        transform(gc);
        clear(gc);

        gc.save();
        gc.translate(camera.offsetX, camera.offsetY);
        render(gc, elapsedTime);
        gc.restore();


        handleEvents();
    }

    private void transform(GraphicsContext gc) {
        int w = (int) gc.getCanvas().getWidth();
        int h = (int) gc.getCanvas().getHeight();
        gc.scale(1, -1); // Effetto flip verticale
        gc.translate(w / 2, h / 2); // Trasla l'origine al centro del canvas
    }

    private void clear(GraphicsContext gc) {
        int w = (int) gc.getCanvas().getWidth();
        int h = (int) gc.getCanvas().getHeight();
        gc.setFill(Color.WHITE);
        gc.fillRect(-w / 2, -h / 2, w, h);
    }


    protected void render(GraphicsContext gc, double elapsedTime) {
        // Impostazioni di rendering
        gc.setLineWidth(1.0); // Imposta la larghezza del tratto
        gc.setLineCap(StrokeLineCap.ROUND); // Imposta il tipo di estremità della linea
        gc.setLineJoin(StrokeLineJoin.ROUND); // Imposta il tipo di unione della linea

        // Rendering dei limiti del mondo
        if (this.renderBounds.isActive()) {
            Bounds bounds = this.world.getBounds();
            if (bounds != null && bounds instanceof AxisAlignedBounds) {
                AxisAlignedBounds aab = (AxisAlignedBounds) bounds;
                AABB aabb = aab.getBounds();
                double minX = aabb.getMinX() * this.camera.scale;
                double minY = aabb.getMinY() * this.camera.scale;
                double width = aabb.getWidth() * this.camera.scale;
                double height = aabb.getHeight() * this.camera.scale;
                gc.setStroke(Color.PURPLE); // Imposta il colore del tratto
                gc.strokeRect(minX, minY, width, height); // Disegna il rettangolo
            }
        }

        // Rendering dei corpi fisici
        for (int i = 0; i < this.world.getBodyCount(); ++i) {
            SimulationBody body = (SimulationBody) this.world.getBody(i);
            render(gc, elapsedTime, body); // Disegna il corpo fisico

            if (this.renderBodyAABBs.isActive()) {
                AABB aabb = this.world.getBroadphaseDetector().getAABB(body);
                double minX = aabb.getMinX() * this.camera.scale;
                double minY = aabb.getMinY() * this.camera.scale;
                double width = aabb.getWidth() * this.camera.scale;
                double height = aabb.getHeight() * this.camera.scale;
                gc.setStroke(Color.CYAN); // Imposta il colore del tratto
                gc.strokeRect(minX, minY, width, height); // Disegna il rettangolo
            }

            if (this.renderBodyRotationRadius.isActive()) {
                Vector2 c = body.getWorldCenter();
                double rr = body.getRotationDiscRadius() * this.camera.scale;
                double centerX = (c.x - rr);
                double centerY = (c.y - rr);
                double radius = rr * 2.0;
                gc.setStroke(Color.PINK); // Imposta il colore del tratto
                gc.strokeOval(centerX, centerY, radius, radius); // Disegna l'ellisse
            }
            Iterator<BodyFixture> fixtureIterator = body.getFixtures().iterator();

            while (fixtureIterator.hasNext()) {
                BodyFixture fixture = fixtureIterator.next();
                if (this.renderFixtureAABBs.isActive()) {
                    AABB aaabb = this.world.getBroadphaseDetector().getAABB(body, fixture);
                    double minX = aaabb.getMinX() * this.camera.scale;
                    double minY = aaabb.getMinY() * this.camera.scale;
                    double width = aaabb.getWidth() * this.camera.scale;
                    double height = aaabb.getHeight() * this.camera.scale;
                    gc.setStroke(Color.CYAN.darker()); // Imposta il colore del tratto
                    gc.strokeRect(minX, minY, width, height); // Disegna il rettangolo
                }

                if (this.renderFixtureRotationRadius.isActive()) {
                    Transform tx = body.getTransform();
                    Vector2 c = tx.getTransformed(fixture.getShape().getCenter());
                    double r = fixture.getShape().getRadius();
                    double centerX = (c.x - r) * this.camera.scale;
                    double centerY = (c.y - r) * this.camera.scale;
                    double radius = r * 2.0 * this.camera.scale;
                    gc.setStroke(Color.MAGENTA); // Imposta il colore del tratto
                    gc.strokeOval(centerX, centerY, radius, radius); // Disegna l'ellisse
                }
            }

        }

        // Rendering dei giunti (joints)
        for (int i = 0; i < this.world.getJointCount(); ++i) {
            Joint<SimulationBody> j = this.world.getJoint(i);
            Line vn;
            double max;
            int red;
            if (j instanceof DistanceJoint) {
                DistanceJoint<SimulationBody> dj = (DistanceJoint<SimulationBody>) j;
                double x1 = dj.getAnchor1().x * this.camera.scale;
                double y1 = dj.getAnchor1().y * this.camera.scale;
                double x2 = dj.getAnchor2().x * this.camera.scale;
                double y2 = dj.getAnchor2().y * this.camera.scale;
                vn = new Line(x1, y1, x2, y2);
                max = dj.getRestDistance();
                double r = Math.abs(max - dj.getAnchor1().distance(dj.getAnchor2())) * 100.0;
                red = (int) Math.floor(Math.min(r, 255.0));
                gc.setStroke(new Color(red / 255.0, 0, 0, 1.0)); // Imposta il colore del tratto
                gc.strokeLine(x1, y1, x2, y2); // Disegna la linea
            } else if (j instanceof PinJoint) {
                PinJoint<SimulationBody> pj = (PinJoint<SimulationBody>) j;
                double x1 = pj.getTarget().x * this.camera.scale;
                double y1 = pj.getTarget().y * this.camera.scale;
                double x2 = pj.getAnchor().x * this.camera.scale;
                double y2 = pj.getAnchor().y * this.camera.scale;
                vn = new Line(x1, y1, x2, y2);
                max = pj.getMaximumSpringForce();
                if (!pj.isSpringEnabled()) {
                    max = pj.getMaximumCorrectionForce();
                }

                double r = pj.getReactionForce(this.world.getTimeStep().getInverseDeltaTime()).getMagnitude();
                red = (int) Math.floor(r / max * 255.0);
                gc.setStroke(new Color(red / 255.0, 0, 0, 1.0)); // Imposta il colore del tratto
                gc.strokeLine(x1, y1, x2, y2); // Disegna la linea
            }
        }


        // Rendering dei contatti
        if (this.renderContacts.isActive()) {
            drawContacts(gc);
        }
    }

    private void drawContacts(GraphicsContext gc) {
        Iterator<WorldCollisionData<SimulationBody>> it = this.world.getCollisionDataIterator();

        while (true) {
            WorldCollisionData wcd;
            do {
                if (!it.hasNext()) {
                    return;
                }

                wcd = (WorldCollisionData) it.next();
            } while (!wcd.isContactConstraintCollision());

            ContactConstraint<SimulationBody> cc = wcd.getContactConstraint();
            Iterator var5 = cc.getContacts().iterator();

            while (var5.hasNext()) {
                SolvedContact c = (SolvedContact) var5.next();
                double r = 2.5 / this.camera.scale;
                double d = r * 2.0;
                double pointX = (c.getPoint().x - r) * this.camera.scale;
                double pointY = (c.getPoint().y - r) * this.camera.scale;
                gc.setFill(Color.ORANGE); // Imposta il colore di riempimento
                gc.fillRect(pointX, pointY, d * this.camera.scale, d * this.camera.scale); // Disegna il rettangolo
                if (!cc.isSensor() && cc.isEnabled()) {
                    double vnd = c.getNormalImpulse() / 2.0;
                    double normalEndX = (c.getPoint().x - cc.getNormal().x * vnd) * this.camera.scale;
                    double normalEndY = (c.getPoint().y - cc.getNormal().y * vnd) * this.camera.scale;
                    gc.setStroke(Color.BLUE); // Imposta il colore del tratto
                    gc.strokeLine(c.getPoint().x * this.camera.scale, c.getPoint().y * this.camera.scale, normalEndX, normalEndY); // Disegna la linea
                    double vtd = c.getTangentialImpulse() / 2.0;
                    double tangentialEndX = (c.getPoint().x - cc.getTangent().x * vtd) * this.camera.scale;
                    double tangentialEndY = (c.getPoint().y - cc.getTangent().y * vtd) * this.camera.scale;
                    gc.setStroke(Color.RED); // Imposta il colore del tratto
                    gc.strokeLine(c.getPoint().x * this.camera.scale, c.getPoint().y * this.camera.scale, tangentialEndX, tangentialEndY); // Disegna la linea
                }
            }
        }
    }

    protected void render(GraphicsContext gc, double elapsedTime, SimulationBody body) {
        Color color = body.getColor();
        if (this.picking.isEnabled() && this.picking.isActive() && this.picking.getBody() == body) {
            color = Color.MAGENTA;
        }

        body.render(gc, this.camera.scale, color);
    }

    protected Vector2 toWorldCoordinates(Point2D p) {
        return this.camera.toWorldCoordinates((double) this.canvas.getWidth(), (double) this.canvas.getHeight(), p);
    }


    protected void handleEvents() {
        if (this.printSimulation.isActive()) {
            this.printSimulation.setActive(false);
            System.out.println(this.toCode());
        }


        if (this.resetCamera.isActiveButNotHandled()) {
            this.resetCamera.setHasBeenHandled(true);
            this.resetCamera();
        }

        Vector2 cameraMove = this.panning.getOffsetAndReset();
        Camera var10000 = this.camera;
        var10000.offsetX += cameraMove.x;
        var10000 = this.camera;
        var10000.offsetY += cameraMove.y;
        double scale = this.zoom.getScaleAndReset();
        var10000 = this.camera;
        var10000.scale *= scale;
        var10000 = this.camera;
        var10000.offsetX *= scale;
        var10000 = this.camera;
        var10000.offsetY *= scale;
        this.picking.updateMousePickingState();
    }

    protected void onBodyMousePickingStart(SimulationBody body) {
    }

    protected void onBodyMousePickingEnd(SimulationBody body) {
    }

    public void stop() {
        this.stopped = true;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public void pause() {
        this.paused.setActive(true);
    }

    public void resume() {
        this.lastUpdate = System.nanoTime();
        this.paused.setActive(false);
    }

    public boolean isPaused() {
        return this.paused.isActive();
    }


    public void resetCamera() {
        this.initializeCamera(this.camera);
    }

    public boolean isMousePickingEnabled() {
        return this.picking.isEnabled();
    }

    public void setMousePickingEnabled(boolean flag) {
        this.picking.setEnabled(flag);
    }

    public boolean isMousePanningEnabled() {
        return this.panning.isEnabled();
    }

    public void setMousePanningEnabled(boolean flag) {
        this.panning.setEnabled(flag);
    }

    public boolean isFixtureAABBDrawingEnabled() {
        return this.renderFixtureAABBs.isActive();
    }

    public void setFixtureAABBDrawingEnabled(boolean flag) {
        this.renderFixtureAABBs.setActive(flag);
    }

    public boolean isBodyAABBDrawingEnabled() {
        return this.renderBodyAABBs.isActive();
    }

    public void setBodyAABBDrawingEnabled(boolean flag) {
        this.renderBodyAABBs.setActive(flag);
    }

    public boolean isFixtureRotationRadiusDrawingEnabled() {
        return this.renderFixtureRotationRadius.isActive();
    }

    public void setFixtureRotationRadiusDrawingEnabled(boolean flag) {
        this.renderFixtureRotationRadius.setActive(flag);
    }

    public boolean isBodyRotationRadiusDrawingEnabled() {
        return this.renderBodyRotationRadius.isActive();
    }

    public void setBodyRotationRadiusDrawingEnabled(boolean flag) {
        this.renderBodyRotationRadius.setActive(flag);
    }

    public boolean isContactDrawingEnabled() {
        return this.renderContacts.isActive();
    }

    public void setContactDrawingEnabled(boolean flag) {
        this.renderContacts.setActive(flag);
    }

    public double getCameraScale() {
        return this.camera.scale;
    }

    public double getCameraOffsetX() {
        return this.camera.offsetX;
    }

    public double getCameraOffsetY() {
        return this.camera.offsetY;
    }
    protected void printControl(String name, String input, String message) {
        System.out.println(String.format("%1$-18s %2$-8s %3$s", name, input, message));
    }

    protected void printControls() {
        System.out.println("Controls:");
        System.out.println("------------------------------------------------------------------------------");
        this.printControl("Name", "Input", "Description");
        System.out.println("------------------------------------------------------------------------------");
        this.printControl("Move", "LMB", "Click & hold the left mouse button to move object");
        this.printControl("Pan", "LMB", "Click & hold the left mouse button anywhere to pan");
        this.printControl("Zoom", "MW", "Mouse wheel up and down to zoom in and out");
        this.printControl("Pause", "Space", "Use the space bar to pause/unpause");
        this.printControl("Step", "Enter", "Use the enter key to step the scene when paused");
        this.printControl("Reset", "r", "Use the r key to reset the simulation");
        this.printControl("Home", "h", "Use the h key to reset the camera");
        this.printControl("Contacts", "c", "Use the c key to toggle drawing of contacts");
        this.printControl("Body Bounds", "b", "Use the b key to toggle drawing of body bounds");
        this.printControl("Fixture Bounds", "f", "Use the f key to toggle drawing of fixture bounds");
        this.printControl("World Bounds", "z", "Use the z key to toggle drawing of world bounds");
        this.printControl("Print Code", "0", "Use the 0 key to print the scene to code");
        this.printControl("Print Step", "1", "Use the 1 key to print the scene step number");
    }

    public String toCode() {
        return CodeExporter.export("Simulation", this.world);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
