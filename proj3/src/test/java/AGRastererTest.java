import org.junit.Test;
import org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.HashSet;


import java.util.Map;

public class AGRastererTest extends AGMapTest {
    /**
     * Test the rastering functionality of the student code, by calling their getMapRaster method
     * and ensuring that the resulting map is correct. All of the test data is stored in a
     * TestParameters object that is loaded by the AGMapTest constructor. Note that this test file
     * extends AGMapTest, where most of the interesting stuff happens.
     *
     * @throws Exception
     */
    @Test
    public void testGetMapRaster() throws Exception {
        for (int i = 0; i < 10000; i++) {
            for (TestParameters p : params) {
                Map<String, Object> studentRasterResult = rasterer.getMapRaster(p.rasterParams);
                checkParamsMap("Returned result differed for input: " + p.rasterParams + ".\n",
                        p.rasterResult, studentRasterResult);
            }
        }
    }
}
