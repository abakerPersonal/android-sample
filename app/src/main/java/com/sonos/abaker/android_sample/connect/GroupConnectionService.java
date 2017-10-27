package com.sonos.abaker.android_sample.connect;

import android.content.Context;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import com.sonos.abaker.android_sample.control.request.BaseRequest;
import com.sonos.abaker.android_sample.control.request.SetGroupVolumeCommandRequest;
import com.sonos.abaker.android_sample.control.request.SetMuteRequest;
import com.sonos.abaker.android_sample.control.request.SubscribeRequest;
import com.sonos.abaker.android_sample.control.response.BaseResponse;
import com.sonos.abaker.android_sample.control.response.ResponseParser;
import com.sonos.abaker.android_sample.model.Group;

import org.json.JSONException;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by alan.baker on 10/27/17.
 */

public class GroupConnectionService {
    private static final String LOG_TAG = GroupConnectionService.class.getSimpleName();

    private final SonosSocketConnectionManager socketConnectionManager;

    private final PublishSubject<BaseResponse> commandResponsePublishSubject = PublishSubject.create();
    private final PublishSubject<WebSocketState> webSocketStatePublishSubject = PublishSubject.create();

    public final Observable<BaseResponse> commandResponseObservable = commandResponsePublishSubject;
    public final Observable<WebSocketState> webSocketStateObservable = webSocketStatePublishSubject;

    private final WebSocketAdapter groupSocketAdapter = new WebSocketAdapter() {
        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            Log.v(LOG_TAG, "Socket Connected");
            webSocketStatePublishSubject.onNext(websocket.getState());
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            webSocketStatePublishSubject.onNext(websocket.getState());
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
    };

    public GroupConnectionService(Context context) {
        socketConnectionManager = new SonosSocketConnectionManager(context);
    }

    public void connectToGroup(Group group) {
        socketConnectionManager.openSocket(group.getWebsocketURL(), groupSocketAdapter);
    }

    public void disconnectFromGroup() {
        socketConnectionManager.closeSocket();
    }

    public void setVolume(Group group, int progress) {
        try {
            socketConnectionManager.sendCommand(new SetGroupVolumeCommandRequest(group, progress));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error sending command", e);
        }
    }

    public void mute(Group group,boolean mute) {
        try {
            socketConnectionManager.sendCommand(new SetMuteRequest(group, mute));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error sending command", e);
        }
    }

    public void subscribeUpdates(Group group, BaseRequest.Namespace... namespaces) {
        try {
            for (BaseRequest.Namespace namespace: namespaces) {
                socketConnectionManager.sendCommand(new SubscribeRequest(namespace, group));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error sending command", e);
        }
    }
}
