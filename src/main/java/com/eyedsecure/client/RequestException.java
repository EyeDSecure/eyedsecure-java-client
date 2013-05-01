package com.eyedsecure.client;

/**
 * Created with IntelliJ IDEA.
 * User: boksman
 * Date: 4/30/13
 * Time: 9:27 AM
 */
public class RequestException extends Exception {
    private static final long serialVersionUID = 1L;

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestException(String message) {
        super(message);
    }
}
