package com.utilo.core.properties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Oktay CEKMEZ<br>
 * Date: 24.02.2015<br>
 * Time: 12:17<br>
 */

public class PropertiesUtil {
	public static Properties GetMatchedProperties(String propertyKeyRegex, Properties properties) {
		Matcher matcher = Pattern.compile(propertyKeyRegex).matcher("");
		Enumeration<Object> props = properties.keys();
		Properties specifiedProps = new Properties();
		Object prop = null;

		while (props.hasMoreElements()) {
			prop = props.nextElement();
			if (matcher.reset(prop.toString()).matches()) {
				specifiedProps.put(prop, properties.get(prop));
			}
		}

		return specifiedProps;
	}

	public static void writePropertiesToFile(Properties properties, String filePath) throws IOException {
		try {
			FileOutputStream outputStream = new FileOutputStream(filePath);
			properties.store(outputStream, null);
			outputStream.close();
		} catch (Exception e) {
			throw new IOException("Can't write properties to specified path:" + filePath, e);
		}
	}
}
