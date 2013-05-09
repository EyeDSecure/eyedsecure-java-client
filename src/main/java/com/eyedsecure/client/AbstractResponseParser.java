package com.eyedsecure.client;

import java.util.Map;

/**
 * User: boksman
 * Date: 5/8/13
 * Time: 10:40 PM
 */
public abstract class AbstractResponseParser implements ResponseParser {

    /**
     * Return appropriate response for given set of elements
     * @param responseMap
     * @return
     */
     Response parse( Response response, Map<String, String> responseMap) throws InvalidResponse {
        String action = responseMap.get("a");
        if(action==null) throw new InvalidResponse("Missing action");

        response.setAction(action);

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
            } else if(key.equals("cid") && val.length()>0) {
                response.setChallengeId(val);
            }
        }


        return response;

    }
}
