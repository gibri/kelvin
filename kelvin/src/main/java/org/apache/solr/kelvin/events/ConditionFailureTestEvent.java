/* 
    Copyright 2013 Giovanni Bricconi

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


 */
package org.apache.solr.kelvin.events;

import java.util.Properties;

import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.TestEvent;

public class ConditionFailureTestEvent extends TestEvent {

	@Override
	public String toString() {
		if (hasPosition())
			// Humans like first result is number one.
			return String.format("%s position %s description %s", this.getParameters(), this.position+1, this.description);
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
