package org.apache.solr.kelvin.responseanalyzers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.solr.kelvin.QueryPerformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import junit.framework.TestCase;

public class LegacyResponseAnalyzerTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ra = new LegacyResponseAnalyzer();
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

	public static Map<String,Object> quickParseForTest(String resourceName) throws Exception {
		LegacyResponseAnalyzer ra = new LegacyResponseAnalyzer();
		JsonNode emptyConf = JsonNodeFactory.instance.objectNode();
		ra.configure(emptyConf); //empty conf
		
		XmlResponseAnalyzer ra_xml = new XmlResponseAnalyzer();
		ra_xml.configure(emptyConf);
		
		Map<String,Object> previousResponses = new HashMap<String, Object>();
		previousResponses. put(QueryPerformer.RAW_RESPONSE, IOUtils.toString(XmlResponseAnalyzerTest.class.getResourceAsStream(resourceName), "utf8"));
		try {
		ra_xml.decode(previousResponses);
		} catch (Exception e) {
			//its ok to skip, the class must works also in case of errors
		}
		ra.decode(previousResponses);
		
		return previousResponses;
	}
	
	private LegacyResponseAnalyzer ra;
	private XmlResponseAnalyzer ra_xml;
	
	public void testResponses() throws Exception {
		
		ArrayNode  resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/legacyOneResult.xml");
		assertEquals(1, resp.size());
		
		resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/legacyMultiResult.xml");
		assertEquals(10, resp.size());
	}
}
