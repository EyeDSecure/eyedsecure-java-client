package com.eyedsecure.client;

/**
 * Created with IntelliJ IDEA.
 * User: boksman
 * Date: 2/17/13
 * Time: 11:23 PM
 */
public class App {

    private static final int TOKEN_ID_MIN_LEN = 14;
    private static final int TOKEN_ID_MAX_LEN = 14;

    private static final int PROFILE_ID_MIN_LEN = 1;
    private static final int PROFILE_ID_MAX_LEN = 32;


    private static final int OTP_MIN_LEN = 5;
    private static final int OTP_MAX_LEN = 8;

    private static final int MIN_DPI = 300;
    private static final int MAX_DPI = 3000;

    private static final int MIN_COLOR_ADJ = -50;
    private static final int MAX_COLOR_ADJ = +50;

    public static void main(String args[]) throws Exception {
        String tokenId = null;
        String clientId= null;
        String sharedKey = null;

        String imageProfileId = null;
        String otp = null;

        Integer dpiX = null, dpiY = null;
        Integer red = null, green = null, blue = null;


        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-tid") || args[i].equals("--token-id")) {
                // Token Id
                tokenId = args[i + 1];
            } else if (args[i].equals("-cid") || args[i].equals("--client-id")) {
                // Client API Id
                clientId = args[i + 1];

            } else if (args[i].equals("-sk") || args[i].equals("--shared-key")) {
                // Shared Secret Key
                sharedKey = args[i + 1];
            } else if (args[i].equals("-pid") || args[i].equals("--image-profile-id")) {
                // Image settings profile id, for saved resolutions
                imageProfileId = args[i + 1];
            } else if (args[i].equals("-otp") || args[i].equals("--otp")) {
                // One Time password for validations
                otp = args[i + 1];
            } else if (args[i].equals("-dx") || args[i].equals("--dpi-x")) {
                // Dots per inch x
                dpiX = Integer.parseInt(args[i + 1]);
            } else if (args[i].equals("-dy") || args[i].equals("--dpi-y")) {
                // Dots per inch y
                dpiY = Integer.parseInt(args[i + 1]);
            }
        }

        if (tokenId == null || tokenId.length() > TOKEN_ID_MAX_LEN || tokenId.length() < TOKEN_ID_MIN_LEN) {
            System.err.println("Error: Invalid or missing public-id");
            printUsage();
            return;
        }

        if (clientId == null || clientId.length()!=16) {
            System.err.println("Error: Invalid or missing client-id. Must be 16 chars long.");
            printUsage();
            return;
        }


        String cmd = args[args.length - 1];

        if (cmd.equals("rc") || cmd.equals("request-challenge")) {
            if (imageProfileId != null && (imageProfileId.length() > PROFILE_ID_MAX_LEN || imageProfileId.length() < TOKEN_ID_MIN_LEN)) {
                System.err.println("Error: Invalid image-profile-id");
                printUsage();
                return;
            }


            requestChallenge(tokenId, imageProfileId);

        } else if (cmd.equals("activate")) {
            activate(clientId, tokenId, sharedKey);
        } else if (cmd.equals("deactivate")) {
            if (tokenId == null || tokenId.length() > TOKEN_ID_MAX_LEN || tokenId.length() < TOKEN_ID_MIN_LEN) {
                System.err.println("Error: Invalid or missing public-id");
                printUsage();
                return;
            }


            deactivate(tokenId);

        } else if (cmd.equals("val") || cmd.equals("validate")) {

            if (otp == null || otp.length() > OTP_MAX_LEN || otp.length() < OTP_MIN_LEN) {
                System.err.println("Error: Invalid or missing otp");
                printUsage();
                return;
            }

            if (imageProfileId != null && (imageProfileId.length() > PROFILE_ID_MAX_LEN || imageProfileId.length() < PROFILE_ID_MIN_LEN)) {
                System.err.println("Error: Invalid image-profile-id");
                printUsage();
                return;
            }

            if (imageProfileId != null && dpiX != null && (dpiX > MAX_DPI || dpiX < MIN_DPI)) {
                System.err.println("Error: Invalid dpi-x");
                printUsage();
                return;
            }

            if (imageProfileId != null && dpiY != null && (dpiY > MAX_DPI || dpiY < MIN_DPI)) {
                System.err.println("Error: Invalid dpi-y");
                printUsage();
                return;
            }

            if (imageProfileId != null && red != null && (red > MAX_COLOR_ADJ || red < MIN_COLOR_ADJ)) {
                System.err.println("Error: Invalid red adjustment");
                printUsage();
                return;
            }

            if (imageProfileId != null && green != null && (green > MAX_COLOR_ADJ || green < MIN_COLOR_ADJ)) {
                System.err.println("Error: Invalid green adjustment");
                printUsage();
                return;
            }

            if (imageProfileId != null && blue != null && (blue > MAX_COLOR_ADJ || blue < MIN_COLOR_ADJ)) {
                System.err.println("Error: Invalid blue adjustment");
                printUsage();
                return;
            }

            validateOTP(tokenId, otp, imageProfileId, dpiX, dpiY, red, green, blue);

        } else {
            System.err.println("Error: Missing or invalid command");
            printUsage();
        }

        System.exit(0);
    }

    /**
     * Request a challenge image
     *
     * @param publicId       - public id of the users Eye-D Card
     * @param imageProfileId - [optional] refers to the saved image profile used to generate the image
     */
    private static void requestChallenge(String publicId, String imageProfileId) {
        // todo:
    }


    private static void activate(String clientId, String tokenId, String sharedKey) throws Exception {
        EyeDSecureClient c = new EyeDSecureClient(clientId, sharedKey);
        Response response = c.activate(tokenId, true);

        System.out.println("Response: " + response);

        if (response != null && response.getResponseCode() == ResponseCode.SUCCESS) {
            System.out.println("Token Activated");
        } else {
            System.out.println("Failed to activate token");
        }


    }


    /**
     * Deactivate the token
     *
     * @param publicId - public id of the users Eye-D Card
     */
    private static void deactivate(String publicId) {
        // todo:
    }


    /**
     * Validate given OTP for given challengeID
     * If imageprofileId is specified then save associated image information for later use, this typically indicates a calibration session
     *
     * @param publicId       - public id of the users Eye-D Card
     * @param otp            - One-time password to validate
     * @param imageProfileId - [optional] Image profile id to save, null to not save
     * @param dpiX           - [optional] dpi-x to save with image profile id
     * @param dpiY           - [optional] dpi-y to save with image profile id
     * @param r              - [optional] red adjustment to save with image profile id
     * @param g              - [optional] green adjustment to save with image profile id
     * @param b              - [optional] blue adjustment to save with image profile id
     */
    private static void validateOTP(String publicId, String otp, String imageProfileId, int dpiX, int dpiY, int r, int g, int b) {
        // todo:
    }


    private static void printUsage() {
        System.err.println("\nTest Eye-D Secure client functions against Eye-D Secure server");
        System.err.println("\nUsage: java -jar client.jar <options> <command>");

        System.err.println("\n    java -jar client.jar -tid XXXX-YYYY-ZZZZZ -cid 5 -sk xxxxxxxxxxxxxxxxxxxxxxxdkfjdskjfhjdhf activate");

        System.err.println("\n\nCommands:");
        System.err.println("request-challenge - Request a challenge image from the server");
        System.err.println("validate - Validate a one-time (OTP) password given challenge id");
        System.err.println("activate - Activate given tokenId");
        System.err.println("deactivate - Deactivate given tokenId");

        System.err.println("\n\nOptions:");
        System.err.println("-tid, --token-id         - Eye-D Card public token id");
        System.err.println("-cid, --client-id        - API client id");
        System.err.println("-sk,  --shared-key       - API shared secret key");

        System.err.println("-pid, --image-profile-id - Image profile id, identifier referring to saved profile defining dpi-x, dpi-y, rgb color profile, etc");
        System.err.println("-otp                     - One-time password");
        System.err.println("-dx, --dpi-x             - Horizontal dots per inch for given image profile");
        System.err.println("-dy, --dpi-y             - Vertical dots per inch for given image profile");
        System.err.println("-r, --red                - Red adjustment");
        System.err.println("-g, --green              - Green adjustment");
        System.err.println("-b, --blue               - Blue adjustment");


        System.err.println("\n\nPlace your Eye-D Card over the challenge image to determine OTP");

    }
}

