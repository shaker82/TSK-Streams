package tskstreams.rules;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import com.yahoo.labs.samoa.instances.Instance;

/**
 * This class serves the purpose of carrying all rules in the system of rules.
 * @author shaker
 *
 */
public class RuleSet extends Vector<FuzzyRule> implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int SumPremiseSize =0 ;
	@Override
	public synchronized String toString() {
		String out = "";
		for( FuzzyRule rule : this){
			out += rule.getPrefix() +",";
		}
		out += "\n" ;
		for( FuzzyRule rule : this){
			out += "\n"+ rule;
		}
		return out;
	}
	@Override
	public boolean add(FuzzyRule e) {
		SumPremiseSize += e.getNumNonEmptyTerms() ;
		return super.add(e) ;
	}

	@Override
	public boolean addAll(Collection<? extends FuzzyRule> c) {
		for (FuzzyRule fr:c)
			SumPremiseSize += fr.getNumNonEmptyTerms() ;
		return super.addAll(c) ;
	}
	
	@Override
	public boolean remove(Object e) {
		SumPremiseSize -= ((FuzzyRule)e).getNumNonEmptyTerms() ;
		return super.remove(e) ;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object fr:c)
			SumPremiseSize -= ((FuzzyRule)fr).getNumNonEmptyTerms() ;
		return super.removeAll(c) ;
	}
	
	public int getSumPremiseSize() {
		return SumPremiseSize ;
	}
	
}
