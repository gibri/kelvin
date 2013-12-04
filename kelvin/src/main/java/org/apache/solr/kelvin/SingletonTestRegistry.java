package org.apache.solr.kelvin;

import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.kelvin.testcases.SimpleTestCase;

import com.fasterxml.jackson.databind.JsonNode;

public class SingletonTestRegistry {
	private static SimpleClassRegistry<ITestCase> registry= new SimpleClassRegistry<ITestCase>(ITestCase.class);
	
	public static void configure(JsonNode config) throws Exception {
		registry.configure(config);
		//default built in conditions
		Map<String,Class<?>> defaults = new TreeMap<String,Class<?>>();
		//defaults.put(null, SimpleTestCase.class);
		defaults.put("", SimpleTestCase.class);
		defaults.put("default", SimpleTestCase.class);
		registry.addMappingsFromClasses(defaults);
	}
	
	public static ITestCase instantiate(JsonNode conf) throws Exception {
		String type = "default";
		if (conf.has("type"))
			type = conf.get("type").asText();
		return (ITestCase) registry.instantiate(type);
	}

}
