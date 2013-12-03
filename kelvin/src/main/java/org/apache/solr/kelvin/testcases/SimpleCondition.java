package org.apache.solr.kelvin.testcases;

import static org.apache.solr.kelvin.testcases.SimpleTestCase.mandatory;
import static org.apache.solr.kelvin.testcases.SimpleTestCase.readStringArrayOpt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.solr.kelvin.ConfigurableLoader;
import org.apache.solr.kelvin.ICondition;
import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.events.ConditionFailureTestEvent;
import org.apache.solr.kelvin.events.MissingFieldTestEvent;
import org.apache.solr.kelvin.events.MissingResultTestEvent;
import org.apache.solr.kelvin.responseanalyzers.XmlDoclistExtractorResponseAnalyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class SimpleCondition implements ICondition {
	private List<String> fields;
	private int length;
	private List<String> correctValuesList = new ArrayList<String>();
	
	public void init(int _len, List<String> _fields, List<String> _values) {
		fields = _fields;
		length=_len;
		correctValuesList=_values;
	}

	public void configure(JsonNode condition) throws Exception {
		mandatory(condition,"len");
		int len = condition.get("len").asInt();
		List<String> values = new LinkedList<String>();
		mandatory(condition, "field");
		ArrayList<String> realFiels = new ArrayList<String>();
		ArrayNode fieldsArray = ConfigurableLoader.assureArray(condition.get("field"));
		for (int i=0; i<fieldsArray.size(); i++) {
			if (fieldsArray.get(i).asText().contains("+")) {
				//legacy
				for (String f : fieldsArray.get(i).asText().split("[+]"))
					realFiels.add(f);
			}
			else {
				realFiels.add(fieldsArray.get(i).asText());
			}
		}
		if (condition.has("args"))
			readStringArrayOpt(condition.get("args"),values); //legacy
		if (condition.has("values"))
			readStringArrayOpt(condition.get("values"),values);
		if (values.size()==0)
			throw new Exception("missin condition values");
		init(len, realFiels, values);
	}

	public List<String> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public int getLength() {
		return length;
	}

	public List<String> getCorrectValuesList() {
		return Collections.unmodifiableList(correctValuesList);
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
				boolean allFields = true;
				for (String field : this.fields) {
					if (!row.has(field)) {
						ret.add(new MissingFieldTestEvent(testCase, queryParams, "missing field from result",i));
						allFields=false;
					}
				}
				if (allFields) {
					String allText="";
					for (String field : fields) {
						JsonNode fieldValue = row.get(field);
						if (fieldValue.isArray()) {
							for (int j=0;j<fieldValue.size();j++) {
								allText=allText+" "+fieldValue.get(j);
							}
							checkAllWords(testCase, queryParams, ret, i,
									allText);
							
						} else {
							String stringFieldValue = fieldValue.asText();
							allText =allText+" "+stringFieldValue;
						}
					}
					checkAllWords(testCase, queryParams, ret, i,
							allText);
				}
			}
		}
		return ret;
	}

	private void checkAllWords(ITestCase testCase, Properties queryParams,
			List<ConditionFailureTestEvent> ret, int i, String stringFieldValue) {
		boolean failure = false;
		for (String value:correctValuesList) {
			if (! stringFieldValue.toLowerCase().contains(value.toLowerCase())) {
				//legacy 
				if (! checkRegexp(value, stringFieldValue.toLowerCase())) {
					failure =true;
					break;
				}
			}
		}
		if (failure)
			ret.add(new ConditionFailureTestEvent(testCase, queryParams, "not all words found",i));
	}

	private boolean checkRegexp(String re, String lowerCase) {
		try {
			Pattern p = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
			return p.matcher(lowerCase).find();
		} catch (Exception e ) {
			
		}
		return false;
	}

}
