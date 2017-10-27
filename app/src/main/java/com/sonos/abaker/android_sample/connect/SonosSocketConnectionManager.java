package com.sonos.abaker.android_sample.connect;

import android.content.Context;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
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

public class SonosSocketConnectionManager {
    private static final String LOG_TAG = SonosSocketConnectionManager.class.getSimpleName();

    private final int CONNECTION_TIMEOUT = 3000;

    private WebSocketFactory factory;
    private WebSocket socket;

    private final PublishSubject<BaseResponse> commandResponsePublishSubject = PublishSubject.create();
    private final PublishSubject<WebSocketState> webSocketStatePublishSubject = PublishSubject.create();

    public final Observable<BaseResponse> commandResponseObservable = commandResponsePublishSubject;
    public final Observable<WebSocketState> webSocketStateObservable = webSocketStatePublishSubject;

    public SonosSocketConnectionManager(Context context) {
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

    public void openSocket(final String socketUri, WebSocketListener sockectAdapter) {
        try {
            socket = factory.createSocket(socketUri);
            //socket.addHeader("Sec-WebSocket-Protocol", "v1.api.smartspeaker.audio");  TODO: Was not allowing the socket to open but docs say to add it
            socket.addHeader("X-Sonos-Api-Key", "INSERT API KEY HERE");
            socket.addListener(sockectAdapter);
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
