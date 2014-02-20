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
		
		DiscourseApiClient mDiscourseApiClient = new DiscourseApiClient(
				args[0] // api_url  : e.g. http://your_domain.com
				, args[1] // api_key : you get from discourse admin
				, args[2] // api_username : you make calls on behalf of
				);
		
		Map<String, String> parameters = null;
		
		// --- createUser ---
		/*
		// createUser parameters MUST already contain
		'name': name,
	    'email': email,
	    'username': username,
	    'password': password,
	    */
		parameters = new HashMap<String, String>();
		parameters.put("name", "test_monkey_1");
		parameters.put("email", "test_monkey_1@dummy.com");
		parameters.put("username", "test_monkey_1");
		parameters.put("password", "test_monkey_1_pwd");
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
		
		
		// --- searchForUser ---
		parameters = new HashMap<String, String>();
		parameters.put("username", "test_monkey_1");
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
		parameters.put("term", "test_monkey");
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
		
		// --- getUser ---
		parameters = new HashMap<String, String>();
		mDiscourseApiClient.getUser(parameters, new ResponseListener(){

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
