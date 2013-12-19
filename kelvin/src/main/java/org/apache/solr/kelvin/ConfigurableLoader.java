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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public abstract class ConfigurableLoader implements IConfigurable {
	protected List<IConfigurable> resources = new ArrayList<IConfigurable>();

	protected Class<?> mustBe;
	public ConfigurableLoader(Class<?> _mustBe) {
		mustBe = _mustBe;
	}
	
	public void configure(JsonNode config) throws Exception {
		if (! config.isMissingNode()) {
			ArrayNode res = assureArray(config);
			
			Iterator<JsonNode> i = res.iterator();
			while (i.hasNext()) {
				JsonNode itemCfg = i.next();
				IConfigurable item = factory(itemCfg);
				item.configure(itemCfg);
				resources.add(item);
			}
		}
		addDefaults();
	}
	
	protected IConfigurable factory(JsonNode itemCfg) throws Exception {
		if (itemCfg.has("class")) {
			String className = itemCfg.get("class").asText();
			Class<?> target = Class.forName(className);
			IConfigurable object = (IConfigurable)(target.getConstructor().newInstance());
			if (! checkInstanceType(object)) {
				throw new Exception("Does not implements "+mustBe.getName());
			}
			return object;
		} else {
			throw new Exception("json node has no class attribute! provide custom ConfigurableLoader factory");
		}
	}


	protected boolean checkInstanceType(IConfigurable object) {
		if (mustBe != null) {
			return mustBe.isAssignableFrom(object.getClass());
		} else return true;
	}

	public static ArrayNode assureArray(JsonNode node) {
		if (node.isArray())
			return (ArrayNode) node;
		return new ArrayNode(null).add(node);
	}
	
	protected abstract void addDefaults();
}
