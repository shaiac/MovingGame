import LinearMath.Vector;

public class CoordinateSystem {
 private Vector x;
 private Vector y;
 private Vector z;
 private Vector origin;

 public CoordinateSystem() {
        double[] vec = {0,0,8  };
        origin = new Vector(vec,3 );
        double[] vecZ = {0, 0, 1};
        z = new Vector(vecZ, 3);
        double[] vecX = {1, 0, 0};
        x = new Vector(vecX, 3);
        double[] vecY = {0, 1, 0};
        y = new Vector(vecY, 3);
 }

 public void rotate(char axis, double angle) {
     if (axis == 'y') {
         x = x.Multiply(Math.cos(angle)).Add(z.Multiply(Math.sin(angle)));
         x = x.normal();
         z = x.Multiply(Math.sin(angle)).Add(z.Multiply(Math.cos(angle)));
         z = z.normal();
     } if (axis == 'x') {
         z = z.Multiply(Math.cos(angle)).Add(y.Multiply(Math.sin(angle)));
         z = z.normal();
         y = z.Multiply(Math.sin(angle)).Add(y.Multiply(Math.cos(angle)));
         y = y.normal();
     } if (axis == 'z') {
         x = y.Multiply(Math.cos(angle)).Add(x.Multiply(Math.sin(angle)));
         x = x.normal();
         y = y.Multiply(Math.sin(angle)).Add(x.Multiply(Math.cos(angle)));
         y= y.normal();
     }
 }

 public void moveStep(char axis, double step) {
     if (axis == 'x') {
        origin = origin.Add(x.Multiply(step));
     } if (axis == 'y') {
         origin = origin.Add(y.Multiply(step));
     } if (axis == 'z') {
         origin = origin.Add(z.Multiply(step));
     }
 }

 public Vector getOrigin() {
     return this.origin;
 }
 public Vector getX() {
     return this.x;
 }
 public Vector getY() {
     return this.y;
 }
 public Vector getZ() {
     return this.z;
 }
}
