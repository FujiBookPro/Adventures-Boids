package boids;

import com.raylib.Raylib;
import com.raylib.Raylib.Vector2;

import java.util.ArrayList;

import com.raylib.Colors;

public class App {
    public static void main(String[] args) {
        Raylib.InitWindow(800, 450, "Demo");
        Raylib.SetTargetFPS(60);

        var birds = new ArrayList<Bird>();
        birds.add(Bird.random(800, 450));
        birds.add(Bird.random(800, 450));
        birds.add(Bird.random(800, 450));

        while (!Raylib.WindowShouldClose()) {
            Raylib.BeginDrawing();

            Raylib.ClearBackground(Colors.BLACK);

            for (Bird b : birds) {
                b.draw();
            }

            Raylib.EndDrawing();
        }
        Raylib.CloseWindow();
    }
}
