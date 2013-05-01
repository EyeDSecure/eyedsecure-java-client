package com.eyedsecure.client;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EyeDSecureClientTest {

    private EyeDSecureClient client = null;

    /*
     * API key for signing/verifying requests.
     *
     */
    private final String clientId = "0000000000000001";
    private final String sharedKey = "0123456789";
    private final String testTokenId = "CXXXXYYYYZZZZZ";

    @Before
    public void setup() {
        client = new EyeDSecureClient(clientId, sharedKey);
    }

    @Test
    public void testMissingParam() throws RequestException {
        client.setClientId(null);
        Response response = client.activate(testTokenId);
        assertEquals(ResponseCode.MISSING_PARAMETER, response.getResponseCode());
    }

    @Test
    public void testShortClientId() throws RequestException {
        client.setClientId("xxx");
        Response response = client.activate(testTokenId);
        assertEquals(ResponseCode.NO_SUCH_CLIENT_ID, response.getResponseCode());
    }


    @Test
    public void testNoSuchClientId() throws RequestException {
        client.setClientId("000000000000000X");
        Response response = client.activate(testTokenId);
        assertEquals(ResponseCode.NO_SUCH_CLIENT_ID, response.getResponseCode());
    }


    @Test
    public void testOperationNotAllowed() throws RequestException {
        client.setClientId("0000000000000002");     // User not allowed any functions
        Response response = client.activate(testTokenId);
        assertEquals(ResponseCode.OPERATION_NOT_ALLOWED, response.getResponseCode());
    }



    @Test
    public void testActivate() throws RequestException {
        Response response = client.activate(testTokenId);
        assertNotNull(response);
        assertEquals(response.getTokenId(), testTokenId);
        assertEquals(response.getAction(), "activate");
        assertEquals(ResponseCode.SUCCESS, response.getResponseCode());
    }

    @Test
    public void testBadSignature() throws RequestException {
        client.setSharedKey("badapikey");
        Response response = client.activate(testTokenId);
        assertEquals(ResponseCode.BAD_SIGNATURE, response.getResponseCode());
    }


    // todo: add additional test

}
