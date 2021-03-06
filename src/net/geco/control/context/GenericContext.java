/**
 * Copyright (c) 2013 Simon Denier
 * Released under the MIT License (see LICENSE file)
 */
package net.geco.control.context;

import java.util.HashMap;
import java.util.Properties;

/**
 * @author Simon Denier
 * @since May 6, 2013
 *
 */
public class GenericContext extends HashMap<String, Object> {

	public static GenericContext createSingleElement(String key, Object value) {
		GenericContext context = new GenericContext();
		context.put(key, value);
		return context;
	}
	
	public ContextList createContextList(String key) {
		return createContextList(key, 10);
	}

	public ContextList createContextList(String key, int initialCapacity) {
		put(key, new ContextList(initialCapacity));
		return (ContextList) get(key);
	}

	public void mergeProperties(Properties props) {
		for (String key : props.stringPropertyNames()) {
			put(key, props.get(key));
		}
	}
	
}
