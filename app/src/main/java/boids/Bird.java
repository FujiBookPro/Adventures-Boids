package boids;

import com.raylib.Raylib.Vector2;
import com.raylib.Raylib;
import com.raylib.Colors;

import java.lang.Math;
import java.util.List;

public class Bird {
    private static float speed = 5;
    private static float turnSpeed = 5;

    private static float separationStrength = 10;
    private static float alignmentStrength = 10;
    private static float cohesionStrength = 10;

    private Vector2 position;
    private float rotation;

    public Bird(Vector2 startPosition, float startRotation) {
        position = startPosition;
        rotation = startRotation;
    }

    public Bird(Vector2 startPosition) {
        this(startPosition, 0.0f);
    }

    public static Bird random(int width, int height) {
        Vector2 position = new Vector2().x((float)(Math.random() * width)).y((float)(Math.random() * height));
        float rotation = (float)(Math.random() * 360);

        return new Bird(position, rotation);
    }

    public static Bird random() {
        return random(App.WIDTH, App.HEIGHT);
    }

    public void draw() {
        Raylib.DrawPoly(position, 3, 15, rotation, Colors.WHITE);
    }

    public void update(List<Bird> allBirds) {
        float desiredRotation = separtation(allBirds) * separationStrength;
        desiredRotation += alignment(allBirds) * alignmentStrength;
        desiredRotation += cohesion(allBirds) * cohesionStrength;
        desiredRotation /= separationStrength + alignmentStrength + cohesionStrength;

        if (rotation > desiredRotation) {
            rotation -= turnSpeed;
        } else if (rotation < desiredRotation) {
            rotation += turnSpeed;
        }

        float newX = position.x() + (float)Math.cos(rotation * Math.PI / 180) * speed;
        float newY = position.y() + (float)Math.sin(rotation * Math.PI / 180) * speed;

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

        position = new Vector2().x(newX).y(newY);
    }

    private float separtation(List<Bird> birds) {
        Vector2 totalRepulsiveForce = new Vector2();
        for (Bird b : birds) {
            if (b == this) {
                continue;
            }
            Vector2 bToSelf = Raylib.Vector2Subtract(b.position, this.position);
            float r = Raylib.Vector2Length(bToSelf);

            bToSelf = Raylib.Vector2Normalize(bToSelf);

            Vector2 repulsiveForce = Raylib.Vector2Scale(bToSelf, 1.0f / (r * r));
            totalRepulsiveForce = Raylib.Vector2Add(totalRepulsiveForce, repulsiveForce);
        }
        float angleRadians = Raylib.Vector2Angle(totalRepulsiveForce, new Vector2().x(1.0f).y(0.0f));

        return angleRadians * 180 / (float)Math.PI;
    }

    private float alignment(List<Bird> birds) {
        float avgRotation = 0;
        for (Bird b : birds) {
            avgRotation += b.rotation;
        }
        avgRotation /= birds.size();
        return avgRotation;
    }

    private float cohesion(List<Bird> birds) {
        Vector2 avgPosition = new Vector2();
        for (Bird b : birds) {
            avgPosition = Raylib.Vector2Add(avgPosition, b.position);
        }
        avgPosition = Raylib.Vector2Scale(avgPosition, 1.0f / birds.size());

        float angleRadians = (float)Math.atan2(avgPosition.y() - position.y(), avgPosition.x() - position.x());

        return angleRadians * 180 / (float)Math.PI;
    }
}
