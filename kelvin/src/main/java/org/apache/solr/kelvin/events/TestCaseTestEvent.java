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
package org.apache.solr.kelvin.events;

import java.util.Properties;

import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.TestEvent;

public class TestCaseTestEvent extends TestEvent {
	private boolean starting;
	
	public boolean isStarting() {return starting;}
	public boolean isEnding() {return !starting; }
	
	/**
	 * tracks entering and exiting test cases. Fired with empty parameters when beginning
	 * or ending whole test. with parameters when running for specific parameter instances
	 * @param testCase the current test case
	 * @param parameters can be null when completing the whole test or starting the whole test. 
	 * @param _starting
	 */
	public TestCaseTestEvent(ITestCase testCase, Properties parameters, boolean _starting) {
		super(testCase,parameters);
		starting = _starting;
	}
	
	/** is real query or is just test x is starting */
	public boolean isSingleQuery() {
		return this.getParameters()!=null;
	}
}
