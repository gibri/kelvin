package org.apache.solr.kelvin.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.Measure;

public class MissingFieldTestEvent extends ConditionFailureTestEvent {
	public MissingFieldTestEvent(ITestCase testCase, Properties parameters,
			String _description, int _position) {
		super(testCase, parameters, _description, _position);
	}

}
