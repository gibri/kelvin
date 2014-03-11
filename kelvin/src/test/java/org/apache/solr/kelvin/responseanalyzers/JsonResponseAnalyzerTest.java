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
package org.apache.solr.kelvin.responseanalyzers;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.solr.kelvin.QueryPerformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class JsonResponseAnalyzerTest extends TestCase {
	public void testNoResultsResponse() throws Exception {
		checkDecode("/org/apache/solr/kelvin/responseanalyzers/empty.json");
	}
	
	public void testOneResult() throws Exception {
		checkDecode("/org/apache/solr/kelvin/responseanalyzers/oneResult.json");
	}

	public void testMultiResult() throws Exception {
		checkDecode("/org/apache/solr/kelvin/responseanalyzers/multiResult.json");
	}
	
	public void testMalformedResponse() {
		try {
			checkDecode("/org/apache/solr/kelvin/responseanalyzers/malformedResponse.json");
		} catch (Exception e) {
			return;
		}
		fail("does not throw exception");
	}
	
	private void checkDecode(String fileName) throws Exception {
		String raw = IOUtils.toString(XmlResponseAnalyzerTest.class.getResourceAsStream(fileName), "utf8");
		Map<String,Object> previousResponses = new HashMap<String, Object>();
		previousResponses.put(QueryPerformer.RAW_RESPONSE, raw);
		
		ra.decode(previousResponses);
		
		assertTrue( previousResponses.get(JsonResponseAnalyzer.JSON_NODE) instanceof JsonNode );
	}

	JsonResponseAnalyzer ra;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ra = new JsonResponseAnalyzer();
		ra.configure(JsonNodeFactory.instance.objectNode());
	}
}

