package org.apache.solr.kelvin;

import java.util.Iterator;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class QueryPerformerLoader extends ConfigurableLoader {

	public QueryPerformerLoader() {
		super(QueryPerformer.class);
	}

	@Override
	protected void addDefaults() {
		if (resources.size() == 0) {
			URLQueryPerformer qp = new URLQueryPerformer();
			try {
				qp.configure(JsonNodeFactory.instance.objectNode());
				resources.add(qp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Iterator<QueryPerformer> iterator() {
		return new Iterator<QueryPerformer>() {

			private Iterator<IConfigurable> i = resources.iterator();
			public boolean hasNext() {
				return i.hasNext();
			}

			public QueryPerformer next() {
				return (QueryPerformer)i.next();
			}

			public void remove() {
				throw new UnsupportedOperationException ();
				
			}
			
		};
	}
}
