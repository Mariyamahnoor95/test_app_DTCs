package com.example.test_app_DTCs.response;

import com.example.test_app_DTCs.TroubleCode;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class DiagnosticTroubleCodeResponse extends RawResponse {
    public DiagnosticTroubleCodeResponse(byte[] rawResult) {
        super(rawResult);
    }

    public TroubleCode getTroubleCode() {
        return TroubleCode.createFromHex(new String(getRawResult()));
    }

    public String getFormattedString() {
        String description;
        TroubleCode tc = getTroubleCode();
        String decodecode = tc.getDetail().toString();
        String troubleCode = tc.toString();
        try {
            description = " " + tc.getDescription(tc.toString());
        } catch (IOException e) {
            description = StringUtils.EMPTY;
        }
        return tc.toString() + "\nTrouble causing Part: " + tc.getType() + "\nGenre: " + decodecode + "\nDescription: " + description;
    }
}
