import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
public class LinearRegression {
	public static void main(String[] args) {
		
		
		
		//get x values for closest neighbour
		
		
		int [] y = {1, 5, 10, 15, 20 ,25, 30, 35, 40, 45, 50};
		//save y values to array
		
		double [] x = { 0.5005, 0.41349,0.3355, 0.29300,0.261,0.2075, 0.175,  0.15750, 0.13849, 0.12500};
		
		for(int i = 0; i < y.length-1; i++)
		{
			RecEngine engine = new RecEngine(y[i]);
			
			//x[i] = engine.run();
			
		}
		
		for(int i = 0; i < y.length-1; i++)
		{
			System.out.println("When " + y[i] + " neighbours are used, the average precision is " + x[i]);
		}
		
		 OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		 
		 
		
		
		
	
	}
}
