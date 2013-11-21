package org.apache.solr.kelvin;

import java.util.List;
import java.util.Observer;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An observer of Test events
 * 
 * implements Observer methods to track test progress
 * 
 * provide formatting for reporting errors
 * 
 * @author giovannibricconi
 *
 */
public abstract class Scorer implements Observer, IConfigurable {
	
	/**
	 * Have a chanche of reading configuration
	 * 
	 * @throws Exception if misconfigured
	 */
	public abstract void configure(JsonNode config) throws Exception;
	
	/**
	 * called at test ends to print a simple description of results
	 * @return
	 */
	public String simpleReport() {
		List<Measure> measures = measureReport();
		StringBuilder sb = new StringBuilder();
		for (Measure m: measures) {
			sb.append(m.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * called at test ends to print a simple description of results
	 * @return
	 */
	public abstract List<Measure> measureReport();
}
