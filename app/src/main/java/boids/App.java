package boids;

import com.raylib.Raylib;

import boids.Vector.Vector2;

import java.util.ArrayList;
import java.util.List;

import com.raylib.Colors;

public class App {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;

    private static List<Bird> initEnvironment() {
        List<Bird> birds = new ArrayList<Bird>();
        for (int i = 0; i < 100; i++) {
            birds.add(Bird.random(WIDTH, HEIGHT));
        }
        return birds;
    }

    public static void main(String[] args) {
        Raylib.InitWindow(WIDTH, HEIGHT, "Demo");
        Raylib.SetTargetFPS(30);

        boolean debugEnabled = false;
        boolean paused = false;

        List<Bird> birds = initEnvironment();

        while (!Raylib.WindowShouldClose()) {
            // reset with r
            if (Raylib.IsKeyPressed(Raylib.KEY_R)) {
                birds = initEnvironment();
            }
            // show debug view with d
            if (Raylib.IsKeyPressed(Raylib.KEY_D)) {
                debugEnabled = !debugEnabled;
            }
            // pause simulation with p
            if (Raylib.IsKeyPressed(Raylib.KEY_P)) {
                paused = !paused;
            }
            // step simulation with n
            if (Raylib.IsKeyDown(Raylib.KEY_N) && paused) {
                for (Bird b : birds) {
                    b.update(birds);
                }
            }
            // spawn new birds with mouse click
            if (Raylib.IsMouseButtonDown(Raylib.MOUSE_BUTTON_LEFT)) {
                birds.add(new Bird(new Vector2(Raylib.GetMouseX(), Raylib.GetMouseY())));
            }

            Raylib.BeginDrawing();

            Raylib.ClearBackground(Colors.BLACK);

            for (Bird b : birds) {
                if (!paused) {
                    b.update(birds);
                }
                b.draw(debugEnabled);
            }

            Raylib.EndDrawing();
        }
        Raylib.CloseWindow();
    }
}
