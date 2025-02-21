package boids;

import com.raylib.Raylib;

import boids.Vector.Vector2;

import com.raylib.Colors;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class Bird {
    private static double speed = 18;
    private static double turnSpeed = .1;

    private static double separationConstant = 0.01;
    private static double cohesionConstant = 0.2;

    private static double separationStrength = 5;
    private static double alignmentStrength = 100;
    private static double cohesionStrength = 10;

    private static double viewDistance = 50;

    private Vector2 position;
    private double rotation;
    private double desiredRotation;

    public Bird(Vector2 startPosition, double startRotation) {
        position = startPosition;
        rotation = startRotation;
        desiredRotation = startRotation;
    }

    public Bird(Vector2 startPosition) {
        this(startPosition, 0.0);
    }

    public static Bird random(int width, int height) {
        Vector2 position = new Vector2(Math.random() * width, Math.random() * height);
        double rotation = Math.random() * Math.PI * 2;

        return new Bird(position, rotation);
    }

    public static Bird random() {
        return random(App.WIDTH, App.HEIGHT);
    }

    public void draw(boolean showDebugAnnotations) {
        Raylib.DrawPoly(position.toRaylibVector2(), 3, 10, (float)(rotation * 180.0 / Math.PI), Colors.WHITE);

        if (showDebugAnnotations) {
            // show movement direction
            Vector2 rotationIndicator = position.plus(Vector2.unitVector(rotation).times(50));
            Raylib.DrawLineV(position.toRaylibVector2(), rotationIndicator.toRaylibVector2(), Colors.GREEN);
            // show desired rotation
            Vector2 desireIndicator = position.plus(Vector2.unitVector(desiredRotation).times(50));
            Raylib.DrawLineV(position.toRaylibVector2(), desireIndicator.toRaylibVector2(), Colors.PURPLE);
            // show view distance
            Raylib.DrawCircleLinesV(position.toRaylibVector2(), (float)viewDistance, Colors.BLUE);
        }
    }

    public void update(List<Bird> allBirds) {
        List<Bird> visibleBirds = findVisibleBirds(allBirds);

        desiredRotation = separtation(visibleBirds) * separationStrength;
        desiredRotation += alignment(visibleBirds) * alignmentStrength;
        desiredRotation += cohesion(visibleBirds) * cohesionStrength;
        desiredRotation /= separationStrength + alignmentStrength + cohesionStrength;

        if (rotation > desiredRotation) {
            rotation -= Math.min(turnSpeed, rotation - desiredRotation);
        } else if (rotation < desiredRotation) {
            rotation += Math.min(turnSpeed, desiredRotation - rotation);
        }

        rotation %= 2 * Math.PI;

        double newX = position.x() + Math.cos(rotation) * speed;
        double newY = position.y() + Math.sin(rotation) * speed;

        if (newX > App.WIDTH) {
            newX %= App.WIDTH;
        } else if (newX < 0) {
            newX += App.WIDTH;
        }

        if (newY > App.HEIGHT) {
            newY %= App.HEIGHT;
        } else if (newY < 0) {
            newY += App.HEIGHT;
        }

        position = new Vector2(newX, newY);
    }

    private List<Bird> findVisibleBirds(List<Bird> birds) {
        List<Bird> visibleBirds = new ArrayList<>();
        for (Bird b : birds) {
            if (b == this) {
                continue;
            }

            Vector2 thisToOther = b.position.minus(this.position);
            Vector2 forward = Vector2.unitVector(rotation);

            if (thisToOther.magnitude() < viewDistance) {
                visibleBirds.add(b);
            }
        }

        return visibleBirds;
    }

    private double separtation(List<Bird> birds) {
        Vector2 totalRepulsiveForce = new Vector2();
        for (Bird b : birds) {
            Vector2 bToSelf = this.position.minus(b.position);
            double r = bToSelf.magnitude();

            bToSelf = bToSelf.normalize();

            Vector2 repulsiveForce = bToSelf.times(separationConstant / r);
            totalRepulsiveForce = totalRepulsiveForce.plus(repulsiveForce);
        }
        if (totalRepulsiveForce.equals(new Vector2())) {
            return this.rotation;
        }
        double angleRadians = totalRepulsiveForce.angleWith(new Vector2(1.0, 0.0));

        return angleRadians;
    }

    private double alignment(List<Bird> birds) {
        // use circular mean to correctly calculate average angle
        double avgCos = Math.cos(rotation);
        double avgSin = Math.sin(rotation);
        for (Bird b : birds) {
            avgCos += Math.cos(b.rotation);
            avgSin += Math.sin(b.rotation);
        }
        double avgRotation = Math.atan2(avgSin, avgCos);
        return avgRotation;
    }

    private double cohesion(List<Bird> birds) {
        Vector2 avgPosition = new Vector2();
        for (Bird b : birds) {
            avgPosition = avgPosition.plus(b.position);
        }
        avgPosition = avgPosition.times(cohesionConstant / birds.size());

        Vector2 attractiveForce = avgPosition.minus(this.position);
        double r = attractiveForce.magnitude();
        attractiveForce = attractiveForce.normalize();
        attractiveForce = attractiveForce.times(1.0 / (r * r));

        double angleRadians = attractiveForce.angleWith(new Vector2(1.0, 0.0));

        return angleRadians;
    }
}
