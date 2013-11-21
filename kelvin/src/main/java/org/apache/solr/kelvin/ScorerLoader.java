package org.apache.solr.kelvin;

import java.util.Iterator;
import java.util.Observable;

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
