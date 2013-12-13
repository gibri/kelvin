package org.apache.solr.kelvin.scorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.kelvin.ConfigurableLoader;
import org.apache.solr.kelvin.Measure;
import org.apache.solr.kelvin.Scorer;
import org.apache.solr.kelvin.TestEvent;
import org.apache.solr.kelvin.events.ExceptionTestEvent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class EventCollectorScorer extends Scorer {

	private Map<Class<?>, List<Object>> collectedEvents = new HashMap<Class<?>, List<Object>>();
	
	private Set<String> excludeList = new TreeSet<String>();
	
	public void update(Observable o, Object arg) {
		if (excludeList.contains(arg.getClass().getName()))
			return; // do nothing is excluded
		if (arg instanceof ExceptionTestEvent) {
			arg = ((ExceptionTestEvent)arg).getException();
		}
		Class<? extends Object> eventClass = arg.getClass();
		if (! collectedEvents.containsKey(eventClass)) {
			collectedEvents.put(eventClass, new ArrayList<Object>());
		}
		collectedEvents.get(eventClass).add(arg);
	}

	@Override
	public void configure(JsonNode config) throws Exception {
		if (! config.path("excludes").isMissingNode() ) {
			ArrayNode excludes = ConfigurableLoader.assureArray(config.path("excludes"));
			for (int i=0; i<excludes.size(); i++) 
				excludeList.add(excludes.get(i).asText());
		} else {
			//defaults
			excludeList.add("org.apache.solr.kelvin.events.ResponseDecodedTestEvent");
			excludeList.add("org.apache.solr.kelvin.events.TestCaseTestEvent");
			//excludeList.add("org.apache.solr.kelvin.events.MissingFieldTestEvent");
			//excludeList.add("org.apache.solr.kelvin.events.MissingResultTestEvent");
		}
	}

	@Override
	public List<Measure> measureReport() {
		ArrayList<Measure> ret = new ArrayList<Measure>();
		for (Entry<Class<?>, List<Object>> entry : collectedEvents.entrySet() ) {
			ret.add(new Measure(this.getClass().getName(), entry.getKey().getName(), entry.getValue().size()));
		}
		return ret;
	}
	
	public String simpleReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("Collected Events =======================================================\n");
		for (Class<?> type : collectedEvents.keySet()) {
			sb.append(type.getName()).append('\n');
			for (Object event : collectedEvents.get(type)) {
				sb.append(event.toString()).append('\n');
			}
		}
		sb.append("End Collected Events ===================================================\n");

		return sb.toString();
	}
}
