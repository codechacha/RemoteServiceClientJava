package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.remoteservice.IRemoteService;
import com.example.remoteservice.IRemoteServiceCallback;

public class MainActivity2 extends AppCompatActivity {
    public static final String TAG = "MainActivity2";

    private boolean bound = false;
    private IRemoteService iRemoteService = null;
    private IRemoteServiceCallback callback = new IRemoteServiceCallback.Stub() {
        @Override
        public void onItemAdded(String name) {
            Log.d(TAG, "onItemAdded: " + name);
            bound = true;
        }

        @Override
        public void onItemRemoved(String name) {
            Log.d(TAG, "onItemRemoved: " + name);
            bound = false;
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: " + name);
            iRemoteService = IRemoteService.Stub.asInterface(service);
            try {
                iRemoteService.addCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: " + name);
            iRemoteService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bound) {
            Intent intent = new Intent("com.example.remoteservice.MY_SERVICE");
            intent.setPackage("com.example.remoteservice");
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            try {
                iRemoteService.removeCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(connection);
        }
    }
}