package stat ;

public class IncrementalVariance {
	
	private double currentMean= 0 ;
	private double currentSTemp= 0 ;
	private double currentVariance= 0 ;

	private double previousMean= 0 ;
			
	private double sumWeight = 0 ;

	private long counter = 0 ;
	
	private double min = Double.MAX_VALUE ;
	private double max = -Double.MAX_VALUE ;
	
	public void update(double value, double weight){
		sumWeight += weight ;
		previousMean = currentMean ;
		currentMean = previousMean + (weight/sumWeight)*(value - previousMean ) ;
		
		if(counter!=0){
			currentSTemp = currentSTemp + weight*(value - currentMean) * (value - previousMean) ;
			currentVariance = currentSTemp/sumWeight ;
		}
		if (value>max)
			max = value ;
		if (value<min)
			min = value ;

		counter++ ;
	}

	public void update(double value){
		update(value,1) ;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		IncrementalVariance inc = new IncrementalVariance() ;
		for (int i = 0 ; i <1001 ; i++){
			inc.update(i);
			System.out.println(i+"\t"+inc.getCurrentMean() +"\t" + inc.getCurrentVariance() +"\t" + inc.getStandardDeviation());
		}		
	}

	public double getCurrentMean() {
		return currentMean;
	}
	
	public double getCurrentVariance() {
		return currentVariance;
	}

	public double getStandardDeviation() {
		return Math.sqrt(currentVariance) ;
	}
	
	public double getRange(){
		return max - min ;
	}

	public double getNormalizedRange(){
		if (getStandardDeviation() !=0)
			return 	(max - min)/getStandardDeviation() ;
		else
			return 	(max - min) ;
	}
	
	public double getMax(){
		return max ;
	}

	public double getMin(){
		return min ;
	}
	
	public double getCounter(){
		return counter ;
	}
}
