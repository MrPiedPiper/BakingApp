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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
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

    @BindView(R.id.step_scrollview)
    ScrollView scrollView;
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
    static long startPos;
    static boolean isFullScreen = false;

    boolean isInitialized = false;

    SimpleExoPlayer mExoPlayer;

    private static MediaSessionCompat mMediaSession;
    private static final String TAG = StepActivity.class.getSimpleName();
    private PlaybackStateCompat.Builder mStateBuilder;

    private stepFragmentInterface mListener;

    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;
        rootView = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);
        aspectLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        aspectLayout.setAspectRatio(((float)16/9));
        buttonEnableCheck();
        stepPrev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(stepIndex != 0){
                    stepIndex--; }updateLayout();
                buttonEnableCheck(); }});

        stepNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(stepIndex < recipe.getSteps().size() - 1){
                    stepIndex++; }
                    updateLayout();
                buttonEnableCheck(); }
        });


        if(isFullScreen){
            scrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }


        updateLayout();


        isInitialized = true;

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
        stepDetails.setText(currStep.getDescription());
        if(mExoPlayer != null) releasePlayer();
        mListener.stepIndexChanged(stepIndex);
        initializeMediaSession();
        initializePlayer(Uri.parse(currStep.getVideoURL()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof stepFragmentInterface) {
            mListener = (stepFragmentInterface) context;
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

    public interface stepFragmentInterface {
        void stepIndexChanged(int newIndex);
        void videoSeekChanged(long seekPos);
    }

    public void setRecipe(Recipe newRecipe){
        recipe = newRecipe;
    }

    public void setStepIndex(int newStepIndex){
        this.stepIndex = newStepIndex;
        if(isInitialized){
            updateLayout();
        }
    }

    public void setStartPos(long newSeek){
        Log.d("naputest", "seek set to " + newSeek);
        startPos = newSeek;
    }

    public void setFullscreen(boolean isFullscreen){
        this.isFullScreen = isFullscreen;
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
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    context, userAgent), new DefaultExtractorsFactory(), null, null);

            mExoPlayer.seekTo(startPos);

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        }
    }

    private void releasePlayer(){
        startPos = 0;
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
        mListener.videoSeekChanged(mExoPlayer.getCurrentPosition());
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
