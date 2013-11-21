package org.apache.solr.kelvin.events;

import java.util.Map;
import java.util.Properties;

import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.TestEvent;

public class ResponseDecodedTestEvent extends TestEvent {

	private Map<String, Object> responseFragments;

	public ResponseDecodedTestEvent(ITestCase testCase, Properties parameters,
			Map<String, Object> _responseFragments) {
		super(testCase, parameters);
		responseFragments = _responseFragments;

	}

	public Map<String, Object> getResponseFragments() {
		return responseFragments;
	}
}
