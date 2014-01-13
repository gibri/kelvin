package org.apache.solr.kelvin.testcases;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.kelvin.events.ConditionFailureTestEvent;
import org.apache.solr.kelvin.responseanalyzers.XmlDoclistExtractorResponseAnalyzerTest;

import com.fasterxml.jackson.databind.JsonNode;

import junit.framework.TestCase;

/**
 *  Copyright 2014 Giovanni Bricconi

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   
 * Checks any and reverse fields of simple and value list conditions
 * 
 * @author giovannibricconi
 * 
 */
public class AnyLengthReverseTest extends TestCase {
	
	private JsonNode configs;

	private Map<String,Object> decodedResponses;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configs = SimpleConditionTest.readJsonResource(
				AnyLengthReverseTest.class, "AnyLengthReverse.json");
		decodedResponses = XmlDoclistExtractorResponseAnalyzerTest
				.quickParseForTest("/org/apache/solr/kelvin/responseanalyzers/argentina.xml");
	}
	
	public void testAny() throws Exception {
		SimpleCondition sc = new SimpleCondition();
		sc.configure(configs.get("anySimple"));
		Properties queryParams = new Properties();
		
		List<ConditionFailureTestEvent> errors = sc.verifyConditions(null, queryParams, decodedResponses, null);
		assertEquals(0,  errors.size());
	}

	
	public void testAnyReverse() throws Exception {
		SimpleCondition sc = new SimpleCondition();
		sc.configure(configs.get("anyReverse"));
		Properties queryParams = new Properties();
		
		List<ConditionFailureTestEvent> errors = sc.verifyConditions(null, queryParams, decodedResponses, null);
		assertEquals(1,  errors.size());
	}
	
	public void testAnyList() throws Exception {
		ValueListCondition sc = new ValueListCondition();
		sc.configure(configs.get("anyList"));
		Properties queryParams = new Properties();
		
		List<ConditionFailureTestEvent> errors = sc.verifyConditions(null, queryParams, decodedResponses, null);
		assertEquals(6,  errors.size());
	}
	
	public void testAnyListReverse() throws Exception {
		ValueListCondition sc = new ValueListCondition();
		sc.configure(configs.get("anyListReverse"));
		Properties queryParams = new Properties();
		
		List<ConditionFailureTestEvent> errors = sc.verifyConditions(null, queryParams, decodedResponses, null);
		assertEquals(4,  errors.size());
	}
}
