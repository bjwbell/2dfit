//JTS imports
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import javax.swing.*;
import java.awt.*;
import java.util.*;


class GUIOutput {
    public volatile Renderer renderer = null;
    public synchronized Renderer getRenderer() {
	return this.renderer;
    }
    
    public int width = 640;
    public int height = 480;
    
    public void run() {
	renderer = new Renderer(width, height);
	System.out.println("GUIOuput.renderer:" + renderer.hashCode());
	System.out.println("Hello from a thread!");
	
	JFrame frame = new JFrame("GUI Output");
	frame.setBackground(new Color(0, 0, 0));
	frame.setSize(width, height);
	frame.setVisible(true);
	System.out.println("adding renderer");
	frame.getContentPane().add(renderer);
	//frame.pack();
    }
}



class Renderer extends JPanel
{
    //public synchronized Geometry geometry = null;
    public static  int NumberOfObjects = 5;
    private volatile LinearRing[] linearRings = new LinearRing[NumberOfObjects];
    private Color[] linearRingColors = new Color[NumberOfObjects];
    private Color defaultColor = new Color(255, 0, 0);
    
    public int width = 640;
    public int height = 480;
    
    public Renderer(int width, int height){
	this.width = width;
	this.height = height;
    }

    public synchronized void setLinearRing(LinearRing lr, int index){
	if(index >= NumberOfObjects){
	    return;
	}
	this.linearRings[index] = lr;
	repaint();
    }

    public synchronized LinearRing getLinearRing(int index) {
	if(index >= NumberOfObjects){
	    return null;
	}
	return linearRings[index];
    }
    public synchronized void setLinearRingColor(Color c, int index){
	if(index >= NumberOfObjects){
	    return;
	}
	this.linearRingColors[index] = c;
	repaint();
    }
    public synchronized void setDefaultColor(Color c){
	this.defaultColor = c;
    }
    
    public void paintComponent(Graphics g)  {
	super.paintComponent(g);
	for(int i = 0; i < NumberOfObjects; i++){	    
	    if(linearRings[i] == null){
		continue;
	    }
	    RenderObject(g, i);
	}
	try {
	    Thread.currentThread().sleep(1000);
	}catch(java.lang.InterruptedException e){
	    
	}
	repaint();
	
    }
    public void RenderObject(Graphics g, int index){
	if(linearRings[index] == null){
	    return;
	}
	g.setColor(defaultColor);
	//g.setColor(new Color(255, 0, 0));
	//g.drawRect(0, 0, 400, 200);
	if(linearRingColors[index] != null){
	    g.setColor(linearRingColors[index]);
	}
	int numPoints = linearRings[index].getNumPoints();
	for(int i = 0; i < numPoints - 1; i++){
	    Point p1 = linearRings[index].getPointN(i);
	    Point p2 = linearRings[index].getPointN(i + 1);
	    int titleBarOffset = -35;
	    //translate and then flip the y-coordinate for the output.
	    g.drawLine((int)p1.getX(), -1 * ((int)p1.getY() - height) + titleBarOffset, (int)p2.getX(), -1 * ( (int)p2.getY() - height) + titleBarOffset);
	}
	
    }
}







class Runner
{
    public static GeometryFactory geometryFactory = null;
    public static GUIOutput gui = null;
    public static void main(String args[]) throws java.lang.InterruptedException{
	
	gui = new GUIOutput();
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    gui.run();
		}
 	    });
	while(gui.getRenderer() == null){
	    
	}
 	//gui.run();
 	geometryFactory = new GeometryFactory();
	Util.geometryFactory = geometryFactory;
 	//while (gui.getRenderer() == null){
 	//    int x = 1;	
 	renderGeometry();
     }

    public static List<LinearRing> makeTangramPieces(){
	
    }

     public static void renderGeometry() {
 	//Coordinate ptc = new Coordinate(14.0d, 14.0d);	
 	Renderer renderer = gui.getRenderer();
 	/*renderer.setLinearRing(makeTriangle(new Coordinate(0, 0), new Coordinate(400, 400), new Coordinate(400, 0)), 1);
	  renderer.setLinearRing(makeSquare(100), 2);*/
	/*int offset = 2;
	LinearRing shape = Util.makeS2(200);//makeSquare(200);
	renderer.setLinearRing(shape, 0);
	Vector<LinearRing> shapes = Util.decomposeTriangles(shape, 1);
	System.out.println("number of shapes:" + shapes.size());
	for(int i = 0; i < shapes.size() && i + offset < renderer.NumberOfObjects; i++){
	    if(i < 1){
		continue;
	    }
	    renderer.setLinearRing(shapes.get(i), i + offset);
	    }*/

	LinearRing s1 = Util.makeS1(100);
	Coordinate p1 = new Coordinate(0, 0);
	Coordinate p2 = new Coordinate(-100, 0);
	Coordinate p3 = new Coordinate(0, 100);
	
	LinearRing tri = Util.makeTriangle(p1, p2, p3);
	LinearRing tri2 = Util.makeTriangle(new Coordinate(100, 0), new Coordinate(50, 50), new Coordinate(100, 100));
	LinearRing newS1 = null;
	Util.PrintShape("s1", s1);
	Util.PrintShape("tri", tri);
	
	Polygon poly1 = new Polygon(s1, null, geometryFactory);
	Polygon poly2 = new Polygon(tri, null, geometryFactory);
	System.out.println("p1.covers(p2):" + poly1.covers(poly2));
	
	//newS1 = Util.FitShape(s1, tri);
	ArrayList<LinearRing> shapes = new ArrayList<LinearRing>();
	shapes.add(tri);
	shapes.add(tri2);
	//Util.Fit(s1, shapes);
	//Util.FitShape(s1, tri2);
	System.out.println("Fit:" + Util.Fit(s1, shapes));
	//Util.PrintShape("newS1", newS1);
	renderer.setLinearRing(s1, 0);
	renderer.setLinearRingColor(new Color(0, 0, 0), 0);
	renderer.setLinearRing(tri, 1);
	renderer.setLinearRingColor(new Color(255, 0, 0), 1);
	//renderer.setLinearRing(newS1, 2);
	//renderer.setLinearRingColor(new Color(0, 255, 0), 2);
	renderer.setLinearRing(tri2, 2);
	renderer.setLinearRingColor(new Color(0, 255, 0), 2);
    }


    
    
}






