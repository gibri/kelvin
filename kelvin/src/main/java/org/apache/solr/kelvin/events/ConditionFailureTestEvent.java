package org.apache.solr.kelvin.events;

import java.util.Properties;

import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.TestEvent;

public class ConditionFailureTestEvent extends TestEvent {

	@Override
	public String toString() {
		if (hasPosition())
			return String.format("%s position %s description %s", this.getParameters(), this.position, this.description);
		else 
			return String.format("%s description %s", this.getParameters(), this.description);
	}

	private Integer position;
	private String description;

	public ConditionFailureTestEvent(ITestCase testCase, Properties parameters,
			String _description) {
		super(testCase, parameters);
		description = _description;
	}

	public ConditionFailureTestEvent(ITestCase testCase, Properties parameters,
			String _description, int _position) {
		super(testCase, parameters);
		description = _description;
		position = _position;
	}

	public boolean hasPosition() {
		return position != null;
	}

	public int getPosition() {
		return position.intValue();
	}

	public String getDescription() {
		return description;
	}

}
