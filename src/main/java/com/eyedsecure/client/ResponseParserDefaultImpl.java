package com.eyedsecure.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: boksman
 * Date: 4/30/13
 * Time: 10:35 PM
 */
public class ResponseParserDefaultImpl extends AbstractResponseParser {


    public Response parse(InputStream inputStream) throws IOException, InvalidResponse {
        if(inputStream == null) {
            throw new IllegalArgumentException("InputStream argument was null");
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        // We use a TreeMap so we get consistent signature
        Map<String, String> responseMap = new TreeMap<String, String>();
        String line;
        while ((line = in.readLine()) != null) {
            int delimiterIndex=line.indexOf("=");
            if(delimiterIndex==-1) continue; // Malformed line

            String key=line.substring(0,delimiterIndex);
            String val=line.substring(delimiterIndex+1);
            responseMap.put(key, val);
        }
        in.close();

        return super.parse(new Response(), responseMap);
    }




}
