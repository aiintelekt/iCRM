package test.fio.admin.portal.example;
/**
 * 
 */


/**
 * @author Sharif
 *
 */
public class Calculator {

	public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero");
        }
        return a / b;
    }
	
}
