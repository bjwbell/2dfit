import com.vividsolutions.jts.geom.LinearRing;


public class Shape {
	public TypeOfShape type;
	public LinearRing shape;
	
	public Shape(LinearRing s, TypeOfShape t){
		shape = s;
		type = t;
	}
}
