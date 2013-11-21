package org.apache.solr.kelvin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.kelvin.events.ConditionFailureTestEvent;

import com.fasterxml.jackson.databind.JsonNode;

public interface ITestCase {
	
	/**
	 * A test can generate more than one query, suppose you want to test the result of
	 * two queries "ipad" and "i pad". The test is the same but you must run two
	 * different searches.
	 * 
	 * 
	 * @return an iterator of properties. each property is the name of the parameter
	 * to add to the request and its value. You don't need to urlencode values. 
	 * Just provide param value pairs
	 */
	public Iterator<Properties> getQueryParameterIterator();

	/**
	 * Evautate condition on actual response and return a list of errors found
	 * 
	 * @param queryParams current parameters as obtained from getQueryParameterIterator
	 * @param decodedResponses response content as returned by configured analyers
	 * @return a list of errors found
	 */
	public List<ConditionFailureTestEvent> verifyConditions(Properties queryParams,
			Map<String, Object> decodedResponses);
	
	/**
	 * read the json config and configure this test
	 * @param config
	 * @throws Exception if misconfigured
	 */
	public void configure(JsonNode config) throws Exception;
}
