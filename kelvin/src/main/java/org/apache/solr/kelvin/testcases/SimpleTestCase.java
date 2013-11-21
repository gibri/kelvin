package org.apache.solr.kelvin.testcases;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
		// TODO Auto-generated method stub
		return null;
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
			for (JsonNode condition :  (ArrayNode) config.get(configName)) {
				conditonList.add(SingletonConditionRegistry.instantiate(condition));
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
