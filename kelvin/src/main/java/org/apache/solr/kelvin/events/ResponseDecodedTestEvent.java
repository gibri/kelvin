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

import java.util.Map;
import java.util.Properties;

import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.TestEvent;

public class ResponseDecodedTestEvent extends TestEvent {

	private Map<String, Object> responseFragments;

	public ResponseDecodedTestEvent(ITestCase testCase, Properties parameters,
			Map<String, Object> _responseFragments) {
		super(testCase, parameters);
		responseFragments = _responseFragments;

	}

	public Map<String, Object> getResponseFragments() {
		return responseFragments;
	}
}
