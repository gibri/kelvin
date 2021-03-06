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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.kelvin.ConfigurableLoader;
import org.apache.solr.kelvin.ICondition;
import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.SingletonConditionRegistry;
import org.apache.solr.kelvin.events.ConditionFailureTestEvent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class SimpleTestCase implements ITestCase {

	public Iterator<Properties> getQueryParameterIterator() {
		
		return  new Iterator<Properties>() {
			private Iterator<String> it = queries.iterator();
			public boolean hasNext() {
				return it.hasNext();
			}

			public Properties next() {
				Properties ret = new Properties();
				ret.put("q", it.next());
				return ret;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}};
	}

	public List<ConditionFailureTestEvent> verifyConditions(
			Properties queryParams, Map<String, Object> decodedResponses) {
		List<ConditionFailureTestEvent> ret = new ArrayList<ConditionFailureTestEvent>();
		for (ICondition condition: this.conditonList) {
			ret.addAll( condition.verifyConditions(this, queryParams, decodedResponses, null) );
		}
		return ret;
	}

	private List<String> queries=new LinkedList<String>();
	
	public void configure(JsonNode config) throws Exception {
		JsonNode type = config.path("type");
		if (type.isMissingNode() || "SimpleTestCase".equals(type.asText())) {
			readQueries(config, "k"); //legacy
			readQueries(config, "q");
			if (queries.size()==0)
				throw new Exception("missing q parameter for simple test case");
			if (! config.has("test") && ! config.has("conditions"))
				throw new Exception("missing conditions for simple test case");
			
			readConditions(config,"test"); //legacy
			readConditions(config,"conditions"); 
			
		} else throw new Exception("trying to configure on different test type");
		
	}

	private List<ICondition> conditonList = new LinkedList<ICondition>();
	
	private void readConditions(JsonNode config, String configName) throws Exception {
		if (config.has(configName)) {
			for (JsonNode condition :  (ArrayNode) ConfigurableLoader.assureArray(config.get(configName))) {
				try {
				conditonList.add(SingletonConditionRegistry.instantiate(condition));
				} catch (Exception e) {
					Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Error in config "+config.toString());
				}
			}
			if (conditonList.isEmpty())
				throw new Exception("missing conditions in simple test case");
		}
		
	}
	
	public static void mandatory(JsonNode node,String field) throws Exception {
		if (!node.has(field))
			throw new Exception ("missing "+field+" in simple test case");
	}

	private void readQueries(JsonNode config, String queryKey) {
		if (config.has(queryKey)) {
			JsonNode queryNode = config.get(queryKey);
			readStringArrayOpt(queryNode,queries);
		}
	}

	public static void readStringArrayOpt(JsonNode queryNode,  List<String> ret) {
		if (queryNode.isArray()) {
			for (JsonNode q : ((ArrayNode)queryNode)) {
				ret.add(q.asText());
			}
		} else {
			ret.add(
					queryNode.asText()
					);
		}
	}

}
