package tskstreams.rules;

import java.util.Vector;

import com.yahoo.labs.samoa.instances.Instance;

import moa.core.DoubleVector;
/**
 * This class serves the purpose of holding the performance of prediction of each rule in the system of rules.
 * @author shaker
 *
 */
public class ResultSummary{
	
	private double sumMemberships ;
	private double sumWeightedPredictions ;
	private double finalPrediction ;
	
	private Vector<ResultPair> resultPairs ;
	private DoubleVector normaliezedExtendedInstance ;
	private double normaliezedTargetValue ;
	
	private ResultPair resultPairDefaultRule ;
	
	public ResultSummary(DoubleVector normaliezedExtendedInstance, double normaliezedTargetValue){
		this.normaliezedExtendedInstance =normaliezedExtendedInstance ;
		this.sumMemberships = 0 ;
		this.sumWeightedPredictions = 0 ;
		this.finalPrediction =0 ;
		this.resultPairs = new Vector<ResultPair>() ;
		this.normaliezedTargetValue = normaliezedTargetValue ;
	}
	
	public void addResultPair(ResultPair rp){
		resultPairs.addElement(rp);
		sumMemberships+=rp.getMembership() ;
		sumWeightedPredictions+=rp.getMembership()*rp.getScore() ;
	}

	public void addResultPair(FuzzyRule rule,double memebership,double score){
		resultPairs.add(new ResultPair(rule, memebership, score)) ;
		sumMemberships+=memebership ;
		sumWeightedPredictions+=memebership*score ;
	}

	public void computePrediction(){		
		for (ResultPair rp: resultPairs) {			
			rp.setNormalizedMembership(rp.getMembership()/sumMemberships);
		}
		finalPrediction = sumWeightedPredictions/sumMemberships ;		
	}
	
	public double getFinalPrediction() {
		return finalPrediction;
	}

	public DoubleVector getNormaliezedExtendedInstance() {
		return normaliezedExtendedInstance;
	}
	
	public double getNormaliezedTargetValue() {
		return normaliezedTargetValue ;
	}

	public double getSumWeightedPredictions() {
		return sumWeightedPredictions;
	}

	public double getSumMemberships() {
		return sumMemberships;
	}


	public Vector<ResultPair> getResultPairs() {
		return resultPairs;
	}

	public ResultPair getResultPairDefaultRule() {		
		return resultPairDefaultRule;
	}
	
	public void setResultPairDefaultRule(ResultPair resultPairDefaultRule) {
		this.resultPairDefaultRule = resultPairDefaultRule ;
	}
	
	public class ResultPair{
		
		
		private double membership ;
		private double normalizedMembership ;
		private double score;
		private FuzzyRule rule = null ;
		
		public ResultPair(FuzzyRule rule, double membership, double score) {
			this.membership = membership ;
			this.score = score ;
			this.rule =rule ;
		}
		
		public double getMembership() {
			return membership;
		}
		public double getScore() {
			return score;
		}
		public FuzzyRule getRule() {
			return rule;
		}

		public double getNormalizedMembership() {
			return normalizedMembership;
		}

		public void setNormalizedMembership(double normalizedMembership) {
			this.normalizedMembership = normalizedMembership;
		}
	}
}
