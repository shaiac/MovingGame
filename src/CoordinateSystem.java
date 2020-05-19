import LinearMath.Vector;

public class CoordinateSystem {
 private Vector x;
 private Vector y;
 private Vector z;
 private Vector origin;
 public void rotate(char axis, double angle) {
     if (axis == 'y') {
         x = x.Multiply(Math.cos(angle)).Add(z.Multiply(Math.sin(angle)));
         z = x.Multiply(Math.cos(angle)).Add(z.Multiply(Math.cos(angle)));
     }
 }
}
