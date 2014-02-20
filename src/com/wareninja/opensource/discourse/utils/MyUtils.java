/***
 *   Copyleft 2014 - WareNinja.com / Rumble In The Jungle!
 * 
 *  @author: yg@wareninja.com
 *  @see https://github.com/WareNinja
 *  disclaimer: I code for fun, dunno what I'm coding about :-)
 */

package com.wareninja.opensource.discourse.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.util.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class MyUtils {
	
	protected static final String TAG = MyUtils.class.getSimpleName();

	public static Gson getGson() {
		return new GsonBuilder()
		    .excludeFieldsWithModifiers( new int[] { 
		    		Modifier.STATIC, Modifier.TRANSIENT//, Modifier.FINAL 
		    		} )
		    .excludeFieldsWithoutExposeAnnotation()
		    .create();
	}
	public static Gson getGsonWithPrettyPrinting() {
		return new GsonBuilder()
		    .excludeFieldsWithModifiers( new int[] { 
		    		Modifier.STATIC, Modifier.TRANSIENT//, Modifier.FINAL 
		    		} )
		    .excludeFieldsWithoutExposeAnnotation()
		    .setPrettyPrinting()
		    .create();
	}
	public static Gson getGsonSimple() {
		return new GsonBuilder()
		    .excludeFieldsWithModifiers( new int[] { 
		    		Modifier.STATIC, Modifier.TRANSIENT//, Modifier.FINAL 
		    		} )
		    .create();
	}
	public static Gson getGsonSimpleWithPrettyPrinting() {
		return new GsonBuilder()
		    .excludeFieldsWithModifiers( new int[] { 
		    		Modifier.STATIC, Modifier.TRANSIENT//, Modifier.FINAL 
		    		} )
		    .setPrettyPrinting()
		    .create();
	}

	/**
	 * getShortFormattedDate 
	 * 
	 * return in string format: yyyy-MM-dd
	 */
	public static String getShortFormattedDate() {
		return getShortFormattedDate(System.currentTimeMillis());
	}
	public static String getShortFormattedDate(long millis) {
		String resp = "";
		try {
			resp = getShortFormattedDate(new Date(millis));
		} catch (Exception ex){}
		return resp;
	}
	public static String getShortFormattedDate(Date date) {
		
		String resp = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			resp = sdf.format(date);
		} catch (Exception ex){}
		return resp;
	}
	 
	/**
	 * getFormattedDate 
	 * 
	 * return in string format: yyyy-MM-dd'T'HH:mm:ssZ
	 */
	public static String getFormattedDate() {
		return getFormattedDate(System.currentTimeMillis());
	}
	public static String getFormattedDate(long millis) {
		String resp = "";
		try {
			resp = getFormattedDate(new Date(millis));
		} catch (Exception ex){}
		return resp;
	}
	public static String getFormattedDate(Date date) {
		
		String resp = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
			resp = sdf.format(date);
		} catch (Exception ex){}
		return resp;
	}
	public static String getFormattedDate(Long millis, String timeZone) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
		if (!TextUtils.isEmpty(timeZone)) sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		
		return millis!=null?sdf.format( new Date(millis) ):"";
	}
	
    /**
     * Generate the multi-part post body providing the parameters and boundary
     * string
     * 
     * @param parameters the parameters need to be posted
     * @param boundary the random string as boundary
     * @return a string of the post body
     */
    public static String encodePostBody(Map<String, Object> parameters, String boundary) {
        if (parameters == null) return "";
        StringBuilder sb = new StringBuilder();
        
        for (String key : parameters.keySet()) {
        	
        	/*//YG:removed this from sdk
        	try{
            if (parameters.getByteArray(key) != null) {
        	    continue;
            }
        	}catch(Exception ex){}
        	*/
        	
            sb.append("Content-Disposition: form-data; name=\"" + key + 
            		//"\"\r\n\r\n" + parameters.getString(key));
            		"\"\r\n\r\n" + parameters.get(key));//to avoid type clash
            sb.append("\r\n" + "--" + boundary + "\r\n");
        }
        
        return sb.toString();
    }

    public static String encodeUrl(Map<String, Object> parameters) {
        if (parameters == null) {
        	return "";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            if (first) first = false; else sb.append("&");
            sb.append(URLEncoder.encode(key) + "=" +
                      URLEncoder.encode(parameters.get(key)+""));
        }
        return sb.toString();
    }

    public static Map<String, String> decodeUrl(String s) {
    	Map<String, String> params = new HashMap<String, String>();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                // YG: in case param has no value
                if (v.length==2){
                	params.put(URLDecoder.decode(v[0]),
                                 URLDecoder.decode(v[1]));
                }
                else {
                	params.put(URLDecoder.decode(v[0])," ");
                }
            }
        }
        return params;
    }

    /**
     * Parse a URL query and fragment parameters into a key-value bundle.
     * 
     * @param url the URL to parse
     * @return a dictionary bundle of keys and values
     */
    public static Map<String, String> parseUrl(String url) {
        
        try {
            URL u = new URL(url);
            Map<String, String> b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
        	//Log.w(TAG, "parseUrl ex ->" + e.toString());
            return new HashMap<String, String>();
        }
    }

    
    public static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    /*
     * building Gravatar URL;
     * 	String email = "someone@somewhere.com";
     * 	String hash = Util.md5Hex(email);
     * 
     * 	http://www.gravatar.com/205e460b479e2e5b48aec07710c08d50.json
     * 
     * 	check here for gravatar profiles: http://en.gravatar.com/site/implement/profiles/json/
     * 
     * OR very simply request the image; http://en.gravatar.com/site/implement/images/ 
     * -> http://www.gravatar.com/avatar/HASH.png
     * By default, images are presented at 80px by 80px if no size parameter is supplied
     * optional; ?s=200   to set size  (1px up to 512px)
     * 
     */
    public static String hex(byte[] array) {
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < array.length; ++i) {
    		sb.append(Integer.toHexString((array[i]& 0xFF) | 0x100).substring(1,3));        
    	}
    	return sb.toString();
    }
    public static String md5Hex (String message) {
    	try {
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		return hex (md.digest(message.getBytes("CP1252")));
      } catch (NoSuchAlgorithmException e) {
      } catch (UnsupportedEncodingException e) {
      }
      return null;
    }
    	
	
	// -> source from CastUtils: https://github.com/apache/pig/blob/89c2e8e76c68d0d0abe6a36b4e08ddc56979796f/src/org/apache/pig/impl/util/CastUtils.java
    private static Integer mMaxInt = Integer.valueOf(Integer.MAX_VALUE);
    private static Long mMaxLong = Long.valueOf(Long.MAX_VALUE);

    public static Double stringToDouble(String str) {
	    if (str == null) {
	    	return null;
	    } else {
		    try {
		    return Double.parseDouble(str);
		    } catch (NumberFormatException e) {
		    	System.err.println(TAG+"|"+ "Unable to interpret value "
		    		    + str
		    		    + " in field being "
		    		    + "converted to double, caught NumberFormatException <"
		    		    + e.getMessage() + "> field discarded");
		    	return null;
		    }
	    }
    }
    public static Float stringToFloat(String str) {
	    if (str == null) {
	    	return null;
	    } else {
		    try {
		    	return Float.parseFloat(str);
		    } catch (NumberFormatException e) {
		    	System.err.println(TAG+"|"+ "Unable to interpret value "
		    		    + str
		    		    + " in field being "
		    		    + "converted to float, caught NumberFormatException <"
		    		    + e.getMessage() + "> field discarded");
		    	return null;
		    }
	    }
    }
    public static Integer stringToInteger(String str) {
	    if (str == null) {
	    	return null;
	    } else {
		    try {
		    	return Integer.parseInt(str);
		    } catch (NumberFormatException e) {
			    // It's possible that this field can be interpreted as a double.
			    // Unfortunately Java doesn't handle this in Integer.valueOf. So
			    // we need to try to convert it to a double and if that works
			    // then
			    // go to an int.
			    try {
				    Double d = Double.valueOf(str);
				    // Need to check for an overflow error
				    if (d.doubleValue() > mMaxInt.doubleValue() + 1.0) {
				    	System.err.println(TAG+"|"+ "Value " + d
							    + " too large for integer");
				    	return null;
				    }
				    return Integer.valueOf(d.intValue());
			    } catch (NumberFormatException nfe2) {
			    	System.err.println(TAG+"|"+ "Unable to interpret value "
						    + str
						    + " in field being "
						    + "converted to int, caught NumberFormatException <"
						    + e.getMessage()
						    + "> field discarded");
			    	return null;
			    }
		    }
	    }
    }
    public static Long stringToLong(String str) {
	    if (str == null) {
	    	return null;
	    } else {
		    try {
		    	return Long.parseLong(str);
		    } catch (NumberFormatException e) {
			    // It's possible that this field can be interpreted as a double.
			    // Unfortunately Java doesn't handle this in Long.valueOf. So
			    // we need to try to convert it to a double and if that works
			    // then
			    // go to an long.
			    try {
				    Double d = Double.valueOf(str);
				    // Need to check for an overflow error
				    if (d.doubleValue() > mMaxLong.doubleValue() + 1.0) {
				    	System.err.println(TAG+"|"+ "Value " + d
							    + " too large for long");
				    	return null;
				    }
				    return Long.valueOf(d.longValue());
			    } catch (NumberFormatException nfe2) {
			    	System.err.println(TAG+"|"+ "Unable to interpret value "
						    + str
						    + " in field being "
						    + "converted to long, caught NumberFormatException <"
						    + nfe2.getMessage()
						    + "> field discarded");
			    	return null;
			    }
		    }
	    }
    }
	// ---
    
}
