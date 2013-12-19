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

import java.util.List;
import java.util.Map;

import org.apache.solr.kelvin.QueryPerformer;
import org.apache.solr.kelvin.ResponseAnalyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonResponseAnalyzer implements ResponseAnalyzer {
	public final static String JSON_NODE = "kelvin.jsonNode";

	public void decode(Map<String,Object> previousResponses) throws Exception {
		if (previousResponses.containsKey(QueryPerformer.RAW_RESPONSE) &&
				previousResponses.get(QueryPerformer.RAW_RESPONSE) instanceof String) {
			String rawResponse =(String)previousResponses.get(QueryPerformer.RAW_RESPONSE);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue((String)rawResponse, JsonNode.class); 
			previousResponses.put(JSON_NODE, rootNode);
		}
	}

	public void configure(JsonNode config) throws Exception {
		// pass
		
	}

}
