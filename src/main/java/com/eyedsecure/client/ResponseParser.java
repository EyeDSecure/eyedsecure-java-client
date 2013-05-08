package com.eyedsecure.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: boksman
 * Date: 5/8/13
 * Time: 3:54 PM
 */
public interface ResponseParser {

    public Response parse(InputStream in) throws IOException, InvalidResponse;



}
