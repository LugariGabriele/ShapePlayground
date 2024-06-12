package game.framework.input;

import java.util.Iterator;

import game.framework.Camera;
import game.framework.SimulationBody;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.DetectFilter;
import org.dyn4j.world.World;
import org.dyn4j.world.result.DetectResult;

public class MousePickingInputHandler extends AbstractMouseInputHandler implements InputHandler {
    private final Object lock = new Object();
    private final Camera camera;
    private final World<SimulationBody> world;
    private boolean dragging;
    private Vector2 point;
    private SimulationBody body;
    private Joint<SimulationBody> mouseHandle;

    public MousePickingInputHandler(Node component, Camera camera, World<SimulationBody> world) {
        super(component, MouseButton.PRIMARY);
        this.camera = camera;
        this.world = world;
    }


    protected void onMousePressed(Point2D point) {
        super.onMousePressed(point);
        this.handleMouseStartOrDrag(point);
    }

    protected void onMouseDrag(Point2D start, Point2D current) {
        super.onMouseDrag(start, current);
        this.handleMouseStartOrDrag(current);
    }

    protected void onMouseRelease() {
        this.onReleaseCleanUp();
        super.onMouseRelease();
    }

    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        if (!flag) {
            this.onReleaseCleanUp();
        }

    }

    public boolean isActive() {
        return this.dragging;
    }

    public void uninstall() {
        super.uninstall();
        this.onReleaseCleanUp();
    }

    private boolean handleMouseStartOrDrag(Point2D point) {
        Bounds bounds = this.component.getBoundsInLocal();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        Vector2 p = this.camera.toWorldCoordinates(width, height, point);
        synchronized(this.lock) {
            this.point = p;
            if (!this.dragging) {
                SimulationBody body = this.getBodyAt(p);
                if (body != null) {
                    this.dragging = true;
                    this.body = body;
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }


    private void onReleaseCleanUp() {
        synchronized(this.lock) {
            this.point = null;
            this.body = null;
            this.dragging = false;
        }
    }

    public SimulationBody getBody() {
        return this.mouseHandle != null ? (SimulationBody)this.mouseHandle.getBody(0) : null;
    }

    private SimulationBody getBodyAt(Vector2 p) {
        SimulationBody body = null;
        AABB aabb = new AABB(new Vector2(p.x, p.y), 1.0E-4);
        Iterator<DetectResult<SimulationBody, BodyFixture>> it = this.world.detectIterator(aabb, (DetectFilter)null);

        while(it.hasNext()) {
            SimulationBody b = (SimulationBody)((DetectResult)it.next()).getBody();
            if (!b.getMass().isInfinite() && b.contains(p)) {
                body = b;
                break;
            }
        }

        return body;
    }

    private Joint<SimulationBody> createControlJoint(SimulationBody body, Vector2 p) {
        PinJoint<SimulationBody> pj = new PinJoint(body, new Vector2(p.x, p.y));
        pj.setSpringEnabled(true);
        pj.setSpringFrequency(4.0);
        pj.setSpringDamperEnabled(true);
        pj.setSpringDampingRatio(0.3);
        pj.setMaximumSpringForceEnabled(true);
        pj.setMaximumSpringForce(500.0);
        return pj;
    }

    public void updateMousePickingState() {
        boolean dragging = false;
        Vector2 point = null;
        SimulationBody body = null;
        synchronized(this.lock) {
            dragging = this.dragging;
            point = this.point;
            body = this.body;
        }

        Joint joint;
        if (dragging && this.mouseHandle == null && point != null) {
            joint = this.createControlJoint(body, point);
            this.mouseHandle = joint;
            this.world.addJoint(joint);
            this.onPickingStart(body);
        } else if (dragging && this.mouseHandle != null && point != null) {
            joint = this.mouseHandle;
            if (joint instanceof PinJoint) {
                PinJoint<?> pj = (PinJoint)joint;
                pj.setTarget(new Vector2(point.x, point.y));
            }

        } else if (!dragging) {
            if (this.mouseHandle != null) {
                this.world.removeJoint(this.mouseHandle);
                this.onPickingEnd(body);
            }

            this.mouseHandle = null;
        }
    }

    public void onPickingStart(SimulationBody body) {
    }

    public void onPickingEnd(SimulationBody body) {
    }
}

