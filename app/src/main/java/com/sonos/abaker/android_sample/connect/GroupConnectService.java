package com.sonos.abaker.android_sample.connect;

import android.content.Context;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import com.sonos.abaker.android_sample.R;
import com.sonos.abaker.android_sample.control.response.BaseResponse;
import com.sonos.abaker.android_sample.control.request.BaseRequest;
import com.sonos.abaker.android_sample.control.response.ResponseParser;

import org.json.JSONException;

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

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by alan.baker on 10/25/17.
 */

public class GroupConnectService {
    private static final String LOG_TAG = GroupConnectService.class.getSimpleName();

    private final int CONNECTION_TIMEOUT = 3000;

    private WebSocketFactory factory;
    private WebSocket socket;

    private final PublishSubject<BaseResponse> commandResponsePublishSubject = PublishSubject.create();
    private final PublishSubject<WebSocketState> webSocketStatePublishSubject = PublishSubject.create();

    public final Observable<BaseResponse> commandResponseObservable = commandResponsePublishSubject;
    public final Observable<WebSocketState> webSocketStateObservable = webSocketStatePublishSubject;

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


    public void sendCommand(BaseRequest command) throws JSONException {
        Log.d(LOG_TAG, "Sending Command" + command.toJsonString() );
        socket.sendText(command.toJsonString());
    }

    public void openSocket(final String socketUri) {
        try {
            socket = factory.createSocket(socketUri);
            //socket.addHeader("Sec-WebSocket-Protocol", "v1.api.smartspeaker.audio");
            socket.addHeader("X-Sonos-Api-Key", "4073edd5-afe9-47a2-ae79-4e90fc4f2236");
            socket.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    Log.v(LOG_TAG, "Socket Connected");
                    webSocketStatePublishSubject.onNext(socket.getState());
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    webSocketStatePublishSubject.onNext(socket.getState());
                    commandResponsePublishSubject.onComplete();
                    webSocketStatePublishSubject.onComplete();
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    super.onConnectError(websocket, exception);
                    Log.e(LOG_TAG, "Socket Connection Error", exception);
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    super.onError(websocket, cause);
                    Log.e(LOG_TAG, "Socket Error", cause);
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);
                    Log.v(LOG_TAG, "Socket Response: " + text);
                    try {
                        commandResponsePublishSubject.onNext(ResponseParser.fromJsonString(text));
                    } catch (JSONException e) {
                        commandResponsePublishSubject.onError(e);
                    }
                }

                @Override
                public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
                    super.onTextMessageError(websocket, cause, data);
                    Log.e(LOG_TAG, "Socket Text Error", cause);
                    commandResponsePublishSubject.onError(cause);
                }
            });
            socket.connect();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Socket ERROR", e);
        }
    }

    public void closeSocket() {
        if (socket != null) {
            socket.disconnect();
        }
    }

    public WebSocketState getStatus() {
        if (socket != null) {
            return socket.getState();
        }

        return WebSocketState.CLOSED;
    }
}
