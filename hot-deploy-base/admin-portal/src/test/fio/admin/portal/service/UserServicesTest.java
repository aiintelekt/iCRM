/**
 * 
 */
package test.fio.admin.portal.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import test.fio.admin.portal.example.Calculator;

/**
 * @author Sharif
 *
 */
public class UserServicesTest {

	private Calculator calculator;
	
	public UserServicesTest() {}

	@Before
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void testAddition() {
        int result = calculator.add(2, 3);
        assertEquals(5, result);
    }
	
}
