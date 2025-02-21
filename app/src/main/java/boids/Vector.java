package boids;

import com.raylib.Raylib;

import java.lang.Math;

public class Vector {
    // Possibly add Vector3 later
    public record Vector2(double x, double y) {
        public Vector2() {
            this(0, 0);
        }

        public static Vector2 unitVector(double angle) {
            return new Vector2(Math.cos(angle), Math.sin(angle));
        }

        public Raylib.Vector2 toRaylibVector2() {
            return new Raylib.Vector2().x((float)this.x).y((float)this.y);
        }
        
        public Vector2 plus(Vector2 rhs) {
            return new Vector2(this.x() + rhs.x(), this.y() + rhs.y());
        }

        public Vector2 minus(Vector2 rhs) {
            return new Vector2(this.x() - rhs.x(), this.y() - rhs.y());
        }

        public double dot(Vector2 rhs) {
            return this.x() * rhs.x() + this.y() * rhs.y();
        }

        public double angleWith(Vector2 other) {
            if (other.magnitude() == 0.0 || this.magnitude() == 0.0) {
                throw new ArithmeticException("Cannot find angle with vector of magnitude 0.0");
            }
            // this implementation is likely slow
            double cos = this.dot(other) / (this.magnitude() * other.magnitude());
            return Math.acos(cos);
        }

        public double magnitude() {
            return Math.sqrt(this.x() * this.x() + this.y() * this.y());
        }

        public Vector2 times(double s) {
            return new Vector2(this.x() * s, this.y() * s);
        }

        public Vector2 normalize() {
            if (this.magnitude() == 0.0) {
                throw new ArithmeticException("Cannot normalize vector of magnitude 0.0");
            }
            return this.times(1.0 / this.magnitude());
        }

    }
}
