package com.utilo.core.properties.file;

import com.utilo.core.exception.PropertyNotFoundException;
import com.utilo.core.exception.PropertyTypeMismatchException;
import com.utilo.core.properties.BaseProperties;

import java.nio.file.ProviderMismatchException;

/**
 * User: Oktay CEKMEZ<br>
 * Date: 04.02.2015<br>
 * Time: 17:14<br>
 */
public class PropertiesFile extends BaseProperties {
    protected String fileName;

    public PropertiesFile() {

    }

    public PropertiesFile(String fileName) {
        this.fileName = fileName;
    }

    public String getFileProperty(String key) throws PropertyNotFoundException {
        String propertyValue = getOptionalFileProperty(key);
        if (propertyValue == null)
            if (fileName != null)
                throw new PropertyNotFoundException(key, fileName);

        return propertyValue;
    }


	public String getOptionalFileProperty(String key) {
		return super.getProperty(key);
	}

	public Integer getOptionalInteger(String key) throws PropertyTypeMismatchException {
		String  value = getOptionalFileProperty(key);
		if(value!=null) {
			try {
				return Integer.parseInt(value);
			}catch (Exception e){
				throw new PropertyTypeMismatchException(key,fileName,"Integer");
			}
		}

		return null;
	}

	public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
