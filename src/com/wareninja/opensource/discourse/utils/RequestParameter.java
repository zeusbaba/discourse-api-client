/***
 *   Copyleft 2014 - WareNinja.com / Rumble In The Jungle!
 * 
 *  @author: yg@wareninja.com
 *  @see https://github.com/WareNinja
 *  disclaimer: I code for fun, dunno what I'm coding about :-)
 */

package com.wareninja.opensource.discourse.utils;


public interface RequestParameter {

	/**
	 * @return the key
	 */
	public abstract String getKey();

	/**
	 * @param key the key to set
	 */
	public abstract void setKey(String key);

	/**
	 * @return the value
	 */
	public abstract Object getValue();
	public abstract String getValueStr();

	/**
	 * @param value the value to set
	 */
	public abstract void setValue(Object value);

	/**
	 * Return the formatted pair.
	 */
	public abstract String format();

}