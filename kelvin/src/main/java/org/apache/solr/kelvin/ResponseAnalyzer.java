package org.apache.solr.kelvin;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface ResponseAnalyzer extends IConfigurable {

	/**
	 * given raw data obtained by IQueryPerformer (usually a big string with all the response)
	 * and other analysis results return a new data subset to be
	 * verified by test cases
	 * 
	 * @param previousResponses is useful, if a previous analyzer is a Dom tree or a json object
	 * it is easier to extract interesting data. If The analysis is ok put the result inside the map!
	 * @throws exception if the data to decode is illegally formatted
	 */
	public void decode(Map<String,Object> previousResponses) throws Exception;
	
	/**
	 * read configuration and prepare to work
	 * @param config
	 * @throws Exception if something is missing in the configuration
	 */
	public void configure(JsonNode config) throws Exception ;

}
