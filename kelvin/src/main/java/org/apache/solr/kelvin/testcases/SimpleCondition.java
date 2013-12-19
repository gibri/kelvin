/* 
    Copyright 2013 Giovanni Bricconi

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


 */
package org.apache.solr.kelvin.testcases;

import static org.apache.solr.kelvin.testcases.SimpleTestCase.mandatory;
import static org.apache.solr.kelvin.testcases.SimpleTestCase.readStringArrayOpt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
			throw new Exception("missing condition values");
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

	/** shourd manufacturer match Manufacturer */
	private boolean caseInsentiveFieldsName=true;
	
	private boolean hasField(JsonNode o, String field) {
		if (!caseInsentiveFieldsName)
			return o.has(field);
		Iterator<String> names = o.fieldNames();
		while (names.hasNext()) 
			if (field.equalsIgnoreCase(names.next()))
				return true;
		return false;
	}
	
	private JsonNode getField(JsonNode o, String field) {
		if (!caseInsentiveFieldsName)
			return o.get(field);
		Iterator<Entry<String, JsonNode>> i = o.fields();
		while (i.hasNext()) {
			Entry<String,JsonNode> e = i.next();
			if (field.equalsIgnoreCase(e.getKey()))
				return e.getValue();
		}
		throw new NullPointerException();
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
					if (! hasField(row,field)) {
						ret.add(new MissingFieldTestEvent(testCase, queryParams, "missing field from result - "+field,i));
						allFields=false;
					}
				}
				if (allFields) {
					String allText="";
					for (String field : fields) {
						JsonNode fieldValue = getField(row,field);
						if (fieldValue.isArray()) {
							for (int j=0;j<fieldValue.size();j++) {
								allText=allText+" "+fieldValue.get(j);
							}
							//checkAllWords(testCase, queryParams, ret, i,
							//		allText);
							
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
			ret.add(new ConditionFailureTestEvent(testCase, queryParams, String.format("not all words %s found in [%s]",correctValuesList.toString(),stringFieldValue),i));
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
