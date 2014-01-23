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
package org.apache.solr.kelvin;

import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.kelvin.testcases.DateRangeCondition;
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
		defaults.put("dateRange", DateRangeCondition.class);
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
