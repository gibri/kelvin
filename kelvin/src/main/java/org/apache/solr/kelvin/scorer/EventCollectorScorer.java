package org.apache.solr.kelvin.scorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;

import org.apache.solr.kelvin.Measure;
import org.apache.solr.kelvin.Scorer;
import org.apache.solr.kelvin.TestEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class EventCollectorScorer extends Scorer {

	private Map<Class<?>, List<TestEvent>> collectedEvents = new HashMap<Class<?>, List<TestEvent>>();
	
	public void update(Observable o, Object arg) {
		Class<? extends Object> eventClass = arg.getClass();
		if (! collectedEvents.containsKey(eventClass)) {
			collectedEvents.put(eventClass, new ArrayList<TestEvent>());
		}
		collectedEvents.get(eventClass).add((TestEvent)arg);
	}

	@Override
	public void configure(JsonNode config) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Measure> measureReport() {
		ArrayList<Measure> ret = new ArrayList<Measure>();
		for (Entry<Class<?>, List<TestEvent>> entry : collectedEvents.entrySet() ) {
			ret.add(new Measure(this.getClass().getName(), entry.getKey().getName(), entry.getValue().size()));
		}
		return ret;
	}
	
	public String simpleReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("Collected Events =======================================================\n");
		for (Class<?> type : collectedEvents.keySet()) {
			sb.append(type.getName()).append('\n');
			for (TestEvent event : collectedEvents.get(type)) {
				sb.append(event.toString()).append('\n');
			}
		}
		sb.append("End Collected Events ===================================================\n");

		return sb.toString();
	}
}
