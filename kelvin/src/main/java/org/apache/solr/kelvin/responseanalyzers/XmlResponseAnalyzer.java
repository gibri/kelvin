package org.apache.solr.kelvin.responseanalyzers;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.solr.kelvin.QueryPerformer;
import org.apache.solr.kelvin.ResponseAnalyzer;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.JsonNode;

public class XmlResponseAnalyzer implements ResponseAnalyzer {
	
	public final static String XML_DOM = "kelvin.dom";

	private DocumentBuilderFactory dbf;
	private DocumentBuilder documentBuilder;
	
	public void config(JsonNode config) throws Exception {
		dbf = DocumentBuilderFactory.newInstance();
		documentBuilder = dbf.newDocumentBuilder();
	}
	
	public void decode(Map<String,Object> previousResponses) throws Exception {
		if (previousResponses.containsKey(QueryPerformer.RAW_RESPONSE) &&
				previousResponses.get(QueryPerformer.RAW_RESPONSE) instanceof String) {
			String rawResponse =(String)previousResponses.get(QueryPerformer.RAW_RESPONSE);
			previousResponses.put(XML_DOM, documentBuilder.parse(new InputSource(new StringReader((String)rawResponse))));
		}
	}

	public void configure(JsonNode config) throws Exception {
		// pass
		
	}

}
