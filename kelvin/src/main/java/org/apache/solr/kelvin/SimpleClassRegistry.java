package org.apache.solr.kelvin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

public class SimpleClassRegistry<T> {
	public SimpleClassRegistry() {	
	}
	
	private Map<String , Class<?>> map = new HashMap<String, Class<?>>();

	public void addMappings(Map<String,String> mappings) throws ClassNotFoundException {
		for (Map.Entry<String, String> entry :mappings.entrySet()){
			Class<?> target = Class.forName(entry.getValue());
			Class<?> required = this.getClass().getTypeParameters()[0].getClass();
			if (!required.isAssignableFrom(target)) {
				throw new ClassCastException(String.format("%s does not implements %s",target.getName(),required.getName()));
			}
		     
			map.put(entry.getKey(),
					target
					);
		}
	}
	
	public void addMappingsFromClasses(Map<String,Class<?>> mappings) throws ClassNotFoundException {
		for (Map.Entry<String, Class<?>> entry :mappings.entrySet()){
			Class<?> target = entry.getValue();
			Class<?> required = this.getClass().getTypeParameters()[0].getClass();
			if (!required.isAssignableFrom(target)) {
				throw new ClassCastException(String.format("%s does not implements %s",target.getName(),required.getName()));
			}
		     
			map.put(entry.getKey(),
					target
					);
		}
	}
	
	public boolean canMap(String name) { return map.containsKey(name); }
	
	public T instantiate(String name) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return  (T)(map.get(name).getConstructor().newInstance());
	}
	
	public void configure(JsonNode config) throws Exception {
		if (!config.isMissingNode()) {
			Iterator<Entry<String,JsonNode>> it = config.fields();
			Map<String,String> mappings = new HashMap<String, String>();
			while (it.hasNext()) {
				Entry<String,JsonNode> entry = it.next();
				mappings.put(entry.getKey(),
						entry.getValue().asText() );
			}
			this.addMappings(mappings);
		}
	}
}
