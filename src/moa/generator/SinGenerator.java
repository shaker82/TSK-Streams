package moa.generator;

import java.util.Random;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import moa.core.FastVector;
import moa.core.InstanceExample;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.streams.InstanceStream;
import moa.tasks.TaskMonitor;

public class SinGenerator extends AbstractOptionHandler implements InstanceStream
{
	
	private static final long serialVersionUID = 1L;
	protected InstancesHeader streamHeader;
    protected Random instanceRandom;
    
    public IntOption numAttsOption = new IntOption("numAtts", 'a',
            "The number of attributes to generate.", 4, 0, 100);
    
    public IntOption instanceRandomSeedOption = new IntOption(
            "instanceRandomSeed", 'i',
            "Seed for random generation of instances.", 1);
	
    public SinGenerator()
    {
    	this.instanceRandom = new Random(this.instanceRandomSeedOption.getValue());
    }
    
	protected void generateHeader() {
        FastVector attributes = new FastVector();
        for (int i = 0; i < this.numAttsOption.getValue(); i++) {
            attributes.addElement(new Attribute("att" + (i + 1)));
        }
        attributes.addElement(new Attribute("TargetValue"));

        this.streamHeader = new InstancesHeader(new Instances(getCLICreationString(InstanceStream.class), attributes, 0));
        this.streamHeader.setClassIndex(this.streamHeader.numAttributes() - 1);
    }
	
	@Override
    public InstanceExample nextInstance() {

		
		
		
        int numAtts = this.numAttsOption.getValue();
        double[] attVals = new double[numAtts+1];
        
        double range = 20 / (numAtts);
        
    	attVals[0] = this.instanceRandom.nextDouble()*20 - 10;
    	
    	// Random Noise attributes
        for(int i=1; i < numAtts; i++)
        {
        	attVals[i] = this.instanceRandom.nextDouble()*(this.instanceRandom.nextDouble()*20);        	
        }
        
//        double classLabel = Math.sin(attVals[0] * (Math.PI/10.0) - 0.5*Math.PI) * 10;
        double classLabel = Math.sin(attVals[0] * (Math.PI/10.0) - 0.5*Math.PI) * 10;
        attVals[numAtts] = classLabel;
        
        Instance inst = new DenseInstance(1.0, attVals);
        inst.setDataset(getHeader());
        inst.setClassValue(classLabel);
                
        return new InstanceExample(inst);
    }

	
	@Override
	public long estimatedRemainingInstances() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InstancesHeader getHeader() {
		// TODO Auto-generated method stub
		return this.streamHeader;
	}

	@Override
	public boolean hasMoreInstances() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isRestartable() {
		// TODO Auto-generated method stub
		return true;
	}

//	@Override
//	public Example<Instance> nextInstance() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public void restart() {
		this.instanceRandom = new Random(this.instanceRandomSeedOption.getValue());
        
	}

	@Override
	public void getDescription(StringBuilder arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
		
		generateHeader();
		restart();
	}

}
