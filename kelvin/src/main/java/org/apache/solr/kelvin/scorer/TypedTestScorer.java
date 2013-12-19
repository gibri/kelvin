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

import com.fasterxml.jackson.databind.JsonNode;

public class TypedTestScorer extends Scorer {

	protected long counter = 0;
	protected String type;
	protected Class<?> typeClass;

	public TypedTestScorer() {}
	public TypedTestScorer(Class<?> _type) {
		typeClass = _type;
		type = _type.getName();
	}
	
	public void update(Observable arg0, Object arg1) {
		if (typeClass.isAssignableFrom(arg1.getClass()))
			counter++;
	}

	@Override
	public void configure(JsonNode config) throws Exception {
		type = config.get("scorerClass").asText();
		typeClass = Class.forName(type);
	}
	
	public void configure(Class<?> c) throws Exception {
		this.typeClass = c;
		this.type=c.getName();
	}

	@Override
	public List<Measure> measureReport() {
		List<Measure> ret = new ArrayList<Measure>();
		ret.add(new Measure(this.getClass().getName(), type, counter));
		return ret;
	}
	

}
