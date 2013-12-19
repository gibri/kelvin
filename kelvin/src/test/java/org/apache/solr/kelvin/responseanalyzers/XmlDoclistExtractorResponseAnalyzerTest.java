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
package org.apache.solr.kelvin.responseanalyzers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.solr.kelvin.QueryPerformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import junit.framework.TestCase;

public class XmlDoclistExtractorResponseAnalyzerTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ra = new XmlDoclistExtractorResponseAnalyzer();
		JsonNode emptyConf = JsonNodeFactory.instance.objectNode();
		ra.configure(emptyConf); //empty conf
		
		ra_xml = new XmlResponseAnalyzer();
		ra_xml.configure(emptyConf);
	}
	
	private ArrayNode parseResource(String name) throws Exception {
		Map<String,Object> previousResponses = new HashMap<String, Object>();
		previousResponses. put(QueryPerformer.RAW_RESPONSE, IOUtils.toString(XmlResponseAnalyzerTest.class.getResourceAsStream(name), "utf8"));
		try {
		ra_xml.decode(previousResponses);
		} catch (Exception e) {
			//its ok to skip, the class must works also in case of errors
		}
		ra.decode(previousResponses);
		return (ArrayNode) previousResponses.get(XmlDoclistExtractorResponseAnalyzer.DOC_LIST);
	}

	private XmlDoclistExtractorResponseAnalyzer ra;
	private XmlResponseAnalyzer ra_xml;
	
	public void testResponses() throws Exception {
		ArrayNode resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/empty.xml");
		assertEquals(0,  resp.size() );
		
		resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/malformedResponse.xml");
		assertEquals(0,  resp.size() );
		
		resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/oneResult.xml");
		assertEquals(1, resp.size());
		
		resp = parseResource("/org/apache/solr/kelvin/responseanalyzers/multiResult.xml");
		assertEquals(10, resp.size());
	}
	
	public static Map<String,Object> quickParseForTest(String resName) throws Exception {
		Map<String,Object> previousResponses = new HashMap<String, Object>();
		JsonNode emptyConf = JsonNodeFactory.instance.objectNode();
		XmlResponseAnalyzer ra_xml = new XmlResponseAnalyzer();
		ra_xml.configure(emptyConf);
		XmlDoclistExtractorResponseAnalyzer ra = new XmlDoclistExtractorResponseAnalyzer();
		ra.configure(emptyConf); //empty conf
		
		previousResponses. put(QueryPerformer.RAW_RESPONSE, IOUtils.toString(XmlResponseAnalyzerTest.class.getResourceAsStream(resName), "utf8"));
		try {
			ra_xml.decode(previousResponses);
		} catch (Exception e) {
			//its ok to skip, the class must works also in case of errors
		}
		ra.decode(previousResponses);
		return previousResponses;
	}
}
