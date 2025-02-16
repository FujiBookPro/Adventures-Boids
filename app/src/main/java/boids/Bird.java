package boids;

import com.raylib.Raylib.Vector2;
import com.raylib.Raylib;
import com.raylib.Colors;

import java.lang.Math;

public class Bird {
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
        Vector2 position = new Vector2().x((int)(Math.random() * width)).y((int)(Math.random() * height));
        float rotation = (float)(Math.random() * 360);

        return new Bird(position, rotation);
    }

    public void draw() {
        Raylib.DrawPoly(position, 3, 15, rotation, Colors.WHITE);
    }
}
