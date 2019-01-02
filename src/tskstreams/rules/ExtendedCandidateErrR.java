package tskstreams.rules;

import java.io.Serializable;
import java.util.Vector;

import fuzzyset.FuzzySet;
import moa.core.DoubleVector;
import stat.IncrementalVariance;
import tskstreams.learner.FuzzyLearner;

public class ExtendedCandidateErrR extends FuzzyRuleExtendedCandidate implements Comparable<ExtendedCandidateErrR>, Serializable {
	private static final long serialVersionUID = 1L;

	protected double SSE = 0;

	protected double SSE1 = 0;
	protected double SSE2 = 0;

	protected boolean readyToTrain = false ;
	
	protected int alpha = 5 ;
	protected double decayAge = 0;
		
	public ExtendedCandidateErrR(RuleErrR parent, FuzzySet fs, DoubleVector weights, int attribute) {
		super(parent, fs, weights, attribute) ;
	}

	/**
	 * This function is used for the training of the current extension on a anew example
	 * @param resultSummary
	 * @param resultPairParentRule
	 */
	public void trainOnInstance(ResultSummary resultSummary, ResultSummary.ResultPair resultPairParentRule) {

		double val = resultSummary.getNormaliezedExtendedInstance().getValue(attributeIndex) ;
		incVariance.update(val);

		if (readyToTrain){
//			if (this.attributeIndex==1)
//				System.out.println(fsR1+"\t"+fsR2);

			double membershiptWithoutParent = resultSummary.getSumMemberships() - resultPairParentRule.getMembership() ;
			double scoreWithoutParent = resultSummary.getSumWeightedPredictions() 
					- resultPairParentRule.getScore()* resultPairParentRule.getMembership();
			
			double membership1 = fsR1.getMembershipOf(val) ;
			double membership2 = fsR2.getMembershipOf(val) ;

			double score1 = FuzzyLearner.scalarProduct(resultSummary.getNormaliezedExtendedInstance(), weightsR1);
			double score2 = FuzzyLearner.scalarProduct(resultSummary.getNormaliezedExtendedInstance(), weightsR2);
			
			double error1 = Math.abs(resultSummary.getNormaliezedTargetValue() - score1) ;
			double error2 = Math.abs(resultSummary.getNormaliezedTargetValue() - score2) ;
					
			double tempPred = (membership1*score1 + membership2*score2 + scoreWithoutParent)/(membershiptWithoutParent+membership1+membership2) ;
			SSE += Math.pow(tempPred - resultSummary.getNormaliezedTargetValue(), 2) ;

			if (parentRule.RC.FL.chooseSingleRuleOption.isSet()) {
				if (membershiptWithoutParent+membership1>0){
					double tempPred1 = (membership1*score1 + scoreWithoutParent)/(membershiptWithoutParent+membership1) ;
					SSE1 += Math.pow(tempPred1 - resultSummary.getNormaliezedTargetValue(), 2) ;
				}else {
					double tempPred1 = (resultSummary.getResultPairDefaultRule().getMembership() *resultSummary.getResultPairDefaultRule().getScore() 
							+ scoreWithoutParent)/(membershiptWithoutParent+resultSummary.getResultPairDefaultRule().getMembership()) ;
					SSE1 += Math.pow(tempPred1 - resultSummary.getNormaliezedTargetValue(), 2) ;						
				}
				
				if (membershiptWithoutParent+membership2>0){
					double tempPred2 = (membership2*score2 + scoreWithoutParent)/(membershiptWithoutParent+membership2) ;
					SSE2 += Math.pow(tempPred2 - resultSummary.getNormaliezedTargetValue(), 2) ;
				}else {
					double tempPred1 = (resultSummary.getResultPairDefaultRule().getMembership() *resultSummary.getResultPairDefaultRule().getScore() 
							+ scoreWithoutParent)/(membershiptWithoutParent+resultSummary.getResultPairDefaultRule().getMembership()) ;
					SSE2 += Math.pow(tempPred1 - resultSummary.getNormaliezedTargetValue(), 2) ;
				}
			}else{
				ResultSummary.ResultPair defaultResultPair = null ;
				for (ResultSummary.ResultPair rp: resultSummary.getResultPairs()) {
					if (parentRule.RC.defaultRule == rp.getRule())
						defaultResultPair = rp ;
				}				
			}
			
			if (membership1 > membership2 && error1 > error2){
			// shift right
				double memberhsip = resultPairParentRule.getNormalizedMembership() ;
				double errorDiff = Math.abs(error1- error2) ;
				shiftRight(errorDiff, memberhsip);
//				if (this.attributeIndex==1)
//					System.out.println("shiftRight\t"+fsR1+"\t"+fsR2);
				
			}else if (membership1 < membership2 && error1 < error2){
			// shift left
				double memberhsip = resultPairParentRule.getNormalizedMembership() ;
				double errorDiff = Math.abs(error1- error2) ;
				shiftLeft(errorDiff, memberhsip);
//				if (this.attributeIndex==1)
//					System.out.println("shiftLeft\t"+fsR1+"\t"+fsR2);
			}else{
				this.decayAge ++ ;
			// just update the weights
				double normalizedMembership = membership1/(membershiptWithoutParent + membership1 + membership2) ;
				error1 = resultSummary.getNormaliezedTargetValue() - tempPred ;
//				error1 = resultSummary.getNormaliezedTargetValue() - score1 ;
//				System.out.print("update weight1:"+weightsR1+"\terror1:"+error1+"\tnormMem:"+normalizedMembership);
//				if (Double.isNaN(error1))
//					System.out.print("nan update weight1:"+weightsR1+"\terror1:"+error1+"\tnormMem:"+normalizedMembership);
					
				updatesWeightAndRate(resultSummary.getNormaliezedExtendedInstance(), error1, weightsR1, normalizedMembership);
//				System.out.print("after update weight1:"+weightsR1+"\terror1:"+error1+"\tnormMem:"+normalizedMembership);
				
				normalizedMembership = membership2/(membershiptWithoutParent + membership1 + membership2) ;
				error2 = resultSummary.getNormaliezedTargetValue() - tempPred ;
//				error2 = resultSummary.getNormaliezedTargetValue() - score2 ;
//				System.out.print("update weight2:"+weightsR2+"\terror2:"+error2+"\tnormMem:"+normalizedMembership);
//				if (Double.isNaN(error1))
//					System.out.print("nan update weight2:"+weightsR2+"\terror2:"+error2+"\tnormMem:"+normalizedMembership);

				updatesWeightAndRate(resultSummary.getNormaliezedExtendedInstance(), error2, weightsR2, normalizedMembership);
//				System.out.print("after update weight2:"+weightsR2+"\terror2:"+error2+"\tnormMem:"+normalizedMembership);
			}	
			
		}else if (incVariance.getCounter()>FuzzyLearner.graceperiod/2 && ! readyToTrain){
//			if (this.attributeIndex==1)
//				System.out.println(fsR1+"\t"+fsR2);
			
			readyToTrain = true ;
			SSE += Math.pow(resultSummary.getFinalPrediction() - resultSummary.getNormaliezedTargetValue(), 2) ;

			if (parentFS instanceof FuzzySet.LOToRO){				
				fsR1 = new FuzzySet.LO_S_Shaped(incVariance.getCurrentMean()+incVariance.getStandardDeviation()/alpha,
						incVariance.getCurrentMean()+2*incVariance.getStandardDeviation()/alpha) ;
				fsR2 = new FuzzySet.RO_S_Shaped(incVariance.getCurrentMean()-2*incVariance.getStandardDeviation()/alpha,
						incVariance.getCurrentMean()-incVariance.getStandardDeviation()/alpha) ;				

			}else if (parentFS instanceof FuzzySet.LO_S_Shaped){
				
				double a = Math.min(((FuzzySet.LO_S_Shaped) parentFS).getA(),
						incVariance.getCurrentMean()+incVariance.getStandardDeviation()/alpha)  ;
				double b = Math.min(((FuzzySet.LO_S_Shaped) parentFS).getA(),
						incVariance.getCurrentMean()+2*incVariance.getStandardDeviation()/alpha) ;
				fsR1 = new FuzzySet.LO_S_Shaped(a,b) ;
				
//				a = incVariance.getCurrentMean() - 2 * incVariance.getStandardDeviation()/alpha;
//				b = incVariance.getCurrentMean() - incVariance.getStandardDeviation()/alpha ;

				double c = ((FuzzySet.LO_S_Shaped) parentFS).getA() ;
				double d = ((FuzzySet.LO_S_Shaped) parentFS).getB() ;

				a = Math.min(c, incVariance.getCurrentMean() - 2 * incVariance.getStandardDeviation()/alpha);
				b = Math.min(c, incVariance.getCurrentMean() - incVariance.getStandardDeviation()/alpha) ;

				fsR2 = new FuzzySet.S_Shaped(a, b, c, d) ;
								
			}else if (parentFS instanceof FuzzySet.RO_S_Shaped){
				
				double a = ((FuzzySet.RO_S_Shaped) parentFS).getA() ;
				double b = ((FuzzySet.RO_S_Shaped) parentFS).getB() ;

//				double c = incVariance.getCurrentMean() + incVariance.getStandardDeviation()/alpha;
//				double d = incVariance.getCurrentMean() + 2*incVariance.getStandardDeviation()/alpha;

				double c = Math.max(b,incVariance.getCurrentMean() + incVariance.getStandardDeviation()/alpha);
				double d = Math.max(b,incVariance.getCurrentMean() + 2*incVariance.getStandardDeviation()/alpha);
				
				fsR1 = new FuzzySet.S_Shaped(a, b, c, d) ;

				a = Math.max(((FuzzySet.RO_S_Shaped) parentFS).getB(), incVariance.getCurrentMean() - 2* incVariance.getStandardDeviation()/alpha);
				b = Math.max(((FuzzySet.RO_S_Shaped) parentFS).getB(), incVariance.getCurrentMean() - incVariance.getStandardDeviation()/alpha);
				
				fsR2 = new FuzzySet.RO_S_Shaped(a,b) ;
								
			}else if (parentFS instanceof FuzzySet.S_Shaped){
				
				double a = ((FuzzySet.S_Shaped) parentFS).getA() ;
				double b = ((FuzzySet.S_Shaped) parentFS).getB() ;

				double c = Math.max(b, Math.min(((FuzzySet.S_Shaped) parentFS).getC(), incVariance.getCurrentMean() + incVariance.getStandardDeviation()/alpha));
				double d = Math.max(b, Math.min(((FuzzySet.S_Shaped) parentFS).getC(), incVariance.getCurrentMean() + 2* incVariance.getStandardDeviation()/alpha));				
				
//				double c = Math.min(((FuzzySet.S_Shaped) parentFS).getC(), incVariance.getCurrentMean() + incVariance.getStandardDeviation()/alpha);
//				double d = Math.min(((FuzzySet.S_Shaped) parentFS).getC(), incVariance.getCurrentMean() + 2* incVariance.getStandardDeviation()/alpha);				
				
				fsR1 = new FuzzySet.S_Shaped(a, b, c, d) ;

				c = ((FuzzySet.S_Shaped) parentFS).getC() ;
				d = ((FuzzySet.S_Shaped) parentFS).getD() ;
				
				a = Math.min(c,Math.max(((FuzzySet.S_Shaped) parentFS).getB(), incVariance.getCurrentMean() - 2 * incVariance.getStandardDeviation()/alpha));
				b = Math.min(c,Math.max(((FuzzySet.S_Shaped) parentFS).getB(), incVariance.getCurrentMean() - incVariance.getStandardDeviation()/alpha));

//				a = Math.max(((FuzzySet.S_Shaped) parentFS).getB(), incVariance.getCurrentMean() - 2 * incVariance.getStandardDeviation()/alpha);
//				b = Math.max(((FuzzySet.S_Shaped) parentFS).getB(), incVariance.getCurrentMean() - incVariance.getStandardDeviation()/alpha);
				
				 
				fsR2 = new FuzzySet.S_Shaped(a, b, c, d) ;
			}
		}
		if (incVariance.getCounter()<FuzzyLearner.graceperiod && ! readyToTrain){
			SSE += Math.pow(resultSummary.getFinalPrediction() - resultSummary.getNormaliezedTargetValue(), 2) ;
		}			
	}
	
	/**
	 * This function is used for the shifting of a fuzzy sets to the left
	 * @param errorDiff
	 * @param normMembership
	 */
	public void shiftLeft(double errorDiff, double normMembership){

		double shift = errorDiff*normMembership*FuzzyLearner.learningRate ;

		if (fsR1 instanceof FuzzySet.LO_S_Shaped){
			FuzzySet.LO_S_Shaped left = (FuzzySet.LO_S_Shaped) fsR1 ;
			left.SetAll(left.getA()-shift, left.getB()-shift);
			if(fsR2 instanceof FuzzySet.S_Shaped){
				FuzzySet.S_Shaped right = (FuzzySet.S_Shaped) fsR2 ;
				right.SetAll(right.getA()-shift, right.getB()-shift, right.getC(), right.getD()); 
			}
			else if(fsR2 instanceof FuzzySet.RO_S_Shaped){
				FuzzySet.RO_S_Shaped right = (FuzzySet.RO_S_Shaped) fsR2 ;
				right.SetAll(right.getA()-shift, right.getB()-shift);
			}
		}else if (fsR1 instanceof FuzzySet.S_Shaped){
			FuzzySet.S_Shaped left = (FuzzySet.S_Shaped) fsR1 ;
			if(left.getB()<left.getC()-shift){
				left.SetAll(left.getA(), left.getB(), left.getC()-shift, left.getD()-shift); 
				if(fsR2 instanceof FuzzySet.S_Shaped){
					FuzzySet.S_Shaped right = (FuzzySet.S_Shaped) fsR2 ;
					right.SetAll(right.getA()-shift, right.getB()-shift, right.getC(), right.getD()); 
				}
				else if(fsR2 instanceof FuzzySet.RO_S_Shaped){
					FuzzySet.RO_S_Shaped right = (FuzzySet.RO_S_Shaped) fsR2 ;
					right.SetAll(right.getA()-shift, right.getB()-shift);
				}				
			}
		}
	}
	
	/**
	 * This function is used for the shifting of fuzzy set to the right
	 * @param errorDiff
	 * @param normMembership
	 */
	public void shiftRight(double errorDiff, double normMembership){

		double shift = errorDiff*normMembership*FuzzyLearner.learningRate ;
		
		if (fsR2 instanceof FuzzySet.RO_S_Shaped){
			FuzzySet.RO_S_Shaped right = (FuzzySet.RO_S_Shaped) fsR2 ;
			right.SetAll(right.getA()+shift, right.getB()+shift);
			if(fsR1 instanceof FuzzySet.S_Shaped){
				FuzzySet.S_Shaped left = (FuzzySet.S_Shaped) fsR1 ;
				left.SetAll(left.getA(), left.getB(), left.getC()+shift, left.getD()+shift); 
			}
			else if(fsR1 instanceof FuzzySet.LO_S_Shaped){
				FuzzySet.LO_S_Shaped left = (FuzzySet.LO_S_Shaped) fsR1 ;
				left.SetAll( left.getA()+shift, left.getB()+shift);
			}			
		}else if (fsR2 instanceof FuzzySet.S_Shaped){
			FuzzySet.S_Shaped right = (FuzzySet.S_Shaped) fsR2 ;
			if(right.getB()+shift<right.getC()){
				right.SetAll(right.getA()+shift, right.getB()+shift, right.getC(), right.getD()); 
				if(fsR1 instanceof FuzzySet.S_Shaped){
					FuzzySet.S_Shaped left = (FuzzySet.S_Shaped) fsR1 ;
					left.SetAll(left.getA(), left.getB(), left.getC()+shift, left.getD()+shift); 
				}
				else if(fsR1 instanceof FuzzySet.LO_S_Shaped){
					FuzzySet.LO_S_Shaped left = (FuzzySet.LO_S_Shaped) fsR1 ;
					left.SetAll(left.getA()+shift, left.getB()+shift);
				}				
			}
		}
	}
	
	public void updatesWeightAndRate(DoubleVector normaliezedExtendedInstance, double error, 
			DoubleVector weights,double normalizedMembership){		

		error *= FuzzyLearner.learningRate / (1 + this.decayAge * FuzzyLearner.decayFactor) ;
		error /=weights.numValues() ;
		DoubleVector temp = new DoubleVector(normaliezedExtendedInstance);
		if (temp.sumOfValues()>1)
			FuzzyLearner.normalize(temp) ;
		temp.scaleValues(error*normalizedMembership);		
		weights.addValues(temp); 
	}	

//	public void updatesWeightAndRate(DoubleVector normaliezedExtendedInstance, double error, 
//			DoubleVector weights,double normalizedMembership){		
//		
//		error *= FuzzyLearner10.learningRate/(1 + this.decayAge * FuzzyLearner10.decayFactor) ;
//		error /=parentRule.terms.size() ;
//		DoubleVector temp = new DoubleVector(normaliezedExtendedInstance);
//		FuzzyLearner10.normalize(temp) ;
//		temp.scaleValues(error*normalizedMembership);		
//		weights.addValues(temp);
////		if (Math.abs(error)>1000)
////			System.out.println(error+"\t["+temp+"]\t["+weights+"]");
////		if (attributeIndex==0)
////			System.out.println(error+"\t["+temp+"]\t["+weights+"]");
//	}	
		
	@Override
	public int compareTo(ExtendedCandidateErrR other) {
		if (this.parentRule.seenInstances > 0) {
			Double a = this.getSSE() / this.parentRule.seenInstances;
			Double b = other.getSSE() / other.parentRule.seenInstances;
			return a.compareTo(b);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public void reset() {
		this.SSE = 0;
		this.decayAge = 0 ;
	}
	
	@Override
	public String toString() {
		return "[EXT]" + fsR1 + ", " + fsR2;
	}

	public double getSSE() {
		return SSE + ((RuleErrR)parentRule).floatingSSE;
	}

	public double getMeanSSE() {

		if (this.parentRule.seenInstances > 0) {
			return (SSE + ((RuleErrR)parentRule).floatingSSE) / this.parentRule.seenInstances;
		} else {
			return (SSE + ((RuleErrR)parentRule).floatingSSE) / 1;
		}
	}

	public double [] getMeanSSESingleExension() {
		double [] result = {3.4,7.5} ;//new double[] ;
		
		if (this.parentRule.seenInstances > 0) {
			result[0] = (SSE1 + ((RuleErrR)parentRule).floatingSSE)/ this.parentRule.seenInstances ;
			result[1] = (SSE2 + ((RuleErrR)parentRule).floatingSSE)/ this.parentRule.seenInstances ;
		} else {
			result[0] = (SSE1 + ((RuleErrR)parentRule).floatingSSE) ;
			result[1] = (SSE2 + ((RuleErrR)parentRule).floatingSSE) ;
		}
		return result ;
	}

	
	public Vector<RuleErrR> CreateRuleFromExtension(){
		return ((RuleErrR)parentRule).CreateRuleFromExtension(this.attributeIndex) ;
	}
	
	public boolean getIsReadyToTrain(){
		return readyToTrain ;
	}
	
	public int getAttributeIndex() {
		return attributeIndex ;
	}
}
