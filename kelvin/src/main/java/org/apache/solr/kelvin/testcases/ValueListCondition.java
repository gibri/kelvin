package org.apache.solr.kelvin.testcases;

import static org.apache.solr.kelvin.testcases.SimpleTestCase.mandatory;
import static org.apache.solr.kelvin.testcases.SimpleTestCase.readStringArrayOpt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.kelvin.ConfigurableLoader;
import org.apache.solr.kelvin.ICondition;
import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.events.ConditionFailureTestEvent;
import org.apache.solr.kelvin.events.ConditionsNotMetTestEvent;
import org.apache.solr.kelvin.events.MissingFieldTestEvent;
import org.apache.solr.kelvin.events.MissingResultTestEvent;
import org.apache.solr.kelvin.responseanalyzers.XmlDoclistExtractorResponseAnalyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ValueListCondition  implements ICondition {
	private String field;
	private int length;
	private List<String> correctValuesList;
	
	private boolean legacy = false;
	
	public String getField() {
		return field;
	}

	public int getLength() {
		return length;
	}

	public List<String> getCorrectValuesList() {
		return Collections.unmodifiableList(correctValuesList);
	}

	public void init(int _len, String _field, List<String> _values) {
		field = _field;
		length=_len;
		correctValuesList=_values;
	}

	public void configure(JsonNode condition) throws Exception {
		mandatory(condition,"len");
		int len = condition.get("len").asInt();
		String fieldName="";
		List<String> values = new LinkedList<String>();
		if (condition.has("mode") && condition.get("mode").asText().equals("slug")) {
			//legacy
			fieldName = "slug_preferenziale";	
			readStringArrayOpt(condition.get("slug"),values);
			legacy = true;
		} else {
			mandatory(condition, "field");
			fieldName = condition.get("field").asText();
			if (condition.has("values"))
				readStringArrayOpt(condition.get("values"),values);
		}
		if (values.size()==0)
			throw new Exception("missin condition values");
		init(len, fieldName, values);
	}

	public List<ConditionFailureTestEvent> verifyConditions(ITestCase testCase,
			Properties queryParams, Map<String, Object> decodedResponses,
			Object testSpecificArgument) {
		List<ConditionFailureTestEvent> ret = new ArrayList<ConditionFailureTestEvent>();
		ArrayNode results = new ArrayNode(null);
		if (decodedResponses.containsKey(XmlDoclistExtractorResponseAnalyzer.DOC_LIST)){
			results = (ArrayNode) decodedResponses.get(XmlDoclistExtractorResponseAnalyzer.DOC_LIST);
		}
		for (int i=0; i<length; i++) {
			if (results.size()<=i) {
				ret.add(new MissingResultTestEvent( testCase, queryParams, "result set too short",i));
			} else {
				JsonNode row = results.get(i);
				if (!row.has(field)) {
					ret.add(new MissingFieldTestEvent(testCase, queryParams, "missing field from result",i));
				} else {
					JsonNode fieldValue = row.get(field);
					fieldValue = ConfigurableLoader.assureArray(fieldValue);
					boolean found = false;
					for (int j=0;j<fieldValue.size();j++) {
						if (this.correctValuesList.contains( fieldValue.get(j).asText() ))
						{ found=true; break; }
						else if (legacy)  {
							for (String cond : correctValuesList) {
								if (cond.endsWith("*")) {
									if (fieldValue.get(j).asText().startsWith(cond.substring(0, cond.length()-1)))
									{ found=true; break; }
								}
							}
						}
					}
					if (!found)
						ret.add(new ConditionsNotMetTestEvent(testCase, queryParams, "unexpected vaule", i));
				}
			}
		}
		return ret;
	}

}
