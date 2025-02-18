package boids;

import com.raylib.Raylib;

import java.util.ArrayList;

import com.raylib.Colors;

public class App {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static void main(String[] args) {
        Raylib.InitWindow(WIDTH, HEIGHT, "Demo");
        Raylib.SetTargetFPS(30);

        var birds = new ArrayList<Bird>();
        for (int i = 0; i < 50; i++) {
            birds.add(Bird.random());
        }

        while (!Raylib.WindowShouldClose()) {
            Raylib.BeginDrawing();

            Raylib.ClearBackground(Colors.BLACK);

            for (Bird b : birds) {
                b.update(birds);
                b.draw();
            }

            Raylib.EndDrawing();
        }
        Raylib.CloseWindow();
    }
}
