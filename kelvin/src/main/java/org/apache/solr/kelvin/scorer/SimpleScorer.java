package org.apache.solr.kelvin.scorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.solr.kelvin.Measure;
import org.apache.solr.kelvin.Scorer;
import org.apache.solr.kelvin.events.ConditionFailureTestEvent;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Every ConditionFailureTestEvent is an error, simply count them
 * 
 * @author giovannibricconi
 *
 */
public class SimpleScorer extends Scorer {

	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof ConditionFailureTestEvent) {
			ConditionFailureTestEvent failure = (ConditionFailureTestEvent) arg1;
			if (countUnpositionedErrors || failure.hasPosition()) {
				failureCount++;
			}
		}
	}

	/**
	 * should count only missing lines in results or should also count 
	 * global listing errors
	 * defaults to false
	 */
	private boolean countUnpositionedErrors = false;
	
	private int failureCount=0;
	
	@Override
	public void configure(JsonNode config) throws Exception {
		JsonNode countUnpositionedErrorsNode = config.path("unpositionedErrors");
		if ( ! countUnpositionedErrorsNode.isMissingNode() ) {
			countUnpositionedErrors = countUnpositionedErrorsNode.asBoolean();
		}
	}

	@Override
	public List<Measure> measureReport() {
		Measure count = new Measure("SimpleScorer","failires",failureCount);
		ArrayList<Measure> ret = new ArrayList<Measure>();
		ret.add(count);
		return ret;
	}

}
