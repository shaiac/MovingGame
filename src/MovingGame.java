import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import LinearMath.Vector;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class MovingGame extends KeyAdapter implements GLEventListener {
    private Texture texture;
    private CoordinateSystem cooSystem;
    private static GLU glu;
    private static GLCanvas canvas;
    private static Frame frame;
    private static Animator animator;

    public MovingGame() {
        this.cooSystem =  new CoordinateSystem();
        glu = new GLU();
        canvas = new GLCanvas();
        frame = new Frame("Moving Game");
        animator = new Animator(canvas);
    }

    public void display(GLAutoDrawable gLDrawable) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        float position0[] = {60f,20f,-30f,1.0f};		// red light on the right side (light 0)
        float position1[] = {-60f,20f,-30f,1.0f};	// blue light on the left side (light 1)
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();  // Reset The View
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glTexParameteri ( GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT );
        gl.glTexParameteri( GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT );
        Vector origin = cooSystem.getOrigin();
        Vector lookat = origin.minus(cooSystem.getZ());
        lookat.normal();
        Vector y = cooSystem.getY();
        texture.bind(gl);
        //The light
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position0, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, position1, 0);

        glu.gluLookAt(origin.get(0), origin.get(1), origin.get(2), lookat.get(0), lookat.get(1), lookat.get(2), 0, 1, 0);
               // y.get(0), y.get(1), y.get(2));
        gl.glBegin(GL2.GL_QUADS);

        createWalls(gl);
        createCube(gl,-100,0,-100,5);
        createCube(gl,0,0,-100,5);
    }

    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GL2.GL_SMOOTH);              // Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
        gl.glClearDepth(1.0f);                      // Depth Buffer Setup
        gl.glEnable(GL2.GL_DEPTH_TEST);              // Enables Depth Testing
        gl.glDepthFunc(GL2.GL_LEQUAL);               // The Type Of Depth Testing To Do
        // Really Nice Perspective Calculations
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        try {
            String filename="resources/Picture1.jpg"; // the FileName to open
            texture=TextureIO.newTexture(new File( filename ),true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

        // Light
        float	ambient[] = {0.1f,0.1f,0.1f,1.0f};
        float	diffuse0[] = {1f,0f,0f,1.0f};
        float	diffuse1[] = {0f,0f,1f,1.0f};


        gl.glShadeModel(GL2.GL_SMOOTH);

        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse0, 0);
        //gl.glEnable(GL2.GL_LIGHT0);

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse1, 0);
        //gl.glEnable(GL2.GL_LIGHT1);

        //gl.glEnable(GL2.GL_LIGHTING);

        if (drawable instanceof Window) {
            Window window = (Window) drawable;
            window.addKeyListener(this);
        } else if (GLProfile.isAWTAvailable() && drawable instanceof java.awt.Component) {
            java.awt.Component comp = (java.awt.Component) drawable;
            new AWTKeyAdapter(this, drawable).addTo(comp);
        }
    }

    public void reshape(GLAutoDrawable drawable, int x,
                        int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if(height <= 0) {
            height = 1;
        }
        float h = (float)width / (float)height;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(100.0f, h, 1.0, 1000.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void keyPressed(KeyEvent e) {
        float step = 1.0f;
        double angle = 2;
        char keyPressed = e.getKeyChar();
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            exit();
        } else if (keyPressed == 'i' || keyPressed == 'I') {
            cooSystem.rotate('x', angle);
        } else if (keyPressed == 'k' || keyPressed == 'K') {
            cooSystem.rotate('x', -angle);
        } else if (keyPressed == 'l' || keyPressed == 'L') {
            cooSystem.rotate('y', angle);
        } else if (keyPressed == 'j' || keyPressed == 'J') {
            cooSystem.rotate('y', angle);
        } else if (keyPressed == 'o' || keyPressed == 'O') {
            cooSystem.rotate('z', angle);
        } else if (keyPressed == 'u' || keyPressed == 'U') {
            cooSystem.rotate('z', angle);
        } else if (keyPressed == 'w' || keyPressed == 'W') {
            cooSystem.moveStep('z', step);
        } else if (keyPressed == 's' || keyPressed == 'S') {
            cooSystem.moveStep('z', -step);

        } else if (keyPressed == 'd' || keyPressed == 'D') {
            cooSystem.moveStep('x', -step);
        } else if (keyPressed == 'a' || keyPressed == 'A') {
            cooSystem.moveStep('x', step);
        } else if (keyPressed == 'e' || keyPressed == 'E') {
            cooSystem.moveStep('y', step);
        } else if (keyPressed == 'q' || keyPressed == 'Q') {
            cooSystem.moveStep('y', -step);
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public static void exit(){
        animator.stop();
        frame.dispose();
        System.exit(0);
    }

    public void startGame() {
        canvas.addGLEventListener(this);
        frame.add(canvas);
        frame.setSize(800, 600);
//		frame.setUndecorated(true);
//		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        frame.setVisible(true);
        animator.start();
        canvas.requestFocus();
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
        // TODO Auto-generated method stub

    }

    public void displayChanged(GLAutoDrawable gLDrawable,
                               boolean modeChanged, boolean deviceChanged) {
    }

    public void createWalls(GL2 gl){
        gl.glBegin(GL2.GL_QUADS);
        //front wall
        gl.glColor3f(1.0f,0.0f,0.0f);
        gl.glVertex3f(-128.0f, 0.0f, -128.0f);
        gl.glVertex3f(-128.0f, 64.0f, -128.0f);
        gl.glVertex3f(128.0f, 64.0f, -128.0f);
        gl.glVertex3f(128.0f, 0.0f, -128.0f);

        // back wall
        gl.glColor3f(1.0f,1.0f,0.0f);
        gl.glVertex3f(-128.0f,0.0f,128.0f);
        gl.glVertex3f(-128.0f,64.0f,128.0f);
        gl.glVertex3f(128.0f,0.0f,128.0f);
        gl.glVertex3f(128.0f,64.0f,128.0f);

        //right wall
        gl.glColor3f(0.0f,1.0f,0.0f);
        gl.glVertex3f(128.0f,0.0f,-128.0f);
        gl.glVertex3f(128.0f,64.0f,-128.0f);
        gl.glVertex3f(128.0f,64.0f,128.0f);
        gl.glVertex3f(128.0f,0.0f,128.0f);

        //left wall
        gl.glColor3f(0.0f,1.0f,0.0f);
        gl.glVertex3f(-128.0f,0.0f,128.0f);
        gl.glVertex3f(-128.0f,64.0f,128.0f);
        gl.glVertex3f(-128.0f,64.0f,-128.0f);
        gl.glVertex3f(-128.0f,0.0f,-128.0f);

        //top wall
        gl.glColor3f(0.5f,0.5f,0.5f);
        gl.glVertex3f(-128.0f,64.0f,-128.0f);
        gl.glVertex3f(128.0f,64.0f,-128.0f);
        gl.glVertex3f(128.0f,64.0f,128.0f);
        gl.glVertex3f(-128.0f,64.0f,128.0f);

        //bottom wall
        gl.glColor3f(0.5f,0.5f,0.5f);
        gl.glVertex3f(-128.0f,0.0f,-128.0f);
        gl.glVertex3f(128.0f,0.0f,-128.0f);
        gl.glVertex3f(128.0f,0.0f,128.0f);
        gl.glVertex3f(-128.0f,0.0f,128.0f);
        gl.glEnd();
    }

    public void createCube(GL2 gl,float x, float y, float z, float width ){
        gl.glBegin(GL2.GL_QUADS);
        // front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(x, y, z+width);
        gl.glTexCoord2f(2f, 0.0f);
        gl.glVertex3f(x, y+width, z+width);
        gl.glTexCoord2f(2f, 1.0f);
        gl.glVertex3f(x+width, y+width, z+width);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(x+width, y, z+width);
        // Back Face
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(x, y, z);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(x, y+width, z);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(x+width, y+width, z);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(x+width, y, z);
        // Top Face
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(x, y+width, z);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(x+width, y+width, z);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(x+width, y+width, z+width);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(x, y+width, z+width);
        // Bottom Face
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(x, y, z);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(x+width, y, z);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(x+width, y, z+width);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(x, y, z+width);
        // Right face
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(x+width, y, z);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(x+width, y+width, z);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(x+width, y+width, z+width);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(x+width, y, z+width);
        // Left Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(x, y, z);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(x, y+width, z);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(x, y+width, z+width);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(x, y, z+width);
        gl.glEnd();
    }

}