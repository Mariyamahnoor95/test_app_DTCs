package com.example.test_app_DTCs.command;

import com.example.test_app_DTCs.response.Response;

import java.io.IOException;
import java.io.InputStream;

public interface Command {
    String getRequest();
    Response getResponse(byte[] bArr);
    String readRawData(InputStream inputStream) throws IOException;
}
