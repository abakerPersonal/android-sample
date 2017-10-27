package com.sonos.abaker.android_sample;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.neovisionaries.ws.client.WebSocketState;
import com.sonos.abaker.android_sample.connect.GroupConnectService;
import com.sonos.abaker.android_sample.control.request.BaseRequest;
import com.sonos.abaker.android_sample.control.request.SetMuteRequest;
import com.sonos.abaker.android_sample.control.request.SubscribeRequest;
import com.sonos.abaker.android_sample.control.response.BaseResponse;
import com.sonos.abaker.android_sample.control.request.SetGroupVolumeCommandRequest;
import com.sonos.abaker.android_sample.control.response.GroupVolumeResponse;
import com.sonos.abaker.android_sample.control.response.PlaybackResponse;
import com.sonos.abaker.android_sample.control.response.PlaybackMetadataResponse;
import com.sonos.abaker.android_sample.control.response.ResponseFilter;
import com.sonos.abaker.android_sample.control.response.ResponseObserver;
import com.sonos.abaker.android_sample.databinding.ControlActvityBinding;
import com.sonos.abaker.android_sample.handlers.ControlActivityHelper;
import com.sonos.abaker.android_sample.model.ControlActivityPageModel;
import com.sonos.abaker.android_sample.model.Group;

import org.json.JSONException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class ControlActivity extends AppCompatActivity implements ControlActivityHelper {
    private static final String LOG_TAG = ControlActivity.class.getSimpleName();

    public static final String GROUP_EXTRA = "group";

    private GroupConnectService groupConnectService;
    private Group group;

    ControlActvityBinding binding;
    private SeekBar seekBar;
    private ToggleButton muteButton;
    private ProgressBar progressBar;
    private final ControlActivityPageModel pageModel = new ControlActivityPageModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupConnectService = new GroupConnectService(this);
        getExtras();


        groupConnectService.webSocketStateObservable.subscribe(new Observer<WebSocketState>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull WebSocketState webSocketState) {
                if (webSocketState == WebSocketState.OPEN) {
                    setupAdditionalObservers();
                    subscribeUpdates(group);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        ControlActvityBinding binding = DataBindingUtil.setContentView(this, R.layout.control_actvity);
        Bundle extras = getIntent().getExtras();
        this.group = (Group) extras.getSerializable(GROUP_EXTRA);
        binding.setGroup(group);
        binding.setHandler(this);


        new ConnectAsyncTask().execute(group);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindUIElements();
    }

    public void setVolume(int progress) {
        try {
            groupConnectService.sendCommand(new SetGroupVolumeCommandRequest(group, progress));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error sending command", e);
        }
    }

    public void mute(boolean mute) {
        try {
            groupConnectService.sendCommand(new SetMuteRequest(group, mute));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error sending command", e);
        }
    }

    public void subscribeUpdates(Group group) {
        try {
            groupConnectService.sendCommand(new SubscribeRequest(BaseRequest.Namespace.GROUP_VOLUME, group));
            groupConnectService.sendCommand(new SubscribeRequest(BaseRequest.Namespace.PLAYBACK_METADATA, group));
            groupConnectService.sendCommand(new SubscribeRequest(BaseRequest.Namespace.PLAYBACK, group));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error sending command", e);
        }
    }

    @Override
    public void onClickSetVolumeToZero(View view) {
        try {
            groupConnectService.sendCommand(new SetGroupVolumeCommandRequest(group, 0));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error sending command", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        groupConnectService.closeSocket();
    }

    private class ConnectAsyncTask extends AsyncTask<Group, Void, Void> {

        @Override
        protected Void doInBackground(Group... group) {
            groupConnectService.openSocket(group[0].getWebsocketURL());
            Log.d(LOG_TAG, groupConnectService.getStatus().toString());
            return null;
        }
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        group = (Group) extras.getSerializable(GROUP_EXTRA);
    }

    private void bindUIElements() {
        binding = DataBindingUtil.setContentView(this, R.layout.control_actvity);
        binding.setGroup(group);
        binding.setHandler(this);
        binding.setPageModel(pageModel);
        binding.executePendingBindings();

        seekBar = (SeekBar) findViewById(R.id.group_seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        muteButton = (ToggleButton) findViewById(R.id.group_mute_button);
        muteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mute(isChecked);
            }
        });
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.group_playback_progress_bar);

    }

    private void setupAdditionalObservers() {

        groupConnectService
                .commandResponseObservable
                .filter(new ResponseFilter(GroupVolumeResponse.class))
                .subscribe(new ResponseObserver() {
                    @Override
                    public void onNext(@NonNull BaseResponse response) {
                        GroupVolumeResponse groupVolumeResponse = (GroupVolumeResponse) response;
                        pageModel.volume.set(groupVolumeResponse.getVolume());
                        pageModel.muted.set(groupVolumeResponse.isMuted());
                    }
                });

        groupConnectService
                .commandResponseObservable
                .filter(new ResponseFilter(PlaybackMetadataResponse.class))
                .subscribe(new ResponseObserver() {
                    @Override
                    public void onNext(@NonNull BaseResponse response) {
                        super.onNext(response);
                        pageModel.updateMetadata((PlaybackMetadataResponse) response);
                    }
                });

        groupConnectService
                .commandResponseObservable
                .filter(new ResponseFilter(PlaybackResponse.class))
                .subscribe(new ResponseObserver() {
                    @Override
                    public void onNext(@NonNull BaseResponse response) {
                        super.onNext(response);
                        PlaybackResponse playbackResponse = (PlaybackResponse) response;
                        pageModel.currentPlaybackPosition.set(playbackResponse.getPositionMillis());
                        //TODO: Need to start timer to continously update timer
                    }
                });
    }
}
