package boids;

import com.raylib.Raylib;

import boids.Vector.Vector2;

import com.raylib.Colors;

import java.lang.Math;
import java.util.List;

public class Bird {
    private static double speed = 10;
    private static double turnSpeed = 10;

    private static double separationStrength = 10;
    private static double alignmentStrength = 5;
    private static double cohesionStrength = 5;

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

    public void draw() {
        Raylib.DrawPoly(position.toRaylibVector2(), 3, 15, (float)rotation, Colors.WHITE);
    }

    public void update(List<Bird> allBirds) {
        double desiredRotation = separtation(allBirds) * separationStrength;
        desiredRotation += alignment(allBirds) * alignmentStrength;
        desiredRotation += cohesion(allBirds) * cohesionStrength;
        desiredRotation /= separationStrength + alignmentStrength + cohesionStrength;

        if (rotation > desiredRotation) {
            rotation -= turnSpeed;
        } else if (rotation < desiredRotation) {
            rotation += turnSpeed;
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
            if (b == this) {
                continue;
            }
            Vector2 bToSelf = b.position.minus(this.position);
            double r = bToSelf.magnitude();

            bToSelf = bToSelf.normalize();

            Vector2 repulsiveForce = bToSelf.times(1.0 / (r * r));
            totalRepulsiveForce = totalRepulsiveForce.plus(repulsiveForce);
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
        avgPosition = avgPosition.times(1.0 / birds.size());

        double angleRadians = Math.atan2(avgPosition.y() - position.y(), avgPosition.x() - position.x());

        return angleRadians * 180.0 / Math.PI;
    }
}
