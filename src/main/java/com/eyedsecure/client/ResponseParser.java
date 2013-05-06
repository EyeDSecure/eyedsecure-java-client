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
 * Time: 10:35 AM
 */
public class ResponseParser {
    private InputStream inputStream;


    public ResponseParser(InputStream in) {
        if(in == null) {
            throw new IllegalArgumentException("InputStream argument was null");
        }
        this.inputStream = in;
    }


    public Response parse() throws IOException, InvalidResponse {
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

        return parse(responseMap);
    }

    /**
     * Return appropriate response for given set of elements
     * @param responseMap
     * @return
     */
    private Response parse(Map<String, String> responseMap) throws InvalidResponse {
        String action = responseMap.get("a");
        if(action==null) throw new InvalidResponse("Missing action");

        Response response = new Response();
        response.setAction(action);
        /*if(action.equals("a")) {
            // Activate Token Response
            response = new Response();
            response.setAction("a");
        } else if(action.equals("d")) {
            // Deactivate Token Response
            response = new Response();
            response.setAction("deactivate");
        } else {
            throw new InvalidResponse("Invalid action");
        } */

        response.setKeyValueMap(responseMap);
        for(String key: responseMap.keySet()) {
            String val = responseMap.get(key);

            if(key.equals("no")) {
                response.setNonce(val);
            } else if(key.equals("tid")) {
                response.setTokenId(val);
            } else if(key.equals("s")) {
                response.setSig(val);
            } else if ("t".equals(key)) {
                response.setServerTimeStamp(val);
            } else if (key.equals("c") && val.length()>0)  {
                response.setResponseCode(ResponseCode.valueOf(val));
            }
        }


        //if(response.getResponseCode()==null) throw new InvalidResponse("Missing response code");
        //if(response.getNonce()==null) throw new InvalidResponse("Missing nonce");
        //if(response.getServerTimeStamp()==null) throw new InvalidResponse("Missing server timestamp");
        //if(response.getTokenId()==null) throw new InvalidResponse("Missing token id");


        return response;

    }


}
