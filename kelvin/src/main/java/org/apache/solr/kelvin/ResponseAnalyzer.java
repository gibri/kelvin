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
package org.apache.solr.kelvin;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface ResponseAnalyzer extends IConfigurable {

	/**
	 * given raw data obtained by IQueryPerformer (usually a big string with all the response)
	 * and other analysis results return a new data subset to be
	 * verified by test cases
	 * 
	 * @param previousResponses is useful, if a previous analyzer is a Dom tree or a json object
	 * it is easier to extract interesting data. If The analysis is ok put the result inside the map!
	 * @throws exception if the data to decode is illegally formatted
	 */
	public void decode(Map<String,Object> previousResponses) throws Exception;
	
	/**
	 * read configuration and prepare to work
	 * @param config
	 * @throws Exception if something is missing in the configuration
	 */
	public void configure(JsonNode config) throws Exception ;

}
