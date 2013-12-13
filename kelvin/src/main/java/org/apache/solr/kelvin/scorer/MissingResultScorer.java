package org.apache.solr.kelvin.scorer;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.kelvin.Measure;
import org.apache.solr.kelvin.events.MissingResultTestEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class MissingResultScorer extends TypedTestScorer {

	@Override
	public void configure(JsonNode config) throws Exception {
		super.configure( MissingResultTestEvent.class );
	}

	@Override
	public List<Measure> measureReport() {
		List<Measure> ret = new ArrayList<Measure>();
		ret.add(new Measure("MissingResultScorer", "misses", counter));
		return ret;
	}
}
