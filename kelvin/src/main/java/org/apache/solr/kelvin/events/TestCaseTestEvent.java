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
