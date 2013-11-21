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
