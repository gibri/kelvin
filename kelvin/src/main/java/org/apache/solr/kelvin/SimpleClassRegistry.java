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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

public class SimpleClassRegistry<T> {
	public SimpleClassRegistry(Class<T> _targetClass) {
		requiredClass = _targetClass;
	}
	
	private Class<T> requiredClass;
	
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
			if (!requiredClass.isAssignableFrom(target)) {
				throw new ClassCastException(String.format("%s does not implements %s",target.getName(),requiredClass.getName()));
			}
		     
			map.put(entry.getKey(),
					target
					);
		}
	}
	
	public boolean canMap(String name) { return map.containsKey(name); }
	
	public T instantiate(String name) throws Exception {
		try {
			return  (T)(map.get(name).getConstructor().newInstance());
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"illegal argument "+name,e);
			throw e;
		}
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
