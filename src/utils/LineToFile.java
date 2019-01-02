package utils ;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class LineToFile {	
	
   	public static BufferedWriter bw =null ;

	
	public static void print(String fname,String line)
    {
            try
            {
           		bw =new BufferedWriter(new FileWriter(fname,true)) ;
            		
            	bw.write(line+"\n") ;
                bw.close() ;                
            }
            catch (Exception ex)
            {
                    ex.printStackTrace() ;
            }
    }
}
