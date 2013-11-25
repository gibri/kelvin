package org.apache.solr.kelvin.responseanalyzers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.solr.kelvin.QueryPerformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import junit.framework.TestCase;

public class XmlDoclistExtractorResponseAnalyzerTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ra = new XmlDoclistExtractorResponseAnalyzer();
		JsonNode emptyConf = JsonNodeFactory.instance.objectNode();
		ra.configure(emptyConf); //empty conf
		
		ra_xml = new XmlResponseAnalyzer();
		ra_xml.configure(emptyConf);
	}
	
	private ArrayNode parseResource(String name) throws Exception {
		Map<String,Object> previousResponses = new HashMap<String, Object>();
		previousResponses. put(QueryPerformer.RAW_RESPONSE, IOUtils.toString(XmlResponseAnalyzerTest.class.getResourceAsStream(name), "utf8"));
		try {
		ra_xml.decode(previousResponses);
		} catch (Exception e) {
			//its ok to skip, the class must works also in case of errors
		}
		ra.decode(previousResponses);
		return (ArrayNode) previousResponses.get(XmlDoclistExtractorResponseAnalyzer.DOC_LIST);
	}

	private XmlDoclistExtractorResponseAnalyzer ra;
	private XmlResponseAnalyzer ra_xml;
	
	public void testResponses() throws Exception {
		ArrayNode resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/empty.xml");
		assertEquals(0,  resp.size() );
		
		resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/malformedResponse.xml");
		assertEquals(0,  resp.size() );
		
		resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/oneResult.xml");
		assertEquals(1, resp.size());
		
		resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/multiResult.xml");
		assertEquals(10, resp.size());
	}
}
