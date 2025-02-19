package boids;

import com.raylib.Raylib;

import boids.Vector.Vector2;

import com.raylib.Colors;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class Bird {
    private static double speed = 18;
    private static double turnSpeed = 6;

    private static double separationConstant = 0.01;
    private static double cohesionConstant = 0.2;

    private static double separationStrength = 5;
    private static double alignmentStrength = 100;
    private static double cohesionStrength = 10;

    private static double viewDistance = 50;

    private Vector2 position;
    public double rotation;

    public Bird(Vector2 startPosition, double startRotation) {
        position = startPosition;
        rotation = startRotation;
    }

    public Bird(Vector2 startPosition) {
        this(startPosition, 0.0);
    }

    public static Bird random(int width, int height) {
        Vector2 position = new Vector2(Math.random() * width, Math.random() * height);
        double rotation = Math.random() * 360;

        return new Bird(position, rotation);
    }

    public static Bird random() {
        return random(App.WIDTH, App.HEIGHT);
    }

    public void draw(boolean showDebugAnnotations) {
        Raylib.DrawPoly(position.toRaylibVector2(), 3, 10, (float)rotation, Colors.WHITE);

        if (showDebugAnnotations) {
            // show movement direction
            // TODO: Make this code (and the main movement code) less redundant
            float nextX = (float)(position.x() + Math.cos(rotation * Math.PI / 180) * 50);
            float nextY = (float)(position.y() + Math.sin(rotation * Math.PI / 180) * 50);
            Raylib.DrawLineV(position.toRaylibVector2(), new Raylib.Vector2().x(nextX).y(nextY), Colors.GREEN);
            // show view distance
            Raylib.DrawCircleLinesV(position.toRaylibVector2(), (float)viewDistance, Colors.BLUE);
        }
    }

    public void update(List<Bird> allBirds) {
        List<Bird> visibleBirds = new ArrayList<>();
        for (Bird b : allBirds) {
            if (b == this) {
                continue;
            }
            if (this.position.minus(b.position).magnitude() < viewDistance) {
                visibleBirds.add(b);
            }
        }

        double desiredRotation = separtation(visibleBirds) * separationStrength;
        desiredRotation += alignment(visibleBirds) * alignmentStrength;
        desiredRotation += cohesion(visibleBirds) * cohesionStrength;
        desiredRotation /= separationStrength + alignmentStrength + cohesionStrength;

        if (rotation > desiredRotation) {
            rotation -= Math.min(turnSpeed, rotation - desiredRotation);
        } else if (rotation < desiredRotation) {
            rotation += Math.min(turnSpeed, desiredRotation - rotation);
        }

        double newX = position.x() + Math.cos(rotation * Math.PI / 180) * speed;
        double newY = position.y() + Math.sin(rotation * Math.PI / 180) * speed;

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

        return angleRadians * 180.0 / Math.PI;
    }

    private double alignment(List<Bird> birds) {
        double avgRotation = 0;
        for (Bird b : birds) {
            avgRotation += b.rotation;
        }
        avgRotation /= birds.size();
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

        return angleRadians * 180.0 / Math.PI;
    }
}
