package com.example.test_app_DTCs.response;

public class RawResponse implements Response {
    private byte[] rawResult;

    public RawResponse(byte[] rawResult2) {
        this.rawResult = rawResult2;
    }

    public byte[] getRawResult() {
        return this.rawResult;
    }

    public String getFormattedString() {
        return new String(this.rawResult);
    }
}
