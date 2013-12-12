package org.apache.solr.kelvin.testcases;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.kelvin.SingletonConditionRegistry;
import org.apache.solr.kelvin.SingletonTestRegistry;
import org.apache.solr.kelvin.events.ConditionFailureTestEvent;
import org.apache.solr.kelvin.responseanalyzers.XmlDoclistExtractorResponseAnalyzerTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import junit.framework.TestCase;

public class SimpleTestCaseTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configs=SimpleConditionTest.readJsonResource(this.getClass(), "/org/apache/solr/kelvin/testcases/TestCase.json");
		decodedResponses =
				XmlDoclistExtractorResponseAnalyzerTest.quickParseForTest("/org/apache/solr/kelvin/responseanalyzers/multiResult.xml");
		JsonNode empty = JsonNodeFactory.instance.objectNode();
		SingletonTestRegistry.configure(empty);
		SingletonConditionRegistry.configure(empty);
	}
	
	public void testConfig() throws Exception {
		SimpleTestCase stc = new SimpleTestCase();
		stc.configure(configs.get("base")); 
		/* only one usb on first results -> 2 errors, 2 different vertical in position 9 and 10  ==> 4 errors*/
		
		Iterator<Properties> i = stc.getQueryParameterIterator();
		while (i.hasNext()) {
			List<ConditionFailureTestEvent> errors = stc.verifyConditions(i.next(), decodedResponses);
			assertFalse(errors.isEmpty());
			assertEquals(4, errors.size());
		}
	}
	
	JsonNode configs;
	private Map<String,Object> decodedResponses;
	
}
