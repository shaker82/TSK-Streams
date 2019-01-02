package tskstreams.rules;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import fuzzyset.FuzzySet;
import fuzzyset.aggregations.AbstractAggregation;
import moa.classifiers.core.driftdetection.ChangeDetector;
import moa.core.DoubleVector;
import tskstreams.learner.FuzzyLearner;
import tskstreams.learner.FuzzyLearner.RuleController10;

public abstract class FuzzyRule {

	// Time
	protected int seenInstances = 0;
	protected double decayAge = 0;

	protected ChangeDetector phtest0 = FuzzyLearner.changeDetector.copy() ;
	boolean driftstatus = false ;

	// Options
	protected AbstractAggregation aggregationTNorm = new fuzzyset.aggregations.Minimum();
	protected AbstractAggregation aggregationCoNorm = new fuzzyset.aggregations.CoMaximum();

	protected Vector<FuzzySet> terms = new Vector<FuzzySet>();
	protected ArrayList<FuzzyRuleExtendedCandidate> setE ;
	protected DoubleVector weights = new DoubleVector(); // w	
	protected Vector<Integer> history = new Vector<Integer>(); 
	protected int numNonEmptyTerms = 0;

	// the prefix and the rulesSystemVersion identifies a rule
	protected String prefix = "";
	protected int rulesSystemVersion = 0;

	protected RuleController10 RC = null ;

	public abstract void buildExtendedCandidates();
	
	public abstract void buildExtendedCandidates(int termId) ;

	public abstract void trainOnInstance(ResultSummary resultSummary, ResultSummary.ResultPair resultPair) ;

	protected Random random = new Random(1);
	protected long seed = 1l;
	
	public ArrayList<FuzzyRuleExtendedCandidate> getExtendedCandidates() {
		return this.setE;
	}
	
	/**
	 * This function is used to set the rule's identifier 
	 * @param name
	 * @param rulesSystemVersion
	 */
	public void setPrefixAndVersion(String name,int rulesSystemVersion){
		this.prefix = name;
		this.rulesSystemVersion = rulesSystemVersion ;
	}
	
	/**
	 * This function is used to set all important parameters for a rule
	 * @param terms
	 * @param weights
	 */
	public void setAll(Vector<FuzzySet> terms, DoubleVector weights){
		
		this.terms = terms ;
		this.weights = (DoubleVector) weights.copy();		
		this.buildExtendedCandidates() ;
		
		for (FuzzySet fs : this.terms)
			if (! (fs instanceof FuzzySet.LOToRO))
				this.numNonEmptyTerms++ ;
	}
	
	/***
	 * Clearing functions
	 */
	public void clearExtendedCandidates() {
		this.setE.clear();		
	}
	
	/**
	 * Returning the number of current extensions
	 * @return
	 */
	public double getNumberOfExtensions() {
		return setE.size();
	}
	
	/**
	 * Clearing collected measutes
	 */
	public void clearStats() {
		// the timeIndex should not be reset, otherwise the learning rate will start over and become very large
		this.seenInstances =0;
		this.decayAge =0;
		for(FuzzyRuleExtendedCandidate extrule : this.setE){
			extrule.reset();
		}
		this.phtest0.resetLearning();
		this.driftstatus = false ;
	}
	
	public double getMembershipDegree(DoubleVector inst) {
		
		double tv = 1.0;
		int idx = 0 ;
		for (FuzzySet fs : terms) {
			try{
				tv = aggregationTNorm.eval(tv, fs.getMembershipOf(inst.getValue(idx)));
				idx++ ;
					
			}catch(Exception ex){
				System.err.println("FS:"+terms.size()+"\tInst:"+inst.numValues());
				ex.printStackTrace() ;
				System.exit(0);
			}
		}
		return tv;
	}
	
	public double getPrediction(DoubleVector input)	{
		double prediction = FuzzyLearner.scalarProduct(input, weights);
		return prediction;
	}

	public double getScore(DoubleVector input){
		double score = FuzzyLearner.scalarProduct(input, weights);
		return score;
	}
	
	public void setRandom(int numInputAttributes){
		for(int i=0; i < numInputAttributes; i++){
			this.weights.setValue(i, this.random.nextDouble()*2 -1);
		}
	}
	
	public boolean mergeWithSibling(FuzzyRule siblingRule) throws CloneNotSupportedException {
		
//		System.out.println("#######MErgging");
//		System.out.println("siblingRule\t"+siblingRule.getPrefix());
//		System.out.println("siblingRule\t"+siblingRule.getHistory());
		String parentPrefix ="" ;
		// checking if the given rule is related to the current one

		parentPrefix = siblingRule.getPrefix().substring(0,siblingRule.getPrefix().length()-1) ;

		if (this.prefix.equals(parentPrefix)){
			return false ;
		}
		if (!this.prefix.startsWith(parentPrefix) || this.rulesSystemVersion != siblingRule.rulesSystemVersion){
			return false ;
		}
		
		int changeIndex = siblingRule.getPrefix().length() -1;
		Integer fIndex = history.get(changeIndex) ;
		FuzzySet currentFS = this.terms.get(fIndex) ;
		FuzzySet siblingFS = siblingRule.terms.get(fIndex) ;

//		System.out.println("siblingRule\t"+currentFS);
//
//		System.out.println("currentRule\t"+this.getPrefix());
//		System.out.println("currentRule\t"+this.getHistory());
//		System.out.println("currentRule\t"+siblingFS);
//
//		System.out.println("changeIndex\t"+changeIndex);

//		Integer fIndex =null;//= history.get(changeIndex) ;

		history.remove(changeIndex) ;
		
		Vector<FuzzySet> merged = currentFS.extendFuzzySet(siblingFS) ;
		// In this case we should just ignore the extension and correct the prefix 
		if (merged.size()>1){
			prefix = (new StringBuffer(prefix)).deleteCharAt(changeIndex).toString() ;			
//			System.out.println("currentFS\t"+currentFS);
//			System.out.println("siblingFS\t"+siblingFS);			
			return true ;
			//throw new UnsupportedOperationException() ;
		}
		
		this.terms.set(fIndex, merged.get(0)) ;
		prefix = (new StringBuffer(prefix)).deleteCharAt(changeIndex).toString() ;
		return true ;
	}
	
	public Vector<FuzzySet> getTerms() {
		return terms;
	}

	public DoubleVector getWeights() {
		return weights;
	}

	public void setWeights(DoubleVector weights) {
		this.weights= weights;
	}
	
	public Vector<Integer> getHistory() {
		return history;
	}

	public void setHistory(Vector<Integer> hist) {
		this.history = hist ;
	}
	
	public String getPrefix(){
		return prefix;
	}

	public int getNumNonEmptyTerms(){
		return this.numNonEmptyTerms ;
	}

	public boolean getChangeStatus(){
		return driftstatus ;
	}
}
