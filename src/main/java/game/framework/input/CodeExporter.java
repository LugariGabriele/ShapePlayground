package game.framework.input;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.dyn4j.Epsilon;
import org.dyn4j.Version;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.broadphase.AABBExpansionMethod;
import org.dyn4j.collision.broadphase.AABBProducer;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseFilter;
import org.dyn4j.collision.broadphase.CollisionItemAABBProducer;
import org.dyn4j.collision.broadphase.CollisionItemBroadphaseDetector;
import org.dyn4j.collision.broadphase.CollisionItemBroadphaseFilter;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.NullAABBExpansionMethod;
import org.dyn4j.collision.broadphase.Sap;
import org.dyn4j.collision.broadphase.StaticValueAABBExpansionMethod;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.ContinuousDetectionMode;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

public class CodeExporter {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String TAB1 = "  ";
    private static final String TAB2 = "    ";
    private static final String TAB3 = "      ";

    public CodeExporter() {
    }

    public static final String export(String name, World<?> world) {
        StringBuilder sb = new StringBuilder();
        Map<Object, String> idNameMap = new HashMap();
        sb.append("import java.util.*;").append(NEW_LINE).append("import org.dyn4j.collision.*;").append(NEW_LINE).append("import org.dyn4j.collision.broadphase.*;").append(NEW_LINE).append("import org.dyn4j.collision.continuous.*;").append(NEW_LINE).append("import org.dyn4j.collision.manifold.*;").append(NEW_LINE).append("import org.dyn4j.collision.narrowphase.*;").append(NEW_LINE).append("import org.dyn4j.dynamics.*;").append(NEW_LINE).append("import org.dyn4j.dynamics.joint.*;").append(NEW_LINE).append("import org.dyn4j.geometry.*;").append(NEW_LINE).append(NEW_LINE).append("// ").append(world.getUserData()).append(NEW_LINE).append("// generated for dyn4j v").append(Version.getVersion()).append(NEW_LINE).append("public class ").append(name).append(" { ").append(NEW_LINE).append(NEW_LINE).append("  ").append("private ").append(name).append("() {}").append(NEW_LINE).append(NEW_LINE).append("  ").append("public static final void setup(World world) {").append(NEW_LINE).append("    ").append("Settings settings = world.getSettings();").append(NEW_LINE);
        sb.append(export(world.getSettings()));
        sb.append(NEW_LINE);
        Vector2 g = world.getGravity();
        if (g != World.EARTH_GRAVITY && !g.equals(0.0, -9.8)) {
            if (g != World.ZERO_GRAVITY && !g.isZero()) {
                sb.append("    ").append("world.setGravity(").append(export(g)).append(");").append(NEW_LINE);
            } else {
                sb.append("    ").append("world.setGravity(World.ZERO_GRAVITY);").append(NEW_LINE);
            }
        }

        CollisionItemBroadphaseDetector<?, ?> bpd = world.getBroadphaseDetector();
        AABBProducer<?> ap = bpd.getAABBProducer();
        AABBExpansionMethod<?> em = bpd.getAABBExpansionMethod();
        BroadphaseFilter<?> bpf = bpd.getBroadphaseFilter();
        BroadphaseDetector<?> bp = bpd.getDecoratedBroadphaseDetector();
        if (!(ap instanceof CollisionItemAABBProducer)) {
            throw new UnsupportedOperationException("The class " + ap.getClass().getName() + " is not known.");
        } else {
            sb.append("    ").append("AABBProducer<CollisionItem<Body, BodyFixture>> aabbProducer = new CollisionItemAABBProducer<Body, BodyFixture>();").append(NEW_LINE);
            if (em instanceof StaticValueAABBExpansionMethod) {
                StaticValueAABBExpansionMethod<?> method = (StaticValueAABBExpansionMethod)em;
                sb.append("    ").append("AABBExpansionMethod<CollisionItem<Body, BodyFixture>> aabbExpansionMethod = new StaticValueAABBExpansionMethod<CollisionItem<Body, BodyFixture>>(" + method.getExpansion() + ");").append(NEW_LINE);
            } else {
                if (!(em instanceof NullAABBExpansionMethod)) {
                    throw new UnsupportedOperationException("The class " + em.getClass().getName() + " is not known.");
                }

                sb.append("    ").append("AABBExpansionMethod<CollisionItem<Body, BodyFixture>> aabbExpansionMethod = new NullAABBExpansionMethod<CollisionItem<Body, BodyFixture>>();").append(NEW_LINE);
            }

            if (!(bpf instanceof CollisionItemBroadphaseFilter)) {
                throw new UnsupportedOperationException("The class " + bpf.getClass().getName() + " is not known.");
            } else {
                sb.append("    ").append("BroadphaseFilter<CollisionItem<Body, BodyFixture>> broadphaseFilter = new CollisionItemBroadphaseFilter<Body, BodyFixture>();").append(NEW_LINE);
                if (bp instanceof Sap) {
                    sb.append("    ").append("BroadphaseDetector<CollisionItem<Body, BodyFixture>> bp = new Sap<CollisionItem<Body, BodyFixture>>(broadphaseFilter, aabbProducer, aabbExpansionMethod);").append(NEW_LINE);
                } else {
                    if (!(bp instanceof DynamicAABBTree)) {
                        throw new UnsupportedOperationException("The class " + bp.getClass().getName() + " is not known.");
                    }

                    sb.append("    ").append("BroadphaseDetector<CollisionItem<Body, BodyFixture>> bp = new DynamicAABBTree<CollisionItem<Body, BodyFixture>>(broadphaseFilter, aabbProducer, aabbExpansionMethod);").append(NEW_LINE);
                }

                sb.append("    ").append("CollisionItemBroadphaseDetector<Body, BodyFixture> bpd = new CollisionItemBroadphaseDetectorAdapter<Body, BodyFixture>(bp);").append(NEW_LINE);
                sb.append("    ").append("world.setBroadphaseDetector(bpd);").append(NEW_LINE);
                NarrowphaseDetector npd = world.getNarrowphaseDetector();
                if (npd instanceof Sat) {
                    sb.append("    ").append("world.setNarrowphaseDetector(new Sat());").append(NEW_LINE);
                } else if (!(npd instanceof Gjk)) {
                    throw new UnsupportedOperationException("The class " + npd.getClass().getName() + " is not known.");
                }

                ManifoldSolver msr = world.getManifoldSolver();
                if (!(msr instanceof ClippingManifoldSolver)) {
                    throw new UnsupportedOperationException("The class " + msr.getClass().getName() + " is not known.");
                } else {
                    sb.append("    ").append("world.setManifoldSolver(new ClippingManifoldSolver());").append(NEW_LINE);
                    TimeOfImpactDetector tid = world.getTimeOfImpactDetector();
                    if (!(tid instanceof ConservativeAdvancement)) {
                        throw new UnsupportedOperationException("The class " + tid.getClass().getName() + " is not known.");
                    } else {
                        sb.append("    ").append("world.setTimeOfImpactDetector(new ConservativeAdvancement());").append(NEW_LINE);
                        Bounds bounds = world.getBounds();
                        if (bounds != null) {
                            if (!(bounds instanceof AxisAlignedBounds)) {
                                throw new UnsupportedOperationException("The class " + bounds.getClass().getName() + " is not known.");
                            }

                            AxisAlignedBounds aab = (AxisAlignedBounds)bounds;
                            double w = aab.getWidth();
                            double h = aab.getHeight();
                            sb.append(NEW_LINE).append("    ").append("AxisAlignedBounds bounds = new AxisAlignedBounds(").append(w).append(", ").append(h).append(");").append(NEW_LINE);
                            if (!aab.getTranslation().isZero()) {
                                sb.append("    ").append("bounds.translate(").append(export(aab.getTranslation())).append(");").append(NEW_LINE);
                            }

                            sb.append("    ").append("world.setBounds(bounds);").append(NEW_LINE).append(NEW_LINE);
                        }

                        int bSize = world.getBodyCount();

                        int i;
                        for(i = 1; i < bSize + 1; ++i) {
                            Body body = (Body)world.getBody(i - 1);
                            idNameMap.put(body, "body" + i);
                            Mass mass = body.getMass();
                            sb.append("    ").append("// body user data: ").append(body.getUserData()).append(NEW_LINE).append("    ").append("GameObject body").append(i).append(" = new GameObject();").append(NEW_LINE);
                            int fSize = body.getFixtureCount();

                            for(int j = 0; j < fSize; ++j) {
                                BodyFixture bf = (BodyFixture)body.getFixture(j);
                                sb.append("    ").append("{// fixture user data: ").append(bf.getUserData()).append(NEW_LINE).append(export(bf.getShape(), "      ")).append("      ").append("BodyFixture bf = new BodyFixture(c);").append(NEW_LINE);
                                if (bf.isSensor()) {
                                    sb.append("      ").append("bf.setSensor(").append(bf.isSensor()).append(");").append(NEW_LINE);
                                }

                                if (bf.getDensity() != 1.0) {
                                    sb.append("      ").append("bf.setDensity(").append(bf.getDensity()).append(");").append(NEW_LINE);
                                }

                                if (bf.getFriction() != 0.2) {
                                    sb.append("      ").append("bf.setFriction(").append(bf.getFriction()).append(");").append(NEW_LINE);
                                }

                                if (bf.getRestitution() != 0.0) {
                                    sb.append("      ").append("bf.setRestitution(").append(bf.getRestitution()).append(");").append(NEW_LINE);
                                }

                                if (bf.getRestitutionVelocity() != 1.0) {
                                    sb.append("      ").append("bf.setRestitutionVelocity(").append(bf.getRestitutionVelocity()).append(");").append(NEW_LINE);
                                }

                                sb.append(export(bf.getFilter(), "      ")).append("      ").append("body").append(i).append(".addFixture(bf);").append(NEW_LINE).append("    ").append("}").append(NEW_LINE);
                            }

                            if (Math.abs(body.getTransform().getRotationAngle()) > Epsilon.E) {
                                sb.append("    ").append("body").append(i).append(".rotate(Math.toRadians(").append(Math.toDegrees(body.getTransform().getRotationAngle())).append("));").append(NEW_LINE);
                            }

                            if (!body.getTransform().getTranslation().isZero()) {
                                sb.append("    ").append("body").append(i).append(".translate(").append(export(body.getTransform().getTranslation())).append(");").append(NEW_LINE);
                            }

                            if (!body.getLinearVelocity().isZero()) {
                                sb.append("    ").append("body").append(i).append(".setLinearVelocity(").append(export(body.getLinearVelocity())).append(");").append(NEW_LINE);
                            }

                            if (Math.abs(body.getAngularVelocity()) > Epsilon.E) {
                                sb.append("    ").append("body").append(i).append(".setAngularVelocity(Math.toRadians(").append(Math.toDegrees(body.getAngularVelocity())).append("));").append(NEW_LINE);
                            }

                            if (!body.isEnabled()) {
                                sb.append("    ").append("body").append(i).append(".setEnabled(false);").append(NEW_LINE);
                            }

                            if (body.isAtRest()) {
                                sb.append("    ").append("body").append(i).append(".setAtRest(true);").append(NEW_LINE);
                            }

                            if (!body.isAtRestDetectionEnabled()) {
                                sb.append("    ").append("body").append(i).append(".setAtRestDetectionEnabled(false);").append(NEW_LINE);
                            }

                            if (body.isBullet()) {
                                sb.append("    ").append("body").append(i).append(".setBullet(true);").append(NEW_LINE);
                            }

                            if (body.getLinearDamping() != 0.0) {
                                sb.append("    ").append("body").append(i).append(".setLinearDamping(").append(body.getLinearDamping()).append(");").append(NEW_LINE);
                            }

                            if (body.getAngularDamping() != 0.01) {
                                sb.append("    ").append("body").append(i).append(".setAngularDamping(").append(body.getAngularDamping()).append(");").append(NEW_LINE);
                            }

                            if (body.getGravityScale() != 1.0) {
                                sb.append("    ").append("body").append(i).append(".setGravityScale(").append(body.getGravityScale()).append(");").append(NEW_LINE);
                            }

                            sb.append("    ").append("body").append(i).append(".setMass(").append(export(mass)).append(");").append(NEW_LINE).append("    ").append("body").append(i).append(".setMassType(MassType.").append(mass.getType()).append(");").append(NEW_LINE);
                            if (!body.getAccumulatedForce().isZero()) {
                                sb.append("    ").append("body").append(i).append(".applyForce(").append(export(body.getAccumulatedForce())).append(");").append(NEW_LINE);
                            }

                            if (Math.abs(body.getAccumulatedTorque()) > Epsilon.E) {
                                sb.append("    ").append("body").append(i).append(".applyTorque(").append(body.getAccumulatedTorque()).append(");").append(NEW_LINE);
                            }

                            sb.append("    ").append("world.addBody(body").append(i).append(");").append(NEW_LINE).append(NEW_LINE);
                        }

                        i = world.getJointCount();

                        for(int j = 1; j < j + 1; ++j) {
                            Joint<?> joint = world.getJoint(j - 1);
                            sb.append("    ").append("// ").append(joint.getUserData()).append(NEW_LINE);
                            Body body1;
                            Body body2;
                            if (joint instanceof AngleJoint) {
                                AngleJoint<?> aj = (AngleJoint)joint;
                                body1 = (Body)aj.getBody1();
                                body2 = (Body)aj.getBody2();
                                sb.append("    ").append("AngleJoint joint").append(j).append(" = new AngleJoint(").append((String)idNameMap.get(body1)).append(", ").append((String)idNameMap.get(body2)).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLimits(Math.toRadians(").append(Math.toDegrees(aj.getLowerLimit())).append("), Math.toRadians(").append(Math.toDegrees(aj.getUpperLimit())).append("));").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLimitEnabled(").append(aj.isLimitsEnabled()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLimitsReferenceAngle(Math.toRadians(").append(Math.toDegrees(aj.getLimitsReferenceAngle())).append("));").append(NEW_LINE).append("    ").append("joint").append(j).append(".setRatio(").append(aj.getRatio()).append(");").append(NEW_LINE);
                            } else if (joint instanceof DistanceJoint) {
                                DistanceJoint<?> dj = (DistanceJoint)joint;
                                body1 = (Body)dj.getBody1();
                                body2 = (Body)dj.getBody2();
                                sb.append("    ").append("DistanceJoint joint").append(j).append(" = new DistanceJoint(").append((String)idNameMap.get(body1)).append(", ").append((String)idNameMap.get(body2)).append(", ").append(export(dj.getAnchor1())).append(", ").append(export(dj.getAnchor2())).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setSpringFrequency(").append(dj.getSpringFrequency()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setSpringDampingRatio(").append(dj.getSpringDampingRatio()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setRestDistance(").append(dj.getRestDistance()).append(");").append(NEW_LINE);
                            } else if (joint instanceof FrictionJoint) {
                                FrictionJoint<?> fj = (FrictionJoint)joint;
                                body1 = (Body)fj.getBody1();
                                body2 = (Body)fj.getBody2();
                                sb.append("    ").append("FrictionJoint joint").append(j).append(" = new FrictionJoint(").append((String)idNameMap.get(body1)).append(", ").append((String)idNameMap.get(body2)).append(", ").append(export(fj.getAnchor1())).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMaximumForce(").append(fj.getMaximumForce()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMaximumTorque(").append(fj.getMaximumTorque()).append(");").append(NEW_LINE);
                            } else if (joint instanceof PinJoint) {
                                PinJoint<?> mj = (PinJoint)joint;
                                body1 = (Body)mj.getBody();
                                sb.append("    ").append("PinJoint joint").append(j).append(" = new PinJoint(").append((String)idNameMap.get(body1)).append(", ").append(export(mj.getAnchor())).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setSpringFrequency(").append(mj.getSpringFrequency()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setSpringDampingRatio(").append(mj.getSpringDampingRatio()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMaximumSpringForce(").append(mj.getMaximumSpringForce()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setTarget(").append(export(mj.getAnchor())).append(");").append(NEW_LINE);
                            } else if (joint instanceof PrismaticJoint) {
                                PrismaticJoint<?> pj = (PrismaticJoint)joint;
                                body1 = (Body)pj.getBody1();
                                body2 = (Body)pj.getBody2();
                                sb.append("    ").append("PrismaticJoint joint").append(j).append(" = new PrismaticJoint(").append((String)idNameMap.get(body1)).append(", ").append((String)idNameMap.get(body2)).append(", ").append(export(pj.getAnchor1())).append(", ").append(export(pj.getAxis())).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLowerLimitEnabled(").append(pj.isLowerLimitEnabled()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setUpperLimitEnabled(").append(pj.isUpperLimitEnabled()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLimits(").append(pj.getLowerLimit()).append(", ").append(pj.getUpperLimit()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setReferenceAngle(Math.toRadians(").append(Math.toDegrees(pj.getReferenceAngle())).append("));").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMotorEnabled(").append(pj.isMotorEnabled()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMotorSpeed(").append(pj.getMotorSpeed()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMaximumMotorForce(").append(pj.getMaximumMotorForce()).append(");").append(NEW_LINE);
                            } else if (joint instanceof PulleyJoint) {
                                PulleyJoint<?> pj = (PulleyJoint)joint;
                                body1 = (Body)pj.getBody1();
                                body2 = (Body)pj.getBody2();
                                sb.append("    ").append("PulleyJoint joint").append(j).append(" = new PulleyJoint(").append((String)idNameMap.get(body1)).append(", ").append((String)idNameMap.get(body2)).append(", ").append(export(pj.getPulleyAnchor1())).append(", ").append(export(pj.getPulleyAnchor2())).append(", ").append(export(pj.getAnchor1())).append(", ").append(export(pj.getAnchor2())).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setRatio(").append(pj.getRatio()).append(");").append(NEW_LINE);
                            } else if (joint instanceof RevoluteJoint) {
                                RevoluteJoint<?> rj = (RevoluteJoint)joint;
                                body1 = (Body)rj.getBody1();
                                body2 = (Body)rj.getBody2();
                                sb.append("    ").append("RevoluteJoint joint").append(j).append(" = new RevoluteJoint(").append((String)idNameMap.get(body1)).append(", ").append((String)idNameMap.get(body2)).append(", ").append(export(rj.getAnchor1())).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLimitsEnabled(").append(rj.isLimitsEnabled()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLimits(Math.toRadians(").append(Math.toDegrees(rj.getLowerLimit())).append("), Math.toRadians(").append(Math.toDegrees(rj.getUpperLimit())).append("));").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLimitsReferenceAngle(Math.toRadians(").append(Math.toDegrees(rj.getLimitsReferenceAngle())).append("));").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMotorEnabled(").append(rj.isMotorEnabled()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMotorSpeed(Math.toRadians(").append(Math.toDegrees(rj.getMotorSpeed())).append("));").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMaximumMotorTorque(").append(rj.getMaximumMotorTorque()).append(");").append(NEW_LINE);
                            } else if (joint instanceof WeldJoint) {
                                WeldJoint<?> wj = (WeldJoint)joint;
                                body1 = (Body)wj.getBody1();
                                body2 = (Body)wj.getBody2();
                                sb.append("    ").append("WeldJoint joint").append(j).append(" = new WeldJoint(").append((String)idNameMap.get(body1)).append(", ").append((String)idNameMap.get(body2)).append(", ").append(export(wj.getAnchor1())).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setSpringFrequency(").append(wj.getSpringFrequency()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setSpringDampingRatio(").append(wj.getSpringDampingRatio()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLimitsReferenceAngle(Math.toRadians(").append(Math.toDegrees(wj.getLimitsReferenceAngle())).append("));").append(NEW_LINE);
                            } else if (joint instanceof WheelJoint) {
                                WheelJoint<?> wj = (WheelJoint)joint;
                                body1 = (Body)wj.getBody1();
                                body2 = (Body)wj.getBody2();
                                sb.append("    ").append("WheelJoint joint").append(j).append(" = new WheelJoint(").append((String)idNameMap.get(body1)).append(", ").append((String)idNameMap.get(body2)).append(", ").append(export(wj.getAnchor1())).append(", ").append(export(wj.getAxis())).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setSpringFrequency(").append(wj.getSpringFrequency()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setSpringDampingRatio(").append(wj.getSpringDampingRatio()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMotorEnabled(").append(wj.isMotorEnabled()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMotorSpeed(Math.toRadians(").append(Math.toDegrees(wj.getMotorSpeed())).append("));").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMaximumMotorTorque(").append(wj.getMaximumMotorTorque()).append(");").append(NEW_LINE);
                            } else {
                                if (!(joint instanceof MotorJoint)) {
                                    throw new UnsupportedOperationException("Unknown joint class: " + joint.getClass().getName());
                                }

                                MotorJoint<?> mj = (MotorJoint)joint;
                                body1 = (Body)mj.getBody1();
                                body2 = (Body)mj.getBody2();
                                sb.append("    ").append("MotorJoint joint").append(j).append(" = new MotorJoint(").append((String)idNameMap.get(body1)).append(", ").append((String)idNameMap.get(body2)).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setLinearTarget(").append(export(mj.getLinearTarget())).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setAngularTarget(Math.toRadians(").append(Math.toDegrees(mj.getAngularTarget())).append("));").append(NEW_LINE).append("    ").append("joint").append(j).append(".setCorrectionFactor(").append(mj.getCorrectionFactor()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMaximumForce(").append(mj.getMaximumForce()).append(");").append(NEW_LINE).append("    ").append("joint").append(j).append(".setMaximumTorque(").append(mj.getMaximumTorque()).append(");").append(NEW_LINE);
                            }

                            sb.append("    ").append("joint").append(j).append(".setCollisionAllowed(").append(joint.isCollisionAllowed()).append(");").append(NEW_LINE);
                            sb.append("    ").append("world.addJoint(joint").append(j).append(");");
                            sb.append(NEW_LINE);
                        }

                        sb.append("  ").append("}").append(NEW_LINE).append("}").append(NEW_LINE);
                        return sb.toString();
                    }
                }
            }
        }
    }

    private static final String export(Settings settings) {
        StringBuilder sb = new StringBuilder();
        if (settings.getStepFrequency() != 0.016666666666666666) {
            sb.append("    ").append("settings.setStepFrequency(").append(1.0 / settings.getStepFrequency()).append(");").append(NEW_LINE);
        }

        if (settings.getMaximumTranslation() != 2.0) {
            sb.append("    ").append("settings.setMaximumTranslation(").append(settings.getMaximumTranslation()).append(");").append(NEW_LINE);
        }

        if (settings.getMaximumRotation() != 1.5707963267948966) {
            sb.append("    ").append("settings.setMaximumRotation(Math.toRadians(").append(Math.toDegrees(settings.getMaximumRotation())).append("));").append(NEW_LINE);
        }

        if (!settings.isAtRestDetectionEnabled()) {
            sb.append("    ").append("settings.setAtRestDetectionEnabled(false);").append(NEW_LINE);
        }

        if (settings.getMaximumAtRestLinearVelocity() != 0.01) {
            sb.append("    ").append("settings.setMaximumAtRestLinearVelocity(").append(settings.getMaximumAtRestLinearVelocity()).append(");").append(NEW_LINE);
        }

        if (settings.getMaximumAtRestAngularVelocity() != Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY) {
            sb.append("    ").append("settings.setMaximumAtRestAngularVelocity(Math.toRadians(").append(Math.toDegrees(settings.getMaximumAtRestAngularVelocity())).append("));").append(NEW_LINE);
        }

        if (settings.getMinimumAtRestTime() != 0.5) {
            sb.append("    ").append("settings.setMinimumAtRestTime(").append(settings.getMinimumAtRestTime()).append(");").append(NEW_LINE);
        }

        if (settings.getVelocityConstraintSolverIterations() != 6) {
            sb.append("    ").append("settings.setVelocityConstraintSolverIterations(").append(settings.getVelocityConstraintSolverIterations()).append(");").append(NEW_LINE);
        }

        if (settings.getPositionConstraintSolverIterations() != 2) {
            sb.append("    ").append("settings.setPositionConstraintSolverIterations(").append(settings.getPositionConstraintSolverIterations()).append(");").append(NEW_LINE);
        }

        if (settings.getMaximumWarmStartDistance() != 0.01) {
            sb.append("    ").append("settings.setMaximumWarmStartDistance(").append(settings.getMaximumWarmStartDistance()).append(");").append(NEW_LINE);
        }

        if (settings.getLinearTolerance() != 0.005) {
            sb.append("    ").append("settings.setLinearTolerance(").append(settings.getLinearTolerance()).append(");").append(NEW_LINE);
        }

        if (settings.getAngularTolerance() != Settings.DEFAULT_ANGULAR_TOLERANCE) {
            sb.append("    ").append("settings.setAngularTolerance(Math.toRadians(").append(Math.toDegrees(settings.getAngularTolerance())).append("));").append(NEW_LINE);
        }

        if (settings.getMaximumLinearCorrection() != 0.2) {
            sb.append("    ").append("settings.setMaximumLinearCorrection(").append(settings.getMaximumLinearCorrection()).append(");").append(NEW_LINE);
        }

        if (settings.getMaximumAngularCorrection() != Settings.DEFAULT_MAXIMUM_ANGULAR_CORRECTION) {
            sb.append("    ").append("settings.setMaximumAngularCorrection(Math.toRadians(").append(Math.toDegrees(settings.getMaximumAngularCorrection())).append("));").append(NEW_LINE);
        }

        if (settings.getBaumgarte() != 0.2) {
            sb.append("    ").append("settings.setBaumgarte(").append(settings.getBaumgarte()).append(");").append(NEW_LINE);
        }

        if (settings.getContinuousDetectionMode() != ContinuousDetectionMode.ALL) {
            sb.append("    ").append("settings.setContinuousDetectionMode(Settings.ContinuousDetectionMode.").append(settings.getContinuousDetectionMode()).append(");").append(NEW_LINE);
        }

        return sb.toString();
    }

    private static final String export(Mass mass) {
        StringBuilder sb = new StringBuilder();
        Mass temp = new Mass(mass);
        temp.setType(MassType.NORMAL);
        sb.append("new Mass(new Vector2(").append(temp.getCenter().x).append(", ").append(temp.getCenter().y).append("), ").append(temp.getMass()).append(", ").append(temp.getInertia()).append(")");
        return sb.toString();
    }

    private static final String export(Vector2 v) {
        StringBuilder sb = new StringBuilder();
        sb.append("new Vector2(").append(v.x).append(", ").append(v.y).append(")");
        return sb.toString();
    }

    private static final String export(Convex c, String tabs) {
        StringBuilder sb = new StringBuilder();
        if (c instanceof Circle) {
            Circle circle = (Circle)c;
            sb.append(tabs).append("Convex c = Geometry.createCircle(").append(circle.getRadius()).append(");").append(NEW_LINE);
            if (!circle.getCenter().isZero()) {
                sb.append(tabs).append("c.translate(").append(export(circle.getCenter())).append(");").append(NEW_LINE);
            }
        } else if (c instanceof Rectangle) {
            Rectangle rectangle = (Rectangle)c;
            sb.append(tabs).append("Convex c = Geometry.createRectangle(").append(rectangle.getWidth()).append(", ").append(rectangle.getHeight()).append(");").append(NEW_LINE);
            if (Math.abs(rectangle.getRotationAngle()) > Epsilon.E) {
                sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(rectangle.getRotationAngle())).append("));").append(NEW_LINE);
            }

            if (!rectangle.getCenter().isZero()) {
                sb.append(tabs).append("c.translate(").append(export(rectangle.getCenter())).append(");").append(NEW_LINE);
            }
        } else if (c instanceof Triangle) {
            Triangle triangle = (Triangle)c;
            sb.append(tabs).append("Convex c = Geometry.createTriangle(").append(export(triangle.getVertices()[0])).append(", ").append(export(triangle.getVertices()[1])).append(", ").append(export(triangle.getVertices()[2])).append(");").append(NEW_LINE);
        } else if (c instanceof Polygon) {
            Polygon polygon = (Polygon)c;
            sb.append(tabs).append("Convex c = Geometry.createPolygon(");
            int vSize = polygon.getVertices().length;

            for(int i = 0; i < vSize; ++i) {
                Vector2 v = polygon.getVertices()[i];
                if (i != 0) {
                    sb.append(", ");
                }

                sb.append(export(v));
            }

            sb.append(");").append(NEW_LINE);
        } else if (c instanceof Segment) {
            Segment segment = (Segment)c;
            sb.append(tabs).append("Convex c = Geometry.createSegment(").append(export(segment.getVertices()[0])).append(", ").append(export(segment.getVertices()[1])).append(");").append(NEW_LINE);
        } else if (c instanceof Capsule) {
            Capsule capsule = (Capsule)c;
            sb.append(tabs).append("Convex c = Geometry.createCapsule(").append(capsule.getLength()).append(", ").append(capsule.getCapRadius() * 2.0).append(");").append(NEW_LINE);
            if (Math.abs(capsule.getRotationAngle()) > Epsilon.E) {
                sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(capsule.getRotationAngle())).append("));").append(NEW_LINE);
            }

            if (!capsule.getCenter().isZero()) {
                sb.append(tabs).append("c.translate(").append(export(capsule.getCenter())).append(");").append(NEW_LINE);
            }
        } else if (c instanceof Ellipse) {
            Ellipse ellipse = (Ellipse)c;
            sb.append(tabs).append("Convex c = Geometry.createEllipse(").append(ellipse.getHalfWidth() * 2.0).append(", ").append(ellipse.getHalfHeight() * 2.0).append(");").append(NEW_LINE);
            if (Math.abs(ellipse.getRotationAngle()) > Epsilon.E) {
                sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(ellipse.getRotationAngle())).append("));").append(NEW_LINE);
            }

            if (!ellipse.getCenter().isZero()) {
                sb.append(tabs).append("c.translate(").append(export(ellipse.getCenter())).append(");").append(NEW_LINE);
            }
        } else {
            double originalX;
            double theta;
            double radius;
            if (c instanceof HalfEllipse) {
                HalfEllipse halfEllipse = (HalfEllipse)c;
                theta = halfEllipse.getHalfWidth() * 2.0;
                radius = halfEllipse.getHeight();
                originalX = 4.0 * radius / 9.42477796076938;
                sb.append(tabs).append("Convex c = Geometry.createHalfEllipse(").append(theta).append(", ").append(radius).append(");").append(NEW_LINE);
                if (Math.abs(halfEllipse.getRotationAngle()) > Epsilon.E) {
                    sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(halfEllipse.getRotationAngle())).append("));").append(NEW_LINE);
                }

                if (halfEllipse.getCenter().y != originalX) {
                    sb.append(tabs).append("c.translate(").append(export(halfEllipse.getCenter())).append(");").append(NEW_LINE);
                }
            } else {
                if (!(c instanceof Slice)) {
                    throw new UnsupportedOperationException(MessageFormat.format("Unknown/Unsupported class {0}", c.getClass().getName()));
                }

                Slice slice = (Slice)c;
                theta = slice.getTheta();
                radius = slice.getSliceRadius();
                originalX = 2.0 * radius * Math.sin(theta * 0.5) / (1.5 * theta);
                sb.append(tabs).append("Convex c = Geometry.createSlice(").append(radius).append(", Math.toRadians(").append(Math.toDegrees(theta)).append("));").append(NEW_LINE);
                if (Math.abs(slice.getRotationAngle()) > Epsilon.E) {
                    sb.append(tabs).append("c.rotate(Math.toRadians(").append(Math.toDegrees(slice.getRotationAngle())).append("));").append(NEW_LINE);
                }

                if (slice.getCenter().x != originalX) {
                    sb.append(tabs).append("c.translate(").append(export(slice.getCenter())).append(");").append(NEW_LINE);
                }
            }
        }

        return sb.toString();
    }

    private static final String export(Filter f, String tabs) {
        StringBuilder sb = new StringBuilder();
        if (f != Filter.DEFAULT_FILTER) {
            if (!(f instanceof CategoryFilter)) {
                throw new UnsupportedOperationException("The class " + f.getClass().getName() + " is not known.");
            }

            CategoryFilter cf = (CategoryFilter)f;
            sb.append(tabs).append("bf.setFilter(new CategoryFilter(").append(cf.getCategory()).append(", ").append(cf.getMask()).append("));").append(NEW_LINE);
        }

        return sb.toString();
    }
}
