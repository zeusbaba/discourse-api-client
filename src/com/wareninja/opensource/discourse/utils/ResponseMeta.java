/***
 *   Copyleft 2014 - WareNinja.com / Rumble In The Jungle!
 * 
 *  @author: yg@wareninja.com
 *  @see https://github.com/WareNinja
 *  disclaimer: I code for fun, dunno what I'm coding about :-)
 */

package com.wareninja.opensource.discourse.utils;

import java.io.Serializable;

/*
 * meta part of json response
 */
public class ResponseMeta implements Serializable {

	private static final String TAG = ResponseMeta.class.getSimpleName(); 
	private static final long serialVersionUID = 1L;
	
	public ResponseMeta() {
		this.code = 200;
		this.errorType = "";
		this.errorDetail = "";
	}
	public ResponseMeta(String errorMsg) {
		//new ResponseMeta(errorMsg, false);
		this.code = 400;
		this.errorType = "general";
		this.errorDetail = errorMsg;
	}
	public ResponseMeta(String errorMsg, boolean isNotContent) {
		this.code = isNotContent?204:400;
		this.errorType = "general";
		this.errorDetail = errorMsg;
	}
	
	public Integer code;
	public String errorType;
	public String errorDetail;
	
	@Override
	public String toString() {
		return TAG+" [code=" + code + ", errorType=" + errorType
				+ ", errorDetail=" + errorDetail
				+ "]";
	}
}

/*
{
  "meta": {
    "code": 200
    , "errorType": "blabla"
    , "errorDetail": "blbla"
  },
  ...
}
*/
