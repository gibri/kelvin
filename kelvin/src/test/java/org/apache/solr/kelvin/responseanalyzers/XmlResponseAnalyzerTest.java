package org.apache.solr.kelvin.responseanalyzers;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.solr.kelvin.QueryPerformer;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class XmlResponseAnalyzerTest extends TestCase {
	public void testNoResultsResponse() throws Exception {
		checkDecode("/org/apache/solr/kelvin/responseanalyzers/empty.xml");
	}
	
	public void testOneResult() throws Exception {
		checkDecode("/org/apache/solr/kelvin/responseanalyzers/oneResult.xml");
	}

	public void testMultiResult() throws Exception {
		checkDecode("/org/apache/solr/kelvin/responseanalyzers/multiResult.xml");
	}
	
	public void testMalformedResponse() {
		try {
			checkDecode("/org/apache/solr/kelvin/responseanalyzers/malformedResponse.xml");
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
		
		assertTrue( previousResponses.get(XmlResponseAnalyzer.XML_DOM) instanceof Document );
	}

	XmlResponseAnalyzer ra;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ra = new XmlResponseAnalyzer();
		ra.configure(JsonNodeFactory.instance.objectNode());
	}
}
