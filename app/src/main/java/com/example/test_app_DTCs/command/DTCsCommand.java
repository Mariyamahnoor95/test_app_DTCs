package com.example.test_app_DTCs.command;

import com.example.test_app_DTCs.response.DiagnosticTroubleCodeResponse;
import com.example.test_app_DTCs.response.Response;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DTCsCommand implements  Command{

    protected ArrayList<Integer> buffer = null;
    protected String rawData = null;
    @Override
    public String getRequest() {
        return "03";
    }

    @Override
    public Response getResponse(byte[] rawResults) {
        return new DiagnosticTroubleCodeResponse(rawResults);
    }

    @Override
    public String readRawData(InputStream inputStream) throws IOException {
        char c;
        StringBuilder res = new StringBuilder();
        while (true) {
            byte read = (byte) inputStream.read();
            byte b = read;
            if (b == -1) {
                // End of input reached
                break; // Exit the loop
            }
            c = (char) b;
            if (c == '>') {
                break; // Exit the loop when '>' is encountered
            }
            res.append(c);
        }

        String replaceAll = res.toString().replaceAll("SEARCHING", StringUtils.EMPTY);
        this.rawData = replaceAll;
        String replaceAll2 = replaceAll.replaceAll("\\s", StringUtils.EMPTY);
        this.rawData = replaceAll2;
        return replaceAll2;
    }

    public String toString() {
        return getClass().getSimpleName();
    }

    public void sendCommand(OutputStream outputStream, String command) throws IOException {
        outputStream.write(command.getBytes());
        outputStream.flush();
    }
}
