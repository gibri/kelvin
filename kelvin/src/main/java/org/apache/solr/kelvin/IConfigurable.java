package org.apache.solr.kelvin;

import com.fasterxml.jackson.databind.JsonNode;

public interface IConfigurable {
	public void configure(JsonNode config) throws Exception;
}
