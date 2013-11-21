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
