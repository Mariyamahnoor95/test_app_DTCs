package com.example.test_app_DTCs;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test_app_DTCs.command.DTCsCommand;
import com.example.test_app_DTCs.enums.Description;
import com.example.test_app_DTCs.response.DiagnosticTroubleCodeResponse;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import org.apache.commons.lang3.StringUtils;

public class MainActivity extends AppCompatActivity {
    String address;
    AlertDialog alertDialog;
    String desc;
    DiagnosticTroubleCodeResponse dg1;
    DiagnosticTroubleCodeResponse dg2;
    DTCsCommand dtCsCommand;
    Button dtc;
    TextView dtcRes;
    List<String[]> dtcdataslist;
    String dtcintResponse;
    EditText dtcip;
    ArrayList<String> dtcsloglist;
    String dtcstatusResponse;
    Button getdtc;
    String input;
    Integer locToken;
    String mobile_ad;
    String[] postdata;
    String rawData;
    TroubleCode tc;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private List<String> allDTCs = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allDTCs.add("P0100");
        allDTCs.add("P0101");
        allDTCs.add("P0702");
        allDTCs.add("P0103");
        allDTCs.add("P0123");
        allDTCs.add("P0104");
        allDTCs.add("P0105");
        allDTCs.add("P0106");
        allDTCs.add("P0107");
        allDTCs.add("P0200");
        allDTCs.add("P0300");
        allDTCs.add("P0400");
        allDTCs.add("P0500");
        allDTCs.add("P0600");
        allDTCs.add("P0700");

        // Initialize variables and UI elements
        locToken = 0;

        dtcRes = findViewById(R.id.dtcResult);
        getdtc = findViewById(R.id.button);
        dtc = findViewById(R.id.dtc);
        dtcdataslist = new ArrayList<>();
        dtcsloglist = new ArrayList<>();
        logwriter();

        // Set click listeners for buttons
        getdtc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Check if the input is empty or too short
                boolean b = dtcip.getText().toString().length() < 5;
                if (dtcip.getText().toString().isEmpty() || b) {
                    Toast.makeText(MainActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                    return;
                }
                input = dtcip.getText().toString();
                String description = Description.getDescription(input);
                tc = TroubleCode.createFromString(input);

                // Check if the TroubleCode is valid and has a description
                if (!tc.isValid() || description == null) {
                    Toast.makeText(MainActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                    Log.w("ContentValues", "Invalid code");
                    return;
                }

                try {
                    TroubleCode.Type type = tc.getType();
                    desc = tc.getDescription(input);
                    dtcintResponse = tc.toString() + "\nTrouble causing Part: " + type + "\nGenre: " + tc.getDetail() + "\nDescription: " + desc;
                    System.out.println(dtcintResponse);
                    dtcRes.setText(StringUtils.EMPTY);
                    dtcRes.setText(dtcintResponse);
                    dtcip.setText(StringUtils.EMPTY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        dtc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {

                        obdConnect();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ContentValues", "Error occurred");
                    Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            // Check if all permissions were granted
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // All permissions are granted, you can proceed with your functionality.
            } else {
                // Some permissions were denied, handle this situation gracefully.
                // You may want to inform the user about the importance of these permissions.
            }
        }
    }

    /* The obdConnect method handles the Bluetooth connection and communication with the OBDII device. */

    private void obdConnect() throws IOException {
        // Check for Bluetooth permissions
        int permission = androidx.core.app.ActivityCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_CONNECT");
        if (permission != 0) {
            String[] permissions = new String[]{"android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.WRITE_EXTERNAL_STORAGE"};
            androidx.core.app.ActivityCompat.requestPermissions(this, permissions, 0);
        }

        android.bluetooth.BluetoothAdapter bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
        android.bluetooth.BluetoothManager bluetoothManager = null;

        // Check for Bluetooth support on the device
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            bluetoothManager = (android.bluetooth.BluetoothManager) getSystemService(android.bluetooth.BluetoothManager.class);
        }

        // Check if Bluetooth is enabled
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();

                Log.e("ContentValues", "Error occurred");
            } else {
                Log.i("ContentValues", "Bluetooth enabled");
            }
        } else {
            Log.w("ContentValues", "Device does not support Bluetooth");
            return;
        }

        // Get paired Bluetooth devices
        java.util.Set<android.bluetooth.BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // Iterate through paired devices to find the OBDII device
        for (android.bluetooth.BluetoothDevice device : pairedDevices) {
            if (device.getName() != null && device.getName().equals("OBDII")) {
                mobile_ad = device.getAddress();
                Log.i("ContentValues", "Pairing with OBDII device...");
            }
        }

        Log.d("ContentValues", mobile_ad);

        // Check if the OBDII device address was found
        if (mobile_ad == null) {
            Toast.makeText(this, "Device not found", Toast.LENGTH_SHORT).show();
            Log.w("ContentValues", "Device not found, cannot pair");
            return;
        }

        android.bluetooth.BluetoothDevice obdDevice = bluetoothAdapter.getRemoteDevice(mobile_ad);
        String uuid = "00001101-0000-1000-8000-00805f9b34fb";
        java.util.UUID obdUuid = java.util.UUID.fromString(uuid);

        android.bluetooth.BluetoothSocket socket = obdDevice.createRfcommSocketToServiceRecord(obdUuid);

        // Check if the socket is connected or connect to the OBDII device
        if (!socket.isConnected()) {
            try {
                socket.connect();
                Toast.makeText(this, "Connected successfully", Toast.LENGTH_SHORT).show();
                Log.d("ContentValues", "Connected successfully");
            } catch (IOException e) {
                Toast.makeText(this, "Error connecting!!", Toast.LENGTH_SHORT).show();
                Log.e("ContentValues", "Error occurred");
            }
        }

        // Get input and output streams for communication
        java.io.InputStream inputStream = socket.getInputStream();
        java.io.OutputStream outputStream = socket.getOutputStream();

        // create a new string array for the responses
        String[] responses = new String[allDTCs.size() + 1];

        // set the first element to the current timestamp
        responses[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Iterate through the list of DTCs and retrieve responses
        for  (int i = 0; i < allDTCs.size(); i++) {
            String dtc = allDTCs.get(i);
            try {
                dtCsCommand = new DTCsCommand();
                dtCsCommand.sendCommand(outputStream, dtc);
                rawData = dtCsCommand.readRawData(inputStream);

                Log.i("ContentValues", "Output response received: " + rawData);
                // store the response in the responses array
                responses[i + 1] = rawData;

                // Store DTC and response in a list
//                dtcdataslist.add(new String[]{dtc, rawData});

                Log.i("ContentValues", "DTC received");

                // Show a toast message indicating success
                Toast.makeText(this, "Response received", Toast.LENGTH_SHORT).show();

                // Add a log entry to the list
                dtcsloglist.add("Response received");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ContentValues", "Error occurred");
                Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
        // add the responses to the dtcdataslist
        dtcdataslist.add(responses);

        // Write the data to a CSV file
        writer(dtcdataslist);

        Log.i("ContentValues", "Response received");

        // Close the Bluetooth socket
        socket.close();
    }

    // Write data to a CSV file
    private void writer(List<String[]> data) {

        try {
            // Define the base directory path
            File baseDir = new File(getExternalFilesDir(null), "test_app_apk_to_soursecodelogs");
            // Create the directories if they don't exist
            if (!baseDir.exists()) {
                if (baseDir.mkdirs()) {
                    Log.d("ContentValues", "Created folder: " + baseDir.getAbsolutePath());
                } else {
                    Log.e("ContentValues", "Failed to create folder: " + baseDir.getAbsolutePath());
                }
            } else {
                Log.d("ContentValues", "Folder already exists: " + baseDir.getAbsolutePath());
            }

            // Define the CSV file
            File file = new File(baseDir, "dtc.csv");

            // Create the CSV file if it doesn't exist
            if (!file.exists()) {
                if (file.createNewFile()) {
                    Log.d("ContentValues", "Created CSV file: " + file.getAbsolutePath());
                    dtcdataslist.add(0, new String[]{"DTC", "Response"});
                } else {
                    Log.e("ContentValues", "Failed to create CSV file: " + file.getAbsolutePath());
                }
            } else {
                Log.d("ContentValues", "CSV file already exists: " + file.getAbsolutePath());
            }

            // Write data to the CSV file
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file, true));
            Log.e("ContentValues", " the whole data " + data);
            csvWriter.writeAll(data);
            csvWriter.close();

            Toast.makeText(this, "DTCs recorded successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "DTCs not recorded", Toast.LENGTH_SHORT).show();
            Log.i("ContentValues", "Error adding records, DTCs not recorded");
        }
    }

    // Write logs to a text file
    // Write logs to a text file
    private void logwriter() {
        Log.w("before", "Logcat save");
        try {
            String currentTime = new SimpleDateFormat("HH_mm_ss", Locale.getDefault()).format(new Date());

            // Define the base directory path
            File baseDir = new File(Environment.getExternalStorageDirectory(), "test_app_apk_to_soursecodelogs");

            // Create the directories if they don't exist
            if (!baseDir.exists()) {
                if (baseDir.mkdirs()) {
                    Log.d("ContentValues", "Created folder: " + baseDir.getAbsolutePath());
                } else {
                    Log.e("ContentValues", "Failed to create folder: " + baseDir.getAbsolutePath());
                }
            } else {
                Log.d("ContentValues", "Folder already exists: " + baseDir.getAbsolutePath());
            }

            // Define the logcat file
            File file = new File(baseDir, "logcat_" + currentTime + ".txt");

            if (!file.exists()) {
                if (file.createNewFile()) {
                    Log.d("ContentValues", "Created logcat file: " + file.getAbsolutePath());
                } else {
                    Log.e("ContentValues", "Failed to create logcat file: " + file.getAbsolutePath());
                }
            } else {
                Log.d("ContentValues", "Logcat file already exists: " + file.getAbsolutePath());
            }

            Runtime.getRuntime().exec("logcat -f " + file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


