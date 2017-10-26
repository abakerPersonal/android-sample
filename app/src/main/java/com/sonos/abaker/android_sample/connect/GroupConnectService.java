package com.sonos.abaker.android_sample.connect;

import android.content.Context;
import android.util.Log;

import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;
import com.sonos.abaker.android_sample.R;
import com.sonos.abaker.android_sample.discover.GroupDiscoveryService;
import com.sonos.abaker.android_sample.model.Group;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by alan.baker on 10/25/17.
 */

public class GroupConnectService {
    private static final String LOG_TAG = GroupConnectService.class.getSimpleName();

    private final int CONNECTION_TIMEOUT = 3000;

    private WebSocketFactory factory;
    private WebSocket socket;

    public GroupConnectService(Context context) {
        factory = new WebSocketFactory();
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(context.getResources().openRawResource(R.raw.sonos_truststore), "password".toCharArray());

            TrustManagerFactory trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustMgrFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustMgrFactory.getTrustManagers(), null);

            factory.setConnectionTimeout(CONNECTION_TIMEOUT);
            factory.setVerifyHostname(false);
            factory.setSSLContext(sslContext);
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | CertificateException | IOException exception) {
            Log.e(LOG_TAG, "There was an error initializing the truststore with the certificate", exception);
        }
    }

    public void send(Group group) {
        socket.sendText("[{ \"namespace\": \"groupVolume:1\", \"command\": \"setVolume\",\"householdId\": \"ABCD1234\", \"groupId\": \"XYZ-123abc456:12\"},{  \"volume\": 80 }]");
    }

    public void openSocket(String socketUri) {
        try {
            socket = factory.createSocket(socketUri);
            //socket.addHeader("Sec-WebSocket-Protocol", "v1.api.smartspeaker.audio");
            socket.addHeader("X-Sonos-Api-Key", "4073edd5-afe9-47a2-ae79-4e90fc4f2236");
            socket.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    Log.d(LOG_TAG, ">>>>>>>>>>> Socket Connected");
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    super.onConnectError(websocket, exception);
                    Log.d(LOG_TAG, ">>>>>>>>>>> Socket Disconnected");
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    super.onError(websocket, cause);
                    Log.d(LOG_TAG, ">>>>>>>>>>> Socket ERROR");
                }

                @Override
                public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                    super.onSendError(websocket, cause, frame);

                    Log.d(LOG_TAG, ">>>>>>>>>>> Socket send error");
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);

                    Log.d(LOG_TAG, ">>>>>>>>>>> Socket text");
                }

                @Override
                public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
                    super.onTextMessageError(websocket, cause, data);

                    Log.d(LOG_TAG, ">>>>>>>>>>> Socket text error");
                }
            });

            socket.connect();
        } catch (Exception e) {
            Log.e(LOG_TAG, ">>>>>>>>>>> Socket ERROR", e);
        }
    }

    public WebSocketState getStatus() {
        if (socket != null) {
            return socket.getState();
        }

        return WebSocketState.CLOSED;
    }
}


//    /**
//     * Sets up the SSL context to use for connecting over SSL.
//     */
//    private void connectWebSocket() {
//        // Only trust a Sonos player certificate that has been signed by the Sonos Root Certificate Authority
//        try {
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(context.getResources().openRawResource(R.raw.sonos_truststore), "password".toCharArray());
//
//            TrustManagerFactory trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustMgrFactory.init(keyStore);
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustMgrFactory.getTrustManagers(), null);
//            client.getSSLSocketMiddleware().setSSLContext(sslContext);
//            client.getSSLSocketMiddleware().setTrustManagers(trustMgrFactory.getTrustManagers());
//            client.getSSLSocketMiddleware().setHostnameVerifier(new AllowAllHostnameVerifier());
//
//            AsyncHttpGet request = new AsyncHttpGet(wsAddress.replace("ws://", "http://").replace("wss://", "https://"));
//            request.setTimeout(CONNECTION_TIMEOUT);
//            request.addHeader("Sec-WebSocket-Protocol", "v1.api.smartspeaker.audio");
//            // Send an app key to the player. This should be replaced with your app key, once it is implemented on Sonos players.
//            request.addHeader("X-Sonos-Api-Key", ApiKey.KEY);
//
//            connState = connectionState.CONNECTING;
//            client.websocket(request, null, this);
//        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | CertificateException | IOException exception) {
//            Log.e(LOG_TAG, "There was an error initializing the truststore with the certificate", exception);
//        }
//    }