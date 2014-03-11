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

import java.util.Iterator;
import java.util.Observable;

import org.apache.solr.kelvin.scorer.EventCollectorScorer;
import org.apache.solr.kelvin.scorer.MissingFieldScorer;
import org.apache.solr.kelvin.scorer.MissingResultScorer;
import org.apache.solr.kelvin.scorer.SimpleScorer;
import org.apache.solr.kelvin.scorer.TestScorer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class ScorerLoader extends ConfigurableLoader {

	@Override
	protected void addDefaults() {
		if (resources.size() == 0) {
			resources.add(new MissingResultScorer());
			resources.add(new TestScorer());
			resources.add(new SimpleScorer());
			resources.add(new EventCollectorScorer());
			resources.add(new MissingFieldScorer());
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

	public void registerScorersAsObservers(Observable target) {
		for (IConfigurable s: resources) {
			Scorer scorer =(Scorer)s;
			target.addObserver(scorer);
		}
	}
	
	public ScorerLoader() { super(Scorer.class); }
	
	public Iterator<Scorer> iterator() {
		return new Iterator<Scorer>() {

			private Iterator<IConfigurable> i = resources.iterator();
			public boolean hasNext() {
				return i.hasNext();
			}

			public Scorer next() {
				return (Scorer)i.next();
			}

			public void remove() {
				throw new UnsupportedOperationException ();
				
			}
			
		};
	}
 
}
