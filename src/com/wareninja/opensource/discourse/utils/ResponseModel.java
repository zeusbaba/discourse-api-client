/***
 *   Copyleft 2014 - WareNinja.com / Rumble In The Jungle!
 * 
 *  @author: yg@wareninja.com
 *  @see https://github.com/WareNinja
 *  disclaimer: I code for fun, dunno what I'm coding about :-)
 */

package com.wareninja.opensource.discourse.utils;

import java.io.Serializable;

public class ResponseModel implements Serializable {
    
	private static final String TAG = ResponseModel.class.getSimpleName(); 
	private static final long serialVersionUID = 1L;

	public ResponseMeta meta;// contains meta information
	public Object data;// main body: contains JsonObject or JsonArray of the response
    //public Object notification;// extra: JsonObject used for notifications
	
    public ResponseModel() {
    	this.meta = new ResponseMeta();
    	data = null;
    }
    
	@Override
	public String toString() {
		return TAG+" [" +
				"meta=" + meta 
				//+ ", notification=" + notification
				+ ", data=" + data
				+ "]";
	}
	
/*
{
  "meta": {
    "code": 200,
     ...errorType and errorDetail... only IF there is any error aka code>201
  },
  "data": [
     ...results... JsonObject OR JsonArray depending on the endpoint
  ]
}

> time format is always -> yyyy-MM-ddThh:mm:ssZ
    e.g. 2011-11-07T13:27:26+02:00

 */
}
