package org.apache.solr.kelvin;

import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.kelvin.testcases.SimpleCondition;
import org.apache.solr.kelvin.testcases.ValueListCondition;

import com.fasterxml.jackson.databind.JsonNode;

public class SingletonConditionRegistry {
	private static SimpleClassRegistry<ICondition> registry= new SimpleClassRegistry<ICondition>(ICondition.class);
	
	public static void configure(JsonNode config) throws Exception {
		registry.configure(config);
		//default built in conditions
		Map<String,Class<?>> defaults = new TreeMap<String,Class<?>>();
		//defaults.put(null, SimpleCondition.class);
		defaults.put("", SimpleCondition.class);
		defaults.put("default", SimpleCondition.class);
		defaults.put("valueList", ValueListCondition.class);
		defaults.put("slug", ValueListCondition.class); //legacy
		registry.addMappingsFromClasses(defaults);
	}
	
	public static ICondition instantiate(JsonNode conf) throws Exception {
		String type="";
		if (conf.has("type"))
			type = conf.get("type").asText();
		//legacy
		if (conf.has("mode"))
			type = conf.get("mode").asText();
		ICondition ret = registry.instantiate(type);
		ret.configure(conf);
		return ret;
	}
}
