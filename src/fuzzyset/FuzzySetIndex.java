package fuzzyset;

import java.io.Serializable;

import com.yahoo.labs.samoa.instances.Instance;

public class FuzzySetIndex implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int ID = 0;
	
	private int id;
	private FuzzySet fSet; 
	private int index = -1;
	
	// this field is used as indicator for the previously performed change
	private Operation operation = Operation.notSet ;
	
	public enum Operation{
		notSet, extension, masking 
	}
	
	public FuzzySetIndex(FuzzySet fs, int index){
		this.id = ++ID;
		this.fSet = fs;
		this.index = index;
	}

	public FuzzySetIndex(FuzzySet fs, int index, Operation operation){
		this(fs,index) ;
		this.operation = operation ;
	}
	
	public int getIndex() {
		return index;
	}

	public FuzzySet getFs() {
		return fSet;
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public double getMembershipDegree(Instance inst)
	{
		double value = inst.valueInputAttribute(index);
		return fSet.getMembershipOf(value);
	}

	@Override
	public FuzzySetIndex clone() throws CloneNotSupportedException {
		FuzzySetIndex result = new FuzzySetIndex(fSet.clone(), index) ; 
		result.setOperation(this.operation);
		return result ;
	}
	
	@Override
	public String toString() {
		return "("+fSet.toString()+", "+index+", "+operation+ ")";
	}

	@Override
	public boolean equals(Object obj) {
		return (obj.toString().equals(this.toString()));	
	}
	
	public void extend(FuzzySet fs){
		this.fSet.extendFuzzySet(fs);
	}
	
	public void subtract(FuzzySet fs){
		this.fSet.subtractFuzzySet(fs);
	}
	
	public double getCoveragePercentage(FuzzySet fs){
		if (fs instanceof FuzzySet.TRA){
			if (fs.checkContainsTRI((FuzzySet.TRA)fs))
				return 1 ;
		}
		
		double support = fs.getCore() ;
		double supportIntersection = this.fSet.getCoreIntersection(fs) ;
		
		if (Double.isInfinite(support) && Double.isInfinite(supportIntersection))
			return 1 ;
		else if (Double.isInfinite(support))
			return 0 ;
		else 
			return (supportIntersection/support) ;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
	public boolean isEmptySet(){
		if (!(fSet instanceof  FuzzySet.FreeSet))
				return false ;
		
		FuzzySet.FreeSet freeSet = (FuzzySet.FreeSet) fSet ;
		if (freeSet.sets.size()!=1)
			return false ;
		if (freeSet.sets.get(0) instanceof FuzzySet.EmptySet)
			return true ;
			
		return false ;
	}
}
