package org.apache.solr.kelvin.scorer;

import org.apache.solr.kelvin.events.MissingResultTestEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class MissingResultScorer extends TypedTestScorer {

	@Override
	public void configure(JsonNode config) throws Exception {
		super.configure( MissingResultTestEvent.class );
	}

}
