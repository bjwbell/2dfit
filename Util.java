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
    public static double EPSILON = 1e-6;
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



    public static LinearRing FitShape(LinearRing p1, LinearRing p2){
	FitShapeReturn ret = FitShape(p1, p2, 0, 0);
	if(ret == null){
	    return null;
	};
	return ret.resultShape;
    }


    public static ArrayList<LinearRing> clone(List<LinearRing> shapes){
	ArrayList<LinearRing> list = new ArrayList<LinearRing>();
	for(LinearRing shape : shapes){
	    list.add(shape);
	}
	return list;
    }

    public static Boolean Fit(LinearRing p1, List<LinearRing> shapes){
	if(shapes.size() == 0){
	    Polygon poly1 = new Polygon(p1, null, geometryFactory);
	    return p1.getArea() < EPSILON;
	}
	for(int k = 0; k < shapes.size(); k++){
	    int i = 0;
	    int j = 0;
	    ArrayList<LinearRing> shapes2 = clone(shapes);
	    shapes2.remove(k);
	    
	    while(true){
		FitShapeReturn ret = FitShape(p1, shapes.get(k), i, j);
		if(ret == null || ret.resultShape == null){
		    break;
		}
		if(Fit(ret.resultShape, shapes2)){
		    return true;
		}
		CoordIdxs idxs = AdvanceCoords(p1, shapes.get(k), i, j);
		if(idxs == null){
		    break;
		}else{
		    i = idxs.p1CoordIdx;
		    j = idxs.p2CoordIdx;
		}
	    }
	}
	
	return false;
	
    }

    public static class CoordIdxs {
	int p1CoordIdx;
	int p2CoordIdx;
    }
    

    public static CoordIdxs AdvanceCoords(LinearRing p1, LinearRing p2, int p1CoordIdx, int p2CoordIdx){
	if(p1.getCoordinateSequence().size() - 2 == p1CoordIdx && p2.getCoordinateSequence().size() - 2 == p2CoordIdx){
	    return null;
	}
	if(p2.getCoordinateSequence().size() - 2 == p2CoordIdx){
	    
	    CoordIdxs idxs = new CoordIdxs();
	    idxs.p2CoordIdx = 0;
	    idxs.p1CoordIdx = p1CoordIdx + 1;
	    return idxs;
	}
	CoordIdxs idxs = new CoordIdxs();
	idxs.p2CoordIdx = p2CoordIdx + 1;
	idxs.p1CoordIdx = p1CoordIdx;
	return idxs;
    }

    public static class FitShapeReturn {
	LinearRing resultShape;
	int p1CoordIdx;
	int p2CoordIdx;
    }


    // fits p2 to p1. That is p2 is a subset of p1.
    public static FitShapeReturn FitShape(LinearRing p1, LinearRing p2, int initialP1CoordIdx, int initialP2CoordIdx) {
	FitShapeReturn ret = new FitShapeReturn();	
	CoordinateSequence s1 = p1.getCoordinateSequence();
	boolean b = false;
	for(int i = initialP1CoordIdx; i < s1.size() - 1; i++) {
	    CoordinateSequence s2 = p2.getCoordinateSequence();
	    for(int j = initialP2CoordIdx; j < s2.size() - 1; j++) {
		boolean print = false;
		if( i == 2 && j == 1){
		    print = true;
		}
		Coordinate c1 = s1.getCoordinate(i);
		Coordinate c2 = s2.getCoordinate(j);
		Coordinate c12 = s1.getCoordinate(getNextCoordIdx(p1, i));
		Coordinate c22 = s2.getCoordinate(getNextCoordIdx(p2, j));
		if(print){
		    PrintCoord("c1", c1);
		    PrintCoord("c12", c12);
		    PrintCoord("c2", c2);
		    PrintCoord("c22", c22);
		}
		LinearRing r = null;
		//System.out.println("t1");
		r = MatchSegs(c1, c12, c2, c22, p1, p2);
		if(r != null){
		    ret.resultShape = r;
		    ret.p1CoordIdx = i;
		    ret.p2CoordIdx = j;
		    return ret;
		}
		c22 = s2.getCoordinate(getPrevCoordIdx(p2, j));
		//System.out.println("t2");
		r = MatchSegs(c1, c12, c2, c22, p1, p2);
		if(r != null){
		    ret.resultShape = r;
		    ret.p1CoordIdx = i;
		    ret.p2CoordIdx = j;
		    return ret;
		}
		

		c12 = s1.getCoordinate(getPrevCoordIdx(p1, i));
		//System.out.println("t3");
		r = MatchSegs(c1, c12, c2, c22, p1, p2);
		if(r != null){
		    ret.resultShape = r;
		    ret.p1CoordIdx = i;
		    ret.p2CoordIdx = j;
		    return ret;

		}
		

		c22 = s2.getCoordinate(getNextCoordIdx(p2, j));
		//System.out.println("t4");
		r = MatchSegs(c1, c12, c2, c22, p1, p2);
		if(r != null){
		    ret.resultShape = r;
		    ret.p1CoordIdx = i;
		    ret.p2CoordIdx = j;
		    return ret;
		}
		
		
		//System.out.println("tranX:" + translateX);
		//System.out.println("tranY:" + translateY);
		
	    }
	    
	}
	return null;
    }

    public static LinearRing MatchSegs(Coordinate c11, Coordinate c12, Coordinate c21, Coordinate c22, LinearRing p1, LinearRing p2){
	
	//System.out.println("j:" + j);
	
	//System.out.println("j2:" + getNextCoordIdx(s2, j));
	//PrintCoord("c2", c2);
	//PrintCoord("c22", c22);
	/*PrintCoord("c11", c11);
	PrintCoord("c12", c12);
	PrintCoord("c21", c21);
	PrintCoord("c22", c22);
	*/
	double translateX = c11.x - c21.x;
	double translateY = c11.y - c21.y;
	//System.out.println("translate:" + translateX + ", " + translateY);
	CoordinateSequence s1 = p1.getCoordinateSequence();
	CoordinateSequence s2 = p2.getCoordinateSequence();
	AffineTransformation translation = new AffineTransformation();

	translation.translate(translateX, translateY);
	p2.apply(translation);
	p2.geometryChanged();
	//PrintShape("p2", p2);


	AffineTransformation rotation = new AffineTransformation();
	double cosTheta = getCosTheta(c11, c12, c21, c22);
	//System.out.println("cosTheta:" + cosTheta);
	if((new Float(cosTheta)).toString().equals("NaN")){
	    PrintCoord("c11", c11);
	    PrintCoord("c12", c12);
	    PrintCoord("c21", c21);
	    PrintCoord("c22", c22);
	    System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR\nERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR\nERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
	    System.exit(0);
	}
	double sineTheta = getSineTheta(makeVector(c21, c22), makeVector(c11, c12));
	//System.out.println("sineTheta:" + sineTheta);
	Coordinate v1 = MakeVector(c11, c12);
	Coordinate w1 = MakeVector(c21, c22);
	if(CrossProd(v1, w1) < 0){
	    sineTheta = -1 * sineTheta;
	}
	rotation.rotate(sineTheta, cosTheta, c11.x, c11.y);
	//PrintShape("p2", p2);		
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
		//System.out.println("Returning null");
		//return null;
	    }
	}
	//PrintShape("p2", p2);	
	//System.out.println("sineTheta: " + -1 * sineTheta);
	//System.out.println("cosTheta: " + cosTheta);
	//PrintCoord("c11", c11);
	rotation = new AffineTransformation();
	rotation.rotate(-1 * sineTheta, cosTheta, c11.x, c11.y);
	p2.apply(rotation);
	p2.geometryChanged();
	
	//PrintShape("p2", p2);	
	translation.translate(-1 * translateX, -1 * translateY);
	p2.apply(translation);
	p2.geometryChanged();
	//System.exit(0);
	return null;
    }
    
    //makes a new vector going from c1 to c2.
    public static Coordinate makeVector(Coordinate c1, Coordinate c2){
	Coordinate v = new Coordinate(c2.x - c1.x, c2.y - c1.y);
	return v;
    }

    // return c1 x c2 = det(c1, c2)
    public static double crossProd(Coordinate c1, Coordinate c2){
	return c1.x * c2.y - c1.y * c2.x;
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
	//PrintCoord("v", v);
	double magV = getMag(v);
	//System.out.println("magV:" + magV);
	double wX = c22.x - c21.x;
	double wY = c22.y - c21.y;
	
	Coordinate w = new Coordinate(wX, wY);
	//PrintCoord("w", w);
	double magW = getMag(w);
	//System.out.println("magW:" + magW);
	return (vX * wX + vY * wY) / (magV * magW);

    }

    public static double getSineTheta(Coordinate v, Coordinate w){	
	return crossProd(v, w)/getMag(v) * 1/getMag(w);
	//return Math.sqrt(1 - cosTheta * cosTheta);
    }

    //makes a new vector going from c1 to c2.
    public static Coordinate MakeVector(Coordinate c1, Coordinate c2){
	Coordinate v = new Coordinate(c2.x - c1.x, c2.y - c1.y);
	return v;
    }

    // return c1 x c2 = det(c1, c2)
    public static double CrossProd(Coordinate c1, Coordinate c2){
	return c1.x * c2.y - c1.y * c2.x;
    }

    public static int getNextCoordIdx(CoordinateSequence s, int coordIdx) {
	if(coordIdx == s.size() - 1){
	    return 0;
	}
	return coordIdx + 1;
    }


    public static int getPrevCoordIdx(CoordinateSequence s, int coordIdx) {
	if(coordIdx == 0){	    
	    return s.size() - 1;
	}
	return coordIdx - 1;
    }


    public static int getNextCoordIdx(LinearRing r, int coordIdx) {
	if(coordIdx == r.getCoordinateSequence().size() - 1){
	    return 1;
	}
	return coordIdx + 1;
    }


    public static int getPrevCoordIdx(LinearRing r, int coordIdx) {
	if(coordIdx == 0){	    
	    return r.getCoordinateSequence().size() - 2;
	}
	return coordIdx - 1;
    }

    public static double getMag(Coordinate c) {
	return Math.sqrt(c.x * c.x + c.y * c.y);
    }

}