package com.fancystachestudios.bakingapp.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepFragment extends Fragment implements ExoPlayer.EventListener {

    Context context;

    @Nullable
    @BindView(R.id.step_aspect_layout)
    AspectRatioFrameLayout aspectLayout;
    @Nullable
    @BindView(R.id.step_player)
    SimpleExoPlayerView mPlayerView;
    @Nullable
    @BindView(R.id.step_details)
    TextView stepDetails;
    @Nullable
    @BindView(R.id.step_prev_button)
    Button stepPrev;
    @Nullable
    @BindView(R.id.step_next_button)
    Button stepNext;

    static Recipe recipe;
    static int stepIndex;
    static Step currStep;

    boolean isInitialized = false;

    SimpleExoPlayer mExoPlayer;

    private static MediaSessionCompat mMediaSession;
    private static final String TAG = StepActivity.class.getSimpleName();
    private PlaybackStateCompat.Builder mStateBuilder;

    private onChangeStep mListener;

    boolean isLandscape = false;

    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;
        if(getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            isLandscape = true;
            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fullscreen_exoplayer, container, false);
        }else{
            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_step, container, false);

        }
        ButterKnife.bind(this, rootView);

        if(isLandscape) {
            ((AppCompatActivity )getActivity()).getSupportActionBar().hide();
        }else{
            aspectLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            aspectLayout.setAspectRatio(((float)16/9));
            buttonEnableCheck();
            stepPrev.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(stepIndex != 0){
                        stepIndex--;
                    }
                    updateLayout();
                    buttonEnableCheck();
                }
            });

            stepNext.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(stepIndex < recipe.getSteps().size() - 1){
                        stepIndex++;
                    }
                    updateLayout();
                    buttonEnableCheck();
                }
            });

        }

        updateLayout();


        isInitialized = true;

        Log.d("naputest", "(OnCreateView) Stepindex "+stepIndex);

        return rootView;
    }

    private void buttonEnableCheck(){
        if(stepIndex == 0){
            stepPrev.setEnabled(false);
        }else{
            stepPrev.setEnabled(true);
        }

        if(stepIndex == recipe.getSteps().size() - 1){
            stepNext.setEnabled(false);
        }else{
            stepNext.setEnabled(true);
        }
    }

    private void updateLayout(){
        currStep = recipe.getSteps().get(stepIndex);
        if(!isLandscape){
            stepDetails.setText(currStep.getDescription());
        }
        if(mExoPlayer != null) releasePlayer();
        mListener.stepIndexChanged(stepIndex);
        initializeMediaSession();
        initializePlayer(Uri.parse(currStep.getVideoURL()));
        Log.d("naputest", "Playing "+currStep.getVideoURL());

        Log.d("naputest", "(updateLayout) Stepindex "+stepIndex);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof onChangeStep) {
            mListener = (onChangeStep) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onChooseStep");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface onChangeStep {
        void stepIndexChanged(int newIndex);
    }

    public void setRecipe(Recipe newRecipe){
        recipe = newRecipe;
        Log.d("naputest", "step recipe set to "+recipe.getName());
    }

    public void setStepIndex(int newStepIndex){
        this.stepIndex = newStepIndex;
        if(isInitialized){
            updateLayout();
        }
    }


    private void initializeMediaSession(){
        mMediaSession = new MediaSessionCompat(context, TAG);

        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        mMediaSession.setCallback(new MySessionCallback());

        mMediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri){
        if(mExoPlayer == null){
            RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(context, "BakingApp");
            Log.d("naputest", mediaUri.toString());
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    context, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        }
    }

    private void releasePlayer(){
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mExoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == Player.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        }else if(playbackState == Player.STATE_READY){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback{
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

}
