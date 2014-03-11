package org.apache.solr.kelvin.testcases;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.solr.kelvin.events.ConditionFailureTestEvent;
import org.apache.solr.kelvin.events.ConditionsNotMetTestEvent;
import org.apache.solr.kelvin.responseanalyzers.XmlDoclistExtractorResponseAnalyzerTest;

import com.fasterxml.jackson.databind.JsonNode;

public class DateRangeContidionTest extends TestCase {

	@SuppressWarnings("deprecation")
	public void testParsing() throws Exception {
		DateRangeCondition condition = new DateRangeCondition();

		condition
				.parseRange("[2013-12-01T00:00:00.0Z to 2014-01-01T00:00:00.0Z]");
		assertTrue(condition.isLeftInclusive() && condition.isRightInclusive());

		condition
				.parseRange("{2013-12-01T00:00:00.0Z to 2014-01-01T12:00:00.0Z}");
		assertTrue(!condition.isLeftInclusive()
				&& !condition.isRightInclusive());

		assertTrue(condition.getLeftDate().equals(
				new Date(Date.UTC(2013 - 1900, 12 - 1, 1, 0, 0, 0))));
		assertTrue(condition.getRightDate().equals(
				new Date(Date.UTC(2014 - 1900, 1 - 1, 1, 12, 0, 0))));

		// time math expressions
		condition.parseRange("[NOW-1YEAR/DAY TO NOW/DAY+1DAY]");
		// no exceptions, i trust solr mathdate
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configs = SimpleConditionTest.readJsonResource(
				DateRangeContidionTest.class, "testDateRange.json");
		decodedResponses = XmlDoclistExtractorResponseAnalyzerTest
				.quickParseForTest("/org/apache/solr/kelvin/responseanalyzers/testDateRange.xml");
	}

	private JsonNode configs;
	private Map<String, Object> decodedResponses;

	private DateRangeCondition get(String name) throws Exception {
		DateRangeCondition v = new DateRangeCondition();
		v.configure(configs.get(name));
		return v;
	}

	public void testRange() throws Exception {
		DateRangeCondition v = get("range");
		Properties queryParams = new Properties();

		List<ConditionFailureTestEvent> errors = v.verifyConditions(null,
				queryParams, decodedResponses, null);
		assertEquals(4, errors.size());
		for (ConditionFailureTestEvent e : errors) {
			assertTrue(e instanceof ConditionsNotMetTestEvent);
		}
	}
}
