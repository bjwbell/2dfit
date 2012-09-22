//JTS imports, ...
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryCollectionIterator;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.operation.overlay.snap.GeometrySnapper;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import java.util.*;

class Util {
    public static GeometryFactory geometryFactory = null;
    public static double EPSILON = 1e-4;
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

    
    /**
     * Makes the shape
     * |       |
     * | |   | | 
     * |   |   |
     * |       |
     * |-------|  
     *   
     * @param s upper right corner is at coordinate (s,s).
     * @return shape
     */
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
    
    /**
     * Makes the shape
     * 
     *           
     *      * |
     *   *    |
     * *      |
     *  *     |
     *   *    |
     *  *     |
     * *------| 
     * 
     * @param s, upper right corner is at coordinate (s,s).
     * @return shape
     */
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
    
    /**
     * Creates triangle with sides of length size.
     * @param size
     * @return triangle
     */
    public static LinearRing makeTriangle(double size){
    	return makeTriangle(new Coordinate(0, 0), new Coordinate(0, size), new Coordinate(size, 0));
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


    public static LinearRing makeSquare(double segmentLength) {
    	Coordinate[] coords = new Coordinate[5];
    	coords[0] = new Coordinate(0, 0);
    	coords[1] = new Coordinate(segmentLength, 0);
    	coords[2] = new Coordinate(segmentLength, segmentLength);
    	coords[3] = new Coordinate(0, segmentLength);
    	coords[4] = new Coordinate(0, 0);	
    	return geometryFactory.createLinearRing(coords);
    }



    /*public static LinearRing FitShape(LinearRing p1, LinearRing p2) throws Exception {
    	FitShapeReturn ret = FitShape(p1, p2, 0, 0, 1);
    	if(ret == null){
    		return null;
    	};
    	return ret.resultShape;
    }*/


    public static ArrayList<Shape> clone(List<Shape> shapes){
    	assert shapes != null;
    	ArrayList<Shape> list = new ArrayList<Shape>();
    	for(Shape shape : shapes){
    		list.add(shape);
    	}
    	return list;
    }
    
    public static boolean CanSkipCoord(Shape shape, int coordIndex){

    	// Triangle, can skip last coordinate.
    	if ((shape.type == TypeOfShape.LARGE_TRIANGLE || shape.type == TypeOfShape.MEDIUM_TRIANGLE || 
    		shape.type == TypeOfShape.SMALL_TRIANGLE) && coordIndex > 1)
    	{
    		return true;
    	}

    	// Square, can skip all but first coordinate.
    	if (shape.type == TypeOfShape.SQUARE && coordIndex > 0)
    	{
    		return true;
    	}
    	
    	if (shape.type == TypeOfShape.PARALLELOGRAM && (coordIndex == 1 || coordIndex == 3)){
    		return true;    	
    	}
    	
    	return false;
    }

    public static boolean CanSkipShape(List<Shape> shapes, int shapeIndex){
    	// The list of shapes is ordered as large Tri1, large Tri2, medium Tri, small Tri1, small Tri2, parallelogram, square
    	// Can skip large Tri2, and small tri2, which have index of 
    	// 1 and 4 respectively.
    	int largeTriangleIdx1 = -1;
    	int largeTriangleIdx2 = -1;
    	int smallTriangleIdx1 = -1;
    	int smallTriangleIdx2 = -1;
    	for (int i = 0; i < shapes.size(); i++) 
    	{
    		if (shapes.get(i).type == TypeOfShape.LARGE_TRIANGLE) {
    			if (largeTriangleIdx1 == -1){
    				largeTriangleIdx1 = i;
    			} else {
    				largeTriangleIdx2 = i;
    			}
    		}
    		
    		if (shapes.get(i).type == TypeOfShape.SMALL_TRIANGLE) {
    			if (smallTriangleIdx1 == -1){
    				smallTriangleIdx1 = i;
    			} else {
    				smallTriangleIdx2 = i;
    			}
    		}
    	}
    	
    		
    	return (shapeIndex == largeTriangleIdx2) || 
    			(shapeIndex == smallTriangleIdx2);
    }
   
    
    public static int TotalNumShapes;
    public static boolean TRACE = false;
    public static List<Shape> originalShapes = null;
    public static int getShapeIndex(LinearRing shape)
    {
    	return originalShapes.indexOf(shape);
    }
    
    public static String toStringTraceData(List<int[]> traceData){
    	String str = "";
    	for (int i = 0; i < traceData.size(); i++){
    		str = str + (str != "" ? "," : "") + "[" + traceData.get(i)[0] + "/" + traceData.get(i)[1] + ", " + 
    				traceData.get(i)[2] + "/" + traceData.get(i)[3] + "," + traceData.get(i)[4] + "/" + traceData.get(i)[5] + "]";
    	}
    	return str;
    }
    
    public static List<int[]> cloneTrace(List<int[]> trace){
    	List<int[]> clone = new ArrayList<int[]>();
    	for (int i = 0; i < trace.size(); i++){
    		clone.add(trace.get(i));
    	}
    	return clone;
    }
    
    public static boolean flipped = false;
    public static Boolean Fit(MultiPolygon p1, List<Shape> shapes, List<int[]> traceData) throws Exception {
    	if (traceData == null && TRACE){
    		traceData = new ArrayList<int[]>();    		
    	}else if (TRACE){
    		traceData = cloneTrace(traceData);
    	}
    	if(shapes.size() == 0){
    		return true; 
    	}
    	int numberOfShapes = shapes.size();
    	int callStackIndent = Thread.currentThread().getStackTrace().length - 3;
    	if (callStackIndent < 0)
    	{
    		callStackIndent = 0;    	
    	}
    	String prependStr = "";
    	if (TRACE){
    		for (int i = 0; i < callStackIndent; i++){
    			prependStr = "    " + prependStr;
    		}
    	}
    	if (TRACE) {
    		Shape fittedShape = null;
    		for (int i = 0; i < originalShapes.size(); i++){
    			if (shapes.get(0) == originalShapes.get(i) && i > 0)
    			{    			
    				fittedShape = originalShapes.get(i - 1);
    				break;
    			}
    		}
    		if (fittedShape != null)
    			System.out.println(prependStr + fittedShape.shape.toText());
    		System.out.println(prependStr + p1.toText());
    	}
    	
    	int[] traceItem = new int[6];
    	for(int k = 0; k < shapes.size(); k++){
    		int i = 0;
    		int j = 0;
    		if (CanSkipShape(shapes, k)){
    			continue;
    		}
    		ArrayList<Shape> shapes2 = clone(shapes);
    		shapes2.remove(k);
    		
    		while(true){
    			Coordinate[] s1 = p1.getCoordinates();    			
			LinearRing p2 = shapes.get(k).shape;
			CoordinateSequence s2 = p2.getCoordinateSequence();
			Coordinate c1 = s1[i];
			Coordinate c2 = s2.getCoordinate(j);
			Coordinate c12 = s1[getNextCoordIdx(p1, i)];
			Coordinate c22 = s2.getCoordinate(getNextCoordIdx(s2, j));    	    	
			MultiPolygon r = null;

			r = MatchSegs(c1, c12, c2, c22, p1, p2, numberOfShapes);
			if(r != null){
			    if(Fit(r, shapes2, traceData)) {
				return true;
			    }
			}
			c22 = s2.getCoordinate(getPrevCoordIdx(s2, j));
			r = MatchSegs(c1, c12, c2, c22, p1, p2, numberOfShapes);
			if(r != null){
			    if(Fit(r, shapes2, traceData)){
				return true;
			    }
			}
    			
			c12 = s1[getPrevCoordIdx(p1, i)];
			r = MatchSegs(c1, c12, c2, c22, p1, p2, numberOfShapes);
			if(r != null){
			    if(Fit(r, shapes2, traceData)){
				return true;
			    }
			}
    			
			c22 = s2.getCoordinate(getNextCoordIdx(p2.getCoordinateSequence(), j));
			r = MatchSegs(c1, c12, c2, c22, p1, p2, numberOfShapes);
			if(r != null){
			    if(Fit(r, shapes2, traceData)){
				return true;
			    }
			}		
    			
			CoordIdxs idxs = AdvanceCoords(p1, shapes.get(k).shape, i, j);
			while (idxs != null && idxs.p1CoordIdx == i && CanSkipCoord(shapes.get(k), idxs.p2CoordIdx)){
			    idxs = AdvanceCoords(p1, shapes.get(k).shape, i, idxs.p2CoordIdx);
			}
			if(idxs == null){
			    if (shapes.size() == originalShapes.size() - 1 && !flipped){
				int pIdx = -1;
				for (int q = 0; q < shapes.size(); q++){
				    if (shapes.get(q).type == TypeOfShape.PARALLELOGRAM){
					pIdx = q;
					break;
				    }
				}
				if (pIdx != -1){
				    Shape s = shapes.get(pIdx);
				    AffineTransformation ref = new AffineTransformation();
				    ref.reflect(0, 1);
				    s.shape.apply(ref);
				    s.shape.geometryChanged();
				    i = 0;
				    j = 0;
				    flipped = true;
				    continue;
				}
			    }    				
			    if (shapes.get(k).type == TypeOfShape.LARGE_TRIANGLE || shapes.get(k).type == TypeOfShape.MEDIUM_TRIANGLE){
				return false;
			    } else {
				break;
			    }    				
			}else{
			    i = idxs.p1CoordIdx;
			    j = idxs.p2CoordIdx;
			}
    		}
    	}
    	if (TRACE){
	    System.out.println();
    	}
    	return false;	
    }
    
    public static class CoordIdxs {
    	int p1CoordIdx;
    	int p2CoordIdx;
    }
    

    public static CoordIdxs AdvanceCoords(MultiPolygon p1, LinearRing p2, int p1CoordIdx, int p2CoordIdx){
    	if(p1.getCoordinates().length - 2 == p1CoordIdx && p2.getCoordinateSequence().size() - 2 == p2CoordIdx){
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

    public static Boolean debug = false;
    public static Boolean matchSegsDebug = false;    

    public static AffineTransformation Rotate(double sinTheta, double cosTheta, double x, double y){
    	AffineTransformation rot = new AffineTransformation();
    	rot.translate(-x, -y);
    	rot.rotate(sinTheta, cosTheta, 0, 0);
    	rot.translate(x, y);
    	return rot;
    }
    
    public static MultiPolygon makeMulti(Polygon p){
    	Polygon[] t = new Polygon[1];
    	t[0] = p;
    	return new MultiPolygon(t, geometryFactory);
    }

    public static MultiPolygon copy(MultiPolygon p) throws Exception{
    	assert p != null;
    	Polygon[] polys = new Polygon[p.getNumGeometries()];
    	for (int i = 0; i < p.getNumGeometries(); i++){
    		if (!(p.getGeometryN(i) instanceof Polygon)){
    			throw new Exception("can't copy multipolygon");
    		}
    		polys[i] = copy((Polygon)p.getGeometryN(i));
    	}
    	
    	return new MultiPolygon(polys, geometryFactory);    	
    }

    public static Polygon copy(Polygon p){
    	assert p != null;    	
    	LinearRing exterior = copy(convertToLinearRing(p.getExteriorRing()));
    	
    	LinearRing[] interiorRings = new LinearRing[p.getNumInteriorRing()];
    	
    	for (int i = 0; i < p.getNumInteriorRing(); i++){
    		interiorRings[i] = copy(convertToLinearRing(p.getInteriorRingN(i)));
    	}
    	
    	return new Polygon(exterior, interiorRings, geometryFactory);    	
    }

    
    public static LinearRing copy(LinearRing p){
    	assert p != null;
    	Coordinate[] coords = p.getCoordinates();
    	Coordinate[] ccoords = new Coordinate[coords.length];
    	for(int i = 0; i < coords.length; i++){
    		Coordinate coord = new Coordinate();
    		coord.x = coords[i].x;
    		coord.y = coords[i].y;
    		ccoords[i] = coord;
    	}
    	return geometryFactory.createLinearRing(ccoords);
    }

    // Rotate the segment c21->c22 into the segment c11->c12.
    public static MultiPolygon MatchSegs(Coordinate c11, Coordinate c12, Coordinate c21, Coordinate c22, MultiPolygon p1, LinearRing pp2, int numberOfShapes) throws Exception {
	    p1 = copy(p1);
    	LinearRing p2 = copy(pp2);
    	
    	double translateX = c11.x - c21.x;
    	double translateY = c11.y - c21.y;
    	AffineTransformation translation = new AffineTransformation();
    	translation.translate(translateX, translateY);
    	p2.apply(translation);
    	p2.geometryChanged();
    	
    	AffineTransformation rotation = new AffineTransformation();
    	double cosTheta = getCosTheta(c11, c12, c21, c22);
    	if((new Float(cosTheta)).toString().equals("NaN")){
    		System.out.println("ERROR: NAN");
    		System.exit(0);
    	}
    	// Rotating back, so multiply by -1, since sin(-theta) = -1 * sin(theta).
    	// Don't need for cosTheta, since cos(-theta) = cos(theta). 
    	double sinTheta = getSinTheta(makeVector(c21, c22), makeVector(c11, c12));
    	//if(CrossProd(v1, w1) < 0){
    	//   sinTheta = -1 * sinTheta;
    	//}

    	rotation = Rotate(sinTheta, cosTheta, c11.x, c11.y);
    	p2.apply(rotation);
    	p2.geometryChanged();
    	Polygon poly22 = new Polygon(p2, null, geometryFactory);
    	MultiPolygon newP1 = ReturnNewShape(poly22, p1, p2);
        if(newP1 != null){            
        	pp2.apply(translation);
        	pp2.geometryChanged();
        	pp2.apply(rotation);
        	pp2.geometryChanged();        	
        	return newP1;
        }        
        return null;
    }

    private static final int DIF_OP = 0;
    private static final int UNION_OP = 1;
    private static final int COVER_PRED = 0;

    public static MultiPolygon ReturnNewShape(Polygon poly22, MultiPolygon p1, LinearRing p2) throws Exception {
	MultiPolygon poly1 = copy(p1);
    	if(SemiRobustGeoPred(poly1, poly22, COVER_PRED)) { //g.covers(poly22)) {
    		Polygon poly2 = new Polygon(p2, null, geometryFactory);
            	Geometry gg = SemiRobustGeoOp(poly1, poly2, DIF_OP);
            	MultiPolygon newP1 = convertToMultiPolygon(gg);
            	return newP1;
    	} else {
    		return null;
    	}
    }

    public static Geometry SemiRobustGeoOp(Geometry g1, Geometry g2, int op) throws Exception {
        double e1 = EPSILON/10;
        double snapTolerance = GeometrySnapper.computeOverlaySnapTolerance(g1, g2);
        while (snapTolerance < e1) {
            try {
                Geometry[] geos = GeometrySnapper.snap(g1, g2, snapTolerance);
                switch (op) {                    
                case DIF_OP:   // difference
                    return geos[0].difference(geos[1]);
                case UNION_OP: // union
                    return geos[0].union(geos[1]);
                default:
                    throw new Exception("unhandled semirobustgeoop: " + op);
                }
            } catch (TopologyException e){
                snapTolerance *= 2;
            }
        }
        return null;
    }

    public static boolean SemiRobustGeoPred(Geometry g1, Geometry g2, int pred) throws Exception {
        double e1 = EPSILON/10;
        double snapTolerance = GeometrySnapper.computeOverlaySnapTolerance(g1, g2);
        while (snapTolerance < e1) {
            try {
                Geometry[] geos = GeometrySnapper.snap(g1, g2, snapTolerance);
                switch (pred) {                    
                case COVER_PRED: // 
                    return geos[0].covers(geos[1]);
                default:
                    throw new Exception("unhandled semirobustgeopred: " + pred);
                }
            } catch (TopologyException e){
                snapTolerance *= 2;
            }
        }
        return false;
    }
    

    public static void verify(LinearRing p){
	
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

    
    public static MultiPolygon convertToMultiPolygon(Geometry g){
    	if(g instanceof LinearRing){
    	    return Util.makeMulti(new Polygon((LinearRing)g, null, geometryFactory));
    	}
    	if(g instanceof LineString){
    	    if(((LineString)g).isRing()){    	    	
    	    	return Util.makeMulti(new Polygon(geometryFactory.createLinearRing(((LineString)g).getCoordinates()), null, geometryFactory));
    	    }
    	    System.out.println("t0");
    	    return null;
    	}
    	if (g instanceof GeometryCollection){
    		if (((GeometryCollection)g).getNumGeometries() == 0){
    			return Util.makeMulti(new Polygon(null, null, geometryFactory));
    		}else if (g.getNumGeometries() == 1){
    			GeometryCollectionIterator ci = new GeometryCollectionIterator(g);
    			while (ci.hasNext())
    			{
    				Object next = ci.next();
    				if (((Geometry)next) instanceof GeometryCollection)
    				{
    					continue;				
    				}
    				return convertToMultiPolygon((Geometry)ci.next());
    			}
    		}
    	}
    	if(! (g instanceof MultiPolygon) && !(g instanceof Polygon)){
    	    if(debug){
    		System.out.println(g.getClass().getName());
    		System.out.println("t1");
    	    }
    	    return null;
    	}    	
    	if (g instanceof Polygon){
    		return Util.makeMulti((Polygon)g);
    	}
    	return (MultiPolygon)g;    	
    }
    
    public static Polygon convertToPolygon(Geometry g){
    	if(g instanceof LinearRing){
    	    return new Polygon((LinearRing)g, null, geometryFactory);
    	}
    	if(g instanceof LineString){
    	    if(((LineString)g).isRing()){    	    	
    	    	return new Polygon(geometryFactory.createLinearRing(((LineString)g).getCoordinates()), null, geometryFactory);
    	    }
    	    System.out.println("t0");
    	    return null;
    	}
    	if (g instanceof GeometryCollection){
    		if (((GeometryCollection)g).getNumGeometries() == 0){
    			return new Polygon(null, null, geometryFactory);
    		}else if (g.getNumGeometries() == 1){
    			GeometryCollectionIterator ci = new GeometryCollectionIterator(g);
    			while (ci.hasNext())
    			{
    				Object next = ci.next();
    				if (((Geometry)next) instanceof GeometryCollection)
    				{
    					continue;				
    				}
    				return convertToPolygon((Geometry)ci.next());
    			}
    		}
    	}
    	if(! (g instanceof Polygon)){
    	    if(debug){
    		System.out.println(g.getClass().getName());
    		System.out.println("t1");
    	    }
    	    return null;
    	}    	
    	return (Polygon)g;
    }

    
    public static LinearRing convertToLinearRing(Geometry g){
    	if(g instanceof LinearRing){
    		return (LinearRing)g;
    	}
    	if(g instanceof LineString){
    		if(((LineString)g).isRing()){
    			return geometryFactory.createLinearRing(((LineString)g).getCoordinates());
    		}
    		System.out.println("t0");
    		return null;
    	}
    	if (g instanceof GeometryCollection){
    		if (((GeometryCollection)g).getNumGeometries() == 0){
    			return new LinearRing(null, geometryFactory);
    		}else if (g.getNumGeometries() == 1){
    			GeometryCollectionIterator ci = new GeometryCollectionIterator(g);
    			while (ci.hasNext())
    			{
    				Object next = ci.next();
    				if (((Geometry)next) instanceof GeometryCollection)
    				{
    					continue;				
    				}
    				return convertToLinearRing((Geometry)ci.next());
    			}
    		}
    	}
    	if(! (g instanceof Polygon)){
    		if(debug){
    			System.out.println(g.getClass().getName());
    			System.out.println("t1");
    		}
    		return null;
    	}
    	Polygon p = (Polygon)g;
    	if(p.getNumInteriorRing() != 0){
    		//System.out.println("t2");
    		return null;
    	}
    	if(!(p.getExteriorRing() instanceof LinearRing)){
    		System.out.println("t3");
    		return null;
    	}
    	if(debug){
    		System.out.println("t4");
    	}
    	return (LinearRing)p.getExteriorRing();
    }

    public static double getCosTheta(Coordinate c11, Coordinate c12, Coordinate c21, Coordinate c22){
    	double vX = c12.x - c11.x;
    	double vY = c12.y - c11.y;

    	Coordinate v = new Coordinate(vX, vY);
    	double magV = getMag(v);

    	double wX = c22.x - c21.x;
    	double wY = c22.y - c21.y;
	
    	Coordinate w = new Coordinate(wX, wY);
    	double magW = getMag(w);

    	return (vX * wX + vY * wY) / (magV * magW);

    }

    public static double getSinTheta(Coordinate v, Coordinate w){	
	return crossProd(v, w)/getMag(v) * 1/getMag(w);
	//return Math.sqrt(1 - cosTheta * cosTheta);
    }

    //makes a new vector going from c1 to c2.
    public static Coordinate MakeVector(Coordinate c1, Coordinate c2){
	return new Coordinate(c2.x - c1.x, c2.y - c1.y);
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
    		return s.getCoordinate(s.size() - 1).equals2D(s.getCoordinate(coordIdx)) ? s.size() - 2 : s.size() - 1;
    		//return s.size() - 2; // minus 2 to avoid the coordinate duplication
    	}
    	return coordIdx - 1;
    }


    public static int getNextCoordIdx(MultiPolygon r, int coordIdx) {
    	if(coordIdx == r.getCoordinates().length - 1){
    		return 1;
    	}
    	
    	if (CoordEqual(r.getCoordinates()[coordIdx], r.getCoordinates()[coordIdx+1])) {
    		return getNextCoordIdx(r, coordIdx + 1);    			
    	}
    	return coordIdx + 1;
    }


    public static int getPrevCoordIdx(MultiPolygon r, int coordIdx) {
    	if(coordIdx == 0){	    
    		return r.getCoordinates().length - 2;
    	}
    	if (CoordEqual(r.getCoordinates()[coordIdx], r.getCoordinates()[coordIdx-1])) {
    		return getPrevCoordIdx(r, coordIdx - 1);    			
    	}
    	
    	return coordIdx - 1;
    }

    public static double getMag(Coordinate c) {        
    	return Math.sqrt(c.x * c.x + c.y * c.y);
    }


    public static ArrayList<LinearRing> makeTangramPieces(){
    	ArrayList<LinearRing> pieces = new ArrayList<LinearRing>();
    	pieces.add(makeLargeTriangle());
    	pieces.add(makeLargeTriangle());
    	pieces.add(makeMediumTriangle());	
    	pieces.add(makeSmallTriangle());
    	pieces.add(makeSmallTriangle());

    	pieces.add(makeSSquare());
    	pieces.add(makeParallelogram());
    	return pieces;
    }

    public static double scaleFactor = 400;

    public static LinearRing makeSmallTriangle(){
    	double side = 1.0/Math.sqrt(2.0) * 1/2.0 * scaleFactor;
    	Coordinate p1 = new Coordinate(0, 0);
    	Coordinate p2 = new Coordinate(0, side);
    	Coordinate p3 = new Coordinate(side, 0);
	
    	LinearRing tri = Util.makeTriangle(p1, p2, p3);
    	return tri;
    }

    public static LinearRing makeMediumTriangle(){
    	double side = 1.0/2.0 * scaleFactor;
    	Coordinate p1 = new Coordinate(0, 0);
    	Coordinate p2 = new Coordinate(0, side);
    	Coordinate p3 = new Coordinate(side, 0);	
    	LinearRing tri = Util.makeTriangle(p1, p2, p3);
    	return tri;
    }

    public static LinearRing makeLargeTriangle(){
    	double side = 1.0/Math.sqrt(2.0) * scaleFactor;
    	Coordinate p1 = new Coordinate(0, 0);
    	Coordinate p2 = new Coordinate(0, side);
    	Coordinate p3 = new Coordinate(side, 0);
	
    	LinearRing tri = Util.makeTriangle(p1, p2, p3);
    	return tri;
    }

    public static LinearRing makeSSquare(){
    	double side  = 1.0/2.0 * 1.0/Math.sqrt(2.0) * scaleFactor;
    	return Util.makeSquare(side);
    }

    public static LinearRing makeParallelogram(){
    	Coordinate[] coords = new Coordinate[5];
    	coords[0] = new Coordinate(0, 0);
    	coords[1] = new Coordinate(0, 0.5 * scaleFactor);
    	coords[2] = new Coordinate(0.25 * scaleFactor, 0.75 * scaleFactor);
    	coords[3] = new Coordinate(0.25 * scaleFactor, 0.25 * scaleFactor);
    	coords[4] = new Coordinate(0, 0);
    	return geometryFactory.createLinearRing(coords);
    }
    
    /*
     * Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2. 
     */
    public static LinearRing makeParallelogramL1(){
    	Coordinate[] coords = new Coordinate[5];
    	coords[0] = new Coordinate(0, 0);
    	coords[1] = new Coordinate(0.5, 0.5);
    	coords[2] = new Coordinate(1.5 , 0.5);
    	coords[3] = new Coordinate(1 , 0 );
    	coords[4] = new Coordinate(0, 0);
    	return geometryFactory.createLinearRing(coords);
    }
    
    public static boolean CoordEqual(Coordinate c1, Coordinate c2){
    	return Math.abs(c1.x - c2.x) < EPSILON && Math.abs(c1.y - c2.y) < EPSILON; 
    }

}
