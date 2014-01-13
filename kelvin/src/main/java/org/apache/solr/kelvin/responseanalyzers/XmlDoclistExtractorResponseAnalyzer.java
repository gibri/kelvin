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

import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.solr.kelvin.ResponseAnalyzer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class XmlDoclistExtractorResponseAnalyzer implements ResponseAnalyzer {

	public final static String DOC_LIST = "kelvin.doclist";

	public void decode(Map<String, Object> previousResponses) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode response = mapper.createArrayNode();
		
		if (!previousResponses.containsKey(XmlResponseAnalyzer.XML_DOM)) {
			previousResponses.put(DOC_LIST, response); //empty
			return;
		}

		NodeList nodeList = (NodeList) expr.evaluate(
				(Document) previousResponses.get(XmlResponseAnalyzer.XML_DOM),
				XPathConstants.NODESET);
		
		for (int i = 0; i<nodeList.getLength(); i++) {
			Node doc = nodeList.item(i);
			ObjectNode oDoc = mapper.createObjectNode();
			Node subel = doc.getFirstChild();
			while (subel!=null) {
				if (subel.getNodeType()!=Node.TEXT_NODE) {
					String fieldName = subel.getAttributes().getNamedItem("name").getNodeValue();
					String elementName = subel.getNodeName();
					if ("arr".equals(elementName)) {
						ArrayNode multivaluedField = mapper.createArrayNode();
						Node mvItem = subel.getFirstChild();
						while (mvItem!=null) {
							multivaluedField.add(mvItem.getTextContent());
							mvItem = mvItem.getNextSibling();
						}
						oDoc.put(fieldName,multivaluedField);
					} else {
						String value = subel.getTextContent();
						oDoc.put(fieldName, value);
					}
				}
				subel = subel.getNextSibling();
			}
			response.add(oDoc);
		}
		previousResponses.put(DOC_LIST, response);
	}

	private XPathExpression expr;

	public void configure(JsonNode config) throws Exception {
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		expr = xpath.compile("/response/result/doc");

	}

}
