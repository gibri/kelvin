package org.apache.solr.kelvin.scorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.solr.kelvin.Measure;
import org.apache.solr.kelvin.Scorer;

import com.fasterxml.jackson.databind.JsonNode;

public class TypedTestScorer extends Scorer {

	private long counter = 0;
	protected String type;
	protected Class<?> typeClass;

	public TypedTestScorer() {}
	public TypedTestScorer(Class<?> _type) {
		typeClass = _type;
		type = _type.getName();
	}
	
	public void update(Observable arg0, Object arg1) {
		if (typeClass.isAssignableFrom(arg1.getClass()))
			counter++;
	}

	@Override
	public void configure(JsonNode config) throws Exception {
		type = config.get("scorerClass").asText();
		typeClass = Class.forName(type);
	}
	
	public void configure(Class<?> c) throws Exception {
		this.typeClass = c;
		this.type=c.getName();
	}

	@Override
	public List<Measure> measureReport() {
		List<Measure> ret = new ArrayList<Measure>();
		ret.add(new Measure(this.getClass().getName(), type, counter));
		return ret;
	}
	

}
