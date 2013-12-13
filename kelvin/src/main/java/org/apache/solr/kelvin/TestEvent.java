package org.apache.solr.kelvin;

import java.util.List;
import java.util.Properties;

public class TestEvent {
	private ITestCase testCase;
	private Properties parameters;
		

	public TestEvent( ITestCase _testCase, Properties _parameters ) {
		testCase = _testCase;
		parameters=_parameters;
	}

	public ITestCase getTestCase() {
		return testCase;
	}

	public Properties getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return parameters!= null ? parameters.toString() : "";
	}
	
}
