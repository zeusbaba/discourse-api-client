/***
 *   Copyleft 2014 - WareNinja.com / Rumble In The Jungle!
 * 
 *  @author: yg@wareninja.com
 *  @see https://github.com/WareNinja
 *  disclaimer: I code for fun, dunno what I'm coding about :-)
 */

package com.wareninja.opensource.discourse.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


public abstract class ResponseListener {
	
	//private static final String TAG = ResponseListener.class.getSimpleName();
	
	public void onBegin() {
	}
	public void onBegin(String info) {
	}
	public void onComplete(String response) {
	}
	public void onComplete_wModel(ResponseModel responseModel) {
	}
	
    public void onError(String e) {
    }
    public void onError_wMeta(ResponseMeta responseMeta) {
    }
    
    public void onFileNotFoundException(FileNotFoundException e) {
        e.printStackTrace();
    }
    public void onIOException(IOException e) {
        e.printStackTrace();
    }
    public void onMalformedURLException(MalformedURLException e) {
        e.printStackTrace();
    }
}
