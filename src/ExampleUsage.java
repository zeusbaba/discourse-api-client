/***
 *   Copyleft 2014 - WareNinja.com / Rumble In The Jungle!
 * 
 *  @author: yg@wareninja.com
 *  @see https://github.com/WareNinja
 *  disclaimer: I code for fun, dunno what I'm coding about :-)
 */


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
		
		ResponseModel responseModel;
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
		responseModel = mDiscourseApiClient.createUser(parameters);
		System.out.println("createUser responseModel -> " + responseModel.toString());
		
		JsonObject userObject = null;
		
		// --- getUser & activate+approve ---
		parameters = new HashMap<String, String>();
		parameters.put("username", test_username);
		responseModel = mDiscourseApiClient.getUser(parameters);
		if (responseModel.meta.code>201 || responseModel.data==null) {// error!
			System.out.println(test_username+" NOT exists!!! responseModel -> " + responseModel.toString());
		}
		else {
			
			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse( ""+responseModel.data );
			
			JsonArray jsonArray = null;
			if (jsonElement!=null) {
				userObject = jsonElement.isJsonObject()?jsonElement.getAsJsonObject():null;
				jsonArray = jsonElement.isJsonArray()?jsonElement.getAsJsonArray():null;
			}
			
			if (userObject!=null && userObject.has("user")) {
				userObject = userObject.getAsJsonObject("user"); 
			}
			else return;
			
			System.out.println("userObject -> "+userObject);
			
			// -> activate
			parameters = new HashMap<String, String>();
			parameters.put("userid", ""+userObject.get("id").getAsInt());
			parameters.put("username", userObject.get("username").getAsString());
			responseModel = mDiscourseApiClient.activateUser(parameters);
			
			// -> approve
			parameters = new HashMap<String, String>();
			parameters.put("userid", ""+userObject.get("id").getAsInt());
			parameters.put("username", userObject.get("username").getAsString());
			responseModel = mDiscourseApiClient.approveUser(parameters);
			
			// -> trust
			parameters = new HashMap<String, String>();
			parameters.put("userid", ""+userObject.get("id").getAsInt());
			parameters.put("username", userObject.get("username").getAsString());
			parameters.put("level", "2"); // level can be: 0 (new user), 1 (basic user), 2 (regular user), 3 (leader), 4 (elder)
			responseModel = mDiscourseApiClient.trustUser(parameters);
			
			// -> generate api_key
			parameters = new HashMap<String, String>();
			parameters.put("userid", ""+userObject.get("id").getAsInt());
			parameters.put("username", userObject.get("username").getAsString());
			responseModel = mDiscourseApiClient.generateApiKey(parameters);
			
			JsonObject jsonObject1 = null;
			jsonObject1 = jsonParser.parse( ""+responseModel.data ).getAsJsonObject();
			
			if (jsonObject1!=null && jsonObject1.has("api_key")) {
				jsonObject1 = jsonObject1.getAsJsonObject("api_key");
				
				if (jsonObject1.has("key")) {
					userObject.addProperty("api_key"
							, jsonObject1.get("key").getAsString());
				}
			}
		}
		
		/*// old way: everything in Async! 
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
		
		// --- getUser & activate+approve ---
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
				
				// responseModel.data=
				// {
				// 	user: {
				// 		id: 5,
				// 		username: "test_monkey_2",
				// 		avatar_template: "//www.gravatar.com/avatar/e40089fe1c655bd80310df235333973c.png?s={size}&r=pg&d=identicon",
				// 		name: "test_monkey_2",
				// 		email: "test_monkey_2%2540dummy.com",
				// 		...
				// 	}
				// }
				 
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
		*/
		
		
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
		
		// --- createTopic with newly created user's api_key! ---
		String new_username = userObject.get("username").getAsString();
		final DiscourseApiClient mDiscourseApiClient_user = new DiscourseApiClient(
				args[0] // api_url  : e.g. http://your_domain.com
				, userObject.get("api_key").getAsString() // api_key : you get from discourse admin
				, new_username // api_username : you make calls on behalf of
				);
		parameters = new HashMap<String, String>();
		//parameters.put("username", new_username);
		parameters.put("category", "tweets");
		parameters.put("title", "title_test");
		parameters.put("raw", "raw_test");
/*
this.post('posts', { 'title': title, 'raw': raw, 'category': category, 'archetype': 'regular' }, function(error, body, httpCode) {
    callback(error, body, httpCode);
  });
*/
		mDiscourseApiClient_user.createTopic(parameters, new ResponseListener(){

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
