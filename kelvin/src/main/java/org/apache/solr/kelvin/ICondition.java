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
