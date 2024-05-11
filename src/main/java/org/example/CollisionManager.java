package org.example;

import org.joml.Vector2f;

public class CollisionManager {
    /**
     * create a verctor between the circle of the ball and check
     * if the sum of the 2 radius(the minimum value for not colliding)
     * is greater or less of the actual position of the balls
     *
     * @return 1 if the balls collide
     */
    public boolean CollisionBallBall(Ball ball1, Ball ball2) {
        double radiusSum = ball1.getRadius() + ball2.getRadius();
        int dx = ball1.getCenter().x - ball2.getCenter().x;
        int dy = ball1.getCenter().y - ball2.getCenter().y;
        Vector2f vecBetweenCenters = new Vector2f(dx, dy);
        return vecBetweenCenters.lengthSquared() <= radiusSum * radiusSum;
    }


}
