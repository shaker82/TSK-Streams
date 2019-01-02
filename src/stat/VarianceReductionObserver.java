package stat;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import moa.classifiers.rules.core.NumericRulePredicate;
import moa.classifiers.rules.core.Predicate;
import moa.classifiers.rules.multilabel.attributeclassobservers.AttributeStatisticsObserver;
import moa.classifiers.rules.multilabel.attributeclassobservers.MultiLabelBSTreeFuzzy;
import moa.classifiers.rules.multilabel.attributeclassobservers.NominalStatisticsObserver;
import moa.classifiers.rules.multilabel.attributeclassobservers.NumericStatisticsObserver;
import moa.classifiers.rules.multilabel.core.AttributeExpansionSuggestion;
import moa.classifiers.rules.multilabel.core.LearningLiteralRegression;
import moa.classifiers.rules.multilabel.core.splitcriteria.MultiLabelSplitCriterion;
import moa.classifiers.rules.multilabel.core.splitcriteria.MultiTargetVarianceRatio;
import moa.core.AutoExpandVector;
import moa.core.DoubleVector;
import moa.options.ClassOption;

public class VarianceReductionObserver {
//	public ClassOption numericObserverOption = new ClassOption("numericObserver",
//			'y', "Numeric observer.", 
//			NumericStatisticsObserver.class,
//			"MultiLabelBSTree");

//	public ClassOption nominalObserverOption = new ClassOption("nominalObserver",
//			'z', "Nominal observer.", 
//			NominalStatisticsObserver.class,
//			"MultiLabelNominalAttributeObserver");
	
	protected AttributeStatisticsObserver attributeObserver ;
	
	private MultiTargetVarianceRatio splitCriterion = null ;
	protected int AttributeIndex = 0 ;
	public VarianceReductionObserver(int AttributeIndex){
		
		this.AttributeIndex = AttributeIndex ;
		attributeObserver = new MultiLabelBSTreeFuzzy() ;
		splitCriterion = new MultiTargetVarianceRatio() ;
	}
	public void addInstance(double AttributeValue, DoubleVector [] exampleStatistics) {
		attributeObserver.observeAttribute(AttributeValue, exampleStatistics);
	}
	
	
	public void Test() {

		DoubleVector [] literalStatistics = new DoubleVector[1];
		literalStatistics[0] = new DoubleVector(new double[3]);

		for (int i =0 ; i < 1000 ; i++) {
			double attributeVal =  Math.random() *10;
			double target = Math.random()*1000 ;
			if (attributeVal>4)
				target = 100 ;
			else
				target = 10 ;
			
			double weight = 1 ; //Math.random() ;

			double sum=weight*target;
			double squaredSum=weight*target*target;
			DoubleVector [] exampleStatistics=new DoubleVector[1];
			exampleStatistics[0]= new DoubleVector(new double[]{weight,sum, squaredSum});

			this.addInstance(attributeVal, exampleStatistics);
			literalStatistics[0].addValues(exampleStatistics[0].getArrayRef());

		}
			
		AttributeExpansionSuggestion suggestions = getBestSplitSuggestions(literalStatistics) ;
		Predicate predicate = suggestions.getPredicate() ;
		System.out.println(suggestions.merit);
		System.out.println(suggestions.predicate);
		
		System.out.println(suggestions.predicate.getAttributeIndex());
		
	}		
	
	public AttributeExpansionSuggestion getBestSplitSuggestions(DoubleVector [] literalStatistics) {
		AttributeExpansionSuggestion bestSuggestion = null ;

		if (attributeObserver != null) {
			bestSuggestion = attributeObserver.getBestEvaluatedSplitSuggestion(this.splitCriterion, literalStatistics, AttributeIndex);

			if (bestSuggestion == null) {
				//ALL attributes must have a best suggestion. Adding dummy suggestion with minimal merit.
				bestSuggestion=new  AttributeExpansionSuggestion(new NumericRulePredicate(0,0,true),null,-Double.MAX_VALUE);
			}
		}
		return bestSuggestion ;
	}

	public MultiTargetVarianceRatio getSplitCriterion() {
		return splitCriterion;
	}
	
	public static void main(String [] args) {
		(new VarianceReductionObserver(13)).Test() ;
	}
	
}
