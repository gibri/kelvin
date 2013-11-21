package org.apache.solr.kelvin;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.kelvin.events.ConditionFailureTestEvent;

import com.fasterxml.jackson.databind.JsonNode;

public interface ICondition {
	
	/**
	 * read the json config and configure this test
	 * @param config
	 * @throws Exception if misconfigured
	 */
	public void configure(JsonNode config) throws Exception;
	
	/**
	 * checks specific values on results
	 * @param queryParams
	 * @param decodedResponses
	 * @param testSpecificArgument eg. current field values for 4th position on listing results
	 * @return list of errors detected
	 */
	public List<ConditionFailureTestEvent> verifyConditions(ITestCase testCase, Properties queryParams,
			Map<String, Object> decodedResponses,
			Object testSpecificArgument);
}
