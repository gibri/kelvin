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

import java.util.List;
import java.util.Observer;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An observer of Test events
 * 
 * implements Observer methods to track test progress
 * 
 * provide formatting for reporting errors
 * 
 * @author giovannibricconi
 *
 */
public abstract class Scorer implements Observer, IConfigurable {
	
	/**
	 * Have a chanche of reading configuration
	 * 
	 * @throws Exception if misconfigured
	 */
	public abstract void configure(JsonNode config) throws Exception;
	
	/**
	 * called at test ends to print a simple description of results
	 * @return
	 */
	public String simpleReport() {
		List<Measure> measures = measureReport();
		StringBuilder sb = new StringBuilder();
		for (Measure m: measures) {
			sb.append(m.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * called at test ends to print a simple description of results
	 * @return
	 */
	public abstract List<Measure> measureReport();
}
