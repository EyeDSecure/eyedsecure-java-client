package com.eyedsecure.client;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: boksman
 * Date: 4/29/13
 * Time: 8:39 PM
 */
@SuppressWarnings("unused")
public class EyeDSecureClient {

    private EyeDSecureService service;

    protected String clientId;
    protected byte[] sharedKey;

    protected String urls[] = {
            "http://localhost:8080/eyedsecure-server/API", // todo: setup https
            // todo: add secondary
    };

    protected String userAgent;

    private static final Integer OTP_MIN_LEN = 4;
    private static final Integer OTP_MAX_LEN = 10;


    public EyeDSecureClient(String clientId, String sharedKey) {
        setClientId(clientId);
        setSharedKey(sharedKey);
        service = new EyeDSecureService();
    }


    private static String getNonce() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * Send token activation request to server
     * The ClientId must be authorized to send this type of request.
     */
    public Response activate(String tokenId) throws RequestException {
        if(sharedKey==null) throw new IllegalArgumentException("This type of request requires a shared key");
        Map<String, String> reqMap = new HashMap<String, String>();
        String nonce = java.util.UUID.randomUUID().toString().replaceAll("-", "");
        //String action = "activate";
        reqMap.put("no", nonce);
        reqMap.put("id", String.valueOf(clientId));
        //reqMap.put("a", "a");
        reqMap.put("tid", tokenId);

        StringBuilder paramStrBuilder = new StringBuilder();
        for (String key : reqMap.keySet()) {
            if (paramStrBuilder.length() != 0) {
                paramStrBuilder.append("&");
            }
            try {
                paramStrBuilder.append(key).append("=").append(URLEncoder.encode(reqMap.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RequestException("Failed to encode parameter.", e);
            }
        }

        String paramStr = paramStrBuilder.toString();



        // todo: Should shared key be required?
        if (sharedKey != null) {
            String s;
            try {
                s = URLEncoder.encode(Signature.calculate(paramStr, sharedKey), "UTF-8");
            } catch (SignatureException e) {
                throw new RequestException("Failed signing of request", e);
            } catch (UnsupportedEncodingException e) {
                throw new RequestException("Failed to encode signature", e);
            }
            paramStr = paramStr.concat("&s=" + s);
        }


        List<String> serverUrls = new ArrayList<String>();
        for (String url : getUrls()) {
            serverUrls.add(url.concat("/activate?").concat(paramStr));
        }

        Response response = service.fetch(serverUrls, userAgent);


        //if(response.getSig()==null) {
        //    throw new RequestException("Response from server is missing signature.");
        //}

        // Verify the signature
        if (sharedKey != null) {
            StringBuilder keyValueStr = new StringBuilder();
            for (Map.Entry<String, String> entry : response.getKeyValueMap().entrySet()) {
                if ("s".equals(entry.getKey())) {
                    continue;
                }
                if (keyValueStr.length() > 0) {
                    keyValueStr.append("&");
                }
                keyValueStr.append(entry.getKey()).append("=").append(entry.getValue());
            }
            try {
                String signature = Signature.calculate(keyValueStr.toString(), sharedKey).trim();
                if (response.getSig()!=null && !response.getSig().equals(signature) &&
                        !response.getResponseCode().equals(ResponseCode.BAD_SIGNATURE)) {
                    // don't throw a RequestFailure if the server responds with bad signature
                    throw new RequestException("Signatures miss-match");
                }
            } catch (SignatureException e) {
                throw new RequestException("Failed to calculate the response signature.", e);
            }
        }


        // All fields are not returned on an error
        // If there is an error response, don't need to check them.
        if (!ResponseCode.isErrorCode(response.getResponseCode())) {
            // Verify the action
            if (response.getAction() == null || !"a".equals(response.getAction())) {
                throw new RequestException("Token mismatch in response, is there a man-in-the-middle?");
            }


            // Verify the nonce
            if (response.getNonce() == null || !nonce.equals(response.getNonce())) {
                throw new RequestException("Nonce mismatch in response, is there a man-in-the-middle?");
            }

            // Verify the tokenId
            if (response.getTokenId() == null || !tokenId.equals(response.getTokenId())) {
                throw new RequestException("Token mismatch in response, is there a man-in-the-middle?");
            }

            // Verify server time, for additional security you can verify the UTC timestamp is reasonable
            if (response.getServerTimeStamp() == null ) {
                throw new RequestException("Missing server timestamp");
            }

            if(response.getSig()==null) {
                throw new RequestException("Missing signature");
            }

        }


        return response;

    }


    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = Base64.decodeBase64(sharedKey.getBytes());
    }

    public String getSharedKey() {
        return new String(Base64.encodeBase64(this.sharedKey));
    }


    /*public static boolean isValidOTPFormat(String otp) {
        if (otp == null){
            return false; //null strings aren't valid OTPs
        }
        int len = otp.length();
        boolean isPrintable = true;
        for (char c : otp.toCharArray()) {
            if (c < 0x20 || c > 0x7E) {
                isPrintable = false;
                break;
            }
        }
        return isPrintable && (OTP_MIN_LEN <= len && len <= OTP_MAX_LEN);
    } */



}
