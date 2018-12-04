package com.utilo.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * User: Oktay CEKMEZ<br>
 * Date: 04.02.2015<br>
 * Time: 17:14<br>
 */
public class BaseException extends Exception {
    public BaseException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public BaseException(String exceptionMessage, Exception e){
        super(exceptionMessage,e);
    }

    public String getStackTraceSummary(){
        String stackTraceString = getStackTraceString(this);
        if(stackTraceString != null) {
            if(stackTraceString.length() > 500)
                return stackTraceString.substring(500);
            return stackTraceString;
        }
        return "";
    }

    public String getStackTraceString(Exception e) {
        if(this.getStackTrace() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            this.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.toString();
        }
        return  null;
    }
}
