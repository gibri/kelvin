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

public class LegacyResponseAnalyzer implements ResponseAnalyzer {

	public void decode(Map<String, Object> previousResponses) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode response = mapper.createArrayNode();
		
		if (!previousResponses.containsKey(XmlResponseAnalyzer.XML_DOM)) {
			previousResponses.put(XmlDoclistExtractorResponseAnalyzer.DOC_LIST, response); //empty
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
				String localName = subel.getLocalName();
				if (localName == "field") {
					String fieldName = subel.getAttributes().getNamedItem("name").getNodeValue();
					if (! "infoold".equals(fieldName)) {
						String value = subel.getTextContent();
						oDoc.put(fieldName, value);
					}
				} else if (localName=="slug_preferenziale") {
					NodeList slugNodes = subel.getChildNodes();
					oDoc.put("slug_preferenziale", slugNodes.item(slugNodes.getLength()-1).getAttributes().getNamedItem("path").getTextContent());
				}

				subel = subel.getNextSibling();
			}
			response.add(oDoc);
		}
		previousResponses.put(XmlDoclistExtractorResponseAnalyzer.DOC_LIST, response);
	}

	private XPathExpression expr;
	
	public void configure(JsonNode config) throws Exception {
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		expr = xpath.compile("/response/federator/result/doc");
	}

}
