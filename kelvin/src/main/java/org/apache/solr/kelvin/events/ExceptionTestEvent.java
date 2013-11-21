package org.apache.solr.kelvin.events;

import java.util.Properties;

import org.apache.solr.kelvin.ITestCase;
import org.apache.solr.kelvin.TestEvent;

/**
 * Sent when a runtime error occurs, should be due only to target system
 * error but can be due to internal Kelvin errors that are not trapped
 * by analyzers or query performers
 * 
 * @author giovannibricconi
 *
 */
public class ExceptionTestEvent extends TestEvent {

	@Override
	public String toString() {
		StackTraceElement [] els = exception.getStackTrace();
		StringBuilder msg = new StringBuilder();
		msg.append(getParameters().toString() + "\n");
		for (StackTraceElement e : els) {
			msg.append('\t').append(e.toString()).append('\n');
		}
		return  msg.toString();
	}

	private Throwable exception=null;

	public Throwable getException() {
		return exception;
	}
	
	public ExceptionTestEvent(ITestCase testCase, Properties parameters, Throwable t) {
		super(testCase, parameters);
		exception = t;
	}
}
