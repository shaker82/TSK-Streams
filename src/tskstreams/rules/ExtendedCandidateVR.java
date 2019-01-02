package tskstreams.rules;

import java.io.Serializable;
import java.util.Vector;

import fuzzyset.FuzzySet;
import moa.classifiers.rules.multilabel.core.AttributeExpansionSuggestion;
import moa.core.DoubleVector;
import stat.IncrementalVariance;
import stat.VarianceReductionObserver;
import tskstreams.learner.FuzzyLearner;

public class ExtendedCandidateVR extends FuzzyRuleExtendedCandidate implements Serializable {
//public class ExtendedCandidate11 implements Comparable<ExtendedCandidate11>, Serializable {
	private static final long serialVersionUID = 1L;

	protected double SSE = 0;

	protected double SSE1 = 0;
	protected double SSE2 = 0;

	protected boolean readyToTrain = false ;
	
	protected int alpha = 5 ;
	protected double decayAge = 0;
	
	protected VarianceReductionObserver VObserver ;

	protected AttributeExpansionSuggestion currentBestExpansionSuggestion = null ;
		
	public ExtendedCandidateVR(RuleVR parent, FuzzySet fs, DoubleVector weights, int attribute) {
		super(parent, fs, weights, attribute) ;
		VObserver= new VarianceReductionObserver(attribute) ;
	}

	/**
	 * This function is used for the training of the current extension on a anew example
	 * @param resultSummary
	 * @param resultPairParentRule
	 */
	public void trainOnInstance(ResultSummary resultSummary, ResultSummary.ResultPair resultPairParentRule, DoubleVector [] exampleStatistics) {
		double val = resultSummary.getNormaliezedExtendedInstance().getValue(attributeIndex) ;
		incVariance.update(val);
		VObserver.addInstance(val, exampleStatistics);
	}
	
	public AttributeExpansionSuggestion getBestSplitSuggestions(DoubleVector [] literalStatistics){
		currentBestExpansionSuggestion = VObserver.getBestSplitSuggestions(literalStatistics) ; 
		return currentBestExpansionSuggestion ;
	}
	
	public void CreateFuzzySets(double value){
		System.out.println("CreateFuzzySets:"+attributeIndex+"\t"+ value);
		
		if (parentFS instanceof FuzzySet.LOToRO){
			fsR1 = new FuzzySet.LO_S_Shaped(value+incVariance.getStandardDeviation()/alpha,
					value+2*incVariance.getStandardDeviation()/alpha) ;
			fsR2 = new FuzzySet.RO_S_Shaped(value-2*incVariance.getStandardDeviation()/alpha,
					value-incVariance.getStandardDeviation()/alpha) ;				

		}else if (parentFS instanceof FuzzySet.LO_S_Shaped){
			
			double a = Math.min(((FuzzySet.LO_S_Shaped) parentFS).getA(),
					value+incVariance.getStandardDeviation()/alpha)  ;
			double b = Math.min(((FuzzySet.LO_S_Shaped) parentFS).getA(),
					value+2*incVariance.getStandardDeviation()/alpha) ;
			fsR1 = new FuzzySet.LO_S_Shaped(a,b) ;
			
			a = value - 2 * incVariance.getStandardDeviation()/alpha;
			b = value - incVariance.getStandardDeviation()/alpha ;
			double c = ((FuzzySet.LO_S_Shaped) parentFS).getA() ;
			double d = ((FuzzySet.LO_S_Shaped) parentFS).getB() ;
			fsR2 = new FuzzySet.S_Shaped(a, b, c, d) ;
							
		}else if (parentFS instanceof FuzzySet.RO_S_Shaped){
			
			double a = ((FuzzySet.RO_S_Shaped) parentFS).getA() ;
			double b = ((FuzzySet.RO_S_Shaped) parentFS).getB() ;

			double c = value + incVariance.getStandardDeviation()/alpha;
			double d = value + 2*incVariance.getStandardDeviation()/alpha;
			
			fsR1 = new FuzzySet.S_Shaped(a, b, c, d) ;

			a = Math.max(((FuzzySet.RO_S_Shaped) parentFS).getB(), value - 2* incVariance.getStandardDeviation()/alpha);
			b = Math.max(((FuzzySet.RO_S_Shaped) parentFS).getB(), value - incVariance.getStandardDeviation()/alpha);
			
			fsR2 = new FuzzySet.RO_S_Shaped(a,b) ;
							
		}else if (parentFS instanceof FuzzySet.S_Shaped){
			
			double a = ((FuzzySet.S_Shaped) parentFS).getA() ;
			double b = ((FuzzySet.S_Shaped) parentFS).getB() ;

			double c = Math.min(((FuzzySet.S_Shaped) parentFS).getC(), value + incVariance.getStandardDeviation()/alpha);
			double d = Math.min(((FuzzySet.S_Shaped) parentFS).getC(), value + 2* incVariance.getStandardDeviation()/alpha);				
			fsR1 = new FuzzySet.S_Shaped(a, b, c, d) ;

			a = Math.max(((FuzzySet.S_Shaped) parentFS).getB(), value - 2 * incVariance.getStandardDeviation()/alpha);
			b = Math.max(((FuzzySet.S_Shaped) parentFS).getB(), value - incVariance.getStandardDeviation()/alpha);
			c = ((FuzzySet.S_Shaped) parentFS).getC() ;
			d = ((FuzzySet.S_Shaped) parentFS).getD() ;
			 
			fsR2 = new FuzzySet.S_Shaped(a, b, c, d) ;
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

	public double [] getRulesMerits() {
		DoubleVector[][] resultingStatistics=currentBestExpansionSuggestion.getResultingNodeStatistics();
		double [] branchMerits=VObserver.getSplitCriterion().getBranchesSplitMerits(resultingStatistics);
		return branchMerits ;
	}
	
	public boolean getIsReadyToTrain(){
		return readyToTrain ;
	}
	
	public int getAttributeIndex() {
		return attributeIndex ;
	}
}
