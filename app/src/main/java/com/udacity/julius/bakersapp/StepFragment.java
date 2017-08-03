package com.udacity.julius.bakersapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.julius.bakersapp.model.Step;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepFragment extends Fragment implements ExoPlayer.EventListener {

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1", ARG_STEP_POSITION = "step_position";
    private static final String ARG_STEP_COUNT = "steps_count";
    private static final String TAG = StepFragment.class.getSimpleName();
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private Step mStep;

    private OnFragmentInteractionListener mListener;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    private int mStepPosition, mStepsCount;
    private int resumeWindow;
    private long resumePosition;

    private NotificationManager mNotificationManager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param step Parameter 1.
     * @return A new instance of fragment StepFragment.
     */
    public static StepFragment newInstance(Step step, int position, int count) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, step);
        args.putInt(ARG_STEP_POSITION, position);
        args.putInt(ARG_STEP_COUNT, count);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStep = getArguments().getParcelable(ARG_PARAM1);
            mStepPosition = getArguments().getInt(ARG_STEP_POSITION);
            mStepsCount = getArguments().getInt(ARG_STEP_COUNT);
        }

        clearResumePosition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step, container, false);

        if (mStepPosition == 0) {
            view.findViewById(R.id.prev_button).setVisibility(View.INVISIBLE);
        }

        if (mStepPosition == (mStepsCount - 1)) {
            view.findViewById(R.id.next_button).setVisibility(View.INVISIBLE);
        }

        view.findViewById(R.id.prev_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMoveToPreviousStep(mStepPosition - 1);
            }
        });

        view.findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMoveToNextStep(mStepPosition + 1);
            }
        });

        mPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.playerView);
        TextView stepInstructionText = (TextView) view.findViewById(R.id.step_instruction);

        stepInstructionText.setText(mStep.getDescription());
        initializeMediaSession();

        initializePlayer(Uri.parse(mStep.getVideoURL()));

        setRetainInstance(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer == null) {
            initializePlayer(Uri.parse(mStep.getVideoURL()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            releasePlayer();
        }
        mMediaSession.setActive(false);
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getActivity(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(getActivity(), "BakersApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);

            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                mExoPlayer.seekTo(resumeWindow, resumePosition);
            }
            mExoPlayer.prepare(mediaSource, !haveResumePosition, false);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            updateResumePosition();
            mNotificationManager.cancelAll();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void updateResumePosition() {
        resumeWindow = mExoPlayer.getCurrentWindowIndex();
        resumePosition = mExoPlayer.isCurrentWindowSeekable() ? Math.max(0, mExoPlayer.getCurrentPosition())
                : C.TIME_UNSET;
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

        //      playWhenReady = true (playing);
        //     false (not playing)
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());

        showNotification(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Shows Media Style notification, with actions that depend on the current MediaSession
     * PlaybackState.
     *
     * @param state The PlaybackState of the MediaSession.
     */
    private void showNotification(PlaybackStateCompat state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }


        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (getActivity(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (getActivity(), 0, new Intent(getActivity(), StepFragment.class), 0);

        builder.setContentTitle(getString(R.string.guess))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));

        mNotificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnFragmentInteractionListener {
        void onMoveToNextStep(int position);

        void onMoveToPreviousStep(int position);
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
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
}
