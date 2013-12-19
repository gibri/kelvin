/* 
    Copyright 2013 Giovanni Bricconi

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


 */
package org.apache.solr.kelvin;

public class Measure {
	private String scorer;
	private String measureName;
	private double value;
	
	public Measure(String _scorer,String _measureName,double _value){
		scorer=_scorer;
		measureName=_measureName;
		value=_value;
	}
	
	@Override
	public String toString() {
		return "" + scorer + " -> " + measureName
				+ " = " + value;
	}
	public String getScorer() {
		return scorer;
	}
	public void setScorer(String scorer) {
		this.scorer = scorer;
	}
	public String getMeasureName() {
		return measureName;
	}
	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
