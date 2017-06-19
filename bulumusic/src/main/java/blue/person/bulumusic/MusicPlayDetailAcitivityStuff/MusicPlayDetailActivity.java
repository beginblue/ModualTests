package blue.person.bulumusic.MusicPlayDetailAcitivityStuff;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.List;

import blue.person.bulumusic.MusicPlayDetailAcitivityStuff.UiStuff.LrcView;
import blue.person.bulumusic.MusicPlayDetailAcitivityStuff.selectLRCStuff.selectLRCActivity;
import blue.person.bulumusic.R;
import blue.person.bulumusic.ShareDataApplication;
import blue.person.internetstuff.LRCRequests;
import blue.person.internetstuff.RequestListEntity;
import blue.person.music.Music;
import blue.person.musicplaystuff.musicControl.BroadcastReceivers;
import blue.person.musicplaystuff.musicControl.PlayController;
import blue.person.musicstuff.DBStuff.DBController;

public class MusicPlayDetailActivity extends AppCompatActivity {

    private static final String TAG = "MusicPlayDetailActivity";
    private PlayController mPlayController;
    private String lrc;
    private LrcView lrcView;
    private BroadcastReceivers mBroadcastReceivers;
    private DBController mDBController;
    private List<Music> mMusicList;
    private SeekBar mSeekBar;
    private ImageView mCover;
    private boolean mIsLoadTaskRunning = false;
    private downloadLRC downloadLRCTask;
    private boolean UpdateTurn = false;
    private long duration;
    private FrameLayout blackBack;
    private Toolbar bar;
    private boolean isPlaying = true;
    private ImageButton btnStart;
    private ImageButton btnNext;
    private ImageButton btnLast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play_detail);

        lrcView = (LrcView) findViewById(R.id.lrcView);
        mSeekBar = (SeekBar) findViewById(R.id.sb_prograssBar);
        mCover = (ImageView) findViewById(R.id.iv_cover);
        mDBController = ((ShareDataApplication) getApplication()).getDBController();
        mPlayController = ((ShareDataApplication) getApplication()).getPlayController(null);
        mMusicList = mPlayController.getMusicList();
        blackBack = (FrameLayout) findViewById(R.id.blackback);
        mSeekBar.setIndeterminate(false);
        Music music = mMusicList.get(mPlayController.getCurrentIndex());
        mCover.setImageBitmap(music.getCover());
        btnStart = (ImageButton) findViewById(R.id.btn_start);
        btnNext = (ImageButton) findViewById(R.id.btn_forward);
        btnLast = (ImageButton) findViewById(R.id.btn_back);
        bar = (Toolbar) findViewById(R.id.toolbar);
        bar.setTitle(music.getTitle());
        setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayDetailActivity.this.finish();
            }
        });
//        getSupportActionBar().hide();
        duration = mPlayController.getDuration();

        downloadLRCTask = new downloadLRC();
        downloadLRCTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, music.getTitle(), mPlayController.getDuration() + "");

        lrcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float alpha = Math.abs(blackBack.getAlpha() - 1f);
                Log.e(TAG, "onClick: " + alpha);
                blackBack.setAlpha(alpha);
                lrcView.setAlpha(alpha);
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int position = (int) ((progress * duration / 100));
                    position = position > duration ? (int) (duration - 1000) : position;
                    mPlayController.seekTo(position);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastReceivers = new BroadcastReceivers(MusicPlayDetailActivity.this);
        setBroadcastResolvers();
    }

    private boolean mPause = false;

    private void setBroadcastResolvers() {
        mBroadcastReceivers.setOnStartListener(new BroadcastReceivers.BroadcastResolver() {
            @Override
            public void run() {
                UpdateTurn = false;
                Music music = mMusicList.get(mPlayController.getCurrentIndex());
                mCover.setImageBitmap(music.getCover());
                bar.setTitle(music.getTitle());
                btnStart.setImageResource(android.R.drawable.ic_media_pause);
                mSeekBar.setProgress(0);
                new downloadLRC().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, music.getTitle(), mPlayController.getDuration() + "");
                Log.e(TAG, "run: order test start");

            }
        });

        mBroadcastReceivers.setOnPauseListener(new BroadcastReceivers.BroadcastResolver() {
            @Override
            public void run() {
                if(!mPause) {
                    btnStart.setImageResource(android.R.drawable.ic_media_play);
                }else {
                    btnStart.setImageResource(android.R.drawable.ic_media_pause);
                }
                    Log.e("OrderTests", "run: order test pause");
                Log.i(TAG, "run: pause in the detail activity");
            }
        });
        mBroadcastReceivers.setOnStopListener(new BroadcastReceivers.BroadcastResolver() {
            @Override
            public void run() {
                Log.i(TAG, "run: stop in the detail activity");

                UpdateTurn = false;
            }
        });
        mBroadcastReceivers.setOnNextListener(new BroadcastReceivers.BroadcastResolver() {
            @Override
            public void run() {


                Log.e("OrderTests", "run: order test next");
            }
        });

    }

    public void searchLRC() {
        Looper.prepare();
        Music currentMusic = mMusicList.get(mPlayController.getCurrentIndex());
        LRCRequests requests = new LRCRequests();
        try {
            List<RequestListEntity.CandidatesBean> lrcList = requests.requestList(
                    currentMusic.getTitle(),
                    mPlayController.getDuration())
                    .getCandidates();
            if (lrcList != null && lrcList.size() > 0) {
                String accessKey = lrcList.get(0).getAccesskey();
                String id = lrcList.get(0).getId();

                lrc = requests.getLRCs(accessKey, id);
                Log.i(TAG, "searchLRC: " + lrc);
                lrcView.post(new Runnable() {
                    @Override
                    public void run() {
                        assert lrcView != null;
                        lrcView.loadLrc(lrc);
                        UpdateTurn = true;
                    }
                });
            } else {
                lrcView.post(new Runnable() {
                    @Override
                    public void run() {
                        String nullStr = "";
                        lrcView.loadLrc(nullStr);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Looper.loop();
    }

    private void updateLRC() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mPlayController.getCurrentPosition() < mPlayController.getDuration() - 100) {
                    try {
                        lrcView.post(new Runnable() {
                            @Override
                            public void run() {
                                lrcView.updateTime(mPlayController.getCurrentPosition());
                                long progress = 0;
                                try {
                                    progress = 100 * mPlayController.getCurrentPosition() / mPlayController.getDuration();
                                } catch (Exception e) {
                                    Log.e(TAG, "run: ", e);
                                } finally {
                                    mSeekBar.setProgress((int) progress);
                                }
                            }
                        });
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        mBroadcastReceivers.unregisterAllBroadcastReceivers();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void btn_click(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_forward:
                UpdateTurn = false;
                mPlayController.next();
                break;
            case R.id.btn_back:
                UpdateTurn = false;
                mPlayController.last();
                break;
            case R.id.btn_start:
                mPlayController.pause();
                mPause = !mPause;
                break;
            default:
                break;

        }
    }


    public class downloadLRC extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            long duration = Long.valueOf(params[1]);
            String res = "";
            Log.e(TAG, "doInBackground: downloading start");
            LRCRequests requests = new LRCRequests();
            try {
                List<RequestListEntity.CandidatesBean> lrcList = requests.requestList(
                        title, duration)
                        .getCandidates();
                if (lrcList != null && lrcList.size() > 0) {
                    String accessKey = lrcList.get(0).getAccesskey();
                    String id = lrcList.get(0).getId();
                    res = requests.getLRCs(accessKey, id);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }


        @Override
        protected void onPostExecute(String s) {
            Log.e(TAG, "onPostExecute: " + s);
            lrcView.loadLrc(s);
            UpdateTurn = true;
            new lrcUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPlayController);
        }
    }

    public PlayController getPlayController() {
        return mPlayController;

    }


    /**
     * 更新歌词
     */
    public class lrcUpdateTask extends AsyncTask<PlayController, Integer, Boolean> {

        PlayController playController;

        @Override
        protected Boolean doInBackground(PlayController... params) {
            playController = params[0];
            mIsLoadTaskRunning = true;
            Log.e(TAG, "doInBackground: updating");
            while (UpdateTurn) {

                try {
                    publishProgress((int) playController.getCurrentPosition());
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mIsLoadTaskRunning = false;
            Log.e(TAG, "onPostExecute: updating stopped");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            lrcView.updateTime(values[0]);

            mSeekBar.setProgress((int) ((values[0] * 100 / duration)));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, selectLRCActivity.class);
        intent.putExtra(
                selectLRCActivity.SONG_NAME,
                mMusicList.get(mPlayController.getCurrentIndex()).getTitle()
        );
        intent.putExtra(
                selectLRCActivity.SONG_DURATION,
                mPlayController.getDuration()
        );
        startActivityForResult(intent, selectLRCActivity.RESULT_CODE);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (data == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String accessKey = data.getStringExtra(selectLRCActivity.ACCESS_KEY);
                String id = data.getStringExtra(selectLRCActivity.ID);
                final String lrCs = new LRCRequests().getLRCs(accessKey, id);
                lrcView.post(new Runnable() {
                    @Override
                    public void run() {
                        lrcView.loadLrc(lrCs);
                    }
                });
            }
        }).start();
    }
}
