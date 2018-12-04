package com.utilo.core.properties;

import java.util.Properties;

/**
 * User: Oktay CEKMEZ<br>
 * Date: 04.02.2015<br>
 * Time: 17:14<br>
 */
public class BaseProperties extends Properties {
    public BaseProperties() {

    }

    public BaseProperties(Properties properties) {
        for (Object key : properties.keySet()) {
            this.put(key, properties.get(key));
        }
    }

    public Properties getMatchedProperties(String propertyKeyRegex) {
        return PropertiesUtil.GetMatchedProperties(propertyKeyRegex,this);
    }
}
