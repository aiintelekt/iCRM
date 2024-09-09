package test.fio.admin.portal.example;
/**
 * 
 */


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sharif
 *
 */
public class SampleTest {

	private Calculator calculator;
	
	public SampleTest() {}

	@Before
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void testAddition() {
        int result = calculator.add(2, 3);
        assertEquals(5, result);
    }

    @Test
    public void testSubtraction() {
        int result = calculator.subtract(5, 2);
        assertEquals(3, result);
    }

    /*@Test
    public void testDivision() {
        double result = calculator.divide(10, 2);
        assertEquals(5.0, result);
    }*/
    
}
