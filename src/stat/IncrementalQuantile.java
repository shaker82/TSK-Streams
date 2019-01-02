package stat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Vector;


public class IncrementalQuantile {
	private BigDecimal  alpha=new BigDecimal(0) ;
	private BigDecimal  quantile=new BigDecimal(0) ;
	private BigDecimal count=new BigDecimal(0) ;
	
	public IncrementalQuantile(double alpha){
		this.alpha=new BigDecimal(alpha) ;
	}
	public void update(double value){
		
		if (count.equals(BigDecimal.ZERO)){
			quantile=new BigDecimal (value) ;
			count=count.add(BigDecimal.ONE) ;
		}
		else{
			BigDecimal c=BigDecimal.ONE.divide(count,50,RoundingMode.DOWN) ;
			BigDecimal y=new BigDecimal(1) ;
			if (quantile.compareTo(new BigDecimal(value))==1)
				y= alpha.subtract(BigDecimal.ONE) ;
			else 
				y= alpha ;
			quantile=quantile.add(c.multiply(y) )  ;
			count=count.add(BigDecimal.ONE) ;
			
			quantile=quantile.max(BigDecimal.ZERO) ;
			quantile=quantile.min(BigDecimal.ONE) ;
		}
	}
	public BigDecimal getQuantile() {
		return quantile;
	}
	
	public static void main(String[] args)throws Exception {
		double [] vec = readFile("E:\\del\\del\\IBLStreams-DS-CDHP-D4-r1-67-r2-109-m-EpiAleaVar1-S500-b0.2.csv.pred") ;

		for (double i=0.01; i<1.0001 ;i +=0.01){
			IncrementalQuantile inq = new IncrementalQuantile(i) ;
			int tempcount =0 ;
			for (double val: vec){
				if (tempcount >500)
				  inq.update(val);
				tempcount++ ;
			}
			System.out.println(i+"\t"+inq.getQuantile());
		}
		 
	}
	
	public static double [] readFile(String infile)
    {
//		Vector<Double >  data = new Vector<Double>(125000) ;
		double [] data = new double [125000] ;
		for (int i=0 ; i < data.length;i++)
			data[i]= -1 ; 
            try
            {
                BufferedReader br =new BufferedReader(new FileReader(infile)) ;
                String line="\"" ;                
                line=br.readLine() ;
                int count=0 ;
                while (line!=null )
                {
                	String [] array = line.split("\t") ;
//                	data.add(Double.parseDouble(array[0])) ;                	
                	data[count++]= Double.parseDouble(array[0]) ;
                	line=br.readLine() ;
                }
                br.close() ;
            }
            catch (Exception ex)
            {
                    ex.printStackTrace() ;
            }
            return data ;
    }
}