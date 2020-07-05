package com.fitreo.wisdmtestapp;

import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class UDPThread  {

    String cnndata;

    public UDPThread(String _cnndata)
    {
        this.cnndata = _cnndata;

    }

    public void send()
    {
        byte bytes [] ;


        try {
            bytes = cnndata.getBytes("UTF-8");
            if (MainActivity.mPacket == null || MainActivity.mSocket == null)
                return ;

            MainActivity.mPacket.setData(bytes);
            MainActivity.mPacket.setLength(bytes.length);

            Log.d("UDP", "UDP message decoded and sending...");
            MainActivity.mSocket.send(MainActivity.mPacket);

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("UDP", "UnsupportedEncodingException");
            return ;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Log.e("Error", "SendBlock");
            Log.d("UDP", "IOException");
            return ;
        }

    }
}
