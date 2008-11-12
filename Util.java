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
import java.util.*;

class Util {
    public static GeometryFactory geometryFactory = null;
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


    // fits p2 to p1. That is p2 is a subset of p1.
    public static LinearRing FitShape(LinearRing p1, LinearRing p2) {
	CoordinateSequence s1 = p1.getCoordinateSequence();
	int numPoints = s1.size();
	boolean b = false;
	for(int i = 0; i < numPoints - 1; i++) {
	    if(b){
		break;
	    }
	    CoordinateSequence s2 = p2.getCoordinateSequence();
	    for(int j = 0; j < s2.size() - 1; j++) {
		Coordinate c1 = s1.getCoordinate(i);
		Coordinate c2 = s2.getCoordinate(j);
		PrintCoord("c1", c1);
		PrintCoord("c2", c2);
		double translateX = c1.x - c2.x;
		double translateY = c1.y - c2.y;
		System.out.println("tranX:" + translateX);
		System.out.println("tranY:" + translateY);
		AffineTransformation translation = new AffineTransformation();
		translation.translate(translateX, translateY);
		p2.apply(translation);
		p2.geometryChanged();
		c2 = s2.getCoordinate(j);
		PrintShape("p2", p2);
		Coordinate c12 = s1.getCoordinate(getNextCoordIdx(s1, i));
		Coordinate c22 = s2.getCoordinate(getNextCoordIdx(s2, j));
		
		System.out.println("j:" + j);

		System.out.println("j2:" + getNextCoordIdx(s2, j));
		PrintCoord("c2", c2);
		PrintCoord("c22", c22);
		AffineTransformation rotation = new AffineTransformation();
		double cosTheta = getCosTheta(c1, c12, c2, c22);
		System.out.println("cosTheta:" + cosTheta);
		if((new Float(cosTheta)).toString().equals("NaN")){
		    b = true;
		    break;
		}
		double sineTheta = getSineTheta(cosTheta);
		rotation.rotate(sineTheta, cosTheta, c1.x, c1.y);

		p2.apply(rotation);
		p2.geometryChanged();
		
		Polygon poly1 = new Polygon(p1, null, geometryFactory);
		Polygon poly2 = new Polygon(p2, null, geometryFactory);
		
		if(poly1.covers(poly2)){
		    Geometry g = poly1.difference(poly2);
		    LinearRing newP1 = ConvertToLinearRing(g);
		    if(newP1 != null){
			return newP1;
		    }else{
			return null;
		    }
		}
		

		rotation.rotate(-1 * sineTheta, cosTheta, c1.x, c1.y);
		p2.apply(rotation);
		p2.geometryChanged();
		

		translation.translate(-1 * translateX, -1 * translateY);
		p2.apply(translation);
		p2.geometryChanged();
	    }
	    
	}
	return null;
    }

    public static void PrintCoord(String name, Coordinate c){
	System.out.println(name + ":" + c.x + ", " + c.y);
    }

    public static void PrintShape(String name, LineString l){
	CoordinateSequence s = l.getCoordinateSequence();
	System.out.println(name + ":");
	for(int i = 0; i < s.size(); i++){
	    System.out.println("    Point:" + s.getX(i) + ", " + s.getY(i));
	}
    }

    public static LinearRing ConvertToLinearRing(Geometry g){
	if(! (g instanceof Polygon)){
	    return null;
	}
	Polygon p = (Polygon)g;
	if(p.getNumInteriorRing() != 0){
	    return null;
	}
	if(!(p.getExteriorRing() instanceof LinearRing)){
	    return null;
	}
	return (LinearRing)p.getExteriorRing();
    }

    public static double getCosTheta(Coordinate c11, Coordinate c12, Coordinate c21, Coordinate c22){
	double vX = c12.x - c11.x;
	double vY = c12.y - c11.y;
	Coordinate v = new Coordinate(vX, vY);
	PrintCoord("v", v);
	double magV = getMag(v);
	System.out.println("magV:" + magV);
	double wX = c22.x - c21.x;
	double wY = c22.y - c21.y;
	
	Coordinate w = new Coordinate(wX, wY);
	PrintCoord("w", w);
	double magW = getMag(w);
	System.out.println("magW:" + magW);
	return (vX * wX + vY * wY) / (magV * magW);

    }

    public static double getSineTheta(double cosTheta){
	return Math.sqrt(1 - cosTheta * cosTheta);
    }

    public static int getNextCoordIdx(CoordinateSequence s, int coordIdx) {
	if(coordIdx == s.size() - 1){
	    return 0;
	}
	return coordIdx + 1;
    }

    public static double getMag(Coordinate c) {
	return Math.sqrt(c.x * c.x + c.y * c.y);
    }

}