package org.apache.solr.kelvin.testcases;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.kelvin.events.ConditionFailureTestEvent;
import org.apache.solr.kelvin.events.ConditionsNotMetTestEvent;
import org.apache.solr.kelvin.events.MissingFieldTestEvent;
import org.apache.solr.kelvin.events.MissingResultTestEvent;
import org.apache.solr.kelvin.responseanalyzers.XmlDoclistExtractorResponseAnalyzerTest;

import com.fasterxml.jackson.databind.JsonNode;

import junit.framework.TestCase;

public class ValueListConditionTest extends TestCase {
	
	private JsonNode configs;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configs = SimpleConditionTest.readJsonResource(ValueListConditionTest.class, "ValueListCondition.json") ;
		decodedResponses =
				XmlDoclistExtractorResponseAnalyzerTest.quickParseForTest("/org/apache/solr/kelvin/responseanalyzers/multiResult.xml");
	}

	private Map<String,Object> decodedResponses;
	
	public void testLegacy() throws Exception {
		ValueListCondition v = get("legacy");
		assertEquals(5, v.getLength());
		assertEquals("slug_preferenziale", v.getField());
		assertTrue(v.getCorrectValuesList().contains("/a/c"));
		assertTrue(v.getCorrectValuesList().contains("/a/d"));
	}
	
	public void testStd() throws Exception {
		ValueListCondition v = get("std");
		assertEquals(5, v.getLength());
		assertEquals("slug", v.getField());
		assertTrue(v.getCorrectValuesList().contains("/a/c"));
		assertTrue(v.getCorrectValuesList().contains("/a/d"));
	}
	
	public void testShort() throws Exception {
		ValueListCondition v = get("short");
		assertEquals(5, v.getLength());
		assertEquals("slug", v.getField());
		assertTrue(v.getCorrectValuesList().contains("true"));
	}
	
	public void testMissing() {
		String cases[]={"nolen","nofield","noval"};

		for (String c:cases ) {
			try {
				get(c);
				fail();
			} catch (Exception e) {}
		}
	}
	
	public void testBase() throws Exception {
		ValueListCondition v = get("base");
		v.getCorrectValuesList().contains("10");
		Properties queryParams = new Properties();
		
		List<ConditionFailureTestEvent> errors = v.verifyConditions(null, queryParams, decodedResponses, null);
		assertEquals(0, errors.size());
	}
	
	public void test2Errs() throws Exception {
		ValueListCondition v = get("twoErrs");
		Properties queryParams = new Properties();
		
		List<ConditionFailureTestEvent> errors = v.verifyConditions(null, queryParams, decodedResponses, null);
		assertEquals(2, errors.size());
		for (ConditionFailureTestEvent e : errors) {
			assertTrue(e instanceof ConditionsNotMetTestEvent);
		}
	}
	
	public void testMissingResult() throws Exception {
		ValueListCondition v = get("missingResult");
		Properties queryParams = new Properties();
		
		List<ConditionFailureTestEvent> errors = v.verifyConditions(null, queryParams, decodedResponses, null);
		assertEquals(1, errors.size());
		for (ConditionFailureTestEvent e : errors) {
			assertTrue(e instanceof MissingResultTestEvent);
		}
	}
	
	public void testUnknownField() throws Exception {
		ValueListCondition v = get("unknownField");
		Properties queryParams = new Properties();
		
		List<ConditionFailureTestEvent> errors = v.verifyConditions(null, queryParams, decodedResponses, null);
		assertEquals(4, errors.size());
		for (ConditionFailureTestEvent e : errors) {
			assertTrue(e instanceof MissingFieldTestEvent);
		}
	}
	
	private ValueListCondition get(String name) throws Exception {
		ValueListCondition v = new  ValueListCondition();
		v.configure(configs.get(name));
		return v;
	}
}
