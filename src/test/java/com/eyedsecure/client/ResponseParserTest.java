package com.eyedsecure.client;

import junit.framework.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: boksman
 * Date: 4/30/13
 * Time: 12:20 PM
 */

public class ResponseParserTest {
    @Test
    public void testParserForNullArg() throws InvalidResponse {
        try {
            @SuppressWarnings("unused")
            ResponseParserDefaultImpl parser = new ResponseParserDefaultImpl();
            parser.parse((InputStream)null);
            fail("Expected an IllegalArgumentException to be thrown.");
        } catch (IllegalArgumentException ioe) {
        } catch (Exception ioe) {
            fail("Encountered an exception " + ioe.toString());
        }
    }

    @Test
    public void testResponseParser() throws InvalidResponse {
        String testResponse = "s=dkjfkdjfkdjfkjdkfj\n" +
                "t=20130430-114821\n" +
                "a=a\n" +
                "tid=CXXXXYYYYZZZZZ\n" +
                "no=xxxxxxxxxxxxxxxxxxxxxxxxxx1xxx\n" +
                "c=" + ResponseCode.NO_SUCH_CLIENT_ID;

        try {
            ResponseParser responseParser = new ResponseParserDefaultImpl();
            Response response = responseParser.parse((new ByteArrayInputStream(testResponse.getBytes("UTF-8"))));
            Assert.assertEquals(response.getResponseCode(), ResponseCode.NO_SUCH_CLIENT_ID);
            Assert.assertEquals(response.getAction(), "a");
            Assert.assertEquals(response.getNonce(), "xxxxxxxxxxxxxxxxxxxxxxxxxx1xxx");
            Assert.assertEquals(response.getServerTimeStamp(), "20130430-114821");
            Assert.assertEquals(response.getSig(), "dkjfkdjfkdjfkjdkfj");
            Assert.assertEquals(response.getTokenId(), "CXXXXYYYYZZZZZ");
        } catch (IOException e) {
            fail("Encountered an exception");
        }
    }

    @Test(expected=InvalidResponse.class)
    public void testMalformedResponse() throws InvalidResponse {
        String testResponse="xx=yy\n" +
                "ben=dana\n";
        try {

            ResponseParser responseParser = new ResponseParserDefaultImpl();
            Response response = responseParser.parse((new ByteArrayInputStream(testResponse.getBytes("UTF-8"))));

        } catch (IOException ioe) {
            fail("Encountered an exception");
        }

        testResponse="xxyy\n" +
                "ben=dana\n";
        try {
            ResponseParser responseParser = new ResponseParserDefaultImpl();
            Response response = responseParser.parse((new ByteArrayInputStream(testResponse.getBytes("UTF-8"))));

        } catch (IOException ioe) {
            fail("Encountered an exception");
        }

        testResponse="xxyy=dana";
        try {
            ResponseParser responseParser = new ResponseParserDefaultImpl();
            Response response = responseParser.parse((new ByteArrayInputStream(testResponse.getBytes("UTF-8"))));

        } catch (IOException ioe) {
            fail("Encountered an exception");
        }

    }
}
