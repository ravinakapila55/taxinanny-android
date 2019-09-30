package com.taxi.nanny.utils.socket;

import android.content.Context;
import android.util.Log;
import com.taxi.nanny.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SocketConnection
{
    Context context;
    ConnectionInterface connectionInterface;
    public Socket mSocket;

    public static String TAG=SocketConnection.class.getSimpleName();

    public SocketConnection(Context context, ConnectionInterface connectionInterface)
    {
        this.context = context;
        this.connectionInterface = connectionInterface;
    }


    public void connectSocket(String rideId)
    {
        try {
            Log.e(TAG, "connectSocket: "+"inside");
            Log.e(TAG, "Url: "+Constant.SOCKET_URL+rideId);
            mSocket = IO.socket(Constant.SOCKET_URL+rideId);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);
            mSocket.on("callbackLoc",callbackLoc);
            mSocket.connect();
            //emitRoomJoin();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void emitUpdateLocation(String lat,String lng,String bearing,String accuracy,String speed,String altitude)
    {
//        if (!mSocket.connected()) return;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat",lat);
            jsonObject.put("lng",lng);
            jsonObject.put("bearing",bearing);
            jsonObject.put("accuracy",accuracy);
            jsonObject.put("speed",speed);
            jsonObject.put("altitude",altitude);
            Log.e(TAG, "emitUpdateLocation: Params "+jsonObject.toString());
            mSocket.emit("updateLoc",jsonObject);
            Log.e(TAG, "emitUpdateLocation: Afterrrrr "+jsonObject.toString());

         /*   mSocket.emit("updateLoc", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    JSONObject object1 = (JSONObject) args[0];
                    Log.e("EmitUpdate", "call:message " + object1.toString());
                }
            });*/

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    private Emitter.Listener onConnect = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onConnect"+args.length);
            Log.e(TAG, "call: onConnect"+args.toString());

            connectionInterface.callSocketConnect(args);
            emitUpdateLocation("30.7046","76.7179",
                    "1","2","34","1");
        }
    };


    private Emitter.Listener callbackLoc = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            Log.e(TAG, "call: callbackLoc"+args.length);
            Log.e(TAG, "call: callbackLoc"+args.toString());
            for (int jj = 0; jj < args.length; jj++)
            {
                Log.e(TAG, "updateLocationResponse " + args[jj]);
            }
            connectionInterface.callUpdateLocation(args);
        }
    };


    public void disconnectSocket()
    {
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);
        mSocket.off("callbackLoc", callbackLoc);
    }



    private Emitter.Listener onDisconnect = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onDisconnect"+args.length );
            connectionInterface.callSocketDisconnect(args);
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onConnectError"+args.length);
           connectionInterface.callonConnectError(args);
        }
    };

    private Emitter.Listener onTimeConnectError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onTimeConnectError"+args.length );
            connectionInterface.callonTimeConnectError(args);
        }
    };



}
