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
