package com.fitreo.wisdmtestapp;

import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {


    //load the tensorflow_inference native library
    static {
        System.loadLibrary("tensorflow_inference");
    }

    //use some constants to specify the path to the model file,
    //the names of the input and output nodes in the computation graph,
    //and the size of the input data as follow
    private static final String MODEL_FILE = "file:///android_asset/optimized_wisdm_model_android.pb";
    private static final String INPUT_NODE_x = "x";
    private static final String INPUT_NODE_h_feat = "h_feat";
    private static final String INPUT_NODE_keep_prob = "keep_prob";
    private static final String OUTPUT_NODE = "y_conv";

    private static final int[] INPUT_SIZE_x = {1,600};
    private static final int[] INPUT_SIZE_h_feat = {1,40};
    private static final int[] INPUT_SIZE_keep_prob = {1,1};
    float[] resu = {0, 0, 0, 0, 0, 0};


    // Create a TensorFlowInferenceInterface instance that we use to make inferences
    // on the graph throughout the app:
    private TensorFlowInferenceInterface inferenceInterface;
    // Accelerometer data
    private AccelerometerDataProcessor accelerometerDataProcessor;


    //UDP streaming
    private static boolean mStream_Active = false;
    public static DatagramSocket mSocket = null;
    public static DatagramPacket mPacket = null;
    private String mIP_Address = "192.168.1.10";
    private String mPort = "5555";
    boolean UDP_Streaming_Enabled = true;
    private String cnnStreamData;

    // For periodic recognition
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initializes the inferenceInterface it and load the model file inside
        //of the onCreate event of the MainActivity
        //then we are ready to perform an inference anywhere in the app
        inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE);
        // create the handler for the accelerometer
        accelerometerDataProcessor = new AccelerometerDataProcessor(this);

        //overrides requirement to keep network stuff in another thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // UI for running the model
        final Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RunCNN();
            }
        });

        mHandler = new Handler();
        startRepeatingTask();
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                RunCNN(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public void RunCNN(){

        double starttime = System.nanoTime();

        if (accelerometerDataProcessor.isQueueFilled()){

            //process
            accelerometerDataProcessor.onReadyToProcessNewAccValues();
            float inputFloats_h_feat[] = accelerometerDataProcessor.getStatisticalFeatures();
            float[] inputFloats_x = accelerometerDataProcessor.inputFloats_x;
            float[] inputFloats_keep_prob = {1};

            //debugging
            Log.d("teststats", "x data: " + accelerometerDataProcessor.dataQueueX);
            Log.d("teststats", "y data: " + accelerometerDataProcessor.dataQueueY);
            Log.d("teststats", "z data: " + accelerometerDataProcessor.dataQueueZ);
            Log.d("teststats", "x mean: " + accelerometerDataProcessor.meanX);
            Log.d("teststats", "x diff: " + accelerometerDataProcessor.diffFromMeanX);
            Log.d("teststats", "x absdiff: " + accelerometerDataProcessor.absDiffFromMeanX);
            Log.d("teststats", "x squareddiff: " + accelerometerDataProcessor.squaredDiffX);
            Log.d("teststats", "x meanabssquareddiff: " + accelerometerDataProcessor.meanAbsDifferenceFromMeanX);
            Log.d("teststats", "x std: " + accelerometerDataProcessor.stdX);
            Log.d("teststats", "mean sqrt pointwise squares " + accelerometerDataProcessor.meanSqrtOfPointwiseSquares);
            Log.d("teststats", "hist x data " + accelerometerDataProcessor.histX);


            //We perform inference by first filling the input nodes
            // with our desired values (as we would do with feed_dict in Python):
            inferenceInterface.feed(INPUT_NODE_x, inputFloats_x, 1, 600);
            inferenceInterface.feed(INPUT_NODE_h_feat, inputFloats_h_feat, 1, 40);
            inferenceInterface.feed(INPUT_NODE_keep_prob, inputFloats_keep_prob);

            //And calling the runInference() method for the OUTPUT_NODE (similar to sess.run()):
            //inferenceInterface.runInference(new String[] {OUTPUT_NODE});
            inferenceInterface.run(new String[]{OUTPUT_NODE}, true);

            //Once the inference is done, we can read the value of the output node:
            inferenceInterface.fetch(OUTPUT_NODE, resu);
        }
        double endtime = (System.nanoTime() - starttime) * java.lang.Math.pow(10.0,-9);
        final TextView textViewR = (TextView) findViewById(R.id.txtViewResult);

        String movement = "";
        if (resu[0] > 0.9)
            movement = "Jogging";
        if (resu[1] > 0.9)
            movement = "Walking";
        if (resu[2] > 0.9)
            movement = "Upstairs";
        if (resu[3] > 0.9)
            movement = "Downstairs";
        if (resu[4] > 0.9)
            movement = "Sitting";
        if (resu[5] > 0.9)
            movement = "Standing";

        String info = movement + " \n " + Float.toString(resu[0]) + "," + Float.toString(resu[1]) + ","
                + Float.toString(resu[2]) + "," + Float.toString(resu[3])+ "," + Float.toString(resu[4]) + ","
                + Float.toString(resu[5]) + " \n " + Double.toString(endtime);

        textViewR.setText(info);




        if(UDP_Streaming_Enabled)
        {
            boolean UDP_Established = start_UDP_Stream();

            if(UDP_Established){
                cnnStreamData = info;
                Log.d("UDP", "UDP streaming sending: ");
                new UDPThread(cnnStreamData).send();
            }
        }
    }


    private boolean start_UDP_Stream()
    {
        boolean isOnWifi = isOnWifi();
        if(isOnWifi == false)
        {
            Log.d("UDP", "Not on wifi");
            return false;
        }


        InetAddress client_adress = null;
        try {
            client_adress = InetAddress.getByName(mIP_Address);
        } catch (UnknownHostException e) {
            Log.d("UDP", "Invalid ip");
            return false;
        }
        try {
            mSocket = new DatagramSocket();
            mSocket.setReuseAddress(true);
        } catch (SocketException e) {
            mSocket = null;
            Log.d("UDP", "Network error 1");
            return false;}

        byte[] buf = new byte[256];
        int port;
        try {
            port = Integer.parseInt(mPort);
            mPacket = new DatagramPacket(buf, buf.length, client_adress, port);
        } catch (Exception e) {
            mSocket.close();
            mSocket = null;
            Log.d("UDP", "Network error 2");
            return false;
        }

        Log.d("UDP", "UDP Stream is ok");
        return true;
    }

    private void stop_UDP_Stream()
    {
        if (mSocket != null)
            mSocket.close();
        mSocket = null;
        mPacket = null;
    }




    private boolean isOnWifi() {
        ConnectivityManager conman = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return conman.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
    }
}
