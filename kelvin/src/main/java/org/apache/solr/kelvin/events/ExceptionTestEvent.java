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
