package tskstreams.rules;

import fuzzyset.FuzzySet;
import moa.core.DoubleVector;
import stat.IncrementalVariance;

public abstract class FuzzyRuleExtendedCandidate {

	protected int attributeIndex = 0;
	protected DoubleVector weightsR1 = new DoubleVector(); 
	protected DoubleVector weightsR2 = new DoubleVector(); 

	protected FuzzySet fsR1 = null ;
	protected FuzzySet fsR2 = null ;
	
	protected FuzzyRule parentRule = null;
	protected FuzzySet parentFS = null;

	protected IncrementalVariance incVariance = null ;

	public abstract void reset() ;

	public FuzzyRuleExtendedCandidate(FuzzyRule parent, FuzzySet fs, DoubleVector weights, int attribute) {
		this.parentRule = parent ;
		this.parentFS = fs;
		this.weightsR1 = (DoubleVector) weights.copy() ;
		this.weightsR2 = (DoubleVector) weights.copy() ;
		this.attributeIndex = attribute ;
		this.incVariance = new IncrementalVariance() ;
	}
	
	public FuzzyRule getParentRule() {
		return parentRule;
	}
}
