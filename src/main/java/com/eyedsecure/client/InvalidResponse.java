package com.eyedsecure.client;

/**
 * Created with IntelliJ IDEA.
 * User: boksman
 * Date: 4/30/13
 * Time: 9:27 AM
 */
public class InvalidResponse extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidResponse(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidResponse(String message) {
        super(message);
    }
}
