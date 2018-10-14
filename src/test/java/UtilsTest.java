import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.util.Vector;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class UtilsTest {
    @Test
    void square() {
        Vector center = new Vector(10, 10, 10);
        Vector up = new Vector(1, 2, 0);
        Vector front = new Vector(1, 0 ,0);
        int xw = 10;
        int zw = 10;
        List<Vector> l = Utils.get2dRectangleAround(center, up, front, xw, zw);
        List<Vector> l2 = Utils.get2dRectangleAround(center, up.crossProduct(front), front, xw, zw);
        System.out.println(vectorsToString(l));
        System.out.println(vectorsToString(l2));
        Assert.assertEquals(40, l.size());
    }

    private String vectorsToString(List<Vector> l) {
        return l.stream().map(v -> String.format("(%.0f, %.0f, %.0f)", v.getX(), v.getY(), v.getZ())).collect(Collectors.joining(", "));
    }
}
