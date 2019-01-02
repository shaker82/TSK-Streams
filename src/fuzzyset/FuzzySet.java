package fuzzyset;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import weka.core.Utils;


/**
 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
 * 
 */
public abstract class FuzzySet implements Serializable {

	/**
	 * used for serialization
	 */
	private static final long serialVersionUID = 2851234652975660550L;

	public static final String FST_STR_NEGATED_TRIANGULAR 	= "NTRI";
	public static final String FST_STR_TRAPEZOIDAL 			= "TRA";
	public static final String FST_STR_NEGATED_TRAPEZOIDAL	= "NTRA";
	public static final String FST_STR_INTERVAL_BASED 		= "INT";
	public static final String FST_STR_GAUSSIAN		 		= "GAUSS";
	public static final String FST_STR_RIGHT_OPEN_GAUSSIAN 	= "RO-GAUSS";
	public static final String FST_STR_LEFT_OPEN_GAUSSIAN 	= "LO-GAUSS";
	
	
	public static final String FST_STR_S_Shaped		 		= "S_SHAPED";
	public static final String FST_STR_RIGHT_OPEN_S_Shaped 	= "RO-S_SHAPED";
	public static final String FST_STR_LEFT_OPEN_S_Shaped 	= "LO-S_SHAPED";
	
	public static final String FST_STR_RIGHT_OPEN 			= "RO";
	public static final String FST_STR_LEFT_OPEN 			= "LO";
	public static final String FST_STR_TRIANGULAR 			= "TRI";

	public static final String FST_STR_LEFT_OPEN_LEFT_TO_RIGHT_OPEN 	= "LO-RO";
	public static final String FST_STR_EMPTY_FUZZY_SET 	= "EMPTYSET";
	public static final String FST_STR_FREE_FUZZY_SET 	= "FREESET";
	
	public static final double SMALL_EPSILON = 1e-8;

	
	/**
	 * Returns the membership belonging to the given value.
	 * 
	 * @param val
	 * @return
	 */
	public abstract double getMembershipOf(double val);
		
	@Override
	public abstract FuzzySet clone() throws CloneNotSupportedException;
	
	public abstract Vector<FuzzySet> extendFuzzySet(FuzzySet fs) throws UnsupportedOperationException;

	public abstract Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException;

	public abstract double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException;

	public abstract double getCore() throws UnsupportedOperationException;

	public abstract boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException;

	/**
	 * Empty Fuzzy Set _________
	 * 
	 * 
	 */
	public static class EmptySet extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -1996332213760791109L;

		public EmptySet() {
		}
	

		@Override
		public double getMembershipOf(double val) {
			return 0d;
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_EMPTY_FUZZY_SET);
			sb.append("--") ;
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new EmptySet();
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(fs) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;			
			//result.add(this) ;
			return result ;
		}


		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			return 0;
		}

		@Override
		public double getCore() throws UnsupportedOperationException {
			return 0;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			return false;
		}
	}
	/**
	 * Left Open to Right Open -----
	 * 
	 * 
	 */
	public static class LOToRO extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -1420519175547851448L;

		public LOToRO() {
		}

		@Override
		public double getMembershipOf(double val) {
			return 1d;
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_LEFT_OPEN_LEFT_TO_RIGHT_OPEN);
			sb.append(" {").append(Utils.doubleToString(Double.NEGATIVE_INFINITY, 5));
			sb.append(',').append(Utils.doubleToString(Double.POSITIVE_INFINITY, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new LOToRO();
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO)
				return this.subtractFuzzySet(( FuzzySet.LO)fs) ;
			else if (fs instanceof FuzzySet.RO)
				return this.subtractFuzzySet(( FuzzySet.RO)fs) ;
			else if (fs instanceof FuzzySet.TRA)
				return this.subtractFuzzySet(( FuzzySet.TRA)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.subtractFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.subtractFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.EmptySet empty = new FuzzySet.EmptySet() ;
			result.add(empty) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.RO ro = new FuzzySet.RO(fs.a, fs.b) ;
			result.add(ro) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.RO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.LO lo = new FuzzySet.LO(fs.a, fs.b) ;
			result.add(lo) ;
			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.TRA fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.LO lo = new FuzzySet.LO(fs.a, fs.b) ;
			result.add(lo) ;

			FuzzySet.RO ro = new FuzzySet.RO(fs.c, fs.d) ;			
			result.add(ro) ;
			
			return result ;
		}
		
		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			if (fs instanceof FuzzySet.LO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.RO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.TRA){
				FuzzySet.TRA temp = (FuzzySet.TRA) fs ;
				return temp.c-temp.b ;
			}
			else if (fs instanceof FuzzySet.LOToRO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.EmptySet)
				return 0 ;
			return 0;
		}
		
		@Override
		public double getCore() throws UnsupportedOperationException {
			return Double.POSITIVE_INFINITY;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			return true;
		}
	}
	
	/**
	 * Right Open Fuzzy Set _ _|/| A B
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class RO extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -1420519175232291448L;

		double a;
		double b;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public RO(double A, double B) {
			this.a = A;
			this.b = B;
		}

		public void SetAll(double A, double B) {
			this.a = A;
			this.b = B;
		}
	
		@Override
		public double getMembershipOf(double val) {

			if (val >= b) return 1d;
			else { //if (val < b){
				double w=  (b-a) ;
				double result = 1/(1+    Math.exp((-4/w)*(val-a))) ;				
				return result ;
			}
			
//			if (val >= b) return 1d;
//			if (val <= a) return 0d;
//			return (val - a) / (b - a);
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_RIGHT_OPEN);
			sb.append(" {").append(Utils.doubleToString(this.a, 5));
			sb.append(',').append(Utils.doubleToString(this.b, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new RO(this.a, this.b);
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO)
				return this.extendFuzzySet(( FuzzySet.LO)fs) ;
			else if (fs instanceof FuzzySet.RO)
				return this.extendFuzzySet(( FuzzySet.RO)fs) ;
			else if (fs instanceof FuzzySet.TRA)
				return this.extendFuzzySet(( FuzzySet.TRA)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.extendFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.extendFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b <= fs.a){
				result.addElement(new FuzzySet.LOToRO());
			}else {
				result.addElement(fs);
				result.addElement(this);
			}
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.RO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b >= fs.b){
				result.addElement(fs);
			}else {
				result.addElement(this);
			}
			return result ;
		}

		public Vector<FuzzySet> extendFuzzySet(FuzzySet.TRA fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b <= fs.b){
				result.addElement(this);
			}else if (this.b >= fs.b && this.b <= fs.c) {
				result.addElement(new FuzzySet.RO(fs.a, fs.b));
			}else{
				result.addElement(fs);
				result.addElement(this);
			}				
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(fs) ;
			return result ;
		}

		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO)
				return this.subtractFuzzySet(( FuzzySet.LO)fs) ;
			else if (fs instanceof FuzzySet.RO)
				return this.subtractFuzzySet(( FuzzySet.RO)fs) ;
			else if (fs instanceof FuzzySet.TRA)
				return this.subtractFuzzySet(( FuzzySet.TRA)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.subtractFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.subtractFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b <= fs.a)
				result.addElement(new FuzzySet.RO(fs.a, fs.b));
			else 
				result.addElement(this);
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.RO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b < fs.b)
				result.addElement(new FuzzySet.TRA(this.a,this.b,fs.a,fs.b));
			else 
				result.addElement(new FuzzySet.EmptySet());
			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.TRA fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b >= fs.c)
				result.add(this) ;
			else if (this.b < fs.b){
				result.add(new FuzzySet.TRA(this.a,this.b,fs.a,fs.b)) ;
				result.add(new FuzzySet.RO(fs.c,fs.d)) ;
			}
			else //if (this.b >= fs.b && this.b <= fs.c)
				result.add(new FuzzySet.RO(fs.c,fs.d)) ;
			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.EmptySet empty = new FuzzySet.EmptySet() ;
			result.add(empty) ;
			return result ;
		}
		
		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			if (fs instanceof FuzzySet.LO){
				FuzzySet.LO temp = (FuzzySet.LO) fs ;
				return Math.max(temp.a - this.b,0) ;
			}
			else if (fs instanceof FuzzySet.RO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.TRA){
				FuzzySet.TRA temp = (FuzzySet.TRA) fs ;
				if (temp.c <this.b)
					return 0 ;
				else
					return Math.min(temp.c - this.b,temp.c - temp.b) ;
			}
			else if (fs instanceof FuzzySet.LOToRO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.EmptySet)
				return 0 ;
			return 0;
		}
		
		@Override
		public double getCore() throws UnsupportedOperationException {
			return Double.POSITIVE_INFINITY;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			if ((fs.getCore() < SMALL_EPSILON)&&(this.b<=fs.b))		
				return true;
			else 
				return false ;
		}
	}
	
	/**
	 * Left Open Fuzzy Set _ |\|_ A B
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class LO extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -1996133473760791109L;

		double a;
		double b;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public LO(double A, double B) {
			this.a = A;
			this.b = B;
		}
		
		public void SetAll(double A, double B) {
			this.a = A;
			this.b = B;
		}
		
		@Override
		public double getMembershipOf(double val) {
			if (val <= a) return 1d;
			else { //if (val > a){
				double w=  (b-a) ;
				double result = 1/(1+    Math.exp((-4/w)*(b-val))) ;				
				return result ;
			}
			
//			if (val <= a) return 1d;
//			if (val >= b) return 0d;
//			return 1d - (val - a) / (b - a);
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_LEFT_OPEN);
			sb.append(" {").append(Utils.doubleToString(this.a, 5));
			sb.append(',').append(Utils.doubleToString(this.b, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new LO(this.a, this.b);
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO)
				return this.extendFuzzySet(( FuzzySet.LO)fs) ;
			else if (fs instanceof FuzzySet.RO)
				return this.extendFuzzySet(( FuzzySet.RO)fs) ;
			else if (fs instanceof FuzzySet.TRA)
				return this.extendFuzzySet(( FuzzySet.TRA)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.extendFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.extendFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LO fs){

			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a >= fs.a){
				result.addElement(this);
			}else {
				result.addElement(fs);
			}
			return result ;			
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.RO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a >= fs.b){
				result.addElement(new FuzzySet.LOToRO());
			}else {
				result.addElement(this);
				result.addElement(fs);
			}
			return result ;		
		}

		public Vector<FuzzySet> extendFuzzySet(FuzzySet.TRA fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a >= fs.c){
				result.addElement(this);
			}else if (this.a >= fs.b && this.a <= fs.c) {
				result.addElement(new FuzzySet.LO(fs.c, fs.d));
			}else{
				result.addElement(this);
				result.addElement(fs);
			}
				
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(fs) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO)
				return this.subtractFuzzySet(( FuzzySet.LO)fs) ;
			else if (fs instanceof FuzzySet.RO)
				return this.subtractFuzzySet(( FuzzySet.RO)fs) ;
			else if (fs instanceof FuzzySet.TRA)
				return this.subtractFuzzySet(( FuzzySet.TRA)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.subtractFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.subtractFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}

		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a > fs.a)
				result.addElement(new FuzzySet.TRA(fs.a,fs.b,this.a,this.b));
			else 
				result.addElement(new FuzzySet.EmptySet());
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.RO fs){

			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a >= fs.b)
				result.addElement(new FuzzySet.LO(fs.a, fs.b));
			else 
				result.addElement(this);
			return result ;

		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.TRA fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a <= fs.b)
				result.add(this) ;
			else if (this.a >= fs.b && this.a <= fs.c){
				result.add(new FuzzySet.LO(fs.a,fs.b)) ;
			}
			else { //if (this.a >= fs.c )
				result.add(new FuzzySet.LO(fs.a,fs.b)) ;
				result.add(new FuzzySet.TRA(fs.c,fs.d, this.a,this.b)) ;
			}

			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.EmptySet empty = new FuzzySet.EmptySet() ;
			result.add(empty) ;
			return result ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			if (fs instanceof FuzzySet.LO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.RO){
				FuzzySet.RO temp = (FuzzySet.RO) fs ;
				return Math.max(this.a - temp.b,0) ;
			}
			else if (fs instanceof FuzzySet.TRA){
				FuzzySet.TRA temp = (FuzzySet.TRA) fs ;
				if (temp.b > this.a)
					return 0 ;
				else
					return Math.min(this.a - temp.b,temp.c - temp.b) ;
			}
			else if (fs instanceof FuzzySet.LOToRO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.EmptySet)
				return 0 ;
			return 0;
		}
		
		@Override
		public double getCore() throws UnsupportedOperationException {
			return Double.POSITIVE_INFINITY;
		}
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			if ((fs.getCore() < SMALL_EPSILON)&&(this.a>=fs.c))		
				return true;
			else 
				return false ;
		}
	}

	/**
	 * Triangular Fuzzy Set
	 * 
	 * _|/|\|_ A B C
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class TRI extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = 7611458578329391684L;

		double a;
		double b;
		double c;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public double getC() {
			return c;
		}

		public TRI(double A, double B, double C) {
			this.a = A;
			this.b = B;
			this.c = C;
		}

		@Override
		public double getMembershipOf(double val) {
			
			if (val <= a) return 0d;
			if (val > a && val < b) return (val - a) / (b - a);
			if (val == b) return 1d;
			if (val > b && val < c) return 1 - (val - b) / (c - b);
			if (val >= c) return 0d;
			return -1d;
			
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_TRIANGULAR);
			sb.append(" {").append(Utils.doubleToString(this.a, 5));
			sb.append(',').append(Utils.doubleToString(this.b, 5));
			sb.append(',').append(Utils.doubleToString(this.c, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new TRI(this.a, this.b, this.c);
		}

		@Override
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCore() throws UnsupportedOperationException {
			return 0d;
			//return Double.POSITIVE_INFINITY;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
	}

	/**
	 * Trapezoidal Fuzzy Set _ _|/| |\|_ A B C D
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class TRA extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = 7687924600923730151L;

		double a;
		double b;
		double c;
		double d;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public double getC() {
			return c;
		}

		public double getD() {
			return d;
		}

		public TRA(double A, double B, double C, double D) {
			this.a = A;
			this.b = B;
			this.c = C;
			this.d = D;
			if ((a==b)&&(a==c)&&(a==d))
				System.out.println(this) ;
		}
		
		public void SetAll(double A, double B, double C, double D) {
			this.a = A;
			this.b = B;
			this.c = C;
			this.d = D;
			if ((a==b)&&(a==c)&&(a==d))
				System.out.println(this) ;
		}

		@Override
		public double getMembershipOf(double val) {
			if ((a==b || c==d)&& (val==a || val==d) ) 
				return 1d;
			
			if (val >= b && val <= c) return 1d;
			if (val < b){
				double w=  (b-a) ;
				double result = 1/(1+    Math.exp((-4/w)*(val-a))) ;
				
				return result ;
//				return (val - a) / (b - a);
			}
			if (val > c){
				double w= (d-c) ;
				double result = 1/(1+    Math.exp((-4/w)*(d-val))) ;
				return result ;
//				return (val - a) / (b - a);
			}
			return -1d;
//			if (val <= a) return 0d;
//			if (val > a && val < b) return (val - a) / (b - a);
//			if (val >= b && val <= c) return 1d;
//			if (val > c && val < d) return 1 - (val - c) / (d - c);
//			if (val >= d) return 0d;
//			return -1d;
		}
		
//		@Override
//		public double getMembershipOf(double val) {
//			if ((a==b || c==d)&& (val==a || val==d) ) 
//				return 1d;
//			
//			if (val <= a) return 0d;
//			if (val > a && val < b) return (val - a) / (b - a);
//			if (val >= b && val <= c) return 1d;
//			if (val > c && val < d) return 1 - (val - c) / (d - c);
//			if (val >= d) return 0d;
//			return -1d;
//		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_TRAPEZOIDAL);
			sb.append(" {").append(Utils.doubleToString(this.a, 5));
			sb.append(',').append(Utils.doubleToString(this.b, 5));
			sb.append(',').append(Utils.doubleToString(this.c, 5));
			sb.append(',').append(Utils.doubleToString(this.d, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new TRA(this.a, this.b, this.c, this.d);
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO)
				return this.extendFuzzySet(( FuzzySet.LO)fs) ;
			else if (fs instanceof FuzzySet.RO)
				return this.extendFuzzySet(( FuzzySet.RO)fs) ;
			else if (fs instanceof FuzzySet.TRA)
				return this.extendFuzzySet(( FuzzySet.TRA)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.extendFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.extendFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LO fs){

			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b > fs.a){
				result.addElement(fs);
				result.addElement(this);
			}else if (this.b <= fs.a && this.c >= fs.a){
				result.addElement(new FuzzySet.LO(this.c, this.d));
			}else{
				result.addElement(fs) ;
			}
			
			return result ;			
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.RO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.c < fs.b){
				result.addElement(this);
				result.addElement(fs);
			}else if (this.c >= fs.b && this.b <= fs.b){
				result.addElement(new FuzzySet.RO(this.a, this.b));
			}else{
				result.addElement(fs) ;
			}
		
			return result ;			
		}

		public Vector<FuzzySet> extendFuzzySet(FuzzySet.TRA fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b>fs.c ){
				result.addElement(fs);
				result.addElement(this);
			}else if (this.c < fs.b ){
				result.addElement(this);				
				result.addElement(fs);
			}else{				
				result.add(new FuzzySet.TRA(Math.min(this.a,fs.a),
						Math.min(this.b,fs.b),
						Math.max(this.c,fs.c),
						Math.max(this.d,fs.d))) ;
			}
		
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(fs) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO)
				return this.subtractFuzzySet(( FuzzySet.LO)fs) ;
			else if (fs instanceof FuzzySet.RO)
				return this.subtractFuzzySet(( FuzzySet.RO)fs) ;
			else if (fs instanceof FuzzySet.TRA)
				return this.subtractFuzzySet(( FuzzySet.TRA)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.subtractFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.subtractFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}

		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b > fs.a){
				result.addElement(this);
			}else if (this.b <= fs.a && this.c > fs.a){
				if (fs.b < this.c)
					result.addElement(new FuzzySet.TRA(fs.a,fs.b, this.c, this.d));
				else
					result.addElement(new FuzzySet.TRA(fs.a,fs.a, this.c, this.d));
				
			}else{
				result.addElement(new FuzzySet.EmptySet()) ;
			}
			return result ;			
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.RO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.c < fs.b){
				result.addElement(this);
			}else if (this.c >= fs.b && this.b < fs.b){
				if (fs.a > this.b)
					result.addElement(new FuzzySet.TRA(this.a, this.b,fs.a,fs.b));
				else
					result.addElement(new FuzzySet.TRA(this.a, this.b,fs.b,fs.b));
			}else{ 
				result.addElement(new FuzzySet.EmptySet()) ;
			}
			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.TRA fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b>fs.c ){
				result.addElement(this);
			}else if (this.c < fs.b ){
				result.addElement(this);				
			}else if (this.c <= fs.c && this.b>= fs.b ){
				result.add(new FuzzySet.EmptySet()) ;
			}else if (fs.b > this.b && this.c> fs.c){
				if (fs.a <= this.b)
					result.add(new FuzzySet.TRA(this.a,this.b,fs.b,fs.b)) ;
				else
					result.add(new FuzzySet.TRA(this.a,this.b,fs.a,fs.b)) ;
				if (fs.d>=this.c)
					result.add(new FuzzySet.TRA(fs.c,fs.c,this.c,this.d)) ;
				else
					result.add(new FuzzySet.TRA(fs.c,fs.d,this.c,this.d)) ;
			}else if (this.b >= fs.b && this.b<= fs.c ){

				if (fs.d>=this.c)
					result.add(new FuzzySet.TRA(fs.c,fs.c,this.c,this.d)) ;
				else
					result.add(new FuzzySet.TRA(fs.c,fs.d,this.c,this.d)) ;
			}else if (this.c >= fs.b && this.c<= fs.c ){
				if (fs.a <= this.b)
					result.add(new FuzzySet.TRA(this.a,this.b,fs.b,fs.b)) ;
				else
					result.add(new FuzzySet.TRA(this.a,this.b,fs.a,fs.b)) ;
			}
		
			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.EmptySet empty = new FuzzySet.EmptySet() ;
			result.add(empty) ;
			return result ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			if (fs instanceof FuzzySet.LO){
				FuzzySet.LO temp = (FuzzySet.LO) fs ;
				if (temp.a<this.b)
					return 0 ;
				else 
					return Math.min(temp.a - this.b,this.c - this.b) ;
			}
			else if (fs instanceof FuzzySet.RO){
				FuzzySet.RO temp = (FuzzySet.RO) fs ;
				if (temp.b>this.c)
					return 0 ;
				else 
					return Math.min(this.c-temp.b,this.c - this.b) ;
			}
			else if (fs instanceof FuzzySet.TRA){
				FuzzySet.TRA temp = (FuzzySet.TRA) fs ;
				if (temp.b > this.c || this.b > temp.c)
					return 0 ;

				else if (temp.b <= this.b && temp.c >= this.c)
					return this.c - this.b ;
				else if (this.b <= temp.b && this.c >= temp.c)
					return temp.c - temp.b ;				
				else if (temp.b <= this.c && temp.c >= this.c)
					return this.c - temp.b ;
				else 
					return temp.c - this .b ;
			}
			else if (fs instanceof FuzzySet.LOToRO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.EmptySet)
				return 0 ;
			return 0;
		}
		
		@Override
		public double getCore() throws UnsupportedOperationException {
			return this.c - this.b ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			if ((fs.getCore() < SMALL_EPSILON)&&(this.b<=fs.b)&&(this.c>=fs.c))		
				return true;
			else 
				return false ;
		}
	}

	/**
	 * Interval based Fuzzy Set. _ _| |_ A B
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class INT extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -3526798031321534026L;

		double a;
		double b;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public INT(double A, double B) {
			this.a = A;
			this.b = B;
		}

		@Override
		public double getMembershipOf(double val) {
			if (val < a || val > b) return 0d;
			return 1d;
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_INTERVAL_BASED);
			sb.append(" {").append(Utils.doubleToString(this.a, 5));
			sb.append(',').append(Utils.doubleToString(this.b, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new INT(this.a, this.b);
		}
		
		@Override
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public double getCore() throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
	}

	/**
	 * Negated Triangular Fuzzy Set (1 - TRI) _ _ |\|/| A B C
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class NTRI extends FuzzySet {

		public TRI getTRI() {
			return tri;
		}

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = 7611458578329391684L;

		TRI tri = null;

		public NTRI(double A, double B, double C) {
			this.tri = new TRI(A, B, C);
		}

		@Override
		public double getMembershipOf(double val) {
			return 1d - this.tri.getMembershipOf(val);
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_NEGATED_TRIANGULAR);
			sb.append(" {").append(Utils.doubleToString(this.tri.a, 5));
			sb.append(',').append(Utils.doubleToString(this.tri.b, 5));
			sb.append(',').append(Utils.doubleToString(this.tri.c, 5)).append(
					'}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new NTRI(this.tri.a, this.tri.b, this.tri.c);
		}

		@Override
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCore() throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
	}
	
	/**
	 * Negated Triangular Fuzzy Set (1 - TRI) _ _ |\|/| A B C
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class NTRA extends FuzzySet {

		private static final long serialVersionUID = -5587310085044596438L;

		public TRA getTRA() {
			return tra;
		}

		TRA tra = null;

		public NTRA(double A, double B, double C, double D) {
			this.tra = new TRA(A, B, C, D);
		}

		@Override
		public double getMembershipOf(double val) {
			return 1d - this.tra.getMembershipOf(val);
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_NEGATED_TRAPEZOIDAL);
			sb.append(" {").append(Utils.doubleToString(this.tra.a, 5));
			sb.append(',').append(Utils.doubleToString(this.tra.b, 5));
			sb.append(',').append(Utils.doubleToString(this.tra.c, 5));
			sb.append(',').append(Utils.doubleToString(this.tra.d, 5)).append(
					'}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new NTRA(this.tra.a, this.tra.b, this.tra.c, this.tra.d);
		}
		
		@Override
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCore() throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
	}

	/**
	 * Gaussian Fuzzy Set
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class GAUSS extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = 7611458578329391684L;

		private double mean = Double.NaN;
		private double sigma = Double.NaN;
		
		public GAUSS(double mean, double sigma) {
			this.mean = mean;
			this.sigma = sigma;
		}

		@Override
		public double getMembershipOf(double val) {
			return Math.pow(Math.E, -Math.pow(val-mean, 2d)/(2d*Math.pow(sigma, 2d)));
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_GAUSSIAN);
			sb.append(" {").append(Utils.doubleToString(this.mean, 5));
			sb.append(',').append(Utils.doubleToString(this.sigma, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new GAUSS(this.mean, this.sigma);
		}
		
		@Override
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCore() throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
	}
	
	
	/**
	 * Left Open Gaussian Fuzzy Set
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class LO_GAUSS extends FuzzySet {
		
		private static final long serialVersionUID = 7801752516746200987L;
		
		private double mean = Double.NaN;
		private double sigma = Double.NaN;
		
		public LO_GAUSS(double mean, double sigma) {
			this.mean = mean;
			this.sigma = sigma;
		}

		@Override
		public double getMembershipOf(double val) {
			if(val < mean) return 1d;
			return Math.pow(Math.E, -Math.pow(val-mean, 2d)/(2d*Math.pow(sigma, 2d)));
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_LEFT_OPEN_GAUSSIAN);
			sb.append(" {").append(Utils.doubleToString(this.mean, 5));
			sb.append(',').append(Utils.doubleToString(this.sigma, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new LO_GAUSS(this.mean, this.sigma);
		}
		
		@Override
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCore() throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
	}
	
	/**
	 * Right Open Gaussian Fuzzy Set
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class RO_GAUSS extends FuzzySet {

		private static final long serialVersionUID = 4578470834056187732L;
		
		private double mean = Double.NaN;
		private double sigma = Double.NaN;
		
		public RO_GAUSS(double mean, double sigma) {
			this.mean = mean;
			this.sigma = sigma;
		}

		@Override
		public double getMembershipOf(double val) {
			if(val > mean) return 1d;
			return Math.pow(Math.E, -Math.pow(val-mean, 2d)/(2d*Math.pow(sigma, 2d)));
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_RIGHT_OPEN_GAUSSIAN);
			sb.append(" {").append(Utils.doubleToString(this.mean, 5));
			sb.append(',').append(Utils.doubleToString(this.sigma, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new RO_GAUSS(this.mean, this.sigma);
		}
		
		@Override
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public double getCore() throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
	}
	
	/**
	 * Custom piecewise liniear fuzzyset
	 * @author Sascha Henzgen
	 *
	 */
	public static class CPLFS extends FuzzySet{
			
		private ArrayList<double[]> points = new ArrayList<double[]>();
		
		public CPLFS(ArrayList<double[]> points){
			
			this.points = points;
		}

		@Override
		public double getMembershipOf(double val) {

			//find lower and upper neighbour
			double[] lowerPoint = null;
			double[] upperPoint = null;
			
			for(double[] cp: points)
			{
				if(cp[0] < val)
				{
					lowerPoint = cp;
				}
				else
				{
					upperPoint = cp;
					break;
				}
			}
			
			if((lowerPoint == null
					&& val < upperPoint[0])
					|| upperPoint == null)
			{
				return 0;
			}
			else if(upperPoint[0] == val)
			{
				return upperPoint[1];
			}
			
			//calc straight line
			//gradient
			double m = (upperPoint[1]-lowerPoint[1])
							/(upperPoint[0]-lowerPoint[0]);
			double b = upperPoint[1]-m*upperPoint[0];
			
			double returnValue = m*val+b;
			
			assert returnValue >= 0: "Der wert ist kleiner Null"; //TODO remove
			
			return returnValue;
		}

		@Override
		public String toString() 
		{
			StringBuffer sb = new StringBuffer();
			
			sb.append("CPLFS ");
			sb.append("[");
			for(double[] p: points)
			{
				sb.append("(" + p[0] + "," + p[1] + ")");
			}
			sb.append("]");
			return sb.toString();
		}
		
		public ArrayList<double[]> getPoints()
		{
			return points;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new CPLFS((ArrayList<double[]>)this.points.clone());
		}
		
		@Override
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
	
		@Override
		public double getCore() throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			throw new UnsupportedOperationException() ;
		}
	}
	
	@Override
	public abstract String toString();
	
	
	public static class FreeSet extends FuzzySet{
		
		public Vector <FuzzySet> sets = new Vector<FuzzySet>() ;
		@Override
		public double getMembershipOf(double val) {
			// TODO Auto-generated method stub
			double result = 0 ;
			for (FuzzySet set:sets){
				double membership = set.getMembershipOf(val) ;
				if (membership==0 && result>0)
					break ;
				result = Math.max(result, membership) ;
			}				
			return result;
		}

		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			FreeSet temp = new FuzzySet.FreeSet() ;
			temp.sets = new Vector<FuzzySet>() ;
			for (FuzzySet set : this.sets){
				temp.sets.add(set.clone()) ;
			}

			return temp;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			String result = "" ;
			for (FuzzySet fs:sets){
				result+= fs ;
			}
			return result;
		}
		
		@Override
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fSet) throws UnsupportedOperationException {
					
			if (sets.size()==0){
				sets.add(fSet) ;
				return sets;
			}
			
			Vector <FuzzySet> tempSets = new Vector<FuzzySet>() ;
			Vector<FuzzySet> res =null ;
			for (int i=0 ; i<sets.size() ; i++){
				FuzzySet set = sets.get(i) ;
				res = set.extendFuzzySet(fSet) ;
				if (res.size()==2){
					tempSets.add(res.get(0)) ;
					res.remove(0) ;
					fSet = res.get(0) ;
				}
				else{
					fSet = res.get(0) ;
				}
			}
			tempSets.addAll(res) ;
			sets = tempSets ;
			return sets ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs) throws UnsupportedOperationException {
			Vector <FuzzySet> tempSets = new Vector<FuzzySet>() ;
			
			for (int i=0 ; i<sets.size() ; i++){
				FuzzySet set = sets.get(i) ;
				Vector<FuzzySet> res = set.subtractFuzzySet(fs) ;
				if (res.size()!=0){
					if(!(res.get(0) instanceof FuzzySet.EmptySet))
						tempSets.addAll(res) ;
				}
			}
			if (tempSets.size()==0){
				tempSets.add(new FuzzySet.EmptySet()) ;
			}
			sets = tempSets ;
			return sets ;
		}
		
		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			double result = 0 ;		
			for (int i=0 ; i<sets.size() ; i++){
				FuzzySet set = sets.get(i) ;
				result += set.getCoreIntersection(fs) ;
			}
			return result ;
		}

		
		@Override
		public double getCore() throws UnsupportedOperationException {
			
			double result = 0 ;		
			for (int i=0 ; i<sets.size() ; i++){
				FuzzySet set = sets.get(i) ;
				result += set.getCore() ;
			}
			return result ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			boolean result = false ;		
			for (int i=0 ; i<sets.size() ; i++){
				FuzzySet set = sets.get(i) ;
				result = result || set.checkContainsTRI(fs) ;
			}
			return result ;
		}
	}
	
	/**
	 * S-Shaped Fuzzy Set A B C D
	 * 
	 * @author Ammar shaker [mailto:ammar.shaker@upb.de]
	 * 
	 */
	public static class S_Shaped extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = 7687111600923730151L;

		double a;
		double b;
		double c;
		double d;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public double getC() {
			return c;
		}

		public double getD() {
			return d;
		}

		public S_Shaped(double A, double B, double C, double D) {
			this.a = A;
			this.b = B;
			this.c = C;
			this.d = D;
		}
		
		public void SetAll(double A, double B, double C, double D) {
			this.a = A;
			this.b = B;
			this.c = C;
			this.d = D;
		}

		@Override
		public double getMembershipOf(double val) {
			if ((a==b || c==d)&& (val==a || val==d) ) 
				return 1d;
			
			if (val >= b && val <= c) return 1d;
			if (val <  a || val >  d) return 0d;

			if (a <= val && val < (a+b)/2){
				double w=  (b-a) ;
				double result = 2* Math.pow((val - a )/ w,2) ;
				return result ;
			}
			else if ((a+b)/2 <= val && val < b){
				double w=  (b-a) ;
				double result = 1- 2* Math.pow((val - b )/ w,2) ;
				return result ;
			}
			else if ( c< val && val <= (c + d)/2){
				double w=  (d-c) ;
				double result = 1- 2* Math.pow((c - val)/ w,2) ;
				return result ;
			}
			else {
				double w=  (d - c) ;
				double result = 2* Math.pow((d - val)/ w,2) ;
				return result ;
			}
		}
				
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_S_Shaped);
			sb.append(" {").append(Utils.doubleToString(this.a, 5));
			sb.append(',').append(Utils.doubleToString(this.b, 5));
			sb.append(',').append(Utils.doubleToString(this.c, 5));
			sb.append(',').append(Utils.doubleToString(this.d, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new S_Shaped(this.a, this.b, this.c, this.d);
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO_S_Shaped)
				return this.extendFuzzySet(( FuzzySet.LO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.RO_S_Shaped)
				return this.extendFuzzySet(( FuzzySet.RO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.S_Shaped)
				return this.extendFuzzySet(( FuzzySet.S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.extendFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.extendFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LO_S_Shaped fs){

			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b > fs.a){
				result.addElement(fs);
				result.addElement(this);
			}else if (this.b <= fs.a && this.c >= fs.a){
				result.addElement(new FuzzySet.LO_S_Shaped(this.c, this.d));
			}else{
				result.addElement(fs) ;
			}
			
			return result ;			
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.RO_S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.c < fs.b){
				result.addElement(this);
				result.addElement(fs);
			}else if (this.c >= fs.b && this.b <= fs.b){
				result.addElement(new FuzzySet.RO_S_Shaped(this.a, this.b));
			}else{
				result.addElement(fs) ;
			}
		
			return result ;			
		}

		public Vector<FuzzySet> extendFuzzySet(FuzzySet.S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b>fs.c ){
				result.addElement(fs);
				result.addElement(this);
			}else if (this.c < fs.b ){
				result.addElement(this);				
				result.addElement(fs);
			}else{				
				result.add(new FuzzySet.S_Shaped(Math.min(this.a,fs.a),
						Math.min(this.b,fs.b),
						Math.max(this.c,fs.c),
						Math.max(this.d,fs.d))) ;
			}
		
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(fs) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO_S_Shaped)
				return this.subtractFuzzySet(( FuzzySet.LO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.RO_S_Shaped)
				return this.subtractFuzzySet(( FuzzySet.RO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.S_Shaped)
				return this.subtractFuzzySet(( FuzzySet.S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.subtractFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.subtractFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}

		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LO_S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b > fs.a){
				result.addElement(this);
			}else if (this.b <= fs.a && this.c > fs.a){
				if (fs.b < this.c)
					result.addElement(new FuzzySet.S_Shaped(fs.a,fs.b, this.c, this.d));
				else
					result.addElement(new FuzzySet.S_Shaped(fs.a,fs.a, this.c, this.d));
				
			}else{
				result.addElement(new FuzzySet.EmptySet()) ;
			}
			return result ;			
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.RO_S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.c < fs.b){
				result.addElement(this);
			}else if (this.c >= fs.b && this.b < fs.b){
				if (fs.a > this.b)
					result.addElement(new FuzzySet.S_Shaped(this.a, this.b,fs.a,fs.b));
				else
					result.addElement(new FuzzySet.S_Shaped(this.a, this.b,fs.b,fs.b));
			}else{ 
				result.addElement(new FuzzySet.EmptySet()) ;
			}
			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b>fs.c ){
				result.addElement(this);
			}else if (this.c < fs.b ){
				result.addElement(this);				
			}else if (this.c <= fs.c && this.b>= fs.b ){
				result.add(new FuzzySet.EmptySet()) ;
			}else if (fs.b > this.b && this.c> fs.c){
				if (fs.a <= this.b)
					result.add(new FuzzySet.S_Shaped(this.a,this.b,fs.b,fs.b)) ;
				else
					result.add(new FuzzySet.S_Shaped(this.a,this.b,fs.a,fs.b)) ;
				if (fs.d>=this.c)
					result.add(new FuzzySet.S_Shaped(fs.c,fs.c,this.c,this.d)) ;
				else
					result.add(new FuzzySet.S_Shaped(fs.c,fs.d,this.c,this.d)) ;
			}else if (this.b >= fs.b && this.b<= fs.c ){

				if (fs.d>=this.c)
					result.add(new FuzzySet.S_Shaped(fs.c,fs.c,this.c,this.d)) ;
				else
					result.add(new FuzzySet.S_Shaped(fs.c,fs.d,this.c,this.d)) ;
			}else if (this.c >= fs.b && this.c<= fs.c ){
				if (fs.a <= this.b)
					result.add(new FuzzySet.S_Shaped(this.a,this.b,fs.b,fs.b)) ;
				else
					result.add(new FuzzySet.S_Shaped(this.a,this.b,fs.a,fs.b)) ;
			}
		
			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.EmptySet empty = new FuzzySet.EmptySet() ;
			result.add(empty) ;
			return result ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			if (fs instanceof FuzzySet.LO_S_Shaped){
				FuzzySet.LO_S_Shaped temp = (FuzzySet.LO_S_Shaped) fs ;
				if (temp.a<this.b)
					return 0 ;
				else 
					return Math.min(temp.a - this.b,this.c - this.b) ;
			}
			else if (fs instanceof FuzzySet.RO_S_Shaped){
				FuzzySet.RO_S_Shaped temp = (FuzzySet.RO_S_Shaped) fs ;
				if (temp.b>this.c)
					return 0 ;
				else 
					return Math.min(this.c-temp.b,this.c - this.b) ;
			}
			else if (fs instanceof FuzzySet.S_Shaped){
				FuzzySet.S_Shaped temp = (FuzzySet.S_Shaped) fs ;
				if (temp.b > this.c || this.b > temp.c)
					return 0 ;

				else if (temp.b <= this.b && temp.c >= this.c)
					return this.c - this.b ;
				else if (this.b <= temp.b && this.c >= temp.c)
					return temp.c - temp.b ;				
				else if (temp.b <= this.c && temp.c >= this.c)
					return this.c - temp.b ;
				else 
					return temp.c - this .b ;
			}
			else if (fs instanceof FuzzySet.LOToRO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.EmptySet)
				return 0 ;
			return 0;
		}
		
		@Override
		public double getCore() throws UnsupportedOperationException {
			return this.c - this.b ;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			if ((fs.getCore() < SMALL_EPSILON)&&(this.b<=fs.b)&&(this.c>=fs.c))		
				return true;
			else 
				return false ;
		}
	}
	
	/**
	 * S-Shaped Right Open Fuzzy Set _ _|/| A B
	 * 
	 * @author Ammar shaker [mailto:ammar.shaker@upb.de]
	 * 
	 */
	public static class RO_S_Shaped extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -1420519175232291448L;

		double a;
		double b;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public RO_S_Shaped(double A, double B) {
			this.a = A;
			this.b = B;
		}

		public void SetAll(double A, double B) {
			this.a = A;
			this.b = B;
		}
	
		@Override
		public double getMembershipOf(double val) {
			
			if (val >= b) return 1d;
			if (val <  a) return 0d;

			if (a <= val && val < (a+b)/2){
				double w=  (b-a) ;
				double result = 2* Math.pow((val - a )/ w,2) ;
				return result ;
			}
			else { //((a+b)/2 <= val && val < b)
				double w=  (b-a) ;
				double result = 1- 2* Math.pow((val - b )/ w,2) ;
				return result ;
			}
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_RIGHT_OPEN_S_Shaped);
			sb.append(" {").append(Utils.doubleToString(this.a, 5));
			sb.append(',').append(Utils.doubleToString(this.b, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new RO_S_Shaped(this.a, this.b);
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO_S_Shaped)
				return this.extendFuzzySet(( FuzzySet.LO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.RO_S_Shaped)
				return this.extendFuzzySet(( FuzzySet.RO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.S_Shaped)
				return this.extendFuzzySet(( FuzzySet.S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.extendFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.extendFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LO_S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b <= fs.a){
				result.addElement(new FuzzySet.LOToRO());
			}else {
				result.addElement(fs);
				result.addElement(this);
			}
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.RO_S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b >= fs.b){
				result.addElement(fs);
			}else {
				result.addElement(this);
			}
			return result ;
		}

		public Vector<FuzzySet> extendFuzzySet(FuzzySet.S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b <= fs.b){
				result.addElement(this);
			}else if (this.b >= fs.b && this.b <= fs.c) {
				result.addElement(new FuzzySet.RO_S_Shaped(fs.a, fs.b));
			}else{
				result.addElement(fs);
				result.addElement(this);
			}				
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(fs) ;
			return result ;
		}

		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO_S_Shaped)
				return this.subtractFuzzySet(( FuzzySet.LO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.RO_S_Shaped)
				return this.subtractFuzzySet(( FuzzySet.RO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.S_Shaped)
				return this.subtractFuzzySet(( FuzzySet.S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.subtractFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.subtractFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LO_S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b <= fs.a)
				result.addElement(new FuzzySet.RO_S_Shaped(fs.a, fs.b));
			else 
				result.addElement(this);
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.RO_S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b < fs.b)
				result.addElement(new FuzzySet.S_Shaped(this.a,this.b,fs.a,fs.b));
			else 
				result.addElement(new FuzzySet.EmptySet());
			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.b >= fs.c)
				result.add(this) ;
			else if (this.b < fs.b){
				result.add(new FuzzySet.S_Shaped(this.a,this.b,fs.a,fs.b)) ;
				result.add(new FuzzySet.RO_S_Shaped(fs.c,fs.d)) ;
			}
			else //if (this.b >= fs.b && this.b <= fs.c)
				result.add(new FuzzySet.RO_S_Shaped(fs.c,fs.d)) ;
			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.EmptySet empty = new FuzzySet.EmptySet() ;
			result.add(empty) ;
			return result ;
		}
		
		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			if (fs instanceof FuzzySet.LO_S_Shaped){
				FuzzySet.LO_S_Shaped temp = (FuzzySet.LO_S_Shaped) fs ;
				return Math.max(temp.a - this.b,0) ;
			}
			else if (fs instanceof FuzzySet.RO_S_Shaped)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.S_Shaped){
				FuzzySet.S_Shaped temp = (FuzzySet.S_Shaped) fs ;
				if (temp.c <this.b)
					return 0 ;
				else
					return Math.min(temp.c - this.b,temp.c - temp.b) ;
			}
			else if (fs instanceof FuzzySet.LOToRO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.EmptySet)
				return 0 ;
			return 0;
		}
		
		@Override
		public double getCore() throws UnsupportedOperationException {
			return Double.POSITIVE_INFINITY;
		}
		
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			if ((fs.getCore() < SMALL_EPSILON)&&(this.b<=fs.b))		
				return true;
			else 
				return false ;
		}
	}
	
	/**
	 * S-Shaped Left Open Fuzzy Set _ |\|_ A B
	 * 
	 * @author Ammar shaker [mailto:ammar.shaker@upb.de]
	 * 
	 */
	public static class LO_S_Shaped extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -1996133473760791109L;

		double a;
		double b;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public LO_S_Shaped(double A, double B) {
			this.a = A;
			this.b = B;
		}
		
		public void SetAll(double A, double B) {
			this.a = A;
			this.b = B;
		}
		
		@Override
		public double getMembershipOf(double val) {

			if (val <= a) return 1d;
			if (val >  b) return 0d;

			else if ( a< val && val <= (a + b)/2){
				double w=  (b-a) ;
				double result = 1- 2* Math.pow((a - val)/ w,2) ;
				return result ;
			}
			else {
				double w=  (b - a) ;
				double result = 2* Math.pow((b - val)/ w,2) ;
				return result ;
			}
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_LEFT_OPEN_S_Shaped);
			sb.append(" {").append(Utils.doubleToString(this.a, 5));
			sb.append(',').append(Utils.doubleToString(this.b, 5)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new LO_S_Shaped(this.a, this.b);
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO_S_Shaped)
				return this.extendFuzzySet(( FuzzySet.LO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.RO_S_Shaped)
				return this.extendFuzzySet(( FuzzySet.RO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.S_Shaped)
				return this.extendFuzzySet(( FuzzySet.S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.extendFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.extendFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LO_S_Shaped fs){

			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a >= fs.a){
				result.addElement(this);
			}else {
				result.addElement(fs);
			}
			return result ;			
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.RO_S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a >= fs.b){
				result.addElement(new FuzzySet.LOToRO());
			}else {
				result.addElement(this);
				result.addElement(fs);
			}
			return result ;		
		}

		public Vector<FuzzySet> extendFuzzySet(FuzzySet.S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a >= fs.c){
				result.addElement(this);
			}else if (this.a >= fs.b && this.a <= fs.c) {
				result.addElement(new FuzzySet.LO_S_Shaped(fs.c, fs.d));
			}else{
				result.addElement(this);
				result.addElement(fs);
			}
				
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> extendFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(fs) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet fs){
			if (fs instanceof FuzzySet.LO_S_Shaped)
				return this.subtractFuzzySet(( FuzzySet.LO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.RO_S_Shaped)
				return this.subtractFuzzySet(( FuzzySet.RO_S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.S_Shaped)
				return this.subtractFuzzySet(( FuzzySet.S_Shaped)fs) ;
			else if (fs instanceof FuzzySet.LOToRO)
				return this.subtractFuzzySet(( FuzzySet.LOToRO)fs) ;
			else if (fs instanceof FuzzySet.EmptySet)
				return this.subtractFuzzySet(( FuzzySet.EmptySet)fs) ;
			return null;
		}

		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LO_S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a > fs.a)
				result.addElement(new FuzzySet.S_Shaped(fs.a,fs.b,this.a,this.b));
			else 
				result.addElement(new FuzzySet.EmptySet());
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.RO_S_Shaped fs){

			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a >= fs.b)
				result.addElement(new FuzzySet.LO_S_Shaped(fs.a, fs.b));
			else 
				result.addElement(this);
			return result ;

		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.S_Shaped fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			if (this.a <= fs.b)
				result.add(this) ;
			else if (this.a >= fs.b && this.a <= fs.c){
				result.add(new FuzzySet.LO_S_Shaped(fs.a,fs.b)) ;
			}
			else { //if (this.a >= fs.c )
				result.add(new FuzzySet.LO_S_Shaped(fs.a,fs.b)) ;
				result.add(new FuzzySet.S_Shaped(fs.c,fs.d, this.a,this.b)) ;
			}

			return result ;
		}

		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.EmptySet fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			result.add(this) ;
			return result ;
		}
		
		public Vector<FuzzySet> subtractFuzzySet(FuzzySet.LOToRO fs){
			Vector <FuzzySet> result = new Vector<FuzzySet>() ;
			FuzzySet.EmptySet empty = new FuzzySet.EmptySet() ;
			result.add(empty) ;
			return result ;
		}

		@Override
		public double getCoreIntersection(FuzzySet fs) throws UnsupportedOperationException {
			if (fs instanceof FuzzySet.LO_S_Shaped)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.RO_S_Shaped){
				FuzzySet.RO_S_Shaped temp = (FuzzySet.RO_S_Shaped) fs ;
				return Math.max(this.a - temp.b,0) ;
			}
			else if (fs instanceof FuzzySet.S_Shaped){
				FuzzySet.S_Shaped temp = (FuzzySet.S_Shaped) fs ;
				if (temp.b > this.a)
					return 0 ;
				else
					return Math.min(this.a - temp.b,temp.c - temp.b) ;
			}
			else if (fs instanceof FuzzySet.LOToRO)
				return Double.POSITIVE_INFINITY ;
			else if (fs instanceof FuzzySet.EmptySet)
				return 0 ;
			return 0;
		}
		
		@Override
		public double getCore() throws UnsupportedOperationException {
			return Double.POSITIVE_INFINITY;
		}
		@Override
		public boolean checkContainsTRI(TRA fs) throws UnsupportedOperationException {
			if ((fs.getCore() < SMALL_EPSILON)&&(this.a>=fs.c))		
				return true;
			else 
				return false ;
		}
	}

	public static void main(String [] args){
		FuzzySet.LO_S_Shaped lo1 = new FuzzySet.LO_S_Shaped(10,20) ;
		FuzzySet.LO_S_Shaped lo2 = new FuzzySet.LO_S_Shaped(25,30) ;
		FuzzySet.LO_S_Shaped lo3 = new FuzzySet.LO_S_Shaped(35,40) ;

		FuzzySet.RO_S_Shaped ro1 = new FuzzySet.RO_S_Shaped(22,25) ;
		FuzzySet.RO_S_Shaped ro2 = new FuzzySet.RO_S_Shaped(30,35) ;
		FuzzySet.RO_S_Shaped ro3 = new FuzzySet.RO_S_Shaped(35,55) ;

		FuzzySet.EmptySet empt = new FuzzySet.EmptySet() ;
		FuzzySet.LOToRO LORO = new FuzzySet.LOToRO() ;

		FuzzySet.S_Shaped tra1 = new FuzzySet.S_Shaped(0,1,2,5) ;
		FuzzySet.S_Shaped tra2 = new FuzzySet.S_Shaped(40,41,42,43) ;
		FuzzySet.S_Shaped tra3 = new FuzzySet.S_Shaped(10,15,20,25) ;

		FuzzySet.S_Shaped tra4 = new FuzzySet.S_Shaped(10,17,25,30) ;
		FuzzySet.S_Shaped tra5 = new FuzzySet.S_Shaped(5,6,30,31) ;			

		FuzzySet.S_Shaped tra6 = new FuzzySet.S_Shaped(4,8,10,11) ;			
		FuzzySet.S_Shaped tra7 = new FuzzySet.S_Shaped(20,25,28,34) ;					
	



		FuzzySet.LO_S_Shaped lox1 = new FuzzySet.LO_S_Shaped(20,25) ;
		FuzzySet.RO_S_Shaped rox1 = new FuzzySet.RO_S_Shaped(42,43) ;
		FuzzySet.S_Shaped trax1 = new FuzzySet.S_Shaped(28,34,40,41) ;					
		FuzzySet.RO_S_Shaped rox2 = new FuzzySet.RO_S_Shaped(42,43) ;
		FuzzySet.RO_S_Shaped rox3 = new FuzzySet.RO_S_Shaped(30,35) ;

				
		FuzzySet.FreeSet fset = new FuzzySet.FreeSet() ;
		System.out.println("fset:\t"+ fset);
		fset.extendFuzzySet(lox1);
		System.out.println("fset:\t"+ fset);
		fset.extendFuzzySet(rox1);
		System.out.println("fset:\t"+ fset);
		fset.extendFuzzySet(trax1);
		System.out.println("fset:\t"+ fset);
		fset.subtractFuzzySet(rox2);
		System.out.println("fset:\t"+ fset);

		
		Vector<FuzzySet> sets = new Vector<FuzzySet>() ;
		sets.add(lo1) ;
		sets.add(lo2) ;
		sets.add(lo3) ;
		sets.add(ro1) ;
		sets.add(ro2) ;
		sets.add(ro3) ;
		
		Vector<FuzzySet> setsNew = new Vector<FuzzySet>() ;
		setsNew.add(empt) ;
		setsNew.add(LORO) ;
		setsNew.add(tra1) ;
		setsNew.add(tra2) ;
		setsNew.add(tra3) ;
		setsNew.add(tra4) ;
		setsNew.add(tra5) ;
		setsNew.add(tra6) ;
		setsNew.add(tra7) ;
		
		Random rnd = new Random() ;

		double support= 0 ;
		double intersection = 0;

		// checking the support of added sets
		for (int i = 0 ; i< 10 ; i++){
				System.out.println(fset);
				int r1 = rnd.nextInt(sets.size()) ;
				int r2 = rnd.nextInt(setsNew.size()) ;

				FuzzySet set1 = sets.get(r1) ;
				FuzzySet set2 = setsNew.get(r2) ;
				
				support= set1.getCore() ;
				intersection = fset.getCoreIntersection(set1) ;
				System.out.println("fset:\t"+ fset);
				
				System.out.println("to be added:\t"+set1);
				System.out.println("support:\t"+ support + "\t intersection:\t"+intersection);

				fset.extendFuzzySet(sets.get(r1));
				System.out.println("fset:\t"+ fset);

				support= set2.getCore() ;
				intersection = fset.getCoreIntersection(set2) ;
				
				System.out.println("to be removed:\t"+setsNew.get(r2));			
				System.out.println("support:\t"+ support + "\t intersection:\t"+intersection);

				fset.subtractFuzzySet(setsNew.get(r2));
				System.out.println("fset:\t"+ fset);

				System.out.println("#############################################");
		}
		
		System.exit(0);

		FuzzySet.S_Shaped setS_Shaped = new FuzzySet.S_Shaped(28,34,40,42) ;	
//		FuzzySet.LO set = new FreeSet.LO(30,35) ;	
//		FuzzySet.RO set = new FreeSet.RO(40,45) ;	
				
		for (int i=0 ; i<300 ; i++){
			double val = 25 + ((double)i)/100;
			System.out.println(Double.toString(val) + "\t"+setS_Shaped.getMembershipOf(val));
		}
		System.exit(0);
		
		FuzzySet.TRA set = new FreeSet.TRA(28,34,40,42) ;	
//		FuzzySet.LO set = new FreeSet.LO(30,35) ;	
//		FuzzySet.RO set = new FreeSet.RO(40,45) ;	
				
		for (int i=0 ; i<300 ; i++){
			double val = 25 + ((double)i)/10;
			System.out.println(Double.toString(val) + "\t"+set.getMembershipOf(val));
		}
		System.exit(0);

		
/*		FuzzySet.LO lo1 = new FreeSet.LO(10,20) ;
		FuzzySet.LO lo2 = new FreeSet.LO(25,30) ;
		FuzzySet.LO lo3 = new FreeSet.LO(35,40) ;

		FuzzySet.RO ro1 = new FreeSet.RO(22,25) ;
		FuzzySet.RO ro2 = new FreeSet.RO(30,35) ;
		FuzzySet.RO ro3 = new FreeSet.RO(35,55) ;

		FuzzySet.EmptySet empt = new FuzzySet.EmptySet() ;
		FuzzySet.LOToRO LORO = new FuzzySet.LOToRO() ;

		FuzzySet.TRA tra1 = new FreeSet.TRA(0,1,2,5) ;
		FuzzySet.TRA tra2 = new FreeSet.TRA(40,41,42,43) ;
		FuzzySet.TRA tra3 = new FreeSet.TRA(10,15,20,25) ;

		FuzzySet.TRA tra4 = new FreeSet.TRA(10,17,25,30) ;
		FuzzySet.TRA tra5 = new FreeSet.TRA(5,6,30,31) ;			

		FuzzySet.TRA tra6 = new FreeSet.TRA(4,8,10,11) ;			
		FuzzySet.TRA tra7 = new FreeSet.TRA(20,25,28,34) ;					
	



		FuzzySet.LO lox1 = new FreeSet.LO(20,25) ;
		FuzzySet.RO rox1 = new FreeSet.RO(42,43) ;
		FuzzySet.TRA trax1 = new FreeSet.TRA(28,34,40,41) ;					
		FuzzySet.RO rox2 = new FreeSet.RO(42,43) ;
		FuzzySet.RO rox3 = new FreeSet.RO(30,35) ;


//		FuzzySet.FreeSet fset = new FuzzySet.FreeSet() ;
//		System.out.println("fset:\t"+ fset);
//		fset.extend(lox1);
//		System.out.println("fset:\t"+ fset);
//		fset.extend(rox1);
//		System.out.println("fset:\t"+ fset);
//		fset.extend(trax1);
//		System.out.println("fset:\t"+ fset);
//		fset.extend(rox3);
//		System.out.println("fset:\t"+ fset);

				
		FuzzySet.FreeSet fset = new FuzzySet.FreeSet() ;
		System.out.println("fset:\t"+ fset);
		fset.extendFuzzySet(lox1);
		System.out.println("fset:\t"+ fset);
		fset.extendFuzzySet(rox1);
		System.out.println("fset:\t"+ fset);
		fset.extendFuzzySet(trax1);
		System.out.println("fset:\t"+ fset);
		fset.subtractFuzzySet(rox2);
		System.out.println("fset:\t"+ fset);

		
		Vector<FuzzySet> sets = new Vector<FuzzySet>() ;
		sets.add(lo1) ;
		sets.add(lo2) ;
		sets.add(lo3) ;
		sets.add(ro1) ;
		sets.add(ro2) ;
		sets.add(ro3) ;
		
		Vector<FuzzySet> setsNew = new Vector<FuzzySet>() ;
		setsNew.add(empt) ;
		setsNew.add(LORO) ;
		setsNew.add(tra1) ;
		setsNew.add(tra2) ;
		setsNew.add(tra3) ;
		setsNew.add(tra4) ;
		setsNew.add(tra5) ;
		setsNew.add(tra6) ;
		setsNew.add(tra7) ;
		
		Random rnd = new Random() ;

		double support= 0 ;
		double intersection = 0;

		// checking the support of added sets
		for (int i = 0 ; i< 10 ; i++){
				System.out.println(fset);
				int r1 = rnd.nextInt(sets.size()) ;
				int r2 = rnd.nextInt(setsNew.size()) ;

				FuzzySet set1 = sets.get(r1) ;
				FuzzySet set2 = setsNew.get(r2) ;
				
				support= set1.getSupport() ;
				intersection = fset.getSupportIntersection(set1) ;
				System.out.println("fset:\t"+ fset);
				
				System.out.println("to be added:\t"+set1);
				System.out.println("support:\t"+ support + "\t intersection:\t"+intersection);

				fset.extendFuzzySet(sets.get(r1));
				System.out.println("fset:\t"+ fset);

				support= set2.getSupport() ;
				intersection = fset.getSupportIntersection(set2) ;
				
				System.out.println("to be removed:\t"+setsNew.get(r2));			
				System.out.println("support:\t"+ support + "\t intersection:\t"+intersection);

				fset.subtractFuzzySet(setsNew.get(r2));
				System.out.println("fset:\t"+ fset);

				System.out.println("#############################################");
		}
*/		System.exit(0);
		

//		// extending and substracting from a free set
//		for (int i = 0 ; i < 10 ; i++){
//			System.out.println(fset);
//			int r1 = rnd.nextInt(sets.size()) ;
//			int r2 = rnd.nextInt(setsNew.size()) ;
//			
//			System.out.println("tobe added:\t"+sets.get(r1));
//			fset.extend(sets.get(r1));
//			System.out.println("fset:\t"+ fset);
//
//			System.out.println("tobe removed:\t"+setsNew.get(r2));			
//			fset.remove(setsNew.get(r2));
//			System.out.println("fset:\t"+ fset);
//
//			
//			System.out.println("#############################################");
//		}
//		System.exit(0);
	
//		for (FuzzySet set1: setsNew){
//			for (FuzzySet set2: setsNew){
//				System.out.println("Set1:\t"+set1);
//				System.out.println("Set2:\t"+set2);
//				System.out.println("Join:\t"+set1.extendFuzzySet(set2));
//				System.out.println("Remove:\t"+set1.subtractFuzzySet(set2));
//				System.out.println("#############################################");
//			}			
//		}
//		
		for (FuzzySet set1: setsNew){
			for (FuzzySet set2: sets){
				System.out.println("Set1:\t"+set1);
				System.out.println("Set2:\t"+set2);
				System.out.println("Join:\t"+set1.extendFuzzySet(set2));
				System.out.println("Remove:\t"+set1.subtractFuzzySet(set2));
				System.out.println("#############################################");
			}			
		}

		for (FuzzySet set1: setsNew){
			for (FuzzySet set2: sets){
				System.out.println("Set1:\t"+set2);
				System.out.println("Set2:\t"+set1);

				System.out.println("Join:\t"+set2.extendFuzzySet(set1));
				System.out.println("Remove:\t"+set2.subtractFuzzySet(set1));
				System.out.println("#############################################");
			}			
		}

//		for (FuzzySet set1: sets){
//			for (FuzzySet set2: sets){
//				System.out.println("Set1:\t"+set1);
//				System.out.println("Set2:\t"+set2);
//				System.out.println("Join:\t"+set1.extendFuzzySet(set2));
//				System.out.println("Remove:\t"+set1.subtractFuzzySet(set2));
//				System.out.println("#############################################");
//			}			
//		}
		System.exit(0);
		
		FuzzySet.FreeSet free = new FuzzySet.FreeSet() ;
		
		free.extendFuzzySet(lo1) ;
		System.out.println(free);

//		free.extend(lo2) ;
		System.out.println(free);

//		free.extend(lo3) ;
		System.out.println(free);

		free.extendFuzzySet(ro3) ;
		System.out.println(free);

//		free.extend(ro2) ;
		System.out.println(free);

//		free.extend(ro1) ;
		System.out.println(free);

		free.extendFuzzySet(tra3) ;
		System.out.println(free);
		
		free.extendFuzzySet(tra1) ;
		System.out.println(free);

		free.extendFuzzySet(tra2) ;
		System.out.println(free);

		
	}

}
