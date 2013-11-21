package org.apache.solr.kelvin;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class URLQueryPerformer extends QueryPerformer {


	private String baseUrl = "http://localhost:8983/solr/select";
	private CloseableHttpClient httpclient = HttpClients.createDefault();

	public void configure(JsonNode baseConfiguration) throws Exception {
		String newBaseUrl = baseConfiguration.path("URLQueryBuilder")
				.path("baseUrl").asText();
		if (newBaseUrl != null)
			baseUrl = newBaseUrl;

	}

	@Override
	protected Object performTestQueries(ITestCase testCase,
			Properties queryParams) throws Exception {
		URIBuilder uriBuilder = new URIBuilder(this.baseUrl);
		for (String paramName : queryParams.stringPropertyNames()) {
			uriBuilder.setParameter(paramName, queryParams.getProperty(paramName));
		}
		URI uri = uriBuilder.build();
		HttpGet httpget = new HttpGet(uri);

        System.out.println("executing request " + httpget.getURI());

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
        String responseBody = httpclient.execute(httpget, responseHandler);
        
        return responseBody;
	}

	public void close() throws IOException {
		httpclient.close();
	}

}
