import org.junit.*;
import static org.junit.Assert.*;

public class UtilTest {

    public static ArrayList<LinearRing> makeTangramPieces(){
	ArrayList<LinearRing> pieces = new ArrayList<LinearRing>();
	pieces.add(makeSmallTriangle());
	pieces.add(makeSmallTriangle());
	//pieces.add(makeMediumTriangle());
	//pieces.add(makeLargeTriangle());
	//pieces.add(makeSquare());
	//pieces.add(makeParallelogram());
	return pieces;
    }

    @Test
    public void test_Fit () {
    }
}