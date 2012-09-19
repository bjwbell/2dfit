//JTS imports
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.io.WKTReader;

import java.util.*;
//import Util;


public class Test
{
    public static GeometryFactory geometryFactory = null;
    public static double EPSILON = 1e-6;
    public static void main(String args[]) throws java.lang.InterruptedException, Exception {
        geometryFactory = new GeometryFactory();
        Util.geometryFactory = geometryFactory;
        System.out.println("Test1 " + Test1());
        //System.out.println("Test2 " + Test2());
        //System.out.println("RotateTest1 " + RotateTest1());
        System.out.println("RotateTest2 " + RotateTest2());
        System.out.println("CloneTest1 " + CloneTest1());
        System.out.println("CopyTest1 " + CopyTest1());
        /*System.out.println("FitShapeTest1 " + FitShapeTest1());
        System.out.println("FitShapeTest2 " + FitShapeTest2());
        System.out.println("FitShapeTest3 " + FitShapeTest3());
        System.out.println("FitShapeTest4 " + FitShapeTest4());*/
        System.out.println("FitTest1 " + FitTest1());
        System.out.println("FitTest2 " + FitTest2());
        System.out.println("FitTest3 " + FitTest3());
        System.out.println("FitTest4 " + FitTest4());
        System.out.println("FitTest5 " + FitTest5());
        System.out.println("FitTest6 " + FitTest6());
        System.out.println("FitTest7 " + FitTest7());
        System.out.println("FitTest8 " + FitTest8());
        System.out.println("FitTest9_0 " + FitTest9_0());
        System.out.println("FitTest9_1 " + FitTest9_1());
        System.out.println("FitTest9_2 " + FitTest9_2());
        System.out.println("FitTest10 " + FitTest10());
        System.out.println("FitTest10_1 " + FitTest10_1());
        System.out.println("FitTest11 " + FitTest11());
        System.out.println("FitTest_BirdShape1 " + FitTest_BirdShape1());
        System.out.println("FitTest_HumanShape1 " + FitTest_HumanShape1());
        System.out.println("FitTest_HumanShape1 " + FitTest_HumanShape1());
        System.out.println("FitTest_ToSingleLargeTriangle " + FitTest_ToSingleLargeTriangle());
        /*assert Test1();
        assert Test2();
        assert RotateTest1();
        assert RotateTest2();
        assert CloneTest1();
        assert CopyTest1();
        assert FitShapeTest1();
        assert FitShapeTest2();
        assert FitShapeTest3();
        assert FitShapeTest4();
        assert FitTest1();
        assert FitTest2();*/
    }

    public static boolean Test1() throws Exception {
    	LinearRing s1 = Util.makeS1(100);
    	LinearRing tri = Util.makeTriangle(new Coordinate(0, 0), new Coordinate(-100, 0), new Coordinate(0, 100));
    	Polygon poly1 = new Polygon(s1, null, geometryFactory);
    	Polygon poly2 = new Polygon(tri, null, geometryFactory);
        return !poly1.covers(poly2);	
    }

        public static boolean RotateTest2() throws Exception {
        Coordinate c1 = new Coordinate(100, 0);
        Coordinate c2 = new Coordinate(50, 50);
        Coordinate c3 = new Coordinate(100, 100);
        LinearRing tri2 = Util.makeTriangle(c1, c2, c3);
        double sinTheta = 1;
        double cosTheta = 0;
        AffineTransformation rot = Util.Rotate(sinTheta, cosTheta, 0, 0);
        tri2.apply(rot);
        tri2.geometryChanged();
        return c1.x == 0 && c1.y == 100 && Math.abs(c2.x + 50) < EPSILON && Math.abs(c2.y - 50) < EPSILON && Math.abs(c3.x + 100) < EPSILON && Math.abs(c3.y - 100) < EPSILON;
    }

    public static boolean CloneTest1() throws Exception {
    	ArrayList<Shape> shapes = new ArrayList<Shape>();
    	shapes.add(new Shape(Util.makeSmallTriangle(), TypeOfShape.SMALL_TRIANGLE));
    	shapes.add(new Shape(Util.makeMediumTriangle(), TypeOfShape.MEDIUM_TRIANGLE));
    	shapes.add(new Shape(Util.makeLargeTriangle(), TypeOfShape.LARGE_TRIANGLE));
    	return Util.clone(shapes) != shapes;
    }


    public static boolean CopyTest1() throws Exception {
    	LinearRing lr = Util.makeSmallTriangle();
    	LinearRing lr2 = Util.copy(lr);
    	lr.getCoordinates()[0].x += -1000;
    	boolean testResult = true;
    	for(int i = 0; i < lr.getCoordinates().length; i++){
    		lr.getCoordinates()[i].x += -1000;
    		lr.getCoordinates()[i].y += 9;
    		testResult &= lr2.getCoordinates()[i].x != lr.getCoordinates()[i].x && lr2.getCoordinates()[i].y != lr.getCoordinates()[i].y;
    	}
    	return testResult;
    }

        public static boolean FitTest1() throws Exception {

      	Shape st1 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
      	Shape st2 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
      	Polygon[] p = new Polygon[1];
      	p[0] = new Polygon(Util.makeSquare(1), null, geometryFactory);
      	MultiPolygon squ = new MultiPolygon(p, geometryFactory);
      	AffineTransformation rot = Util.Rotate(1/Math.sqrt(2), 1/Math.sqrt(2), 0, 0);
      	st2.shape.apply(rot);
      	st2.shape.geometryChanged();
      	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(squ, shapes, null);
      	//retVal &= shapes.get(0).shape.toText().equals("LINEARRING (0 0, 0 1, 1 0, 0 0)");
      	//retVal &= shapes.get(1).shape.toText().equals("LINEARRING (1.0000000000000002 1, 1.0000000000000007 0, 0 1, 1.0000000000000002 1)");
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	           
      	return retVal;
    }
    

    public static boolean FitTest2() throws Exception {

      	Shape st1 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
      	Shape st2 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
      	Polygon st3 = new Polygon(Util.makeTriangle(Math.sqrt(2)), null, geometryFactory);
      	Polygon[] t = new Polygon[1];
      	t[0] = st3;
      	MultiPolygon mst3 = new MultiPolygon(t, geometryFactory);
      	AffineTransformation rot = Util.Rotate(1/Math.sqrt(2), 1/Math.sqrt(2), 0, 0);
      	st2.shape.apply(rot);
      	st2.shape.geometryChanged();
      	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(mst3, shapes, null);
      	
      	//retVal &= shapes.get(0).shape.toText().equals("LINEARRING (0.7071067811865477 0.7071067811865479, 1.4142135623730954 0, 0 0.0000000000000002, 0.7071067811865477 0.7071067811865479)");
      	//retVal &= shapes.get(1).shape.toText().equals("LINEARRING (0.7071067811865539 0.707106781186555, 0 0.0000000000000002, -0 1.4142135623731098, 0.7071067811865539 0.707106781186555)");
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	
      	return retVal;
    }

    /*
     * Tests fitting to the shape
     *                 .  
     *             .   |  
     *          .      |
     *      .          |   
     *        .        |
     *         .       |
     *          .      |
     *        .        |
     *      .          |
     *      ............
     * 
     * 
     */
    public static boolean FitTest3() throws Exception {

      	Shape st1 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
      	Shape st2 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	Shape st3 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);
      	
      	Coordinate[] coords = new Coordinate[6];
    	coords[0] = new Coordinate(0, 0);
    	coords[1] = new Coordinate(1/2.0, 1/2.0);
    	coords[2] = new Coordinate(0, 1);
    	coords[3] = new Coordinate(1, 2);
    	coords[4] = new Coordinate(1, 0);
    	coords[5] = new Coordinate(0, 0);
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	Polygon[] t = new Polygon[1];
    	t[0] = shape;
    	MultiPolygon mshape = new MultiPolygon(t, geometryFactory);
    	/*AffineTransformation rot = Util.Rotate(1/Math.sqrt(2), 1/Math.sqrt(2), 0, 0);
      	st2.apply(rot);
      	st2.geometryChanged();*/
      	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(mshape, shapes, null);
      	
      	//retVal &= shapes.get(0).shape.toText().equals("LINEARRING (0.9999999999999997 0, 0 0, 0.9999999999999997 0.9999999999999997, 0.9999999999999997 0)");
      	//retVal &= shapes.get(1).shape.toText().equals("LINEARRING (0.5 1.5000000000000004, 1 2, 0.9999999999999998 1.0000000000000004, 0.5 1.5000000000000004)");
      	//retVal &= shapes.get(2).shape.toText().equals("LINEARRING (0.5 0.5, 1.0000000000000173 1.000000000000001, 0.5000000000000127 1.500000000000016, -0.0000000000000062 1.0000000000000062, 0.5 0.5)");
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(2).shape.toText() + "\n");
      	
      	return retVal;
    }
    

    public static boolean FitTest4() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st2 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);
    	Shape st3 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	Coordinate[] coords = new Coordinate[5];
    	coords[0] = new Coordinate(1/2.0, 1/2.0);
    	coords[1] = new Coordinate(-0.5, 1/2.0);
    	coords[2] = new Coordinate(1, 2);
    	coords[3] = new Coordinate(1, 1);
    	coords[4] = new Coordinate(1/2.0, 1/2.0);
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	Polygon[] t = new Polygon[1];
    	t[0] = shape;
    	MultiPolygon mshape = new MultiPolygon(t, geometryFactory);
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(mshape, shapes, null);
      	
      	//retVal &= shapes.get(0).toText().equals("LINEARRING (0 1, 0.4999999999999999 0.5, -0.5 0.5, 0 1)");
      	//retVal &= shapes.get(1).toText().equals("LINEARRING (0 1, 0.5000000000000048 0.4999999999999564, 1.0000000000000213 0.9999999999999684, 0.4999999999999972 1.4999999999999973, 0 1)");
      	//retVal &= shapes.get(2).toText().equals("LINEARRING (0.4999999999999972 1.4999999999999973, 1.0000000000000004 2, 1.0000000000000009 0.999999999999993, 0.4999999999999972 1.4999999999999973)");
      	
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(2).shape.toText() + "\n");
      	
      	//System.out.print("Triangle 3: " + shapes.get(3).toText() + "\n");      	
      	
      	return retVal;
    }

    public static boolean FitTest5() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st2 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st3 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	
      	Coordinate[] coords = new Coordinate[6];
    	coords[0] = new Coordinate(0, 0);
    	coords[1] = new Coordinate(1/2.0, 1/2.0);
    	coords[2] = new Coordinate(-0.5, 1/2.0);
    	coords[3] = new Coordinate(1, 2);
    	coords[4] = new Coordinate(1, 0);
    	coords[5] = new Coordinate(0, 0);
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	Polygon[] t = new Polygon[1];
    	t[0] = shape;
    	MultiPolygon mshape = new MultiPolygon(t, geometryFactory);
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	shapes.add(st4);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(mshape, shapes, null);
      	
      	/*retVal &= shapes.get(0).toText().equals("LINEARRING (0.9999999999999997 0, 0 0, 0.9999999999999997 0.9999999999999997, 0.9999999999999997 0)");      
      	retVal &= shapes.get(1).toText().equals("LINEARRING (0 1, 0.4999999999999999 0.5, -0.5 0.5, 0 1)");
      	retVal &= shapes.get(2).toText().equals("LINEARRING (0 1, 0.500000000000272 0.4999999999997349, 1.0000000000005824 1.0000000000000338, 0.5000000000003024 1.5000000000003024, 0 1)");
      	retVal &= shapes.get(3).toText().equals("LINEARRING (0.5000000000003024 1.5000000000003024, 1.0000000000003073 2.0000000000003073, 1.0000000000003082 1.0000000000002944, 0.5000000000003024 1.5000000000003024)");
      	*/
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(2).shape.toText() + "\n");
      	System.out.print("Triangle 3: " + shapes.get(3).shape.toText() + "\n");      	
      	
      	return retVal;
    }

    
    public static boolean FitTest6() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st2 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st3 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	// Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2.
    	Shape st5 = new Shape(Util.makeParallelogramL1(), TypeOfShape.PARALLELOGRAM);
      	
      	Coordinate[] coords = new Coordinate[4];
    	coords[0] = new Coordinate(1, 0);    	
    	coords[1] = new Coordinate(-1, 0);
    	coords[2] = new Coordinate(1, 2);
    	coords[3] = new Coordinate(1, 0);    	
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	shapes.add(st4);
      	shapes.add(st5);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	/*retVal &= shapes.get(0).toText().equals("LINEARRING (1 0, 0 0, 1 1, 1 0)");      
      	retVal &= shapes.get(1).toText().equals("LINEARRING (0.5 1.4999999999999998, 1 2, 1 1.0000000000000007, 0.5 1.4999999999999998)");
      	retVal &= shapes.get(2).toText().equals("LINEARRING (0.5 1.4999999999999998, -0.0000000000036711 0.9999999999962881, 0.4999999999998357 0.4999999999933438, 1.0000000000034386 0.9999999999965623, 0.5 1.4999999999999998)");
      	retVal &= shapes.get(3).toText().equals("LINEARRING (-0.0000000000036711 0.9999999999962881, 0.4999999999968713 0.4999999999963081, -0.5000000000036726 0.4999999999957328, -0.0000000000036711 0.9999999999962881)");
      	retVal &= shapes.get(4).toText().equals("LINEARRING (0.5000000000088877 0.5000000000080457, 0 0, -1.0000000000162634 0, -0.5000000000079642 0.5000000000079124, 0.5000000000088877 0.5000000000080457)");
      	*/
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(2).shape.toText() + "\n");
      	System.out.print("Triangle 3: " + shapes.get(3).shape.toText() + "\n");
      	System.out.print("Parallelog: " + shapes.get(4).shape.toText() + "\n");
      	
      	return retVal;
    }

    public static boolean FitTest7() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st2 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);
    	Shape st3 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	// Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2.      	
    	Shape st4 = new Shape(Util.makeParallelogramL1(), TypeOfShape.PARALLELOGRAM);
    	Shape st5 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
    	Shape st6 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	
      	
      	
      	Coordinate[] coords = new Coordinate[6];
    	coords[0] = new Coordinate(1, 0);    	
    	coords[1] = new Coordinate(-1, 0);
    	coords[2] = new Coordinate(0, 1);
    	coords[3] = new Coordinate(-1, 2);
    	coords[4] = new Coordinate(1, 2);
    	coords[5] = new Coordinate(1, 0);
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	shapes.add(st4);
      	shapes.add(st5);
      	shapes.add(st6);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	/*retVal &= shapes.get(0).toText().equals("LINEARRING (1 0, 0 0, 1 1, 1 0)");      
      	retVal &= shapes.get(1).toText().equals("LINEARRING (0 1, -0.9999999999999982 1.9999999999999982, 0.9999999999999979 1.9999999999999978, 0 1)");
      	retVal &= shapes.get(2).toText().equals("LINEARRING (0.499999999999999 1.4999999999999982, 0.999999999999998 1.9999999999999982, 1 0.9999999999999987, 0.499999999999999 1.4999999999999982)");
      	retVal &= shapes.get(3).toText().equals("LINEARRING (0.5000000000165066 0.5000000000143779, 0 0, -1.0000000000315268 0, -0.5000000000151845 0.5000000000154324, 0.5000000000165066 0.5000000000143779)");
      	retVal &= shapes.get(4).toText().equals("LINEARRING (0 1, 0.5000000000006604 0.4999999999990063, 1.0000000000016065 0.9999999999986006, 0.5000000000011424 1.5000000000011418, 0 1)");
      	retVal &= shapes.get(5).toText().equals("LINEARRING (0.0000000000012252 1.0000000000012252, 0.4999999999870055 0.4999999999848622, -0.5000000000151844 0.5000000000154324, 0.0000000000012252 1.0000000000012252)");
      	*/
      	
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Triangle 3: " + shapes.get(2).shape.toText() + "\n");
      	System.out.print("Parallelog: " + shapes.get(3).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(4).shape.toText() + "\n");
      	System.out.print("Triangle 4: " + shapes.get(5).shape.toText() + "\n");
      	
      	return retVal;
    }

    
    public static boolean FitTest8() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);
    	Shape st2 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);      	
    	Shape st3 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st7 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	// Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2.      	
    	Shape st5 = new Shape(Util.makeParallelogramL1(), TypeOfShape.PARALLELOGRAM);
    	Shape st6 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
      	
      	
      	
      	
      	Coordinate[] coords = new Coordinate[5];
    	coords[0] = new Coordinate(1, 0);    	
    	coords[1] = new Coordinate(-1, 0);
    	coords[2] = new Coordinate(-1, 2);
    	coords[3] = new Coordinate(1, 2);
    	coords[4] = new Coordinate(1, 0);
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	shapes.add(st4);
      	shapes.add(st5);
      	shapes.add(st6);
      	shapes.add(st7);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	/*retVal &= shapes.get(0).toText().equals("LINEARRING (1 0, 0 0, 1 1, 1 0)");      
      	retVal &= shapes.get(1).toText().equals("LINEARRING (0 1, -0.9999999999999982 1.9999999999999982, 0.9999999999999979 1.9999999999999978, 0 1)");
      	retVal &= shapes.get(2).toText().equals("LINEARRING (0.499999999999999 1.4999999999999982, 0.999999999999998 1.9999999999999982, 1 0.9999999999999987, 0.499999999999999 1.4999999999999982)");
      	retVal &= shapes.get(3).toText().equals("LINEARRING (0 1, 0.5000000000006604 0.4999999999990063, 1.0000000000016065 0.9999999999986006, 0.5000000000011424 1.5000000000011418, 0 1)");
      	retVal &= shapes.get(4).toText().equals("LINEARRING (0.0000000000012252 1.0000000000012252, 0.4999999999870055 0.4999999999848622, -0.5000000000151844 0.5000000000154324, 0.0000000000012252 1.0000000000012252)");
      	*/
      	
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Triangle 3: " + shapes.get(2).shape.toText() + "\n");
      	System.out.print("Triangle 4: " + shapes.get(3).shape.toText() + "\n");
      	System.out.print("Parallelog: " + shapes.get(4).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(5).shape.toText() + "\n");
      	System.out.print("Triangle 5: " + shapes.get(6).shape.toText() + "\n");
      	
      	return retVal;
    }


    public static boolean FitTest9_0() throws Exception {
    	
    	
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st7 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	// Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2.      	
    	
    	Shape st6 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
      	      	
      	Coordinate[] coords = new Coordinate[8];
    	coords[0] = new Coordinate(0, Math.sqrt(2) - 0.25);    	
    	coords[1] = new Coordinate(0.5, Math.sqrt(2) + 0.25);
    	coords[2] = new Coordinate(0.75, Math.sqrt(2));    	    
    	coords[3] = new Coordinate(0.75 + Math.sqrt(2), Math.sqrt(2));
    	coords[4] = new Coordinate(0.75 + Math.sqrt(2), Math.sqrt(2)/2.0);
    	coords[5] = new Coordinate(0.75 + Math.sqrt(2)/2.0, Math.sqrt(2)/2.0);
    	coords[6] = new Coordinate(1, Math.sqrt(2) - 0.25);
    	coords[7] = new Coordinate(0, Math.sqrt(2) - 0.25);
    	
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st4);      	
      	shapes.add(st6);
      	shapes.add(st7);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	/*retVal &= shapes.get(0).toText().equals("LINEARRING (1 0, 0 0, 1 1, 1 0)");      
      	retVal &= shapes.get(1).toText().equals("LINEARRING (0 1, -0.9999999999999982 1.9999999999999982, 0.9999999999999979 1.9999999999999978, 0 1)");
      	retVal &= shapes.get(2).toText().equals("LINEARRING (0.499999999999999 1.4999999999999982, 0.999999999999998 1.9999999999999982, 1 0.9999999999999987, 0.499999999999999 1.4999999999999982)");
      	retVal &= shapes.get(3).toText().equals("LINEARRING (0 1, 0.5000000000006604 0.4999999999990063, 1.0000000000016065 0.9999999999986006, 0.5000000000011424 1.5000000000011418, 0 1)");
      	retVal &= shapes.get(4).toText().equals("LINEARRING (0.0000000000012252 1.0000000000012252, 0.4999999999870055 0.4999999999848622, -0.5000000000151844 0.5000000000154324, 0.0000000000012252 1.0000000000012252)");
      	*/
      	
      	System.out.print("Triangle 3: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 4: " + shapes.get(1).shape.toText() + "\n");      	
      	System.out.print("Square    : " + shapes.get(2).shape.toText() + "\n");      	
      	
      	return retVal;
    }
    
    public static boolean FitTest9_1() throws Exception {
    	
    	Shape st1 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st2 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);
    	Shape st3 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	
    	      	
      	      	
      	Coordinate[] coords = new Coordinate[10];
    	coords[0] = new Coordinate(0, Math.sqrt(2) - 0.25);    	
    	coords[1] = new Coordinate(0.5, Math.sqrt(2) + 0.25);
    	coords[2] = new Coordinate(0.75, Math.sqrt(2));    	    
    	coords[3] = new Coordinate(0.75 + Math.sqrt(2), Math.sqrt(2));
    	coords[4] = new Coordinate(3.0 * Math.sqrt(2)/2.0 + 0.75, Math.sqrt(2)/2.0);
    	coords[5] = new Coordinate(0.75 + Math.sqrt(2), 0);
    	coords[6] = new Coordinate(0.75 + Math.sqrt(2), Math.sqrt(2)/2.0);
    	coords[7] = new Coordinate(0.75 + Math.sqrt(2)/2.0, Math.sqrt(2)/2.0);
    	coords[8] = new Coordinate(1, Math.sqrt(2) - 0.25);
    	coords[9] = new Coordinate(0, Math.sqrt(2) - 0.25);
    	
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);      	
      	shapes.add(st3);
      	shapes.add(st4);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	 	return retVal;
    }

    
    public static boolean FitTest9_2() throws Exception {
    	
    	Shape st1 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
      	      	
      	Coordinate[] coords = new Coordinate[12];
    	coords[0] = new Coordinate(0, Math.sqrt(2) - 0.25);    	
    	coords[1] = new Coordinate(0.5, Math.sqrt(2) + 0.25);
    	coords[2] = new Coordinate(0.75, Math.sqrt(2));    	    
    	coords[3] = new Coordinate(0.75 + Math.sqrt(2) + 1.5, Math.sqrt(2));    	    
    	coords[4] = new Coordinate(0.75 + Math.sqrt(2) + 2.0, Math.sqrt(2) - 0.5);    	
    	coords[5] = new Coordinate(1.25 + Math.sqrt(2), Math.sqrt(2) - 0.5);
    	
    	coords[6] = new Coordinate(0.75 + 3.0 * Math.sqrt(2)/2.0, Math.sqrt(2)/2.0);
    	
    	coords[7] = new Coordinate(0.75 + Math.sqrt(2), 0);
    	coords[8] = new Coordinate(0.75 + Math.sqrt(2), Math.sqrt(2)/2.0);
    	coords[9] = new Coordinate(0.75 + Math.sqrt(2)/2.0, Math.sqrt(2)/2.0);
    	coords[10] = new Coordinate(1, Math.sqrt(2) - 0.25);
    	coords[11] = new Coordinate(0, Math.sqrt(2) - 0.25);
    	
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	return Util.Fit(Util.makeMulti(shape), shapes, null);      	
    }

    public static boolean FitTest10() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);
    	Shape st3 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	// Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2.      	
    	//AffineTransformation flip = new AffineTransformation();
       // flip.reflect(0, 1);
        LinearRing p = Util.makeParallelogramL1();
        //p.apply(flip);
       // p.geometryChanged();
        
    	Shape st5 = new Shape(p, TypeOfShape.PARALLELOGRAM);
    	Shape st6 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
    	Shape st7 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	      	
      	Coordinate[] coords = new Coordinate[14];
    	coords[0] = new Coordinate(0, Math.sqrt(2) - 0.25);    	
    	coords[1] = new Coordinate(coords[0].x + 1/2.0, coords[0].y + 1/2.0);
    	coords[2] = new Coordinate(0.75, Math.sqrt(2));
    	coords[3] = new Coordinate(1.75, Math.sqrt(2) + 1);    	
    	coords[4] = new Coordinate(2.75, Math.sqrt(2));
    	coords[5] = new Coordinate(3.25 - (1.5 - Math.sqrt(2)), Math.sqrt(2));
    	coords[6] = new Coordinate(coords[5].x + 0.5, Math.sqrt(2) - 0.5);
    	coords[7] = new Coordinate(coords[6].x - 1, Math.sqrt(2) - 0.5);
    	coords[8] = new Coordinate(coords[7].x + (Math.sqrt(2) - 1)/2, coords[7].y - (Math.sqrt(2) - 1)/2);
    	coords[9] = new Coordinate(coords[8].x - 1/Math.sqrt(2), coords[8].y - 1/Math.sqrt(2));
    	coords[10] = new Coordinate(coords[9].x, coords[9].y + Math.sqrt(2)/2);//(1-Math.sqrt(2)/2.0));
    	coords[11] = new Coordinate(coords[10].x - Math.sqrt(2)/2, coords[10].y);
    	coords[12] = new Coordinate(1, Math.sqrt(2) - 0.25);
    	coords[13] = new Coordinate(0, Math.sqrt(2) - 0.25);
    	for (int i = 0; i < 14; i++){
    		System.out.println("coords[i]: " + i + ", " + coords[i].x + ", " + coords[i].y);
    	}
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st3);
      	shapes.add(st4);
      	shapes.add(st5);
      	shapes.add(st6);
      	shapes.add(st7);
      	Util.originalShapes = shapes;
      	
      	System.out.print("shape " + shape.toText() + "\n");
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	/*retVal &= shapes.get(0).toText().equals("LINEARRING (1 0, 0 0, 1 1, 1 0)");      
      	retVal &= shapes.get(1).toText().equals("LINEARRING (0 1, -0.9999999999999982 1.9999999999999982, 0.9999999999999979 1.9999999999999978, 0 1)");
      	retVal &= shapes.get(2).toText().equals("LINEARRING (0.499999999999999 1.4999999999999982, 0.999999999999998 1.9999999999999982, 1 0.9999999999999987, 0.499999999999999 1.4999999999999982)");
      	retVal &= shapes.get(3).toText().equals("LINEARRING (0 1, 0.5000000000006604 0.4999999999990063, 1.0000000000016065 0.9999999999986006, 0.5000000000011424 1.5000000000011418, 0 1)");
      	retVal &= shapes.get(4).toText().equals("LINEARRING (0.0000000000012252 1.0000000000012252, 0.4999999999870055 0.4999999999848622, -0.5000000000151844 0.5000000000154324, 0.0000000000012252 1.0000000000012252)");
      	*/
      	
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 3: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Triangle 4: " + shapes.get(2).shape.toText() + "\n");
      	System.out.print("Parallelog: " + shapes.get(3).shape.toText() + "\n");
      	
      	Geometry finalShape = Util.makeMulti(shape);      	
      	for (int i = 0; i < 4; i++){
      		finalShape = Util.SemiRobustGeoOp(finalShape, new Polygon(shapes.get(i).shape, null, Util.geometryFactory), 0);//finalShape.difference(new Polygon(shapes.get(i).shape, null, Util.geometryFactory));
      	}
      	System.out.print("finalShape " + finalShape.toText() + "\n");
      	
      	//System.out.print("Square    : " + shapes.get(4).shape.toText() + "\n");
      	//System.out.print("Triangle 5: " + shapes.get(5).shape.toText() + "\n");
      	
      	return retVal;
    }

    public static boolean FitTest10_1() throws Exception {
        String tmp = "                MULTIPOLYGON (((0.75 1.414213562373095, 2.1642135623730954 1.4142135623730951, 2.164213562373095 0.7071067811865477, 1.4571067811865475 0.7071067811865477, 1.0000000000000027 1.1642135623730918, 0.75 1.414213562373095)), ((2.1642135623730954 1.4142135623730951, 2.75 1.414213562373095, 3.164213562373095 1.4142135623730951, 3.664213562373095 0.9142135623730951, 2.664213562373095 0.9142135623730951, 2.1642135623730954 1.4142135623730951)))";
        AffineTransformation flip = new AffineTransformation();
       // flip.reflect(0, 1);
        LinearRing p = Util.makeParallelogramL1();
       // p.apply(flip);
       // p.geometryChanged();
        Shape st1 = new Shape(p, TypeOfShape.PARALLELOGRAM);
    	Shape st2 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	Util.originalShapes = shapes;
      	MultiPolygon shape = (MultiPolygon)(new WKTReader(Util.geometryFactory).read(tmp));
      	Util.flipped = false;
    	boolean ret = Util.Fit(shape, shapes, null);
    	
        return ret;
    }
    
    public static boolean FitTest11() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);
    	Shape st2 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);      	
    	Shape st3 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st7 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	// Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2.      	
    	Shape st5 = new Shape(Util.makeParallelogramL1(), TypeOfShape.PARALLELOGRAM);
    	Shape st6 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
      	
      	
      	
      	
      	Coordinate[] coords = new Coordinate[15];
    	coords[0] = new Coordinate(0, Math.sqrt(2) - 0.25);    	
    	coords[1] = new Coordinate(0.5, Math.sqrt(2) + 0.25);
    	coords[2] = new Coordinate(0.75, Math.sqrt(2));
    	coords[3] = new Coordinate(1.75, Math.sqrt(2) + 1);
    	coords[4] = new Coordinate(3.75, Math.sqrt(2) + 1);
    	coords[5] = new Coordinate(2.75, Math.sqrt(2));
    	coords[6] = new Coordinate(3.25, Math.sqrt(2));
    	coords[7] = new Coordinate(3.75, Math.sqrt(2) - 0.5);
    	coords[8] = new Coordinate(2.75, Math.sqrt(2) - 0.5);
    	coords[9] = new Coordinate(3.0 * Math.sqrt(2)/2.0 + 0.75, Math.sqrt(2)/2.0);
    	coords[10] = new Coordinate(0.75 + Math.sqrt(2), 0);
    	coords[11] = new Coordinate(0.75 + Math.sqrt(2), Math.sqrt(2)/2.0);
    	coords[12] = new Coordinate(0.75 + Math.sqrt(2)/2.0, Math.sqrt(2)/2.0);
    	coords[13] = new Coordinate(1, Math.sqrt(2) - 0.25);
    	coords[14] = new Coordinate(0, Math.sqrt(2) - 0.25);
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	shapes.add(st4);
      	shapes.add(st5);
      	shapes.add(st6);
      	shapes.add(st7);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	/*retVal &= shapes.get(0).toText().equals("LINEARRING (1 0, 0 0, 1 1, 1 0)");      
      	retVal &= shapes.get(1).toText().equals("LINEARRING (0 1, -0.9999999999999982 1.9999999999999982, 0.9999999999999979 1.9999999999999978, 0 1)");
      	retVal &= shapes.get(2).toText().equals("LINEARRING (0.499999999999999 1.4999999999999982, 0.999999999999998 1.9999999999999982, 1 0.9999999999999987, 0.499999999999999 1.4999999999999982)");
      	retVal &= shapes.get(3).toText().equals("LINEARRING (0 1, 0.5000000000006604 0.4999999999990063, 1.0000000000016065 0.9999999999986006, 0.5000000000011424 1.5000000000011418, 0 1)");
      	retVal &= shapes.get(4).toText().equals("LINEARRING (0.0000000000012252 1.0000000000012252, 0.4999999999870055 0.4999999999848622, -0.5000000000151844 0.5000000000154324, 0.0000000000012252 1.0000000000012252)");
      	*/
      	
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Triangle 3: " + shapes.get(2).shape.toText() + "\n");
      	System.out.print("Triangle 4: " + shapes.get(3).shape.toText() + "\n");
      	System.out.print("Parallelog: " + shapes.get(4).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(5).shape.toText() + "\n");
      	System.out.print("Triangle 5: " + shapes.get(6).shape.toText() + "\n");
      	
      	return retVal;
    }

    
    public static boolean FitTest_BirdShape1() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);
    	Shape st2 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);      	
    	Shape st3 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st7 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	// Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2.      	
    	Shape st5 = new Shape(Util.makeParallelogramL1(), TypeOfShape.PARALLELOGRAM);
    	Shape st6 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
      	
      	
      	
      	
      	Coordinate[] coords = new Coordinate[18];
    	coords[0] = new Coordinate(1 - Math.sqrt(2)/2, 0);    	
    	coords[1] = new Coordinate(1, 0);
    	coords[2] = new Coordinate(1, Math.sqrt(2)/2);
    	coords[3] = new Coordinate(2.8 - Math.sqrt(2)/2, Math.sqrt(2)/2);
    	coords[4] = new Coordinate(2.8, 0);
    	coords[5] = new Coordinate(2.8, Math.sqrt(2)/2);
    	coords[6] = new Coordinate(3, Math.sqrt(2)/2);
    	coords[7] = new Coordinate(2, 1 + Math.sqrt(2)/2);
    	coords[8] = new Coordinate(Math.sqrt(2)/2, 1 + Math.sqrt(2)/2);
    	coords[9] = new Coordinate(Math.sqrt(2), 1 + Math.sqrt(2));
    	coords[10] = new Coordinate(Math.sqrt(2), 1 + 3.0/2.0 * Math.sqrt(2));
    	coords[11] = new Coordinate(Math.sqrt(2)/2, 1 + 2 * Math.sqrt(2));
    	coords[12] = new Coordinate(0, 1 + 3 * Math.sqrt(2)/2.0);
    	coords[13] = new Coordinate(Math.sqrt(2)/2.0, 1 + 3.0/2.0 * Math.sqrt(2));
    	coords[14] = new Coordinate(Math.sqrt(2)/2.0, 1 + Math.sqrt(2));
    	coords[15] = new Coordinate(0, 1 + Math.sqrt(2)/2.0);
    	coords[16] = new Coordinate(1, Math.sqrt(2)/2.0);
    	coords[17] = new Coordinate(1 - Math.sqrt(2)/2.0, 0);
    	
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	shapes.add(st4);
      	shapes.add(st5);
      	shapes.add(st6);
      	shapes.add(st7);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	retVal &= shapes.get(0).shape.toText().equals("LINEARRING (1.9999999999999996 1.7071067811865475, 2.9999999999999996 0.7071067811865475, 0.9999999999999996 0.7071067811865475, 1.9999999999999996 1.7071067811865475)");      
      	retVal &= shapes.get(1).shape.toText().equals("LINEARRING (0.9999999999999996 0.7071067811865475, -0.0000000000000005 1.7071067811865468, 1.9999999999999993 1.7071067811865472, 0.9999999999999996 0.7071067811865475)");
      	retVal &= shapes.get(2).shape.toText().equals("LINEARRING (0.7071067811865475 3.8284271247461903, 1.4142135623730945 3.1213203435596437, -0.0000000000000009 3.1213203435596406, 0.7071067811865475 3.8284271247461903)");
      	retVal &= shapes.get(3).shape.toText().equals("LINEARRING (1 0, 0.2928932188134148 0, 0.9999999999999781 0.707106781186587, 1 0)");
      	retVal &= shapes.get(4).shape.toText().equals("LINEARRING (-0.0000000000000006 1.7071067811865468, 0.7071067811871513 1.707106781186951, 1.41421356237381 2.4142135623739236, 0.7071067811870669 2.4142135623736145, -0.0000000000000006 1.7071067811865468)");
      	retVal &= shapes.get(5).shape.toText().equals("LINEARRING (0.707106781187067 2.4142135623736145, 1.4142135623735972 2.4142135623741394, 1.4142135623732508 3.1213203435605767, 0.7071067811865475 3.1213203435601473, 0.707106781187067 2.4142135623736145)");
      	retVal &= shapes.get(6).shape.toText().equals("LINEARRING (2.8 0.7071067811865475, 2.8 -0.0000000000006417, 2.0928932188128906 0.7071067811862632, 2.8 0.7071067811865475)");
      			
      	
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Triangle 3: " + shapes.get(2).shape.toText() + "\n");
      	System.out.print("Triangle 4: " + shapes.get(3).shape.toText() + "\n");
      	System.out.print("Parallelog: " + shapes.get(4).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(5).shape.toText() + "\n");
      	System.out.print("Triangle 5: " + shapes.get(6).shape.toText() + "\n");
      	
      	return retVal;
    }

    
    public static boolean FitTest_HumanShape1() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);
    	Shape st2 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);      	
    	Shape st3 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st7 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	// Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2.      	
    	Shape st5 = new Shape(Util.makeParallelogramL1(), TypeOfShape.PARALLELOGRAM);
    	Shape st6 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
      	
      	
      	
      	
      	Coordinate[] coords = new Coordinate[20];
    	coords[0] = new Coordinate(0.5, 0);    	
    	coords[1] = new Coordinate(0, 0.5);
    	coords[2] = new Coordinate(0.5, 1);
    	coords[3] = new Coordinate(0.5, 1.5);
    	coords[4] = new Coordinate(0.5 - (Math.sqrt(2)- 1)/2.0, 1.5);
    	coords[5] = new Coordinate(0.5 - (Math.sqrt(2)- 1)/2.0, 1.5 + Math.sqrt(2));
    	coords[6] = new Coordinate(1, 1.5 + Math.sqrt(2));
    	coords[7] = new Coordinate(0.5, 2 + Math.sqrt(2));
    	coords[8] = new Coordinate(1, 2.5 + Math.sqrt(2));
    	coords[9] = new Coordinate(1.5, 2 + Math.sqrt(2));
    	coords[10] = new Coordinate(1, 1.5 + Math.sqrt(2));
    	coords[11] = new Coordinate(1.5 + (Math.sqrt(2) - 1)/2, 1.5 + Math.sqrt(2));
    	coords[12] = new Coordinate(1.5 + (Math.sqrt(2) - 1)/2, 1.5);
    	coords[13] = new Coordinate(1.5, 1.5);
    	coords[14] = new Coordinate(1.5, 0.5);
    	coords[15] = new Coordinate(2, 0);
    	coords[16] = new Coordinate(1, 0);
    	coords[17] = new Coordinate(1, 1);
    	coords[18] = new Coordinate(0.5, 0.5);
    	coords[19] = new Coordinate(0.5, 0);
    	
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	shapes.add(st4);
      	shapes.add(st5);
      	shapes.add(st6);
      	shapes.add(st7);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	retVal &= shapes.get(0).shape.toText().equals("LINEARRING (0.2928932188134524 1.5, 0.2928932188134524 2.914213562373095, 1.7071067811865475 1.5, 0.2928932188134524 1.5)");      
      	retVal &= shapes.get(1).shape.toText().equals("LINEARRING (1.7071067811865475 2.914213562373095, 1.7071067811865475 1.5, 0.2928932188134517 2.914213562373095, 1.7071067811865475 2.914213562373095)");
      	retVal &= shapes.get(2).shape.toText().equals("LINEARRING (0.5 1.5, 1.5 1.5, 0.5 0.5, 0.5 1.5)");
      	retVal &= shapes.get(3).shape.toText().equals("LINEARRING (0 0.5, 0.499999999999988 0.999999999999988, 0.4999999999999319 0.000000000000081, 0 0.5)");
      	retVal &= shapes.get(4).shape.toText().equals("LINEARRING (1.5 1.5, 1 1, 1 0, 1.5 0.5, 1.5 1.5)");
      	retVal &= shapes.get(5).shape.toText().equals("LINEARRING (1 2.914213562373095, 1.4999999999999982 3.414213562373096, 0.9999999999999978 3.9142135623679177, 0.5000000000000004 3.4142135623730945, 1 2.914213562373095)");
      	retVal &= shapes.get(6).shape.toText().equals("LINEARRING (1.5 0.5, 1.9999999999994884 0.0000000000005114, 1.0000000000012415 0.0000000000011302, 1.5 0.5)");
      		
      	
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Triangle 3: " + shapes.get(2).shape.toText() + "\n");
      	System.out.print("Triangle 4: " + shapes.get(3).shape.toText() + "\n");
      	System.out.print("Parallelog: " + shapes.get(4).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(5).shape.toText() + "\n");
      	System.out.print("Triangle 5: " + shapes.get(6).shape.toText() + "\n");
      	
      	return retVal;
    }

    public static boolean FitTest_ToSingleLargeTriangle() throws Exception {

    	Shape st1 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);
    	Shape st2 = new Shape(Util.makeTriangle(Math.sqrt(2)), TypeOfShape.LARGE_TRIANGLE);      	
    	Shape st3 = new Shape(Util.makeTriangle(1), TypeOfShape.MEDIUM_TRIANGLE);
    	Shape st4 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
    	Shape st7 = new Shape(Util.makeTriangle(Math.sqrt(2)/2), TypeOfShape.SMALL_TRIANGLE);
      	// Make a parallelogram with long side of length 1 and short side of length sqrt(2)/2.      	
    	Shape st5 = new Shape(Util.makeParallelogramL1(), TypeOfShape.PARALLELOGRAM);
    	Shape st6 = new Shape(Util.makeSquare(Math.sqrt(2)/2), TypeOfShape.SQUARE);      	
      	
      	
      	
      	
      	Coordinate[] coords = new Coordinate[4];
    	coords[0] = new Coordinate(0, 0);    	
    	coords[1] = new Coordinate(0, 4);
    	coords[2] = new Coordinate(2, 2);
    	coords[3] = new Coordinate(0, 0);    	
    	
    	
    	Polygon shape = new Polygon(geometryFactory.createLinearRing(coords), null, geometryFactory);
    	
    	List<Shape> shapes = new ArrayList<Shape> ();    	
      	shapes.add(st1);
      	shapes.add(st2);
      	shapes.add(st3);
      	shapes.add(st4);
      	shapes.add(st5);
      	shapes.add(st6);
      	shapes.add(st7);
      	Util.originalShapes = shapes;
      	Util.flipped = false;
      	boolean retVal = Util.Fit(Util.makeMulti(shape), shapes, null);
      	
      	retVal &= shapes.get(0).shape.toText().equals("LINEARRING (1.0000000000000002 1.0000000000000002, 0 0, 0 2.0000000000000004, 1.0000000000000002 1.0000000000000002)");      
      	retVal &= shapes.get(1).shape.toText().equals("LINEARRING (1.0000000000000004 3.000000000000001, 0 2.0000000000000004, 0 4.000000000000001, 1.0000000000000004 3.000000000000001)");
      	retVal &= shapes.get(2).shape.toText().equals("LINEARRING (0.9999999999999991 1.999999999999997, 1.0000000000000004 3.000000000000001, 2.0000000000000018 1.9999999999999982, 0.9999999999999991 1.999999999999997)");
      	retVal &= shapes.get(3).shape.toText().equals("LINEARRING (1.0000000000000002 1.0000000000000002, 0.499999999999996 1.5000000000000044, 1.5000000000000093 1.500000000000006, 1.0000000000000002 1.0000000000000002)");
      	retVal &= shapes.get(4).shape.toText().equals("LINEARRING (2.0000000000000013 1.9999999999999982, 1.4999999999999751 1.4999999999999722, 0.4999999999999258 1.4999999999998865, 0.9999999999999316 1.9999999999999203, 2.0000000000000013 1.9999999999999982)");
      	retVal &= shapes.get(5).shape.toText().equals("LINEARRING (0.4999999999999258 1.4999999999998863, 1.0000000000000449 1.9999999999998208, 0.500000000000105 2.4999999999999267, 0.000000000000012 1.9999999999999885, 0.4999999999999258 1.4999999999998863)");
      	retVal &= shapes.get(6).shape.toText().equals("LINEARRING (0.5000000000001048 2.4999999999999267, 1.0000000000001206 3.0000000000001212, 1.0000000000000664 1.999999999999931, 0.5000000000001048 2.4999999999999267)");
      	
      	System.out.print("Triangle 1: " + shapes.get(0).shape.toText() + "\n");
      	System.out.print("Triangle 2: " + shapes.get(1).shape.toText() + "\n");
      	System.out.print("Triangle 3: " + shapes.get(2).shape.toText() + "\n");
      	System.out.print("Triangle 4: " + shapes.get(3).shape.toText() + "\n");
      	System.out.print("Parallelog: " + shapes.get(4).shape.toText() + "\n");
      	System.out.print("Square    : " + shapes.get(5).shape.toText() + "\n");
      	System.out.print("Triangle 5: " + shapes.get(6).shape.toText() + "\n");
      	
      	return retVal;
    }    
}
