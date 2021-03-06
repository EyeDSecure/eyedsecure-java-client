package com.eyedsecure.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: boksman
 * Date: 4/30/13
 * Time: 10:35 AM
 */
public class ResponseParserChallengeRequestImpl extends AbstractResponseParser {


    public Response parse(InputStream inputStream) throws IOException, InvalidResponse {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream argument was null");
        }

        ChallengeRequestResponse response = new ChallengeRequestResponse();


        DataInputStream in = new DataInputStream(inputStream);
        int imageLength = in.readInt();
        byte[] image = new byte[imageLength];
        in.readFully(image, 0, imageLength);
        response.setImage(image);
        int mapLen = in.readInt();
        byte[] map = new byte[mapLen];
        in.readFully(map, 0, mapLen);
        String mapString = new String(map, "UTF-8");
        int sigLen = in.readInt();
        byte[] sig = new byte[sigLen];
        in.readFully(sig, 0, sigLen);
        response.setSig(new String(sig, "UTF-8"));


        // We use a TreeMap so we get consistent signature
        Map<String, String> responseMap = new TreeMap<String, String>();

        String keyValPair[] = mapString.split("&");

        for (String keyVal : keyValPair) {
            int delimiterIndex = keyVal.indexOf("=");
            if (delimiterIndex == -1) continue; // Malformed line
            String key = keyVal.substring(0, delimiterIndex);
            String val = keyVal.substring(delimiterIndex + 1);
            responseMap.put(key, val);

            if (key.equals("cid") && val.length() > 0) {
                response.setChallengeId(val);
            }

        }

        in.close();

        return parse(response, responseMap);
    }


}
