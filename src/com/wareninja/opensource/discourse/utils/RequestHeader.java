/***
 *   Copyleft 2014 - WareNinja.com / Rumble In The Jungle!
 * 
 *  @author: yg@wareninja.com
 *  @see https://github.com/WareNinja
 *  disclaimer: I code for fun, dunno what I'm coding about :-)
 */

package com.wareninja.opensource.discourse.utils;


/**
 * An interface used to describe an HTTP Request Header.  Implementations may
 * derive/build headers in any way they see fit (static, dynamic, contact another host, etc).
 * 
 */
public interface RequestHeader {
	
	public String getKey();
	
	public String getValue();
}
