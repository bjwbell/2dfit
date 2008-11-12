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
//import Util;


class Test
{
    public static GeometryFactory geometryFactory = null;
    public static void main(String args[]) throws java.lang.InterruptedException{
	geometryFactory = new GeometryFactory();
	Util.geometryFactory = geometryFactory;
	/*LinearRing s2 = makeS2(100);
	boolean s2IsCCW = CGAlgorithms.isCCW(s2.getCoordinates());
	System.out.println("s2.isCCW:" + s2IsCCW);
	Polygon p2 = new Polygon(s2, null, geometryFactory);
	LinearRing s1 = makeS1(100);
	boolean s1IsCCW = CGAlgorithms.isCCW(s1.getCoordinates());
	System.out.println("s1.isCCW:" + s1IsCCW);
	Polygon p1 = new Polygon(s1, null, geometryFactory);
	Geometry g = p2.difference(p1);
	Polygon p = (Polygon)g;
	System.out.println("g class: " + g.getClass().getName());
	LineString shell = p.getExteriorRing();
	System.out.println("shell class:" + shell.getClass().getName());
	System.out.println("num holes:" + p.getNumInteriorRing());
	PrintShape(shell);
	*/
	
	
	LinearRing s1 = makeS1(100);
	Coordinate p1 = new Coordinate(0, 0);
	Coordinate p2 = new Coordinate(-100, 0);
	Coordinate p3 = new Coordinate(0, 100);
	
	LinearRing tri = makeTriangle(p1, p2, p3);
	LinearRing newS1 = null;
	Util.PrintShape("s1", s1);
	Util.PrintShape("tri", tri);
	
	Polygon poly1 = new Polygon(s1, null, geometryFactory);
	Polygon poly2 = new Polygon(tri, null, geometryFactory);
	System.out.println("p1.covers(p2):" + poly1.covers(poly2));
	
	newS1 = Util.FitShape(s1, tri);
	
	Util.PrintShape("newS1", newS1);
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
	coords[3] = new Coordinate(p1.x, p1.y);
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