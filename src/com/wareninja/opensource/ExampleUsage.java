/***
 *   Copyleft 2014 - WareNinja.com / Rumble In The Jungle!
 * 
 *  @author: yg@wareninja.com
 *  @see https://github.com/WareNinja
 *  disclaimer: I code for fun, dunno what I'm coding about :-)
 */

package com.wareninja.opensource;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wareninja.opensource.discourse.DiscourseApiClient;
import com.wareninja.opensource.discourse.utils.ResponseListener;
import com.wareninja.opensource.discourse.utils.ResponseMeta;
import com.wareninja.opensource.discourse.utils.ResponseModel;

/*
 * simple class to... 
 * make calls for quick validation of each implemented function! 
 */
public class ExampleUsage {

	final static String TAG = ExampleUsage.class.getSimpleName();
	
	/**
	 * @param args
	 * api_url api_key api_username
	 */
	public static void main(String[] args) {
		
		if (args.length<3) {
			System.out.println(TAG+" " + "missing parameters... Read the source Luke! ");
			System.out.println(TAG+" example parameters: " + "api_url api_key api_username");
			return;
		}
		
		final DiscourseApiClient mDiscourseApiClient = new DiscourseApiClient(
				args[0] // api_url  : e.g. http://your_domain.com
				, args[1] // api_key : you get from discourse admin
				, args[2] // api_username : you make calls on behalf of
				);
		
		Map<String, String> parameters = null;
		
		// ## username for testing each function ##
		String test_username = "testmonkey1";
		
		// --- createUser ---
		/*
		// createUser parameters MUST already contain
		'name': name,
	    'email': email,
	    'username': username,
	    'password': password,
	    */
		parameters = new HashMap<String, String>();
		parameters.put("name", test_username);
		parameters.put("email", test_username+"@dummy.com");
		parameters.put("username", test_username);
		parameters.put("password", test_username+"_pwd");
		mDiscourseApiClient.createUser(parameters, new ResponseListener(){

			@Override
			public void onBegin(String info) {
				System.out.println("info: "+info);
			}
			@Override
			public void onComplete_wModel(ResponseModel responseModel) {
				// successful result
				System.out.println("SUCCESS! -> " + responseModel.toString());
			}

			@Override
			public void onError_wMeta(ResponseMeta responseMeta) {
				// error
				System.out.println("ERROR! -> " + responseMeta.toString());
			}
		});
		
		try {// silly way of waiting for user creation!!
			Thread.sleep(3000);
		} catch (Exception ex) {}
		
		
		// --- getUser ---
		parameters = new HashMap<String, String>();
		parameters.put("username", test_username);
		mDiscourseApiClient.getUser(parameters, new ResponseListener(){

			@Override
			public void onBegin(String info) {
				System.out.println("info: "+info);
			}
			@Override
			public void onComplete_wModel(ResponseModel responseModel) {
				// successful result
				System.out.println("SUCCESS! -> " + responseModel.toString());
				
				/* responseModel.data=
				{
					user: {
						id: 5,
						username: "test_monkey_2",
						avatar_template: "//www.gravatar.com/avatar/e40089fe1c655bd80310df235333973c.png?s={size}&r=pg&d=identicon",
						name: "test_monkey_2",
						email: "test_monkey_2%2540dummy.com",
						...
					}
				}
				 */
				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement = jsonParser.parse( ""+responseModel.data );
				JsonObject jsonObject = null;
				JsonArray jsonArray = null;
				if (jsonElement!=null) {
					jsonObject = jsonElement.isJsonObject()?jsonElement.getAsJsonObject():null;
					jsonArray = jsonElement.isJsonArray()?jsonElement.getAsJsonArray():null;
				}
				
				if (jsonObject!=null && jsonObject.has("user")) {
					jsonObject = jsonObject.getAsJsonObject("user"); 
				}
				else return;
				
				System.out.println("jsonObject -> "+jsonObject);
				
				// activate & approve by default!!!
				Map<String, String> parameters1 = null;
				
				// -> activate
				parameters1 = new HashMap<String, String>();
				parameters1.put("userid", ""+jsonObject.get("id").getAsInt());
				parameters1.put("username", jsonObject.get("username").getAsString());
				mDiscourseApiClient.activateUser(parameters1, new ResponseListener(){
					@Override
					public void onComplete_wModel(ResponseModel responseModel) {
						System.out.println("SUCCESS! -> " + responseModel.toString());
					}
					@Override
					public void onError_wMeta(ResponseMeta responseMeta) {
						// error
						System.out.println("ERROR! -> " + responseMeta.toString());
					}
				});
				
				// -> approve
				parameters1 = new HashMap<String, String>();
				parameters1.put("userid", ""+jsonObject.get("id").getAsInt());
				parameters1.put("username", jsonObject.get("username").getAsString());
				mDiscourseApiClient.approveUser(parameters1, new ResponseListener(){
					@Override
					public void onComplete_wModel(ResponseModel responseModel) {
						System.out.println("SUCCESS! -> " + responseModel.toString());
					}
					@Override
					public void onError_wMeta(ResponseMeta responseMeta) {
						// error
						System.out.println("ERROR! -> " + responseMeta.toString());
					}
				});
				
				// -> trust
				parameters1 = new HashMap<String, String>();
				parameters1.put("userid", ""+jsonObject.get("id").getAsInt());
				parameters1.put("username", jsonObject.get("username").getAsString());
				parameters1.put("level", "2"); // level can be: 0 (new user), 1 (basic user), 2 (regular user), 3 (leader), 4 (elder)
				mDiscourseApiClient.trustUser(parameters1, new ResponseListener(){
					@Override
					public void onComplete_wModel(ResponseModel responseModel) {
						System.out.println("SUCCESS! -> " + responseModel.toString());
					}
					@Override
					public void onError_wMeta(ResponseMeta responseMeta) {
						// error
						System.out.println("ERROR! -> " + responseMeta.toString());
					}
				});
				
				// -> generate api_key
				parameters1 = new HashMap<String, String>();
				parameters1.put("userid", ""+jsonObject.get("id").getAsInt());
				parameters1.put("username", jsonObject.get("username").getAsString());
				mDiscourseApiClient.generateApiKey(parameters1, new ResponseListener(){
					@Override
					public void onComplete_wModel(ResponseModel responseModel) {
						System.out.println("SUCCESS! -> " + responseModel.toString());
					}
					@Override
					public void onError_wMeta(ResponseMeta responseMeta) {
						// error
						System.out.println("ERROR! -> " + responseMeta.toString());
					}
				});
			}

			@Override
			public void onError_wMeta(ResponseMeta responseMeta) {
				// error
				System.out.println("ERROR! -> " + responseMeta.toString());
			}
		});
		
		
		// --- searchForUser ---
		parameters = new HashMap<String, String>();
		parameters.put("username", test_username);
		mDiscourseApiClient.searchForUser(parameters, new ResponseListener(){

			@Override
			public void onBegin(String info) {
				System.out.println("info: "+info);
			}
			@Override
			public void onComplete_wModel(ResponseModel responseModel) {
				// successful result
				System.out.println("SUCCESS! -> " + responseModel.toString());
			}

			@Override
			public void onError_wMeta(ResponseMeta responseMeta) {
				// error
				System.out.println("ERROR! -> " + responseMeta.toString());
			}
		});
		
		// --- search ---
		parameters = new HashMap<String, String>();
		parameters.put("term", test_username);
		mDiscourseApiClient.search(parameters, new ResponseListener(){

			@Override
			public void onBegin(String info) {
				System.out.println("info: "+info);
			}
			@Override
			public void onComplete_wModel(ResponseModel responseModel) {
				// successful result
				System.out.println("SUCCESS! -> " + responseModel.toString());
			}

			@Override
			public void onError_wMeta(ResponseMeta responseMeta) {
				// error
				System.out.println("ERROR! -> " + responseMeta.toString());
			}
		});
		
		
		// --- getCreatedTopics ---
		parameters = new HashMap<String, String>();
		//parameters.put("username", test_username);
		mDiscourseApiClient.getCreatedTopics(parameters, new ResponseListener(){

			@Override
			public void onBegin(String info) {
				System.out.println("info: "+info);
			}
			@Override
			public void onComplete_wModel(ResponseModel responseModel) {
				// successful result
				System.out.println("SUCCESS! -> " + responseModel.toString());
			}

			@Override
			public void onError_wMeta(ResponseMeta responseMeta) {
				// error
				System.out.println("ERROR! -> " + responseMeta.toString());
			}
		});
	}

}
