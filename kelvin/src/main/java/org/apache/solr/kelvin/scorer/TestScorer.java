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
package org.apache.solr.kelvin.scorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.solr.kelvin.Measure;
import org.apache.solr.kelvin.Scorer;
import org.apache.solr.kelvin.events.TestCaseTestEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class TestScorer extends Scorer {

	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof  TestCaseTestEvent) {
			TestCaseTestEvent event = (TestCaseTestEvent) arg1;
			if (countQueries && event.isStarting() && event.isSingleQuery())
				nQueries++;
			else if (countTests && event.isStarting())
				nTests++;
		}
		
	}

	private boolean countQueries = true;
	private boolean countTests = true;
	private int nQueries = 0;
	private int nTests = 0;
	
	@Override
	public void configure(JsonNode config) throws Exception {
		JsonNode countQueriesNode = config.path("countQueries");
		JsonNode countTestsNode = config.path("countTests");
		if ( ! countQueriesNode.isMissingNode() ) {
			countQueries = countQueriesNode.asBoolean();
		}
		if ( ! countTestsNode.isMissingNode() ) {
			countTests = countTestsNode.asBoolean();
		}
	}

	@Override
	public List<Measure> measureReport() {
		ArrayList<Measure> ret = new ArrayList<Measure>();
		if (countQueries) {
			ret.add(new Measure("TestCounter","numQueries",nQueries));
		}
		if (countTests) {
			ret.add(new Measure("TestCounter","numTests",nTests));
		}
		return ret;
	}

}
