/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 
Aurelien Lancin (Coati research team, Inria)
Christian Glacet (LaBRi, Bordeaux)
David Coudert (Coati research team, Inria)
Fabien Crequis (Coati research team, Inria)
Grégory Morel (Coati research team, Inria)
Issam Tahiri (Coati research team, Inria)
Julien Fighiera (Aoste research team, Inria)
Laurent Viennot (Gang research-team, Inria)
Michel Syska (I3S, Université Cote D'Azur)
Nathann Cohen (LRI, Saclay) 
Julien Deantoin (I3S, Université Cote D'Azur, Saclay) 

*/
 
 package toools;

import java.io.Serializable;

/**
 * Class used to maintain statistics about a long variable that can represent any measurement 
 * as soon as they can be represented by a long number.
 * 
 * Samples are accumulated by calling the {@link #addSample(long)} function on the object. As soon as
 * more than one sample has been recorded, the average, the variance, the standard deviation as well as
 * the minimum and the maximum can be computed.
 *  
 * @author nchleq
 *
 */
@SuppressWarnings("serial")
public class LongVariableStats implements Serializable {
	
	private long sum = 0;
	private long sqSum = 0;
	private long sampleCount = 0;
	private long min = Long.MAX_VALUE;
	private long max = Long.MIN_VALUE;
		
	public void addSample(long val) {
		sampleCount++;
		sum += val;
		sqSum += (val * val);
		if (val > max) max = val;
		if (val < min) min = val;
	}
	
	public void merge(LongVariableStats other) {
		this.sampleCount += other.sampleCount;
		this.sum += other.sum;
		this.sqSum += other.sqSum;
	}
	
	public long getSampleCount() {
		return sampleCount;
	}
	
	public long getAverage() {
		return sum / sampleCount;
	}
	
	public long getVariance() {
		return (sqSum - (sum * sum) / sampleCount) / sampleCount;
	}

	public long getStdDev() {
		return (long) Math.sqrt(getVariance());
	}
	
	public long getMax() {
		return max;
	}
	
	public long getMin() {
		return min;
	}
	
	@Override
	public String toString() {
		if (getSampleCount() == 0) {
			return "{#: 0, avg: 0, var: 0, min: 0, max: 0}";
		}
		return "{#:" + getSampleCount() + ", avg:" + getAverage()
				+ ", dev: " + getStdDev()
				+ ", min: " + getMin()
				+ ", max: " + getMax() + "}";
	}
}
