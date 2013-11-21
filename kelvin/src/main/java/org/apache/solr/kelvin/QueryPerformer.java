package org.apache.solr.kelvin;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.solr.kelvin.events.ConditionFailureTestEvent;
import org.apache.solr.kelvin.events.ExceptionTestEvent;
import org.apache.solr.kelvin.events.ResponseDecodedTestEvent;
import org.apache.solr.kelvin.events.TestCaseTestEvent;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class QueryPerformer extends Observable implements Closeable, IConfigurable {

	/**
	 * given query parameters and test case performs actual test
	 * 
	 * @param testCase
	 * @param queryParams
	 * @return the raw data obtained, this object will be routed to
	 *         ResponseAnalyzers to be decoded
	 * @throws Exception
	 *             if the service invoked could not return a response eg http
	 *             status 500 wrap the error and raise an exception. the
	 *             exception will be notified to observer to update error count.
	 */
	protected abstract Object performTestQueries(ITestCase testCase,
			Properties queryParams) throws Exception;

	public final static String RAW_RESPONSE = "kelvin.rawResponse";

	/**
	 * given a test set executes the query, parses the responses (if possible)
	 * and notifies the observers of the results.
	 */
	public void performTestSet(Collection<ITestCase> set) {
		for (ITestCase testCase : set) {
			notifyObservers(new TestCaseTestEvent(testCase, null, true));
			Iterator<Properties> queryParamsIterator = testCase
					.getQueryParameterIterator();
			iterateOverParameters(testCase, queryParamsIterator);
			notifyObservers(new TestCaseTestEvent(testCase, null, false));
		}
	}

	private void iterateOverParameters(ITestCase testCase,
			Iterator<Properties> queryParamsIterator) {
		while (queryParamsIterator.hasNext()) {
			Properties queryParams = queryParamsIterator.next();
			notifyObservers(new TestCaseTestEvent(testCase, queryParams, true));
			try {
				performSingleTest(testCase, queryParams);
			} catch (Throwable e) {
				// every observer should know that a test has failed
				notifyObservers(new ExceptionTestEvent(testCase, queryParams, e));
			}
			notifyObservers(new TestCaseTestEvent(testCase, queryParams, false));
		}
	}

	private void performSingleTest(ITestCase testCase, Properties queryParams)
			throws Exception {
		Object rawResponse = this.performTestQueries(testCase, queryParams);
		Map<String, Object> decodedResponses = new TreeMap<String, Object>();
		decodedResponses.put(RAW_RESPONSE, rawResponse);
		for (ResponseAnalyzer analyzer : responseAnalyzers) {
			try {
				analyzer.decode(decodedResponses);
			} catch (Throwable t) {
				notifyObservers(new ExceptionTestEvent(testCase, queryParams, t));
			}
		}
		notifyObservers(new ResponseDecodedTestEvent(testCase, queryParams,
				decodedResponses));

		// notifies observers about conditions not met.
		for (ConditionFailureTestEvent cfte : testCase.verifyConditions(
				queryParams, decodedResponses))
			this.notifyObservers(cfte);
	}

	@Override
	public void notifyObservers(Object arg) {
		setChanged(); // when called always notify, it is managed by the loop.
		super.notifyObservers(arg);
	}

	private List<ResponseAnalyzer> responseAnalyzers = new ArrayList<ResponseAnalyzer>();

	public void addResponseAnalyzer(ResponseAnalyzer r) {
		responseAnalyzers.add(r);
	}

}
