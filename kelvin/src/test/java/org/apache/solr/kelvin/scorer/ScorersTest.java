package org.apache.solr.kelvin.scorer;

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.solr.kelvin.Measure;
import org.apache.solr.kelvin.Scorer;
import org.apache.solr.kelvin.events.ConditionFailureTestEvent;
import org.apache.solr.kelvin.events.ConditionsNotMetTestEvent;
import org.apache.solr.kelvin.events.ExceptionTestEvent;
import org.apache.solr.kelvin.events.MissingFieldTestEvent;
import org.apache.solr.kelvin.events.TestCaseTestEvent;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ScorersTest extends TestCase {
	
	private static Measure peek(Scorer s  , String name) {
		for (Measure m: s.measureReport()) {
			if (m.getMeasureName().contains(name))
				return m;
		}
		return null;
	}
	
	public void testMissingResults() throws Exception {
		//actually tests TypedTestScorer
		MissingFieldScorer mfs = new MissingFieldScorer();
		mfs.configure(JsonNodeFactory.instance.objectNode());
		
		//must be 0
		String measureName = "misses";
		assertEquals( 0.0 ,peek(mfs,measureName).getValue() );
		
		mfs.update(null, new MissingFieldTestEvent(null,null,null,0));
		assertEquals( 1.0 ,peek(mfs,measureName).getValue() );
		mfs.update(null, new ExceptionTestEvent(null,null,null));
		// different base class, shoud not be counted
		assertEquals( 1.0 ,peek(mfs,measureName).getValue() );
		
		mfs.update(null, new MissingFieldTestEvent(null,null,null,0));
		mfs.update(null, new MissingFieldTestEvent(null,null,null,0));
		assertEquals( 3.0 ,peek(mfs,measureName).getValue() );
	}
	
	public void testSimpleScorer() throws Exception {
		SimpleScorer standard = new SimpleScorer();
		standard.configure(JsonNodeFactory.instance.objectNode());
		SimpleScorer unpositioned = new SimpleScorer();
		ObjectNode up = JsonNodeFactory.instance.objectNode();
		up.put("unpositionedErrors", true);
		unpositioned.configure(up);
		
		Object evt = new ConditionsNotMetTestEvent(null, null, null, 1);
		standard.update(null, evt);
		unpositioned.update(null, evt);
		
		evt = new ConditionFailureTestEvent(null, null, null); //unpositioned
		standard.update(null, evt);
		unpositioned.update(null, evt);
		standard.update(null, evt);
		unpositioned.update(null, evt);
		
		evt = new ConditionsNotMetTestEvent(null, null, null, 5);
		standard.update(null, evt);
		unpositioned.update(null, evt);
		
		assertEquals(2.0, peek(standard,"failures").getValue() );
		assertEquals(4.0, peek(unpositioned,"failures").getValue() );		
		
	}
	
	public void testTestScorer() throws Exception {
		TestScorer T = new TestScorer();
		T.configure(JsonNodeFactory.instance.objectNode());
		
		T.update(null, new TestCaseTestEvent(null, null, true));
		T.update(null, new TestCaseTestEvent(null, new Properties(), true));
		T.update(null, new TestCaseTestEvent(null, new Properties(), false));
		T.update(null, new TestCaseTestEvent(null, new Properties(), true));
		T.update(null, new TestCaseTestEvent(null, new Properties(), false));
		T.update(null, new TestCaseTestEvent(null, new Properties(), true));
		T.update(null, new TestCaseTestEvent(null, new Properties(), false));
		T.update(null, new TestCaseTestEvent(null, null, false));
		
		assertEquals(1.0, peek(T,"numTests").getValue());
		assertEquals(3.0, peek(T,"numQueries").getValue());
	}
	
	public void testEventCollector() throws Exception {
		EventCollectorScorer S = new EventCollectorScorer();
		S.configure(JsonNodeFactory.instance.objectNode());
		
		S.update(null, new NullPointerException());
		S.update(null, new NullPointerException());
		S.update(null, new NullPointerException());
		S.update(null, new IOException());
		S.update(null, new IOException());
		S.update(null, new ExceptionTestEvent(null, null, new IllegalAccessError()) );
		
		assertEquals(3,S.measureReport().size());
		assertEquals(3.0,peek(S,"NullPointerException").getValue());
		assertEquals(2.0,peek(S,"IOException").getValue());
		assertEquals(1.0,peek(S,"IllegalAccessError").getValue());
		
	}
}
