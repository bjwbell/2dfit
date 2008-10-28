//JTS imports
import com.vividsolutions.jts.geom.Coordinate;
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
import javax.swing.*;
import java.awt.*;
import java.util.*;


class GUIOutput {
    public volatile Renderer renderer = null;
    public synchronized Renderer getRenderer() {
	return this.renderer;
    }
    
    
    public void run() {
	renderer = new Renderer();
	System.out.println("GUIOuput.renderer:" + renderer.hashCode());
	System.out.println("Hello from a thread!");
	
	JFrame frame = new JFrame("GUI Output");
	frame.setBackground(new Color(0, 0, 0));
	frame.setSize(640, 480);
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
	System.out.println("painting component");
	for(int i = 0; i < NumberOfObjects; i++){	    
	    if(linearRings[i] == null){
		continue;
	    }
	    System.out.println("rendering object");
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
	    g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
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
 	//while (gui.getRenderer() == null){
 	//    int x = 1;	
 	renderGeometry();
     }

     public static Vector<LinearRing> decomposeTriangles(LinearRing lr, int numberOfTriangles){
 	if(numberOfTriangles == 0){
 	    Vector<LinearRing> shapes = new Vector<LinearRing>();
 	    shapes.add(lr);
 	    return shapes;
 	}
	
 	boolean lrIsCCW = CGAlgorithms.isCCW(lr.getCoordinates());
 	LinearRing tri = null;
 	for(int i = 0; i + 2 < lr.getCoordinates().length; i++){
 	    Coordinate[] coords = new Coordinate[4];
 	    coords[0] = lr.getCoordinateN(i);
 	    coords[1] = lr.getCoordinateN(i + 1);
 	    coords[2] = lr.getCoordinateN(i + 2);
 	    coords[3] = lr.getCoordinateN(i);
 	    boolean isCCW = CGAlgorithms.isCCW(coords);
	    if(isCCW == lrIsCCW){
 		System.out.println("removed index: " + (i + 1));
 		tri = geometryFactory.createLinearRing(coords);
 		Coordinate[] newLrCoords = new Coordinate[lr.getCoordinates().length - 1];
 		int offset = 0;
 		for(int j = 0; j < lr.getCoordinates().length; j++){
 		    if(j == i + 1){
 			offset = -1;
 			continue;
 		    }
 		    System.out.println("j:" + j);
 		    newLrCoords[j + offset] = lr.getCoordinateN(j);		    
 		}
 		LinearRing newLr = geometryFactory.createLinearRing(newLrCoords);
 		Vector<LinearRing> shapes = decomposeTriangles(newLr, numberOfTriangles - 1);
 		shapes.add(tri);
 		return shapes;
 	    }else{
 		continue;
 	    }
 	}
 	return null;
     }

     public static void renderGeometry() {
 	//Coordinate ptc = new Coordinate(14.0d, 14.0d);	
 	Renderer renderer = gui.getRenderer();
 	/*renderer.setLinearRing(makeTriangle(new Coordinate(0, 0), new Coordinate(400, 400), new Coordinate(400, 0)), 1);
	  renderer.setLinearRing(makeSquare(100), 2);*/
	int offset = 2;
	LinearRing shape = makeS2(200);//makeSquare(200);
	renderer.setLinearRing(shape, 0);
	Vector<LinearRing> shapes = decomposeTriangles(shape, 1);
	System.out.println("number of shapes:" + shapes.size());
	for(int i = 0; i < shapes.size() && i + offset < renderer.NumberOfObjects; i++){
	    if(i < 1){
		continue;
	    }
	    System.out.println("i:" + i);
	    
	    renderer.setLinearRing(shapes.get(i), i + offset);
	}
    }

    public static LinearRing makeS1(int s){
	Coordinate[] c = new Coordinate[6];
	c[0] = new Coordinate(0, 0);
	c[1] = new Coordinate(0, s);
	c[2] = new Coordinate(s/2, s/2);
	c[3] = new Coordinate(s, s);
	c[4] = new Coordinate(s, 0);
	c[5] = new Coordinate(0, 0);
	return geometryFactory.createLinearRing(c);
    }
    
    public static LinearRing makeS2(int s){
	Coordinate[] c = new Coordinate[6];
	c[0] = new Coordinate(0, 0);
	c[1] = new Coordinate(s/2, s/2);
	c[2] = new Coordinate(0, s);
	c[3] = new Coordinate(s, s);
	c[4] = new Coordinate(s, 0);
	c[5] = new Coordinate(0, 0);
	return geometryFactory.createLinearRing(c);
    }
    
    public static LinearRing makeTriangle(Coordinate p1, Coordinate p2, Coordinate p3) {
	Coordinate[] coords = new Coordinate[4];
	coords[0] = p1;
	coords[1] = p2;
	coords[2] = p3;
	coords[3] = p1;
	LinearRing tri = geometryFactory.createLinearRing(coords);
	return tri;
    }


    public static LinearRing makeSquare(int segmentLength) {
	Coordinate[] coords = new Coordinate[5];
	coords[0] = new Coordinate(0, 0);
	coords[1] = new Coordinate(segmentLength, 0);
	coords[2] = new Coordinate(segmentLength, segmentLength);
	coords[3] = new Coordinate(0, segmentLength);
	coords[4] = new Coordinate(0, 0);	
	return geometryFactory.createLinearRing(coords);
    }
    
    
}






