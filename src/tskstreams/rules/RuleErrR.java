package tskstreams.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import com.yahoo.labs.samoa.instances.Instance;

import fuzzyset.FuzzySet;
import fuzzyset.aggregations.AbstractAggregation;
import moa.classifiers.core.driftdetection.ChangeDetector;
import moa.core.DoubleVector;
import tskstreams.learner.FuzzyLearner;
import tskstreams.learner.FuzzyLearner.RuleController10;

public class RuleErrR extends FuzzyRule implements Serializable
{
	private static final long serialVersionUID = 1L;

	// Time
	public double floatingSSE = 0;
	

	
	public RuleErrR(RuleController10 RC){
		this.RC = RC ;
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
		double error = resultSummary.getNormaliezedTargetValue() - resultSummary.getFinalPrediction() ;

		if(resultPair.getNormalizedMembership() > 0){
//			updatesWeightsAndRate(resultSummary.getNormaliezedExtendedInstance(),error * resultPair.getNormalizedMembership()) ;
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
			for(FuzzyRuleExtendedCandidate e : setE){
				((ExtendedCandidateErrR)e).trainOnInstance(resultSummary, resultPair);	// Most expensive operations	
			}
		}
		else{
			this.floatingSSE += Math.pow(error, 2) ;
		}
	}

	/**
	 * This function is used to create the set of the extended candidates
	 */
	@Override
	public void buildExtendedCandidates() {	
		this.setE = new ArrayList<FuzzyRuleExtendedCandidate>() ;
		for (int i = 0 ; i<this.terms.size() ; i++){
			ExtendedCandidateErrR ex = new ExtendedCandidateErrR(this, terms.get(i), weights, i) ;
			this.setE.add(ex) ;
		}
	}
	
	/**
	 * This function is used to create the extended candidates of a given attribute
	 */
	@Override
	public void buildExtendedCandidates(int termId) {	
		ExtendedCandidateErrR ex = new ExtendedCandidateErrR(this, terms.get(termId), weights, termId) ;
		this.setE.set(termId,ex) ;
	}

	/**
	 * This function is used to create new rules as an extension to this rule
	 * @param attribute
	 * @return
	 */
	public Vector <RuleErrR> CreateRuleFromExtension(int attribute) {
		
		ExtendedCandidateErrR ex= (ExtendedCandidateErrR) this.setE.get(attribute) ;
				
		Vector<FuzzySet> FSs = FuzzyLearner.cloneFSVector(this.terms) ;
		FSs.set(attribute, ex.fsR1) ;
		RuleErrR r1 = new RuleErrR(this.RC) ;
		r1.setAll(FSs, ex.weightsR1);
		r1.setPrefixAndVersion(this.prefix+"a",this.rulesSystemVersion);
		Vector<Integer> tempHist = ((Vector<Integer>)this.getHistory().clone()) ;
		tempHist.add(ex.attributeIndex) ;
		r1.setHistory(tempHist) ;
		
		FSs =  FuzzyLearner.cloneFSVector(this.terms) ;
		FSs.set(attribute, ex.fsR2) ;
		RuleErrR r2 = new RuleErrR(this.RC) ;
		r2.setAll(FSs, ex.weightsR2);
		r2.setPrefixAndVersion(this.prefix+"b",this.rulesSystemVersion);
		tempHist = ((Vector<Integer>)this.getHistory().clone()) ;
		tempHist.add(ex.attributeIndex) ;
		r2.setHistory(tempHist) ;
		
		Vector <RuleErrR> result = new Vector<>() ;
		result.add(r1) ; 
		result.add(r2) ; 
		return result ;		
	}


	@Override
	public void clearStats() {
		super.clearStats();
		this.floatingSSE = 0 ;
	}

}
