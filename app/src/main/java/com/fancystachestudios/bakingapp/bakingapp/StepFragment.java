package com.fancystachestudios.bakingapp.bakingapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

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
    @BindView(R.id.step_thumbnail)
    ImageView stepThumbnail;
    @Nullable
    @BindView(R.id.step_details)
    TextView stepDetails;
    @Nullable
    @BindView(R.id.step_prev_button)
    Button stepPrev;
    @Nullable
    @BindView(R.id.step_next_button)
    Button stepNext;

    private static Recipe recipe;
    private static int stepIndex;
    private static Step currStep;
    private static long startPos;
    static boolean isFullScreen = false;
    private static boolean playOnResume = true;

    boolean isInitialized = false;

    static SimpleExoPlayer mExoPlayer;

    private static MediaSessionCompat mMediaSession;
    private static final String TAG = StepActivity.class.getSimpleName();
    private PlaybackStateCompat.Builder mStateBuilder;

    private stepFragmentInterface mListener;

    private boolean exoIsPlaying = false;

    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;
        rootView = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);

        if(savedInstanceState != null){
            startPos = savedInstanceState.getLong(getString(R.string.step_seek_pos_fragment));
            playOnResume = savedInstanceState.getBoolean(getString(R.string.step_play_on_resume_fragment));
        }

        aspectLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float aspectRatio;
        if(displayMetrics.widthPixels > displayMetrics.heightPixels){
            aspectRatio = ((float) displayMetrics.widthPixels / (float) displayMetrics.heightPixels);
        }else{
            aspectRatio = ((float) displayMetrics.heightPixels / (float) displayMetrics.widthPixels);
        }
                aspectLayout.setAspectRatio(aspectRatio);
        buttonEnableCheck();
        //String thumbnail = Picasso.get().load(currStep.getThumbnailURL()).;
        //mExoPlayer.get
        stepPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stepIndex != 0) {
                    stepIndex--;
                }
                startPos = 0;
                updateLayout();
                refreshVideo();
                buttonEnableCheck();
            }
        });

        stepNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stepIndex < recipe.getSteps().size() - 1) {
                    stepIndex++;
                }
                startPos = 0;
                updateLayout();
                refreshVideo();
                buttonEnableCheck();
            }
        });


        if (isFullScreen) {
            scrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }


        updateLayout();
        refreshVideo();

        isInitialized = true;

        return rootView;
    }

    private void buttonEnableCheck() {
        if (stepIndex == 1) {
            stepPrev.setEnabled(false);
        } else {
            stepPrev.setEnabled(true);
        }

        if (stepIndex == recipe.getSteps().size() - 1) {
            stepNext.setEnabled(false);
        } else {
            stepNext.setEnabled(true);
        }
    }

    private void updateLayout() {
        currStep = recipe.getSteps().get(stepIndex - 1);
        stepDetails.setText(currStep.getDescription());

        if (context instanceof stepFragmentInterface) {
            mListener = (stepFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onChooseStep");
        }
        mListener.stepIndexChanged(stepIndex);
    }

    private void refreshVideo(){
        releasePlayer();
        initializeMediaSession();
        initializePlayer(Uri.parse(currStep.getVideoURL()));
        Log.d("naputest", "vid "+currStep.getVideoURL());
        if(currStep.getVideoURL().equals("")){
            Log.d("naputest", currStep.getThumbnailURL());
            if(!currStep.getThumbnailURL().equals("")){
                Log.d("naputest", "setting image "+currStep.getThumbnailURL()
                );
                Picasso.get().load(currStep.getThumbnailURL()).into(stepThumbnail);
                stepThumbnail.setVisibility(View.VISIBLE);
                mPlayerView.setVisibility(View.INVISIBLE);
            }
        }else{
            stepThumbnail.setVisibility(View.INVISIBLE);
            mPlayerView.setVisibility(View.VISIBLE);
        }
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
        void playOnResumeChanged(boolean playOnResume);
    }

    public void setRecipe(Recipe newRecipe){
        recipe = newRecipe;
    }

    public void setStepIndex(int newStepIndex){
        this.stepIndex = newStepIndex;
        if(isInitialized){
            updateLayout();
            refreshVideo();
        }
    }

    public void setStartPos(long newSeek){
        startPos = newSeek;
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

            mExoPlayer.prepare(mediaSource, false, true
            );
            mExoPlayer.setPlayWhenReady(playOnResume);

        }
    }

    private void releasePlayer(){
        if(mExoPlayer != null){
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
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
        if(mExoPlayer != null) {
            if (exoIsPlaying){
                //playOnResume = true;
            }else{
                //playOnResume = false;
            }
            //mListener.playOnResumeChanged(playOnResume);
            //mExoPlayer.setPlayWhenReady(false);
        }
        playOnResume = mExoPlayer.getPlayWhenReady();
        if(Util.SDK_INT <= 23){
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();/*
        if(mExoPlayer != null) {
            if (exoIsPlaying){
                playOnResume = true;
            }else{
                playOnResume = false;
            }
            mListener.playOnResumeChanged(playOnResume);
            mExoPlayer.setPlayWhenReady(false);
        }*/
        if(Util.SDK_INT > 23){
            releasePlayer();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Util.SDK_INT > 23){
            initializeMediaSession();
            initializePlayer(Uri.parse(currStep.getVideoURL()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.SDK_INT <= 23 || mExoPlayer == null){
            initializeMediaSession();
            initializePlayer(Uri.parse(currStep.getVideoURL()));
        }
        if(mExoPlayer != null){
            //playOnResume = mExoPlayer.getPlayWhenReady();
            initializePlayer(Uri.parse(currStep.getVideoURL()));
            mExoPlayer.setPlayWhenReady(playOnResume);
        }
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
            exoIsPlaying = true;
        }else if(playbackState == Player.STATE_READY){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
            exoIsPlaying = false;
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mExoPlayer != null){
            outState.putLong(getString(R.string.step_seek_pos_fragment), mExoPlayer.getCurrentPosition());
        }
        outState.putBoolean(getString(R.string.step_play_on_resume_fragment), playOnResume);

    }
}
