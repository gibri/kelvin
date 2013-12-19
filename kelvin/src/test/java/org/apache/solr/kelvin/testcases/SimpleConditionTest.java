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
package org.apache.solr.kelvin.testcases;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;

public class SimpleConditionTest extends TestCase {

	public static JsonNode readJsonResource(Class<?> c, String resourceName) throws IOException {
		String raw = IOUtils.toString(c.getResourceAsStream(resourceName), "utf8");
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(raw, JsonNode.class);
	}
	
	public void testConfigurationCases() throws Exception {
		JsonNode configs = readJsonResource(SimpleConditionTest.class, "SimpleCondition.json") ;
		
		try { 
			new SimpleCondition().configure(configs.get("missing len"));
			fail();
		} catch (Exception e) {}
		
		try { 
			new SimpleCondition().configure(configs.get("missing field"));
			fail();
		} catch (Exception e) {}
		
		try { 
			new SimpleCondition().configure(configs.get("missing vals"));
			fail();
		} catch (Exception e) {}
		
		SimpleCondition legacy = new SimpleCondition();
		legacy.configure(configs.get("legacy"));
		assertEquals(10, legacy.getLength() );
		legacy.getFields().contains("name");
		legacy.getCorrectValuesList().contains("iphone");
		
		SimpleCondition legacymv = new SimpleCondition();
		legacymv.configure(configs.get("legacy multivalue"));
		legacymv.getFields().contains("name");
		legacymv.getFields().contains("manufacturer");
		
		SimpleCondition std = new SimpleCondition();
		std.configure(configs.get("std"));
		std.getFields().contains("name");
		std.getFields().contains("manufacturer");
		std.getCorrectValuesList().contains("a");
		std.getCorrectValuesList().contains("b");
		
		
	}
}
