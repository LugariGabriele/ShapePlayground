package game.tool;


public class CollisionManager {
    /**
     * if the sum of the 2 radius(the minimum value for not colliding)
     * is greater or less of the actual position of the balls
     *
     * @return 1 if the balls collide each other
     */
    public static boolean CollisionBallBall(Ball ball1, Ball ball2) {
        double radiusSum = ball1.getRadius() + ball2.getRadius();
        double dx = ball1.getCenter().x - ball2.getCenter().x;
        double dy = ball1.getCenter().y - ball2.getCenter().y;
        double distanceSquared = dx * dx + dy * dy;
        return distanceSquared <= radiusSum * radiusSum;
    }

    public static boolean CollisionInsideBallBox(Ball ball, Box box) {

        /*
         * check if the ball is inside the box
         */
        if (ball.getCenter().x >= box.getUpperLeft().x + ball.getRadius() && ball.getCenter().x <= box.getBottomRight().x - ball.getRadius()
                && ball.getCenter().y >= box.getUpperLeft().y + ball.getRadius() && ball.getCenter().y <= box.getBottomRight().y - ball.getRadius()) {
            return false;
        }

        /*
         * find coordinates of the nearest position of the circle inside the box
         */
        double ballX = ball.getCenter().getX();
        double ballY = ball.getCenter().getY();
        double ballRadius = ball.getRadius();

        double boxLeft = box.getUpperLeft().getX();
        double boxRight = box.getBottomRight().getX();
        double boxTop = box.getUpperLeft().getY();
        double boxBottom = box.getBottomRight().getY();

        boolean insideHorizontally = ballX - ballRadius >= boxLeft && ballX + ballRadius <= boxRight;
        boolean insideVertically = ballY - ballRadius >= boxTop && ballY + ballRadius <= boxBottom;

        return insideHorizontally && insideVertically;
    }
}



