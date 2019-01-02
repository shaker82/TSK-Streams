package tskstreams.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import com.yahoo.labs.samoa.instances.Instance;

import fuzzyset.FuzzySet;
import fuzzyset.aggregations.AbstractAggregation;
import moa.classifiers.core.driftdetection.ChangeDetector;
import moa.classifiers.rules.core.NumericRulePredicate;
import moa.classifiers.rules.core.conditionaltests.NumericAttributeBinaryRulePredicate;
import moa.classifiers.rules.multilabel.core.AttributeExpansionSuggestion;
import moa.classifiers.rules.multilabel.core.splitcriteria.MultiTargetVarianceRatio;
import moa.core.DoubleVector;
import tskstreams.learner.FuzzyLearner;
import tskstreams.learner.FuzzyLearner.RuleController10;

public class RuleVR extends FuzzyRule implements Serializable{
	private static final long serialVersionUID = 1L;

	// Time
	protected double weigthsSeen = 0 ;
	
	protected Random random = new Random(1);
	protected long seed = 1l;
	
	protected DoubleVector [] literalStatistics ;
	private double initialMerit = 0 ;
	
	public RuleVR(RuleController10 RC){
		this.RC = RC ;
		
		literalStatistics= new DoubleVector[1];
		literalStatistics[0] = new DoubleVector(new double[3]);
	}
	
	public void updatesWeightAndRate(DoubleVector normaliezedExtendedInstance, double error, 
			DoubleVector weights,double normalizedMembership){		

		error *= FuzzyLearner.learningRate / (1 + this.decayAge * FuzzyLearner.decayFactor) ;
		error /=terms.size() ;
		DoubleVector temp = new DoubleVector(normaliezedExtendedInstance);
		temp.scaleValues(error*normalizedMembership);		
		if (temp.sumOfValues()>terms.size())
			FuzzyLearner.normalize(temp) ;
		weights.addValues(temp); 
	}	

	@Override
	public void trainOnInstance(ResultSummary resultSummary, ResultSummary.ResultPair resultPair){	
		
		this.seenInstances++ ;
		
		//////
		double sum=resultPair.getNormalizedMembership() * resultSummary.getNormaliezedTargetValue() ;
		double squaredSum=resultPair.getNormalizedMembership()*resultSummary.getNormaliezedTargetValue()*resultSummary.getNormaliezedTargetValue();
		DoubleVector [] exampleStatistics=new DoubleVector[1];
		exampleStatistics[0]= new DoubleVector(new double[]{resultPair.getNormalizedMembership(),sum, squaredSum});

		literalStatistics[0].addValues(exampleStatistics[0].getArrayRef());
		//////
		
		double error = resultSummary.getNormaliezedTargetValue() - resultSummary.getFinalPrediction() ;
		if(resultPair.getNormalizedMembership() > 0){
			updatesWeightAndRate(resultSummary.getNormaliezedExtendedInstance(), error, weights, resultPair.getNormalizedMembership());

			// here we need to see what error to use for the drift detection
			// updating the drift detector
			double currentError = resultSummary.getNormaliezedTargetValue() - resultPair.getScore() ;
			currentError*=resultPair.getNormalizedMembership() ;
			this.phtest0.input(Math.abs(currentError));

			this.driftstatus= this.driftstatus  || phtest0.getChange() ;
		}
		// updating the extended rules
		if(resultPair.getNormalizedMembership() > 0){
			
			this.decayAge++ ;
			this.weigthsSeen += resultPair.getNormalizedMembership() ;
			for(FuzzyRuleExtendedCandidate e : setE){
				((ExtendedCandidateVR)e).trainOnInstance(resultSummary, resultPair, exampleStatistics);
			}
		}
	}

	/**
	 * This function is used to create the set of the extended candidates
	 */
	@Override
	public void buildExtendedCandidates() {	
		this.setE = new ArrayList<FuzzyRuleExtendedCandidate>() ;
		for (int i = 0 ; i<this.terms.size() ; i++){
			ExtendedCandidateVR ex = new ExtendedCandidateVR(this, terms.get(i), weights, i) ;
			this.setE.add(ex) ;
		}
	}
	
	/**
	 * This function is used to create the extended candidates of a given attribute
	 */
	@Override
	public void buildExtendedCandidates(int termId) {	
		ExtendedCandidateVR ex = new ExtendedCandidateVR(this, terms.get(termId), weights, termId) ;
		this.setE.set(termId,ex) ;
	}
	
//	@Override
	public Vector <RuleVR> tryToExpand(double splitConfidence, double tieThreshold) {
		boolean shouldSplit=false;
		
		if (this.weigthsSeen % this.RC.FL.graceperiod < 1 ) {
			return null ;
		}
		//find the best split per attribute and rank the results
		double sumMerit=0;
		AttributeExpansionSuggestion[] bestSplitSuggestions	 = new AttributeExpansionSuggestion[this.terms.size()] ;
		double [] meritPerInput = new double[this.terms.size()] ;
		for(int i =0 ; i< this.terms.size() ; i++) {
			bestSplitSuggestions[i] = ((ExtendedCandidateVR)this.setE.get(i)).getBestSplitSuggestions(literalStatistics) ;
			double merit=bestSplitSuggestions[i].getMerit();
			if(merit>0){
				meritPerInput[bestSplitSuggestions[i].predicate.getAttributeIndex()]=merit;
				sumMerit+=merit;
			}
		}

		//if merit==0 it means the split have not enough examples in the smallest branch
		if(sumMerit==0) {
			meritPerInput=null; //this indicates that no merit should be considered (e.g. for feature ranking)
			return null ;
		}
		Arrays.sort(bestSplitSuggestions);

		AttributeExpansionSuggestion bestSuggestion = null;
		MultiTargetVarianceRatio splitCriterion = new MultiTargetVarianceRatio() ;
		
		// If only one split was returned, use it
		if (sumMerit!=0 && bestSplitSuggestions.length < 2) {
			//shouldSplit = ((bestSplitSuggestions.length > 0) && (bestSplitSuggestions[0].merit > 0)); 
			bestSuggestion = bestSplitSuggestions[bestSplitSuggestions.length - 1];
			shouldSplit = true;
		} // Otherwise, consider which of the splits proposed may be worth trying
		else {
			
			double epsilon = 0 ;

			if (!this.RC.FL.penalizeLargeRuleSets.isSet()) {
				epsilon = this.RC.computeEpsilonHoeffdingBound(1, splitConfidence, this.weigthsSeen);
			}else{
				epsilon = this.RC.computeEpsilonHoeffdingBoundPlusComplexity(1, splitConfidence, this.weigthsSeen);
			}
			//debug("Hoeffding bound " + hoeffdingBound, 4);
			// Determine the top two ranked splitting suggestions
			bestSuggestion = bestSplitSuggestions[bestSplitSuggestions.length - 1];
			AttributeExpansionSuggestion secondBestSuggestion
			= bestSplitSuggestions[bestSplitSuggestions.length - 2];
			if ((((bestSuggestion.merit-secondBestSuggestion.merit)) > epsilon) || (epsilon < tieThreshold)) {
				//if ((((secondBestSuggestion.merit/bestSuggestion.merit) + hoeffdingBound) < 1) || (hoeffdingBound < tieThreshold)) {
				//debug("Expanded ", 5);
				shouldSplit = true;
				//System.out.println(bestSuggestion.merit);
			}
		}

		if(shouldSplit)
		{
			//check which branch is better and update bestSuggestion (in amrules the splits are binary )
			DoubleVector[][] resultingStatistics=bestSuggestion.getResultingNodeStatistics();
			//if not or higher is better, change predicate (negate condition)
//			double [] branchMerits=splitCriterion.getBranchesSplitMerits(resultingStatistics);
			
			int attribute = bestSuggestion.getPredicate().getAttributeIndex() ;
			double SplitValue = ((NumericAttributeBinaryRulePredicate)bestSuggestion.getPredicate()).getSplitValue() ;
			
			return CreateRuleFromExtension(attribute, SplitValue) ;
		}
		return null;
	}
	
	/**
	 * This function is used to create new rules as an extension to this rule
	 * @param attribute
	 * @return
	 */
	public Vector <RuleVR> CreateRuleFromExtension(int attribute, double SplitValue) {

		ExtendedCandidateVR ex= (ExtendedCandidateVR)this.setE.get(attribute) ;
		ex.CreateFuzzySets(SplitValue);
		double [] rulesMerits = ex.getRulesMerits();
				
		Vector<FuzzySet> FSs = FuzzyLearner.cloneFSVector(this.terms) ;
		FSs.set(attribute, ex.fsR1) ;
		RuleVR r1 = new RuleVR(this.RC) ;
		
		r1.setAll(FSs, (DoubleVector) weights.copy());
		r1.setPrefixAndVersion(this.prefix+"a",this.rulesSystemVersion);
		Vector<Integer> tempHist = ((Vector<Integer>)this.getHistory().clone()) ;
		tempHist.add(ex.attributeIndex) ;
		r1.setHistory(tempHist) ;
		r1.setInitialMerit(rulesMerits[0]);
		
		FSs =  FuzzyLearner.cloneFSVector(this.terms) ;
		FSs.set(attribute, ex.fsR2) ;
		RuleVR r2 = new RuleVR(this.RC) ;
		r2.setAll(FSs, (DoubleVector) weights.copy());
		r2.setPrefixAndVersion(this.prefix+"b",this.rulesSystemVersion);
		tempHist = ((Vector<Integer>)this.getHistory().clone()) ;
		tempHist.add(ex.attributeIndex) ;
		r2.setHistory(tempHist) ;
		r2.setInitialMerit(rulesMerits[1]);
			
		Vector <RuleVR> result = new Vector<>() ;
		result.add(r1) ; 
		result.add(r2) ; 
		return result ;		
	}
	
	@Override
	public void clearStats() {
		super.clearStats();
		this.weigthsSeen = 0 ;
	}
	
	private void setInitialMerit(double merit) {
		this.initialMerit = merit ;
	}

	public double getInitialMerit() {
		return initialMerit ;
	}

}
