package changedetection;

/*
 *    SDRSplitCriterionAMRules.java
 *    Copyright (C) 2014 University of Porto, Portugal
 *    @author A. Bifet, J. Duarte, J. Gama
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 *    
 */

import java.io.Serializable;

import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;

public class PageHinkleyTestMod implements Serializable {

    public IntOption minNumInstancesOption = new IntOption(
            "minNumInstances",
            'n',
            "The minimum number of instances before permitting detecting change.",
            30, 0, Integer.MAX_VALUE);

    public FloatOption deltaOption = new FloatOption("delta", 'd',
            "Delta parameter of the Page Hinkley Test", 0.005, 0.0, 1.0);

    public FloatOption lambdaOption = new FloatOption("lambda", 'l',
            "Lambda parameter of the Page Hinkley Test", 50, 0.0, Float.MAX_VALUE);

    public FloatOption alphaOption = new FloatOption("alpha", 'a',
            "Alpha parameter of the Page Hinkley Test", 1 - 0.0001, 0.0, 1.0);
	
    private static final long serialVersionUID = 1L;
    protected double cumulativeSum;
    
    public double getCumulativeSum() {
		return cumulativeSum;
	}

	public double getMinimumValue() {
		return minimumValue;
	}


	protected double minimumValue;
    protected double sumAbsolutError;
    protected long phinstancesSeen;
    protected double threshold;
    protected double alpha;
    protected double delta;

    public PageHinkleyTestMod() {
    
    	this.delta = deltaOption.getValue();
        this.threshold = lambdaOption.getValue();
        this.alpha = alphaOption.getValue();
        this.reset();
    }

    public void reset() {
        this.cumulativeSum = 0.0;
        this.minimumValue = Double.MAX_VALUE;
        this.sumAbsolutError = 0.0;
        this.phinstancesSeen = 0;
    }

    //Compute Page-Hinkley test
    public boolean update(double error, double weight) 
    {

        this.phinstancesSeen++;
        double absolutError = Math.abs(error);
        
        this.sumAbsolutError = this.sumAbsolutError + absolutError*weight;	// mean change only by weighted error!!
        if (this.phinstancesSeen > this.minNumInstancesOption.getValue()) // TODO change to higher value later
        {
        	double mT = absolutError - (this.sumAbsolutError / this.phinstancesSeen) - this.alpha;
        	double mT_0 = (absolutError*weight) - (this.sumAbsolutError / this.phinstancesSeen) - (this.alpha*weight);
        	double mT_1 = (absolutError - (this.sumAbsolutError / this.phinstancesSeen) - this.alpha) * weight; // use this one
        	
        	
        	
        	this.cumulativeSum = this.cumulativeSum + mT_1; // Update the cumulative mT sum
//        	this.cumulativeSum = this.cumulativeSum + mT; // Update the cumulative mT sum
//        	this.cumulativeSum = this.cumulativeSum + mT; // Update the cumulative mT sum
        	
        	
        	if (this.cumulativeSum < this.minimumValue) { // Update the minimum mT value if the new mT is smaller than the current minimum
        		this.minimumValue = this.cumulativeSum;
        	}
        	

        	
        	return (((this.cumulativeSum - this.minimumValue) > this.threshold));
        }
        return false;
    }

}