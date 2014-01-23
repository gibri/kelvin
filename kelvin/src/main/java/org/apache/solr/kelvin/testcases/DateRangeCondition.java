/* 
    Copyright 2014 Giovanni Bricconi

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.kelvin.ICondition;
import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.events.ConditionFailureTestEvent;
import org.apache.solr.kelvin.events.ConditionsNotMetTestEvent;
import org.apache.solr.kelvin.events.MissingFieldTestEvent;
import org.apache.solr.kelvin.events.MissingResultTestEvent;
import org.apache.solr.kelvin.responseanalyzers.XmlDoclistExtractorResponseAnalyzer;
import org.apache.solr.schema.DateField;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class DateRangeCondition extends ListingRowCondition implements ICondition  {

	private String fieldToCheck;
	private String dateRangeString;
	private boolean leftInclusive;
	private boolean rightInclusive;
	private Date leftDate;
	private Date rightDate;
	
	public void configure(JsonNode condition) throws Exception {
		parseLen(condition);
		parseReverseConditions(condition);
	
		mandatory(condition, "field");
		fieldToCheck = condition.get("field").asText();
	
		mandatory(condition,"dateRange");
		dateRangeString = condition.get("dateRange").asText();
		parseRange(dateRangeString);
		
	}
	
	private static Pattern rangePattern = Pattern.compile("\\s*([\\[{])([-+0-9a-z:./])+\\s+to\\s+([-+0-9a-z:./])+([\\]})]\\s*",Pattern.CASE_INSENSITIVE) ;
	
	private void parseRange(String dateRangeString) throws Exception {
		 Matcher m = rangePattern.matcher(dateRangeString);
		 if (m.matches()){
			 leftInclusive = "[".equals(m.group(1));
			 rightInclusive = "]".equals(m.group(4));
			 leftDate = parseDate(m.group(2));
			 rightDate = parseDate(m.group(3));
		 } else
			 throw new Exception("Bad date range configuration "+dateRangeString);
	}
	
	private Date parseDate(String s) throws Exception {
		DateField d  = new DateField();
		return d.parseMath(null, s);
	}

	public List<ConditionFailureTestEvent> verifyConditions(ITestCase testCase,
			Properties queryParams, Map<String, Object> decodedResponses,
			Object testSpecificArgument) {
		List<ConditionFailureTestEvent> ret = new ArrayList<ConditionFailureTestEvent>();
		ArrayNode results = new ArrayNode(null);
		if (decodedResponses.containsKey(XmlDoclistExtractorResponseAnalyzer.DOC_LIST)){
			results = (ArrayNode) decodedResponses.get(XmlDoclistExtractorResponseAnalyzer.DOC_LIST);
		}
		int lengthToCheck = getLengthToCheck( results);
		for (int i=0; i<lengthToCheck; i++) {
			if (results.size()<=i) {
				ret.add(new MissingResultTestEvent( testCase, queryParams, "result set too short",i));
			} else {
				JsonNode row = results.get(i);
				if (! hasField(row,fieldToCheck)) {
					ret.add(new MissingFieldTestEvent(testCase, queryParams, "missing field from result - "+fieldToCheck,i));
				} else  {
					String fieldValue = getField(row,fieldToCheck).asText();
					Date dateReturned = null;
					try {
						dateReturned = parseDate(fieldValue);
					} catch (Exception e) {
						ret.add(new ConditionsNotMetTestEvent(testCase, queryParams, 
								String.format("date parsing error %s", fieldValue), i));
						continue;
					}
					boolean error = false;
					if (leftInclusive && leftDate.compareTo(dateReturned)>0)
						error = true;
					if (!leftInclusive && leftDate.compareTo(dateReturned)>=0)
						error = true;
					if (rightInclusive && rightDate.compareTo(dateReturned)<0)
						error = true;
					if (!rightInclusive && rightDate.compareTo(dateReturned)<=0)
						error = true;
					if (error && ! this.reverseConditions )
						ret.add(new ConditionsNotMetTestEvent(testCase, queryParams, 
								String.format("date range error %s not in %s", fieldValue,this.dateRangeString), i));
					if (!error && this.reverseConditions )
						ret.add(new ConditionsNotMetTestEvent(testCase, queryParams, 
								String.format("reverse date range error %s not in %s", fieldValue,this.dateRangeString), i));
				}
			}
		}
		return ret;
	}

}
