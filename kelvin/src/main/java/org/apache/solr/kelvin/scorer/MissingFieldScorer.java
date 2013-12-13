package org.apache.solr.kelvin.scorer;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.kelvin.Measure;
import org.apache.solr.kelvin.events.MissingFieldTestEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class MissingFieldScorer extends TypedTestScorer {
	@Override
	public void configure(JsonNode config) throws Exception {
		super.configure( MissingFieldTestEvent.class );
	}
	
	@Override
	public List<Measure> measureReport() {
		List<Measure> ret = new ArrayList<Measure>();
		ret.add(new Measure("MissingFieldScorer", "misses", counter));
		return ret;
	}
}
