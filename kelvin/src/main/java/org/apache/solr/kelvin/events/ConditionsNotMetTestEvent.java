package org.apache.solr.kelvin.events;

import java.util.Properties;

import org.apache.solr.kelvin.ITestCase;

public class ConditionsNotMetTestEvent extends ConditionFailureTestEvent {
	public ConditionsNotMetTestEvent(ITestCase testCase, Properties parameters,
			String _description, int _position) {
		super(testCase, parameters, _description, _position);
	}

}
