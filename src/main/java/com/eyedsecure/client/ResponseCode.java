/* Copyright (c) 2011, Linus Widströmer.  All rights reserved.

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:
  
   * Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  
   * Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following
     disclaimer in the documentation and/or other materials provided
     with the distribution.
 
   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
   CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
   BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
   TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
   ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
   TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
   THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
   SUCH DAMAGE.
 
   Written by Linus Widströmer <linus.widstromer@it.su.se>, January 2011.
*/

package com.eyedsecure.client;

public enum ResponseCode {
    SUCCESS,                // The request was successful
    NO_SUCH_CLIENT_ID,      // The request id does not exist.
    BAD_SIGNATURE,          // The signature verification failed.
    MISSING_PARAMETER,      // The request lacks a parameter.
    OPERATION_NOT_ALLOWED,  // Operation is not authorised for given client id
    SERVER_ERROR,           // Server Error
    REPLAYED_REQUEST;       // Server has seen the request/Nonce combination before


    /**
     * Is the response code considered an error
     *
     * @return boolean
     */
    public static boolean isErrorCode(ResponseCode code) {
        return (
                ResponseCode.NO_SUCH_CLIENT_ID.equals(code) ||
                        ResponseCode.BAD_SIGNATURE.equals(code) ||
                        ResponseCode.MISSING_PARAMETER.equals(code) ||
                        ResponseCode.OPERATION_NOT_ALLOWED.equals(code) ||
                        ResponseCode.SERVER_ERROR.equals(code) ||
                        ResponseCode.REPLAYED_REQUEST.equals(code)

        );
    }


}

