package tskstreams.learner;

/**
 * Fuzzy TSK-Rule Learner for streaming data.
 * An adaptive TSK-rules learner for regression problems. See 
 * A. Shaker, W. Heldt and E. Hüllermeier.
 * Learning TSK Fuzzy Rules from Data Streams. 
 * Proceedings of ECML/PKDD, European Conference on Machine Learning and 
 * Knowledge Discovery in Databases, Skopje, Macedonia, 2017.
 *
 * @author Ammar Shaker (ammar dot shaker at uni dash paderborn dot de)
 * @author Waleri Heldt
 */

import utils.LineToFile;
import java.io.Serializable;
import java.util.Vector;

import com.github.javacliparser.FlagOption;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.MultiChoiceOption;
import com.yahoo.labs.samoa.instances.Instance;

import fuzzyset.FuzzySet;
import fuzzyset.FuzzySetIndex;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Regressor;
import moa.classifiers.core.driftdetection.*;
import moa.core.DoubleVector;
import moa.core.Measurement;
import moa.core.SizeOf;
import moa.core.StringUtils;
import stat.*;
import tskstreams.discretize.*;
import tskstreams.rules.ExtendedCandidateErrR;
import tskstreams.rules.FuzzyRule;
import tskstreams.rules.FuzzyRuleExtendedCandidate;
import tskstreams.rules.ResultSummary;
import tskstreams.rules.RuleErrR;
import tskstreams.rules.RuleVR;
import tskstreams.rules.RuleSet;
public class FuzzyLearner extends AbstractClassifier implements Regressor {

	private static final long serialVersionUID = 1L;
	public static double learningRate = 0.02d;
	public static double decayFactor = 0.001d;
	public static double tau = 0.05 ;
	public static double confidence = 0.1 ;
	public static int graceperiod = 100 ;
	public static double max_Membership_For_Choosing_Single_Rule = 0.1 ;
	
	
	public static ChangeDetector changeDetector = null ;
			
	public FlagOption chooseSingleRuleOption = new FlagOption("chooseSingleRule", 'R', "This option allows the learner to "
			+ "choose the single best cadidate rule for extension. This means that the default rule is always there and "
			+ "that in the case of a concept drift no merging is required (just deletion).");

	public FloatOption splitConfidenceOption = new FloatOption("splitConfidence", 'c',
			"Hoeffding Bound Parameter. The allowable error in split decision, values closer to 0 will take longer to decide.",
			0.1, 0.0, 1.0);

	public FloatOption tieThresholdOption = new FloatOption("tieThreshold", 't',
			"Hoeffding Bound Parameter. Threshold below which a split will be forced to break ties.", 0.05, 0.0, 1.0);

	public IntOption gracePeriodOption = new IntOption("gracePeriod", 'g', "grace Period", 80, 1, Integer.MAX_VALUE);

	public FloatOption learnRateOption = new FloatOption("learnRate", 'l',
			"Learning Rate for Stochastic Gradient Descent update", 0.1, 0.0, 1.0);
	
	public FloatOption learningRateDecayFactorOption = new FloatOption(
			"learningRatioDecayFactor", 'd', "Learning rate decay factor (not used when learning rate is constant).",
			0.001, 0, 1.00);

	 public FloatOption deltaOption = new FloatOption("delta", 'e',
	            "Delta parameter of the Page Hinkley Test/AdWin Test", 0.3, 0.0, 1.0);

	 public FloatOption lambdaOption = new FloatOption("lambda", 'b',
	            "Lambda parameter of the Page Hinkley Test", 50, 0.0, Float.MAX_VALUE);

	 public FloatOption alphaOption = new FloatOption("alpha", 'h',
	            "Alpha parameter of the Page Hinkley Test", 1 - 0.0001, 0.0, 1.0);

	public FlagOption penalizeLargeRuleSets = new FlagOption("penalizeLargeRuleSets", 'p', "Prefer smaller number of rules.");

	public MultiChoiceOption driftDetectionOption = new MultiChoiceOption(
            "driftDetection", 'm', "The drift detection method", new String[]{
            		"NoDriftDetection", "PageHinkleyTest","AdWinTest"} ,
                new String[]{
                		"No Drift Detection", "Page Hinkley Test","AdWin Test"
                }, 0);
	
	public MultiChoiceOption normalizeationOption = new MultiChoiceOption(
            "normalization", 'n', "The normalization method", new String[]{
            		"StandardizeAttributesAndTarget", 
            		"NormalizeAttributesStandardizeTarget",
            		"NormalizeAttributesAndTarget",
            		"NoNormalization"} ,
                new String[]{
                		"Standardize the attributes and the target", 
                		"normalize the attributes and Standardize the target",
                		"normalize the attributes and the target",
                		"NoNormalization"}
            , 0);
	
	public MultiChoiceOption learningCriteriaOption = new MultiChoiceOption(
            "learningCriteria", 'C', "The learning criteria", new String[]{
            		"RMSEReduction", 
            		"VarianceRationREduction"} ,
                new String[]{
                		"RMSEReduction", 
                		"VarianceRationREduction"}
            , 0);
	
	private RuleController10 ruleController = null;

	public FuzzyLearner() {
		super() ;
		this.prepareForUse();
		this.resetLearning();
    }
	@Override
	public boolean isRandomizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getModelDescription(StringBuilder out, int indent) {
		StringUtils.appendIndented(out, indent, "Rules: " + ruleController.toString());
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		return new Measurement[] { new Measurement("rules (number) ", this.ruleController.getRuleSetSize()),
				new Measurement("ext.rules (number) ", this.ruleController.getTotalExtendedRuleSize()),
				new Measurement("drifts (number) ", this.ruleController.countChangeDetected )};
	}

	@Override
	public double[] getVotesForInstance(Instance inst) {
		if (! ruleController.initialized) {
			ruleController.initialize(inst);
		}

		DoubleVector normaliezedExtendedInstance = ruleController.PrepareExtendedFeatureVector(inst) ;
		double normaliezedTargetValue= ruleController.NormalizeTarget(inst) ;
		ResultSummary resultSummary = ruleController.getMemberships(normaliezedExtendedInstance,normaliezedTargetValue) ;

		double prediction = resultSummary.getFinalPrediction() ;
		prediction = ruleController.denormalizePrediction(prediction) ;
		return new double[] { prediction };
	}

	@Override
	public void resetLearningImpl() {

		tau = tieThresholdOption.getValue();
		confidence = splitConfidenceOption.getValue();
		graceperiod = gracePeriodOption.getValue();
		learningRate = learnRateOption.getValue() ;
		decayFactor = learningRateDecayFactorOption.getValue() ;
		
		if (driftDetectionOption.getChosenIndex()==0){
			changeDetector = new DummyChangeDetector() ;
		}else if (driftDetectionOption.getChosenIndex()==1){
			changeDetector = new PageHinkleyDM() ;			
			((PageHinkleyDM)changeDetector).alphaOption.setValue(alphaOption.getValue());
			((PageHinkleyDM)changeDetector).deltaOption.setValue(deltaOption.getValue());
			((PageHinkleyDM)changeDetector).lambdaOption.setValue(lambdaOption.getValue());
		}else{
			changeDetector = new ADWINChangeDetector() ;			
			((ADWINChangeDetector)changeDetector).deltaAdwinOption.setValue(deltaOption.getValue());
		}
		ruleController = new RuleController10(this);
	}

	@Override
	public void trainOnInstanceImpl(Instance inst) {
		if (ruleController == null) {
			this.resetLearning();
		}
		if (! ruleController.initialized) {
			ruleController.initialize(inst);
		}		
		ruleController.trainOnInstance(inst);
	}

	public int calcByteSize() {
		return (int) SizeOf.fullSizeOf(this);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.ruleController.toString();
	}


	/**
	 * This inner class is responsible for the maintaining the set of fuzzy TSK rules
	 * @author shaker
	 *
	 */
	public class RuleController10 implements Serializable {

		private static final long serialVersionUID = 1L;

		private RuleSet rs = new RuleSet();
		public FuzzyRule defaultRule ; 
		public FuzzyLearner FL ; 
		
		private int instancesSeen = 0;
		private int currentSystemVersion = 0;
		private boolean initialized = false;

		private double CurModSSE = 0;
		protected int countChangeDetected = 0;

		protected Vector<IncrementalVariance> statsAttributes = new Vector<IncrementalVariance>() ;
		protected IncrementalVariance statsTarget = new IncrementalVariance() ;

		protected Vector<FuzzyRuleExtendedCandidate> currentNonReadyCandidates = null ;
		protected Vector<FuzzyRuleExtendedCandidate> currentValidCandidates = null ;
		
		
		public RuleController10(FuzzyLearner FL) {
			statsAttributes = new Vector<>() ;
			statsTarget = new IncrementalVariance() ;
			this.FL = FL ;
		}

		public void initialize(Instance inst){
			// Add first empty Rule to an Empty RuleSet

			DoubleVector weights = CreateDoubleVector(inst.numAttributes(),0) ;// extended length ;
//    		VarianceRationREduction
			if (learningCriteriaOption.getChosenIndex()==1) {
				defaultRule = new RuleVR(this);
			}else {
				defaultRule = new RuleErrR(this);
			}

			Vector<FuzzySet> terms = new Vector<FuzzySet>();
			for (int i = 0; i < inst.numAttributes(); i++) {
				if (inst.classIndex()==i)
					continue ;
				terms.add(new FuzzySet.LOToRO()) ;
			}
		
			defaultRule.setAll(terms, weights);
			defaultRule.setPrefixAndVersion("", currentSystemVersion);
			rs.add(defaultRule);
			currentValidCandidates = new Vector<FuzzyRuleExtendedCandidate>() ;
			currentNonReadyCandidates = new Vector<FuzzyRuleExtendedCandidate>() ;
			initialized = true ;
	
			if (statsAttributes.size()==0){
				for (int j = 0; j < inst.numAttributes(); j++) {
					if (inst.classIndex()==j)
						continue ;
					statsAttributes.add(new IncrementalVariance()) ;
				}
			}
		}
		

		/**
		 * Updating the set of rules on one instance incrementally
		 * @param inst
		 */
		public void trainOnInstance(Instance inst) {
						
			try{				
				instancesSeen++;
				updateStats(inst);
				
				DoubleVector normaliezedExtendedInstance = PrepareExtendedFeatureVector(inst) ;
				double normaliezedTargetValue= NormalizeTarget(inst) ;
				ResultSummary resultSummary = getMemberships (normaliezedExtendedInstance,normaliezedTargetValue) ;
				
				double error = resultSummary.getNormaliezedTargetValue() - resultSummary.getFinalPrediction() ;
				this.CurModSSE += Math.pow(error, 2) ;
				
				// checking a possible drift
//				boolean drift =checkChangeDetection() ;
//				if (drift)
//					this.countChangeDetected++ ;
				boolean drift = false ;
				if ((this.instancesSeen % graceperiod)==0){
					drift =checkChangeDetection() ;
					if (drift){
						this.countChangeDetected++ ;
//						this.clearAllStats();
						this.currentValidCandidates.clear() ;
						this.currentNonReadyCandidates.clear() ;
						currentSystemVersion++ ;
						System.out.println("########After a drift################");
						System.out.println(this);
						System.out.println("########################");
					}
				}
				
//        		VarianceRationREduction
				if (learningCriteriaOption.getChosenIndex()==1) {
					// checking a possible extension
//					if (this.instancesSeen > graceperiod && !drift ) {
					if (!drift ) {
						boolean systemChanged = false ;
						Vector <RuleVR> removeRules= new Vector<RuleVR>() ;
						Vector <RuleVR> newRules= new Vector<RuleVR>() ;
						for (FuzzyRule rule : rs){
							Vector <RuleVR> expansions = ((RuleVR)rule).tryToExpand(confidence, tau) ;
							if (expansions != null) {
								systemChanged = true ;
								if (chooseSingleRuleOption.isSet()) {
									if (expansions.get(0).getInitialMerit() > expansions.get(1).getInitialMerit()) {
										newRules.add(expansions.get(0)) ;
									}else {
										newRules.add(expansions.get(1)) ;									
									}
									if (rule !=defaultRule)
										removeRules.add((RuleVR)rule) ;
								}else {
									removeRules.add((RuleVR)rule) ;
									newRules.addAll(expansions) ;
								}
							}
						}
						if (systemChanged) {
							rs.removeAll(removeRules) ;
							rs.addAll(newRules) ;
							this.clearAllStats();
							currentSystemVersion++ ;
							System.out.println("########################");
							System.out.println(this);
							System.out.println("########################");			
						}
					}
				}//RMSEReduction
				else{
					// checking a possible extension
					if (this.instancesSeen > graceperiod && !drift ) {
						ExtendedCandidateErrR bestExtension = this.checkValidExpansion();
						if ((bestExtension != null)) {
							if (chooseSingleRuleOption.isSet()) {
		
								Vector<RuleErrR> newRules = bestExtension.CreateRuleFromExtension() ;
								if (bestExtension.getParentRule()!=defaultRule)
									rs.remove(bestExtension.getParentRule()) ;
								
		//						We must choose the best candidate here!
								double [] SSESingleExtenstions = bestExtension.getMeanSSESingleExension() ;
								if (SSESingleExtenstions[0]<SSESingleExtenstions[1])
									rs.add(newRules.get(0)) ;
								else
									rs.add(newRules.get(1)) ;	
									
		//						rs.addAll(newRules) ;
								if (bestExtension.getParentRule()==defaultRule)
									defaultRule.buildExtendedCandidates(bestExtension.getAttributeIndex());
		
								this.clearAllStats();
								this.currentValidCandidates.clear() ;
								this.currentNonReadyCandidates.clear() ;
								currentSystemVersion++ ;
								System.out.println("########################");
								System.out.println(this);
								System.out.println("########################");						
							}else {
							
								ExtendedCandidateErrR cbestExtension = this.checkValidExpansion();

								Vector<RuleErrR> newRules = bestExtension.CreateRuleFromExtension() ;
								rs.remove(bestExtension.getParentRule()) ;
								rs.addAll(newRules) ;
								this.clearAllStats();
								this.currentValidCandidates.clear() ;
								this.currentNonReadyCandidates.clear() ;
								currentSystemVersion++ ;
								System.out.println("########################");
								System.out.println(this);
								System.out.println("########################");
							}						
						}
					}
				}	
				if (chooseSingleRuleOption.isSet()) {
					double maxMembership = 0 ;
					for (ResultSummary.ResultPair resultPair: resultSummary.getResultPairs()) {
						resultPair.getRule().trainOnInstance(resultSummary, resultPair);
						maxMembership = Math.max(maxMembership, resultPair.getMembership()) ;
					}
					if (maxMembership < max_Membership_For_Choosing_Single_Rule)
						defaultRule.trainOnInstance(resultSummary, resultSummary.getResultPairDefaultRule()) ;
				}else{
					for (ResultSummary.ResultPair resultPair: resultSummary.getResultPairs()) {
						resultPair.getRule().trainOnInstance(resultSummary, resultPair);
					}
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
		
		/**
		 * This function remove the rule for which a drift is indicated, and updates the sibling rules. 
		 * In the case of multiple rules with a drift, it removes the rules with 
		 * the largest number of literals. 
		 * @return
		 * @throws Exception
		 */
		public boolean checkChangeDetection() throws Exception{
			int length = -1 ;
			FuzzyRule ruleRemove = null ;
			for (FuzzyRule rule: rs){
				if (rule.getChangeStatus()){
					if (rule.getHistory().size()>length){
						length = rule.getHistory().size() ;
						if (rule!=defaultRule)
							ruleRemove = rule ;
					}					
				}
			}
			boolean drift = false ;
			if (ruleRemove!=null && rs.size()>1){
				if (chooseSingleRuleOption.isSet()) {
					rs.remove(ruleRemove) ;					
				}else {
					for (FuzzyRule rule: rs){  
						if (rule!=ruleRemove){
							boolean tempDrift = rule.mergeWithSibling(ruleRemove) ;
							if (tempDrift){
								rule.clearExtendedCandidates();
								rule.clearStats();
								rule.buildExtendedCandidates();
							}
					        drift = drift || tempDrift ;
						}
					}
					rs.remove(ruleRemove) ;
				}
			}			
			return drift ;
		}
		
		private void clearAllStats() {
			this.CurModSSE = 0;
			this.instancesSeen = 0;
			for (FuzzyRule rule : rs) {
				rule.clearStats();
			}
		}

		public ExtendedCandidateErrR checkValidExpansion() {

			double epsilon = 0;
			if (!penalizeLargeRuleSets.isSet())
				epsilon = computeEpsilonHoeffdingBound(1, confidence,
						this.instancesSeen);
				//epsilon = computeEpsilonHoeffdingBound(statsTarget.getNormalizedRange(), confidence,
				//	this.instancesSeen);
			else
				epsilon = computeEpsilonHoeffdingBoundPlusComplexity(1, confidence,
						this.instancesSeen);
				//epsilon = computeEpsilonHoeffdingBoundPlusComplexity(statsTarget.getNormalizedRange(), confidence,
				//	this.instancesSeen);
			
			// checking the best versus the second best
			if (epsilon >= 1)
				return null ;
			
			if (currentNonReadyCandidates.size()==0 && currentValidCandidates.size()==0){
				for (FuzzyRule rule : rs) {
					for (FuzzyRuleExtendedCandidate ec : rule.getExtendedCandidates()) {
						currentNonReadyCandidates.addElement(ec);
					}
				}
			}
			
			for (int i =0 ; i<currentNonReadyCandidates.size() ; i++){
				if (((ExtendedCandidateErrR)currentNonReadyCandidates.get(i)).getIsReadyToTrain()){
					currentValidCandidates.addElement(currentNonReadyCandidates.get(i));
					currentNonReadyCandidates.remove(i) ;
					i-- ;
				}
			}
					
			if (currentValidCandidates.size() == 0) {
				return null;
			}
			if (currentValidCandidates.size() == 1) // compare with parent
			{
				ExtendedCandidateErrR best = (ExtendedCandidateErrR) currentValidCandidates.get(0) ;
				if ((((best.getSSE() / this.CurModSSE) + epsilon) < 1.0) && (epsilon < tau)) {
					return best;
				}
			}
			if (currentValidCandidates.size() > 1) {
				
				ExtendedCandidateErrR best = (ExtendedCandidateErrR)currentValidCandidates.get(0) ;
				ExtendedCandidateErrR second = (ExtendedCandidateErrR)currentValidCandidates.get(0) ;
				if (((ExtendedCandidateErrR)currentValidCandidates.get(0)).getMeanSSE()<=((ExtendedCandidateErrR)currentValidCandidates.get(1)).getMeanSSE()){
					best = (ExtendedCandidateErrR)currentValidCandidates.get(0) ;
					second = (ExtendedCandidateErrR)currentValidCandidates.get(1) ;
				}else{
					best = (ExtendedCandidateErrR)currentValidCandidates.get(1) ;
					second = (ExtendedCandidateErrR)currentValidCandidates.get(0) ;
				}

				for (int i=1 ; i <currentValidCandidates.size() ; i++){
					if (best.getMeanSSE()> ((ExtendedCandidateErrR)currentValidCandidates.get(i)).getMeanSSE()){
						second = best ;
						best = (ExtendedCandidateErrR)currentValidCandidates.get(i) ;
					}else if (second.getMeanSSE()> ((ExtendedCandidateErrR)currentValidCandidates.get(i)).getMeanSSE())
						second = (ExtendedCandidateErrR)currentValidCandidates.get(i) ;
				}
				
				double conditionA = (best.getMeanSSE() / (this.CurModSSE / this.instancesSeen));
				double conditionB = (best.getMeanSSE() / second.getMeanSSE());

				if 	((conditionA < 1) && 
						((conditionA + epsilon) < 1) || (epsilon < tau)
						|| ((conditionB + epsilon) < 1) || (epsilon < tau)) {
//						System.out.println(conditionA+"\t"+conditionB+"\t"+epsilon+"\t"+this.currentSystemAge+"\t"+rs.size());
					return best;
				}
			

				// checking the best versus the rest 
				epsilon = computeEpsilonHoeffdingBound(statsTarget.getNormalizedRange(), confidence, this.instancesSeen);
				if (epsilon >= 1 || best==null || currentValidCandidates.size()>Math.max(rs.size(),ruleController.statsAttributes.size())) 
					return null ;
				
				int index = 0 ;
				while (index < currentValidCandidates.size() && 
						!currentValidCandidates.isEmpty() &&
						currentValidCandidates.size()>(Math.max(rs.size(),ruleController.statsAttributes.size()))) {
					ExtendedCandidateErrR temp = (ExtendedCandidateErrR)currentValidCandidates.get(index) ;
					double conditionC = (best.getMeanSSE() / temp.getMeanSSE());
					if (conditionC + epsilon < 1 ){ //|| epsilon < 0.1) 
						currentValidCandidates.remove(index) ;
					}else{
						index++ ;
					}					
				}
			}
			return null;
		}

		@Override
		public String toString() {
			String out = "[RuleController]\n";
			int sizeExtRules = 0;
			if(rs.size()<8)
				for (FuzzyRule rule : rs) {
					out += rule.getPrefix()+"\n";
					out += rule.getHistory()+"\n";
					out += rule.getWeights().toString()+"\n";
					out += rule.getTerms().toString()+"\n";
					out += "%%%%%%%%%%%"+"\n";		
				}
			out += "\nExtRules: " + sizeExtRules + "\n";
			out += "\nRules: " + rs.size() + "\n";
//			out += rs;
			return out;
		}

		/**
		 * This function updates the incremental mean and variance calculators
		 * @param inst
		 */
		public void updateStats(Instance inst) {
			statsTarget.update(inst.classValue()) ;			
//			System.out.println("V:"+inst.classValue()+":"+statsTarget.getCurrentVariance()+ ":"+statsTarget.getStandardDeviation());
			
			int index = 0 ;
			for (int j = 0; j < inst.numAttributes(); j++) {
				if (inst.classIndex()==j)
					continue ;
				statsAttributes.get(index).update(inst.value(j));
				index++ ;
			}				
		}

		/**
		 * This function prepares the normalized extended feature vector
		 * adds the bias term
		 * adds the normalized target
		 * @param inst
		 * @return
		 */
		public DoubleVector PrepareExtendedFeatureVector(Instance inst) {
			DoubleVector normalizedInstance = Normalize(inst) ;
			normalizedInstance.setValue(normalizedInstance.numValues(), 1);
			return normalizedInstance ;
		}

		/**
		 * This function normalizes the instance
		 * @param inst
		 * @return
		 */
		public DoubleVector Normalize(Instance inst) {
			DoubleVector normalizedInstance = new DoubleVector();
			int index = 0;
			for (int j = 0; j < inst.numAttributes(); j++) {
				if (inst.classIndex() == j)
					continue;

				double value = 0;

				if (normalizeationOption.getChosenIndex() == 0) {
					if (this.statsAttributes.get(index).getStandardDeviation() < Constants.SMALL_EPSILON)
						value = (inst.value(j) - this.statsAttributes.get(index).getCurrentMean());
					else
						value = (inst.value(j) - this.statsAttributes.get(index).getCurrentMean())
								/ this.statsAttributes.get(index).getStandardDeviation();
				}else if(normalizeationOption.getChosenIndex() <= 2){
					if (this.statsAttributes.get(index).getRange() < Constants.SMALL_EPSILON)
						value = (inst.value(j) - this.statsAttributes.get(index).getMin());
					else
						value = (inst.value(j) - this.statsAttributes.get(index).getMin())
								/ this.statsAttributes.get(index).getRange();
				}else {
					value = inst.value(j) ;
				}

				normalizedInstance.setValue(index, value);
				index++;
			}
			return normalizedInstance;
		}

		/**
		 * This function normalizes the target value
		 * @param inst
		 * @return
		 */
		public double NormalizeTarget(Instance inst) {

			if (this.statsTarget.getCounter()==0)
					return 0 ;
			
			double value = 0;
			if (normalizeationOption.getChosenIndex() == 0 || normalizeationOption.getChosenIndex() == 1) {
				if (this.statsTarget.getStandardDeviation() < Constants.SMALL_EPSILON)
					value = (inst.classValue() - this.statsTarget.getCurrentMean());
				else
					value = (inst.classValue() - this.statsTarget.getCurrentMean())
							/ (this.statsTarget.getStandardDeviation());
			}else if (normalizeationOption.getChosenIndex() == 2){
				if (this.statsTarget.getRange() < Constants.SMALL_EPSILON)
					value = (inst.classValue() - this.statsTarget.getMin());
				else
					value = (inst.classValue() - this.statsTarget.getMin()) / (this.statsTarget.getRange());
			}else {
				value = inst.classValue() ;
			}
			
			return value;
		}

		/**
		 * This function denormalizes the target value
		 * @param normalizedPrediction
		 * @return
		 */
		private double denormalizePrediction(double normalizedPrediction) {
			
			if (this.statsTarget.getCounter()==0)
				return 0 ;
			
			double value = 0;
			if (normalizeationOption.getChosenIndex() == 0 || normalizeationOption.getChosenIndex() == 1) {
				value = normalizedPrediction * this.statsTarget.getStandardDeviation()
						+ this.statsTarget.getCurrentMean();
			} else if (normalizeationOption.getChosenIndex() == 2) {
				value = normalizedPrediction * this.statsTarget.getRange() + this.statsTarget.getMin();
			}else {
				value = normalizedPrediction ;
			}
			return value;
		}

		 /**
		  * This function returns the membership values and the target values from the TSK rules for an example
		  * @param inst
		  * @param extendedNormalizedInstance
		  * @param normaliezedTargetValue
		  * @return
		  */
		public ResultSummary getMemberships(DoubleVector extendedNormalizedInstance,double normaliezedTargetValue) {
			
			ResultSummary resultSummary = new ResultSummary(extendedNormalizedInstance,normaliezedTargetValue) ;
			double membership=0 ;
			double score =0 ;
//			System.out.println("###########getMemberships\n");
			if (chooseSingleRuleOption.isSet()) {
				double maxMembership = 0 ;
				for (FuzzyRule rule : rs) {
					if (rule==defaultRule) {
						membership = rule.getMembershipDegree(extendedNormalizedInstance) ;
						score = rule.getScore(extendedNormalizedInstance) ;
						resultSummary.setResultPairDefaultRule(resultSummary.new ResultPair(rule, membership, score));
					}else {
						membership = rule.getMembershipDegree(extendedNormalizedInstance) ;
						score = rule.getScore(extendedNormalizedInstance) ;
						resultSummary.addResultPair(rule, membership, score) ;
						maxMembership = Math.max(maxMembership, membership) ;
//						System.out.println("["+membership+","+score+"]");						
					}					
				}
				if (rs.size()==1 || maxMembership<max_Membership_For_Choosing_Single_Rule)
					resultSummary.addResultPair(resultSummary.getResultPairDefaultRule()) ;
			}else {
				for (FuzzyRule rule : rs) {
					membership = rule.getMembershipDegree(extendedNormalizedInstance) ;
					score = rule.getScore(extendedNormalizedInstance) ;
					resultSummary.addResultPair(rule, membership, score) ;			
//					System.out.println("["+membership+","+score+"]");
				}
			}

			resultSummary.computePrediction();
//			System.out.println("{"+extendedNormalizedInstance+"}");
//			System.out.println("{"+resultSummary.getSumMemberships()+","+resultSummary.getSumWeightedPredictions()+","+resultSummary.getFinalPrediction()+","+"normaliezedTargetValue=,"+normaliezedTargetValue+"}");
			
			return resultSummary;
		}

		public double computeEpsilonHoeffdingBound(double range, double confidence, double n) {
			return Math.sqrt((range * range) * (Math.log(1 / confidence)) / (2.0 * n));

//			return Math.sqrt(((range * range) * Math.log(1 / confidence)) / (2.0 * n));			
//			return (range * range) *Math.sqrt(( Math.log(1 / confidence)) / (2.0 * n));

		}

		public double computeEpsilonHoeffdingBoundPlusComplexity(double range, double confidence, double n) {
			double complexity = 0 ;	
			if (this.rs.size()>1) {
				double tempSumPremiseSize = ((float)rs.getSumPremiseSize())/statsAttributes.size() ;
//				rs.getSumPremiseSize() ;
				complexity = Math.pow(tempSumPremiseSize,0.5)/Math.pow(statsAttributes.size(),2) ;
			}
//				complexity = Math.pow(this.rs.size(),0.5)/Math.pow(statsAttributes.size(),2) ;
//				complexity = (1 - Math.log(2)/Math.log(this.rs.size()))/(statsAttributes.size()) ;
			return complexity +  Math.sqrt((range * range) * (Math.log(1 / confidence)) / (2.0 * n));
			
//			complexity = (1 - Math.log(2)/Math.log(this.rs.size()))/(Math.sqrt(statsAttributes.size())) ;
//			return complexity +  Math.sqrt(( (range * range) * Math.log(1 / confidence)) / (2.0 * n));
//			return complexity + (range * range) * Math.sqrt(( Math.log(1 / confidence)) / (2.0 * n));
		}
		
		public double getRuleSetSize() {
			return this.rs.size();
		}

		public double getTotalExtendedRuleSize() {
			double total = 0;
			for (FuzzyRule rule : rs) {
				total += rule.getNumberOfExtensions();
			}
			return total;
		}
	}	

	/**
	 * This function creates and initializes double vector with a given default value
	 * @param size
	 * @param value
	 * @return
	 */
	public static DoubleVector CreateDoubleVector(int size,double value) {
		DoubleVector result = new DoubleVector();
		
		for (int j = 0; j < size ; j++) {
			result.setValue(j,value) ;
		}
		return result ;
	}
	
	public static Vector<FuzzySet> cloneFSVector(Vector<FuzzySet> vec) {
		Vector<FuzzySet> temp = new Vector<FuzzySet>() ;
		try{
			for (FuzzySet set : vec){
				temp.add(set.clone()) ;
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
			System.exit(0);
		}
		return temp ;
	}

	
	public static void normalize(DoubleVector u) {
		double lenghtSquare = 0 ;
		for (int i = 0; i < u.numValues(); i++){
			lenghtSquare += Math.pow(u.getValue(i),2) ;
		}
		double length= Math.sqrt(lenghtSquare) ;
		if (length!=0){
			for (int i = 0; i < u.numValues(); i++){
				u.setValue(i, u.getValue(i)/length);
			}
		}
	}

	public static void normalizeExceptIntercept(DoubleVector u) {
		double lenghtSquare = 0 ;
		for (int i = 1; i < u.numValues(); i++){
			lenghtSquare += Math.pow(u.getValue(i),2) ;
		}
		double length= Math.sqrt(lenghtSquare) ;
		if (length!=0){
			for (int i = 1; i < u.numValues(); i++){
				u.setValue(i, u.getValue(i)/length);
			}
		}
	}
	
	public static double scalarProduct(DoubleVector u, DoubleVector v) {
		double ret = 0.0;
		for (int i = 0; i < Math.max(u.numValues(), v.numValues()); i++) {
			ret += u.getValue(i) * v.getValue(i);
		}
		return ret;
	}
}