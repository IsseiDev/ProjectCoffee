package com.example.coffee.uitemplate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;

/**
 * Plays a YouTube video in a YouTube fragment and handles adding video to the content queue
 */

public class VideoDetail extends YouTubeFailureRecoveryActivity implements YouTubePlayer.OnInitializedListener {
    public static final String TAG = "VideoDetail";

    private String videoId;
    private String videoTitle;
    private String videoDescription;
    private String videoThumbnailUrl;
    private String videoChannelTitle;
    private int videoTimestamp;

    private Context context;
    private YouTubePlayer player;

    private MsgManager msgManager = null;

    private String jsonifiedVideo;
    public Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.videoId = (String) getIntent().getSerializableExtra("videoId");
        this.videoTitle = (String) getIntent().getSerializableExtra("videoTitle");
        this.videoDescription = (String) getIntent().getSerializableExtra("videoDescription");
        this.videoChannelTitle = (String) getIntent().getSerializableExtra("channelTitle");
        this.videoThumbnailUrl = (String) getIntent().getSerializableExtra("thumbnailUrl");
        try {
            this.videoTimestamp = Integer.parseInt((String) getIntent().getSerializableExtra("timestamp"));
        } catch (Exception e) {
            this.videoTimestamp = 0;
        }

        this.context = this;

        setContentView(R.layout.activity_video_detail);

        Bundle bundle = getIntent().getExtras();
        Boolean queueStart = bundle.getBoolean("queuestart");

        // handles back button
        Button backBtn = (Button)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // handles send/add button
        Button sendBtn = (Button)findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });


        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    /**
     * Creates alert that pops up when user presses the Add button
     * Adds video to the queue or cancels adding the video
     */
    private void createDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Add to Queue?");
        alert.setCancelable(false);
        final JSONObject jsonVideo = new JSONObject();
        try {
            jsonVideo.put("action", "VIDEO");
            jsonVideo.put("videoId", videoId);
            jsonVideo.put("videoTitle", videoTitle);
            jsonVideo.put("channelTitle", videoChannelTitle);
            jsonVideo.put("videoDescription", videoDescription);
            jsonVideo.put("thumbnailUrl", videoThumbnailUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // add to queue
                try {
                    jsonVideo.put("videoTimestamp", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonifiedVideo = jsonVideo.toString();

                intent = new Intent();
                intent.putExtra("message", jsonifiedVideo);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // auto-generated method stub
            }
        });
        alert.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_queue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menu_now_playing) {
            // bring to kudos page or content master mode ?
            // need logic to determine who is who
            startActivity(new Intent(VideoDetail.this, Kudos.class));
            return true;
        }
        if (id == R.id.menu_queue) {
            // bring to queue page
            startActivity(new Intent(VideoDetail.this, Queue.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets YouTube player listener and starts video when initialization finishes successfully
     * @param provider YouTube player provider
     * @param player The YouTube player
     * @param wasRestored Checks if player was restored or not
     */
    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            this.player = player;
            this.player.setPlayerStateChangeListener(playerStateChangeListener);
            this.player.loadVideo(this.videoId, this.videoTimestamp * 1000);
        }
    }

    /**
     * Makes a YouTube Player Fragment
     * @return YouTube Player Fragment
     */
    @Override
    protected Provider getYouTubePlayerProvider() {
        return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
    }

    /**
     * Container method for listening to YouTube player state
     */
    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {

        }

        @Override
        public void onLoaded(String arg0) {

        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onVideoStarted() {

        }
    };
}
