package com.utilo.core.exception;

/**
 * User: Oktay CEKMEZ<br>
 * Date: 04.02.2015<br>
 * Time: 17:14<br>
 */
public class PropertyNotFoundException extends BaseException {
    public PropertyNotFoundException(String key){
        super("Property '" + key + "' was not found");
    }

    public PropertyNotFoundException(String key, String propertiesFileName){
        super("'" + key + "' property is not found in " + propertiesFileName + " file." );
    }
}
