package com.taxi.nanny.utils.socket;

import android.util.Log;

public interface ConnectionInterface
{
       void callSocketConnect(Object... args);
       void callSocketDisconnect(Object...args);
       void callonConnectError(Object...args);
       void callonTimeConnectError(Object...args);
       void callUpdateLocation(Object...args);
}
