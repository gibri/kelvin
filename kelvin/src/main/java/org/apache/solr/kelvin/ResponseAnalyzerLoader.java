package org.apache.solr.kelvin;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.kelvin.responseanalyzers.XmlDoclistExtractorResponseAnalyzer;
import org.apache.solr.kelvin.responseanalyzers.XmlResponseAnalyzer;
import org.apache.solr.kelvin.scorer.MissingResultScorer;
import org.apache.solr.kelvin.scorer.SimpleScorer;
import org.apache.solr.kelvin.scorer.TestScorer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class ResponseAnalyzerLoader extends ConfigurableLoader {

	public ResponseAnalyzerLoader() {
		super(ResponseAnalyzer.class);
	}
	
	@Override
	protected void addDefaults() {
		if (this.resources.size() == 0) {
			XmlResponseAnalyzer  xmlResponseAnalyzer = new XmlResponseAnalyzer();
			
			resources.add(new XmlResponseAnalyzer());
			resources.add(new XmlDoclistExtractorResponseAnalyzer());
			JsonNode emptyConfig = JsonNodeFactory.instance.objectNode();
			for (IConfigurable r : resources) {
				try {
					r.configure(emptyConfig);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public List<ResponseAnalyzer> getAnalyzers() {
		List<ResponseAnalyzer> ret = new ArrayList<ResponseAnalyzer>();
		for (IConfigurable o : this.resources) {
			ret.add((ResponseAnalyzer)o);
		}
		return ret;
	}

}
