package org.apache.solr.kelvin.scorer;

import org.apache.solr.kelvin.events.MissingFieldTestEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class MissingFieldScorer extends TypedTestScorer {
	@Override
	public void configure(JsonNode config) throws Exception {
		super.configure( MissingFieldTestEvent.class );
	}
}
