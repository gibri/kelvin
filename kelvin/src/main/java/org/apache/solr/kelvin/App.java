package org.apache.solr.kelvin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Hello world!
 *
 */
public class App 
{
	private String defautEncoding ="utf8";
	private JsonNode rootConfigNode;
	private List<JsonNode> testFiles = new ArrayList<JsonNode>();
	
    public static void main( String[] args )
    {
    	new App(args);
    }
    
    public void help() {
    	System.out.println("-c <filename> mandatory configuration file name\n"
    			+ "-d optional directory to scan, all json files therein will be considered tests\n"
    			+ "-e file encoding default utf8\n"
    			+ "\n"
    			+ "all other parameters must be valid json test files\n");
    }
    public App(String [] args) {
    	Options options = new Options();
    	
    	options.addOption("c", false, "configuration file eg kelvin.conf");
    	options.addOption(new Option("d", true,"directory containing tests"));
    	options.addOption(new Option("e", true,"default encoding"));
    	
    	CommandLineParser parser = new org.apache.commons.cli.GnuParser();
    	
    	try {
			CommandLine cmd = parser.parse( options, args);
			
			readEncoding(cmd);
			
			if (cmd.hasOption('c')) {
				rootConfigNode = readFileToJsonNode(cmd.getOptionValue('c'));
			} else {
				if (new File("kelvin.json").canRead()) {
					rootConfigNode = readFileToJsonNode("kelvin.json");
				} else {
					help();
					return;
				}
				//InputStream cfg = this.getClass().getResourceAsStream("org.apache.solr.kelvin.defaultConfig.conf");
				//rootConfigNode = parseJson(IOUtils.toString(cfg, this.defautEncoding));
			}
					
			scanDirectory(cmd);
			
			for (String testCase : cmd.getArgs()) {
				testFiles.add(readFileToJsonNode(testCase));
			}
			
			if (testFiles.size()==0) {
				help();
				System.err.println("no test cases specified!");
				return;
			}
			
			/* we have configuration and tests, let's configure */
	    	configure();
	    	
	    	performTests();
	    	
	    	printReport();
	    	
		} catch (ParseException e) {
			System.err.println("-c kelvin.conf -d /path/to/test/dir test1.json test2.json");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
    }
    
	private void printReport() {
		Iterator<Scorer> i = this.scorerLoader.iterator();
		while (i.hasNext()) {
			System.out.print(i.next().simpleReport());
		}
	}

	private void performTests() throws Exception {
		Iterator<QueryPerformer> q = queryPerformerLoader.iterator();
		while (q.hasNext()){
			q.next().performTestSet(this.testCases);
		}
	}

	private void readEncoding(CommandLine cmd) {
		if (cmd.hasOption('e')) {
			defautEncoding = cmd.getOptionValue('e');
			Charset.forName(defautEncoding); //raise exception if invalid
		}
	}
	
	private JsonNode readFileToJsonNode(String fileName) throws Exception {
		return readFileToJsonNode(new File(fileName));
	}
	
	private JsonNode readFileToJsonNode(File file) throws Exception {
		if (file.canRead()) {
			String text = FileUtils.readFileToString(file, defautEncoding);
			return parseJson(text);  
		} else {
			throw new Exception("Cannot read "+file.getName());
		}
	}

	private JsonNode parseJson(String text) throws IOException,
			JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(text, JsonNode.class);
	}
	
	private void scanDirectory(CommandLine cmd) throws Exception {
		if (cmd.hasOption('d')) {
			String extensions[]={".json"};
			@SuppressWarnings("unchecked")
			Iterator<File> i = FileUtils.iterateFiles(new File(cmd.getOptionValue('d')), extensions, true);
			while (i.hasNext()) {
				testFiles.add(readFileToJsonNode(i.next()));
			}
		}
	}
	
	public void configure() throws Exception {
		configureTestRegistry();
		configureConditionRegistry();
		configureScorers();
		configureQueryPerformer();
		
		configureTests();
	}

	private ScorerLoader scorerLoader;
	
	private void configureScorers() throws Exception {
		scorerLoader = new ScorerLoader();
		scorerLoader.configure(rootConfigNode.path("scorers"));
	}

	private void configureConditionRegistry() throws Exception {
		SingletonConditionRegistry.configure(rootConfigNode.path("conditions"));
		
	}

	private void configureTestRegistry() throws Exception {
		SingletonTestRegistry.configure( this.rootConfigNode.path("testTypes"));
	}
	
	private QueryPerformerLoader queryPerformerLoader;
	
	private void configureQueryPerformer() throws Exception {
		queryPerformerLoader = new QueryPerformerLoader();
		queryPerformerLoader.configure(rootConfigNode.path("queryPerformers"));
		Iterator<QueryPerformer> q = queryPerformerLoader.iterator();
		while (q.hasNext()){
			scorerLoader.registerScorersAsObservers(q.next());
		}
	}
	
	private void configureTests() throws Exception {
		for (JsonNode file : this.testFiles) {
			configureTestsFromJsonNode(file);
		}
	}

	private List<ITestCase> testCases;
	
	private void configureTestsFromJsonNode(JsonNode node) throws Exception {
		if (testCases == null)
			testCases = new ArrayList<ITestCase>(); //otherwise appends
		ArrayNode list = ConfigurableLoader.assureArray(node);
		for (int i=0; i<list.size(); i++) {
			JsonNode config =list.get(i);
			ITestCase t = SingletonTestRegistry.instantiate(config);
			t.configure(config);
			testCases.add(t);
		}
	}
	
}
