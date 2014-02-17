package com.wareninja.opensource.discourse;

import java.util.HashMap;
import java.util.Map;

import com.wareninja.opensource.discourse.utils.ResponseListener;
import com.wareninja.opensource.discourse.utils.ResponseMeta;
import com.wareninja.opensource.discourse.utils.ResponseModel;

/*
 * simple class to... 
 * make calls for quick validation of each implemented function! 
 */
public class DummyMonkey {

	/**
	 * @param args
	 * api_url api_key api_username
	 */
	public static void main(String[] args) {
		
		DiscourseApiClient mDiscourseApiClient = new DiscourseApiClient(
				args[0] // api_url  e.g. http://your_domain.com
				, args[1] // api_key you get from discourse admin
				, args[2] // api_username  you make calls on behalf of
				);
		
		Map<String, String> parameters = new HashMap<String, String>();
		
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
