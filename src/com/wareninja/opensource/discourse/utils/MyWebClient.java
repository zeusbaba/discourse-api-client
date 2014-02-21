/***
 *   Copyleft 2014 - WareNinja.com / Rumble In The Jungle!
 * 
 *  @author: yg@wareninja.com
 *  @see https://github.com/WareNinja
 *  disclaimer: I code for fun, dunno what I'm coding about :-)
 *  
 *  ---
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  source adapted from: 
 *  https://github.com/wareninja
 */

package com.wareninja.opensource.discourse.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;

import com.google.gson.JsonObject;

public class MyWebClient {

	private static final String TAG = MyWebClient.class.getSimpleName();
	private static final boolean DEBUG = true;
	
	CloseableHttpClient httpClient;
	RequestConfig httpRequestConfig;
	
	HttpContext localContext;
	private final static int TIMEOUT = 30000;

	private List<RequestHeader> requestHeaders = new LinkedList<RequestHeader>();

	CloseableHttpResponse httpResponse = null;
	HttpGet httpGet = null; // GET
	HttpPost httpPost = null; // POST
	HttpPut httpPut = null;// PUT
	HttpDelete httpDelete = null; // DELETE
	
	String webServiceUrl;
	Integer httpResponseCode = null;
	
	public MyWebClient(String webServiceUrl) {
		
		this.webServiceUrl = webServiceUrl;
		initBase();
	}
	/*public WebClient() {
		initBase();
	}*/
	protected void initBase() {
		httpClient = HttpClients.createDefault();
		httpRequestConfig = RequestConfig.custom()
		        .setSocketTimeout(TIMEOUT)
		        .setConnectTimeout(TIMEOUT)
		        .build();
		
		localContext = new BasicHttpContext();
	}

	public void setWebServiceUrl(String webServiceUrl) {
		this.webServiceUrl = webServiceUrl;
	}
	public String getWebServiceUrl() {
		return this.webServiceUrl;
	}
	
	public Integer getHttpResponseCode() {
		return this.httpResponseCode;
	}

	public void addRequestHeader(final String key, final String value) {

		if (DEBUG) System.out.println(TAG+"|"+ "addRequestHeader->" + "key:" + key + "|value:" + value);
		if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value))
			return;

		this.requestHeaders.add(new PlainRequestHeader(key, value));
	}
	public void setRequestHeaders(final List<RequestHeader> headers) {
		this.requestHeaders = new LinkedList<RequestHeader>(headers);
	}
	/*private void appendRequestHeaders(final HttpURLConnection conn,
			final List<RequestHeader> headers) {
		for (RequestHeader header : headers) {
			if (header != null) {
				conn.addRequestProperty(header.getKey(), header.getValue());
			}
		}
	}*/

	
	// --- GET ---
	// Use this method to do a HttpGet/WebGet
	public String get(String methodName, Map<String, String> parameters) {
		List<RequestParameter> requestParams = new LinkedList<RequestParameter>();
		for (String key : parameters.keySet()) {
			requestParams.add( new StringRequestParameter(key, parameters.get(key)) );
		}
		return get(methodName, requestParams);
	}
	public String get(String methodName, List<RequestParameter> requestParams) {
		String responseStr = "";
		String requestUrl;
		if (methodName.startsWith(webServiceUrl)) {
			requestUrl = methodName;
		} else {
			requestUrl = webServiceUrl + methodName;
		}
		
		int i = 0;
		for (RequestParameter requestParam: requestParams) {
			requestUrl += (i==0 && !requestUrl.contains("?"))?"?":"&";
			requestUrl += requestParam.format();
			i++;
		}
		
		System.out.println("GET requestUrl : " + requestUrl);
		
		/*
		URI uri = new URIBuilder()
	        .setScheme("http")
	        .setHost("www.google.com")
	        .setPath("/search")
	        .setParameter("q", "httpclient")
	        .setParameter("btnG", "Google Search")
	        .setParameter("aq", "f")
	        .setParameter("oq", "")
	        .build();
		HttpGet httpget = new HttpGet(uri);
		 */
		httpGet = new HttpGet(requestUrl);
		httpGet.setConfig(httpRequestConfig);
		for (RequestHeader requestHeader:requestHeaders) {
			httpGet.addHeader(requestHeader.getKey(), requestHeader.getValue());
		}
		
		//CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			httpResponseCode = httpResponse.getStatusLine().getStatusCode(); 
			HttpEntity entity = httpResponse.getEntity();
			if (httpResponseCode >= 200 && httpResponseCode < 300) {
				responseStr = entity!=null ? EntityUtils.toString(entity) : "";
            } else {
                //throw new ClientProtocolException("Unexpected response status: " + status);
            	responseStr = httpResponseCode + "|"+"ERROR";
            }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try { httpResponse.close(); } catch (Exception ex){}
		}
		
		return responseStr;
	}
		
	
	// --- POST ---
	public String post(String methodName, Map<String, String> parameters){//, String contentType) {
		List<RequestParameter> requestParams = new LinkedList<RequestParameter>();
		for (String key : parameters.keySet()) {
			if ( !key.equalsIgnoreCase("api_key") && !key.equalsIgnoreCase("api_username")) {
				requestParams.add( new StringRequestParameter(key, parameters.get(key)) );
			}
		}
		return post(methodName, requestParams);//, contentType);
	}
	public String post(String methodName, List<RequestParameter> requestParams){
		return post( methodName, getJsonFromParams(requestParams) );
	}
	public String post(String methodName, String jsonBodyStr){//, String contentType) {
		String responseStr = "";
		String requestUrl;
		if (methodName.startsWith(webServiceUrl)) {
			requestUrl = methodName;
		} else {
			requestUrl = webServiceUrl + methodName;
		}
		
		System.out.println("POST requestUrl : " + requestUrl);
		
		httpPost = new HttpPost(requestUrl);
		httpPost.setConfig(httpRequestConfig);
		for (RequestHeader requestHeader:requestHeaders) {
			httpPost.addHeader(requestHeader.getKey(), requestHeader.getValue());
		}
		httpPost.addHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		
		// NOTE: by default we post ONLY json data! 
		StringEntity requestEntity = new StringEntity( 
				jsonBodyStr
				, ContentType.create("application/json", "UTF-8")
				);
		httpPost.setEntity(requestEntity);
		
		try {
			httpResponse = httpClient.execute(httpPost);
			httpResponseCode = httpResponse.getStatusLine().getStatusCode(); 
			HttpEntity responseEntity = httpResponse.getEntity();
			if (httpResponseCode >= 200 && httpResponseCode < 300) {
				responseStr = responseEntity!=null ? EntityUtils.toString(responseEntity) : "";
            } else {
                //throw new ClientProtocolException("Unexpected response status: " + status);
            	responseStr = httpResponseCode + "|"+"ERROR";
            }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try { httpResponse.close(); } catch (Exception ex){}
		}
		
		
		return responseStr;
	}
	
	// --- PUT ---
	public String put(String methodName, Map<String, String> parameters){
		List<RequestParameter> requestParams = new LinkedList<RequestParameter>();
		for (String key : parameters.keySet()) {
			requestParams.add( new StringRequestParameter(key, parameters.get(key)) );
		}
		return put(methodName, requestParams);
	}
	public String put(String methodName, List<RequestParameter> requestParams){
		return put( methodName, getJsonFromParams(requestParams) );
	}
	public String put(String methodName, String jsonBodyStr){
		String responseStr = "";
		String requestUrl;
		if (methodName.startsWith(webServiceUrl)) {
			requestUrl = methodName;
		} else {
			requestUrl = webServiceUrl + methodName;
		}
		
		System.out.println("PUT requestUrl : " + requestUrl);
		
		httpPut = new HttpPut(requestUrl);
		httpPut.setConfig(httpRequestConfig);
		for (RequestHeader requestHeader:requestHeaders) {
			httpPut.addHeader(requestHeader.getKey(), requestHeader.getValue());
		}
		httpPut.addHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		
		// NOTE: by default we post ONLY json data! 
		StringEntity requestEntity = new StringEntity( 
				jsonBodyStr
				, ContentType.create("application/json", "UTF-8")
				);
		httpPut.setEntity(requestEntity);
		
		try {
			httpResponse = httpClient.execute(httpPut);
			httpResponseCode = httpResponse.getStatusLine().getStatusCode(); 
			HttpEntity responseEntity = httpResponse.getEntity();
			if (httpResponseCode >= 200 && httpResponseCode < 300) {
				responseStr = responseEntity!=null ? EntityUtils.toString(responseEntity) : "";
            } else {
                //throw new ClientProtocolException("Unexpected response status: " + status);
            	responseStr = httpResponseCode + "|"+"ERROR" 
            			+ " | "+(responseEntity!=null ? EntityUtils.toString(responseEntity) : "")
                	;
            }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try { httpResponse.close(); } catch (Exception ex){}
		}
		
		
		return responseStr;
	}
	
	// --- DELETE ---
	public String delete(String methodName, Map<String, String> parameters){
		List<RequestParameter> requestParams = new LinkedList<RequestParameter>();
		for (String key : parameters.keySet()) {
			requestParams.add( new StringRequestParameter(key, parameters.get(key)) );
		}
		return delete(methodName, requestParams);
	}
	/*public String delete(String methodName, List<RequestParameter> requestParams){
		return delete( methodName, getJsonFromParams(requestParams) );
	}
	public String delete(String methodName, String jsonBodyStr){*/
	public String delete(String methodName, List<RequestParameter> requestParams){
		String responseStr = "";
		String requestUrl;
		if (methodName.startsWith(webServiceUrl)) {
			requestUrl = methodName;
		} else {
			requestUrl = webServiceUrl + methodName;
		}
		
		httpDelete = new HttpDelete(requestUrl);
		httpDelete.setConfig(httpRequestConfig);
		for (RequestHeader requestHeader:requestHeaders) {
			httpDelete.addHeader(requestHeader.getKey(), requestHeader.getValue());
		}
		httpDelete.addHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		
		// FIXME: we need HTTP DELETE!!!!
		/*
		URI uri = new URIBuilder()
	        .setScheme("http")
	        .setHost("www.google.com")
	        .setPath("/search")
	        .setParameter("q", "httpclient")
	        .setParameter("btnG", "Google Search")
	        .setParameter("aq", "f")
	        .setParameter("oq", "")
	        .build();
		HttpGet httpget = new HttpGet(uri);
		 */
		/* 
		StringEntity requestEntity = new StringEntity( 
				jsonBodyStr
				, ContentType.create("application/json", "UTF-8")
				);
		httpDelete.setEntity(requestEntity);
		*/
		
		try {
			httpResponse = httpClient.execute(httpDelete);
			httpResponseCode = httpResponse.getStatusLine().getStatusCode(); 
			HttpEntity responseEntity = httpResponse.getEntity();
			if (httpResponseCode >= 200 && httpResponseCode < 300) {
				responseStr = responseEntity!=null ? EntityUtils.toString(responseEntity) : "";
            } else {
                //throw new ClientProtocolException("Unexpected response status: " + status);
            	responseStr = httpResponseCode + "|"+"ERROR";
            }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try { httpResponse.close(); } catch (Exception ex){}
		}
		
		
		return responseStr;
	}

	// util funcs
	public String getJsonFromParams(Map<String, String> parameters) {
		List<RequestParameter> requestParams = new LinkedList<RequestParameter>();
		for (String key : parameters.keySet()) {
			requestParams.add( new StringRequestParameter(key, parameters.get(key)) );
		}
		return getJsonFromParams(requestParams);
	}
	public String getJsonFromParams(List<RequestParameter> requestParams) {

		JsonObject jsonObject = new JsonObject();

		for (RequestParameter requestParam: requestParams) {
			try {
				if ( !requestParam.getKey().equalsIgnoreCase("api_key") && !requestParam.getKey().equalsIgnoreCase("api_username")) {
					jsonObject.addProperty(
							requestParam.getKey()
							, URLEncoder.encode(requestParam.getValueStr(), "UTF-8")
							);
				}
			} catch (Exception e) {
				System.err.println(TAG+"|"+ "Exception : " + e);
			}
		}
		return jsonObject.toString();
	}
	
	public String enrichMethodName(String methodName, String api_key, String api_username) {
		
		Map<String, String> parameters = new HashMap<String, String>();
		if (!TextUtils.isEmpty(api_key)) parameters.put("api_key", api_key);
		if (!TextUtils.isEmpty(api_username)) parameters.put("api_username", api_username);
		
		return enrichMethodName(methodName, parameters);
	}
	public String enrichMethodName_addParam(String methodName, String param_key, String param_val) {
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(param_key, param_val);
		
		return enrichMethodName(methodName, parameters);
	}
	public String enrichMethodName(String methodName, Map<String, String> parameters) {
		
		methodName += (!methodName.contains("?") && parameters.size()>0) ? "?":"";
		for (String key : parameters.keySet()) {
			
			methodName += key+"="+parameters.get(key) + "&";
			/*if ( key.equalsIgnoreCase("api_key") || key.equalsIgnoreCase("api_username")) {
				methodName += key+"="+parameters.get(key) + "&";
			}*/
		}
		if (methodName.endsWith("&")) methodName = methodName.substring(0, methodName.length()-1);
		
		return methodName;
	}
}
