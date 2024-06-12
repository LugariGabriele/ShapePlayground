/*package game.framework;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import game.framework.input.*;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;
import org.dyn4j.world.WorldCollisionData;

public abstract class SimulationFrame extends JFrame {
    private static final long serialVersionUID = 7659608187025022915L;
    public static final double NANO_TO_BASE = 1.0E9;
    protected final Canvas canvas;
    protected final World<SimulationBody> world = new World();
    private boolean stopped;
    private long last;
    private long stepNumber;
    private final Camera camera = new Camera();
    private final ToggleStateKeyboardInputHandler paused;
    private final ToggleStateKeyboardInputHandler step;
    private final BooleanStateKeyboardInputHandler reset;
    private final BooleanStateKeyboardInputHandler resetCamera;
    private final MousePickingInputHandler picking;
    private final MousePanningInputHandler panning;
    private final MouseZoomInputHandler zoom;
    private final ToggleStateKeyboardInputHandler renderContacts;
    private final ToggleStateKeyboardInputHandler renderBodyAABBs;
    private final ToggleStateKeyboardInputHandler renderBodyRotationRadius;
    private final ToggleStateKeyboardInputHandler renderFixtureAABBs;
    private final ToggleStateKeyboardInputHandler renderFixtureRotationRadius;
    private final ToggleStateKeyboardInputHandler renderBounds;
    private final ToggleStateKeyboardInputHandler printStepNumber;
    private final ToggleStateKeyboardInputHandler printSimulation;

    public SimulationFrame(String name) {
        super(name);
        this.setDefaultCloseOperation(3);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                SimulationFrame.this.stop();
                super.windowClosing(e);
            }
        });
        /**

            setting del canvas cioè lo spazio dove ci si disegna


        Dimension size = new Dimension(800, 600);
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(size);
        this.canvas.setMinimumSize(size);
        this.canvas.setMaximumSize(size);
        this.add(this.canvas);
        this.setResizable(false);
        this.pack();
        this.canvas.requestFocus();
        /**
         *
         * settings degli input
         *

        this.picking = new MousePickingInputHandler(this.canvas, this.camera, this.world) {
            public void onPickingStart(SimulationBody body) {
                super.onPickingStart(body);
                SimulationFrame.this.onBodyMousePickingStart(body);
            }

            public void onPickingEnd(SimulationBody body) {
                super.onPickingEnd(body);
                SimulationFrame.this.onBodyMousePickingEnd(body);
            }
        };
        this.picking.install();
        this.panning = new MousePanningInputHandler(this.canvas);
        this.panning.install();
        this.picking.getDependentBehaviors().add(this.panning);
        this.panning.getDependentBehaviors().add(this.picking);
        this.zoom = new MouseZoomInputHandler(this.canvas, 1);
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

    protected abstract void initializeWorld();

    protected void initializeCamera(Camera camera) {
        camera.scale = 16.0;
        camera.offsetX = 0.0;
        camera.offsetY = 0.0;
    }

    protected void initializeSettings() {
    }

    private void initializeSimulation() {
        this.initializeCamera(this.camera);
        this.initializeSettings();
        this.initializeWorld();
    }

    private void start() {
        this.initializeSimulation();
        this.last = System.nanoTime();
        this.canvas.setIgnoreRepaint(true);
        this.canvas.createBufferStrategy(2);
        Thread thread = new Thread() {
            public void run() {
                while(!SimulationFrame.this.isStopped()) {
                    SimulationFrame.this.gameLoop();

                    try {
                        Thread.sleep(5L);
                    } catch (InterruptedException var2) {
                    }
                }

            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    private void gameLoop() {
        Graphics2D g = (Graphics2D)this.canvas.getBufferStrategy().getDrawGraphics();
        this.transform(g);
        this.clear(g);
        long time = System.nanoTime();
        long diff = time - this.last;
        this.last = time;
        double elapsedTime = (double)diff / 1.0E9;
        AffineTransform tx = g.getTransform();
        g.translate(this.camera.offsetX, this.camera.offsetY);
        this.render(g, elapsedTime);
        g.setTransform(tx);
        if (!this.paused.isActive()) {
            boolean stepped = this.world.update(elapsedTime);
            if (stepped) {
                ++this.stepNumber;
            }
        } else if (this.step.isActive()) {
            this.world.step(1);
            ++this.stepNumber;
            this.step.setActive(false);
        }

        this.handleEvents();
        g.dispose();
        BufferStrategy strategy = this.canvas.getBufferStrategy();
        if (!strategy.contentsLost()) {
            strategy.show();
        }

        Toolkit.getDefaultToolkit().sync();
    }

    protected void transform(Graphics2D g) {
        int w = this.canvas.getWidth();
        int h = this.canvas.getHeight();
        AffineTransform yFlip = AffineTransform.getScaleInstance(1.0, -1.0);
        AffineTransform move = AffineTransform.getTranslateInstance((double)(w / 2), (double)(-h / 2));
        g.transform(yFlip);
        g.transform(move);
    }

    protected void clear(Graphics2D g) {
        int w = this.canvas.getWidth();
        int h = this.canvas.getHeight();
        g.setColor(Color.WHITE);
        g.fillRect(-w / 2, -h / 2, w, h);
    }

    protected void render(Graphics2D g, double elapsedTime) {
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AABB aabb;
        Rectangle2D.Double ce;
        if (this.renderBounds.isActive()) {
            Bounds bounds = this.world.getBounds();
            if (bounds != null && bounds instanceof AxisAlignedBounds) {
                AxisAlignedBounds aab = (AxisAlignedBounds)bounds;
                aabb = aab.getBounds();
                ce = new Rectangle2D.Double(aabb.getMinX() * this.camera.scale, aabb.getMinY() * this.camera.scale, aabb.getWidth() * this.camera.scale, aabb.getHeight() * this.camera.scale);
                g.setColor(new Color(128, 0, 128));
                g.draw(ce);
            }
        }

        double r;
        int i;
        for(i = 0; i < this.world.getBodyCount(); ++i) {
            SimulationBody body = (SimulationBody)this.world.getBody(i);
            this.render(g, elapsedTime, body);
            if (this.renderBodyAABBs.isActive()) {
                aabb = this.world.getBroadphaseDetector().getAABB(body);
                ce = new Rectangle2D.Double(aabb.getMinX() * this.camera.scale, aabb.getMinY() * this.camera.scale, aabb.getWidth() * this.camera.scale, aabb.getHeight() * this.camera.scale);
                g.setColor(Color.CYAN);
                g.draw(ce);
            }

            if (this.renderBodyRotationRadius.isActive()) {
                Vector2 c = body.getWorldCenter();
                double rr = body.getRotationDiscRadius();
                Ellipse2D.Double e = new Ellipse2D.Double((c.x - rr) * this.camera.scale, (c.y - rr) * this.camera.scale, rr * 2.0 * this.camera.scale, rr * 2.0 * this.camera.scale);
                g.setColor(Color.PINK);
                g.draw(e);
            }

            Iterator var17 = body.getFixtures().iterator();

            while(var17.hasNext()) {
                BodyFixture fixture = (BodyFixture)var17.next();
                if (this.renderFixtureAABBs.isActive()) {
                    AABB aaabb = this.world.getBroadphaseDetector().getAABB(body, fixture);
                    Rectangle2D.Double cee = new Rectangle2D.Double(aaabb.getMinX() * this.camera.scale, aaabb.getMinY() * this.camera.scale, aaabb.getWidth() * this.camera.scale, aaabb.getHeight() * this.camera.scale);
                    g.setColor(Color.CYAN.darker());
                    g.draw(cee);
                }

                if (this.renderFixtureRotationRadius.isActive()) {
                    Transform tx = body.getTransform();
                    Vector2 c = tx.getTransformed(fixture.getShape().getCenter());
                    r = fixture.getShape().getRadius();
                    Ellipse2D.Double e = new Ellipse2D.Double((c.x - r) * this.camera.scale, (c.y - r) * this.camera.scale, r * 2.0 * this.camera.scale, r * 2.0 * this.camera.scale);
                    g.setColor(Color.MAGENTA);
                    g.draw(e);
                }
            }
        }

        for(i = 0; i < this.world.getJointCount(); ++i) {
            Joint<SimulationBody> j = this.world.getJoint(i);
            Line2D.Double vn;
            double max;
            int red;
            if (j instanceof DistanceJoint) {
                DistanceJoint<SimulationBody> dj = (DistanceJoint)j;
                vn = new Line2D.Double(dj.getAnchor1().x * this.camera.scale, dj.getAnchor1().y * this.camera.scale, dj.getAnchor2().x * this.camera.scale, dj.getAnchor2().y * this.camera.scale);
                max = dj.getRestDistance();
                r = Math.abs(max - dj.getAnchor1().distance(dj.getAnchor2())) * 100.0;
                red = (int)Math.floor(Math.min(r, 255.0));
                g.setColor(new Color(red, 0, 0));
                g.draw(vn);
            } else if (j instanceof PinJoint) {
                PinJoint<SimulationBody> pj = (PinJoint)j;
                vn = new Line2D.Double(pj.getTarget().x * this.camera.scale, pj.getTarget().y * this.camera.scale, pj.getAnchor().x * this.camera.scale, pj.getAnchor().y * this.camera.scale);
                max = pj.getMaximumSpringForce();
                if (!pj.isSpringEnabled()) {
                    max = pj.getMaximumCorrectionForce();
                }

                r = pj.getReactionForce(this.world.getTimeStep().getInverseDeltaTime()).getMagnitude();
                red = (int)Math.floor(r / max * 255.0);
                g.setColor(new Color(red, 0, 0));
                g.draw(vn);
            }
        }

        if (this.renderContacts.isActive()) {
            this.drawContacts(g);
        }

    }

    private void drawContacts(Graphics2D g) {
        Iterator<WorldCollisionData<SimulationBody>> it = this.world.getCollisionDataIterator();

        while(true) {
            WorldCollisionData wcd;
            do {
                if (!it.hasNext()) {
                    return;
                }

                wcd = (WorldCollisionData)it.next();
            } while(!wcd.isContactConstraintCollision());

            ContactConstraint<SimulationBody> cc = wcd.getContactConstraint();
            Iterator var5 = cc.getContacts().iterator();

            while(var5.hasNext()) {
                SolvedContact c = (SolvedContact)var5.next();
                double r = 2.5 / this.camera.scale;
                double d = r * 2.0;
                Rectangle2D.Double cp = new Rectangle2D.Double((c.getPoint().x - r) * this.camera.scale, (c.getPoint().y - r) * this.camera.scale, d * this.camera.scale, d * this.camera.scale);
                g.setColor(Color.ORANGE);
                g.fill(cp);
                if (!cc.isSensor() && cc.isEnabled()) {
                    double vnd = c.getNormalImpulse() / 2.0;
                    Line2D.Double vn = new Line2D.Double(c.getPoint().x * this.camera.scale, c.getPoint().y * this.camera.scale, (c.getPoint().x - cc.getNormal().x * vnd) * this.camera.scale, (c.getPoint().y - cc.getNormal().y * vnd) * this.camera.scale);
                    g.setColor(Color.BLUE);
                    g.draw(vn);
                    double vtd = c.getTangentialImpulse() / 2.0;
                    Line2D.Double vt = new Line2D.Double(c.getPoint().x * this.camera.scale, c.getPoint().y * this.camera.scale, (c.getPoint().x - cc.getTangent().x * vtd) * this.camera.scale, (c.getPoint().y - cc.getTangent().y * vtd) * this.camera.scale);
                    g.setColor(Color.RED);
                    g.draw(vt);
                }
            }
        }
    }

    protected void render(Graphics2D g, double elapsedTime, SimulationBody body) {
        Color color = body.getColor();
        if (this.picking.isEnabled() && this.picking.isActive() && this.picking.getBody() == body) {
            color = Color.MAGENTA;
        }

        body.render(g, this.camera.scale, color);
    }

    protected Vector2 toWorldCoordinates(Point p) {
        return this.camera.toWorldCoordinates((double)this.canvas.getWidth(), (double)this.canvas.getHeight(), p);
    }

  /*  protected void handleEvents() {
        if (this.printSimulation.isActive()) {
            this.printSimulation.setActive(false);
            System.out.println(this.toCode());
        }

        if (this.printStepNumber.isActive()) {
            this.printStepNumber.setActive(false);
            System.out.println("Step #" + this.stepNumber);
        }

        if (this.reset.isActiveButNotHandled()) {
            this.reset.setHasBeenHandled(true);
            this.reset();
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
        this.last = System.nanoTime();
        this.paused.setActive(false);
    }

    public boolean isPaused() {
        return this.paused.isActive();
    }

    public void reset() {
        this.last = System.nanoTime();
        this.stepNumber = 0L;
        this.world.removeAllBodiesAndJoints();
        this.world.removeAllListeners();
        this.initializeSettings();
        this.initializeWorld();
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

    public String toCode() {
        return CodeExporter.export(this.getName(), this.world);
    }

    public void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException var2) {
            var2.printStackTrace();
        } catch (InstantiationException var3) {
            var3.printStackTrace();
        } catch (IllegalAccessException var4) {
            var4.printStackTrace();
        } catch (UnsupportedLookAndFeelException var5) {
            var5.printStackTrace();
        }

        this.setVisible(true);
        this.start();
    }
}
       */
