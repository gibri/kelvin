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
package org.apache.solr.kelvin;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.kelvin.responseanalyzers.XmlDoclistExtractorResponseAnalyzer;
import org.apache.solr.kelvin.responseanalyzers.XmlResponseAnalyzer;
import org.apache.solr.kelvin.scorer.MissingResultScorer;
import org.apache.solr.kelvin.scorer.SimpleScorer;
import org.apache.solr.kelvin.scorer.TestScorer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class ResponseAnalyzerLoader extends ConfigurableLoader {

	public ResponseAnalyzerLoader() {
		super(ResponseAnalyzer.class);
	}
	
	@Override
	protected void addDefaults() {
		if (this.resources.size() == 0) {
			XmlResponseAnalyzer  xmlResponseAnalyzer = new XmlResponseAnalyzer();
			
			resources.add(new XmlResponseAnalyzer());
			resources.add(new XmlDoclistExtractorResponseAnalyzer());
			JsonNode emptyConfig = JsonNodeFactory.instance.objectNode();
			for (IConfigurable r : resources) {
				try {
					r.configure(emptyConfig);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public List<ResponseAnalyzer> getAnalyzers() {
		List<ResponseAnalyzer> ret = new ArrayList<ResponseAnalyzer>();
		for (IConfigurable o : this.resources) {
			ret.add((ResponseAnalyzer)o);
		}
		return ret;
	}

}
