package org.apache.solr.kelvin.testcases;

import static org.apache.solr.kelvin.testcases.SimpleTestCase.mandatory;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.solr.kelvin.ICondition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ListingRowCondition {

	public static final int ANY_LENGTH = -1;
	protected int length;
	protected boolean reverseConditions = false;

	/** shourd manufacturer match Manufacturer */
	protected boolean caseInsentiveFieldsName=true;
	protected boolean caseInsensitiveValues = true;
	
	public void parseReverseConditions(JsonNode condition) {
		JsonNode c = condition.path("reverse");
		if (c.isBoolean()) {
			this.reverseConditions = c.asBoolean();
		}
	}
 	public void parseLen(JsonNode condition) throws Exception {
		mandatory(condition,"len");
		int len = 0;
		JsonNode lenNode = condition.get("len");
		if ("any".equalsIgnoreCase(lenNode.asText()))
			len = ANY_LENGTH;
		else 
			len = lenNode.asInt();
		this.length= len;
	}

	public int getLength() {
		return length;
	}

	public int getLengthToCheck( ArrayNode results) {
		int lengthToCheck = this.length;
		if (lengthToCheck == ANY_LENGTH)
			lengthToCheck = results.size();
		return lengthToCheck;
	}
	
	protected boolean hasField(JsonNode o, String field) {
		if (!caseInsentiveFieldsName)
			return o.has(field);
		Iterator<String> names = o.fieldNames();
		while (names.hasNext()) 
			if (field.equalsIgnoreCase(names.next()))
				return true;
		return false;
	}
	
	protected JsonNode getField(JsonNode o, String field) {
		if (!caseInsentiveFieldsName)
			return o.get(field);
		Iterator<Entry<String, JsonNode>> i = o.fields();
		while (i.hasNext()) {
			Entry<String,JsonNode> e = i.next();
			if (field.equalsIgnoreCase(e.getKey()))
				return e.getValue();
		}
		throw new NullPointerException();
	}
	

	public ListingRowCondition() {
		super();
	}

}