package com.utilo.core.exception;

public class PropertyTypeMismatchException extends BaseException {
	public PropertyTypeMismatchException(String exceptionMessage) {
		super(exceptionMessage);
	}

	public PropertyTypeMismatchException(String key, String propertiesFileName, String type){
		super("'" + key + "' property is found in " + propertiesFileName + " file but it not an instance of :'"+type+"'" );
	}
}
