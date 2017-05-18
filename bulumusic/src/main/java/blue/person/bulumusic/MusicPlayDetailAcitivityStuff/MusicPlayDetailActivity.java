package blue.person.bulumusic.MusicPlayDetailAcitivityStuff;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import blue.person.bulumusic.MusicPlayDetailAcitivityStuff.UiStuff.LrcView;
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

    private boolean UpdateTurn = false;


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

        mSeekBar.setIndeterminate(false);
        Music music = mMusicList.get(mPlayController.getCurrentIndex());
        mCover.setImageBitmap(music.getCover());
        setTitle(music.getTitle());
        new downloadLRC().execute(getPlayController());
        new lrcUpdateTask().execute(getPlayController());

        lrcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MusicPlayDetailActivity.this, "test", Toast.LENGTH_SHORT).show();
                int color = (lrcView.isClicked()) ? R.color.colorLrcViewBackgroundClicked : R.color.colorLrcViewBackgroundNotClicked;
                lrcView.setBackgroundResource(color);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastReceivers = new BroadcastReceivers(MusicPlayDetailActivity.this);
        setBroadcastResolvers();
    }

    private void setBroadcastResolvers() {
        mBroadcastReceivers.setOnStartListener(new BroadcastReceivers.BroadcastResolver() {
            @Override
            public void run() {
                Log.e("OrderTests", "run: order test start");
                Music music = mMusicList.get(mPlayController.getCurrentIndex());
                mCover.setImageBitmap(music.getCover());
                setTitle(music.getTitle());
                UpdateTurn = false;
                new downloadLRC().execute(mPlayController);
            }
        });

        mBroadcastReceivers.setOnPauseListener(new BroadcastReceivers.BroadcastResolver() {
            @Override
            public void run() {
                Log.e("OrderTests", "run: order test pause");
                Log.i(TAG, "run: pause in the detail activity");
            }
        });
        mBroadcastReceivers.setOnStopListener(new BroadcastReceivers.BroadcastResolver() {
            @Override
            public void run() {
                Log.i(TAG, "run: stop in the detail activity");
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
                mPlayController.next();
                break;
            case R.id.btn_back:
                mPlayController.seekTo((int) (mPlayController.getCurrentPosition() - 1000L));
                break;
            case R.id.btn_start:
                mPlayController.pause();
                break;
            default:
                break;

        }
    }


    public class downloadLRC extends AsyncTask<PlayController, Integer, String> {

        PlayController playController;

        @Override
        protected String doInBackground(PlayController... params) {
            playController = params[0];
            String res = "";
            Music currentMusic = mMusicList.get(playController.getCurrentIndex());
            LRCRequests requests = new LRCRequests();
            try {
                List<RequestListEntity.CandidatesBean> lrcList = requests.requestList(
                        currentMusic.getTitle(),
                        playController.getDuration())
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
            Log.i(TAG, "onPostExecute: " + s);
            lrcView.loadLrc(s);
            UpdateTurn = true;
            // if(!mIsLoadTaskRunning) new lrcUpdateTask().execute(mPlayController);
        }
    }

    public PlayController getPlayController(){
        synchronized (mPlayController){
            return mPlayController;
        }
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
            int currentPosition = (int) playController.getCurrentPosition();
            int duration = (int) playController.getDuration();
            while (true) {
                if (UpdateTurn && currentPosition < duration - 100) {
                    try {
                        publishProgress(currentPosition, duration);
                        Thread.sleep(500);
                        currentPosition = (int) playController.getCurrentPosition();
                        duration = (int) playController.getDuration();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            //return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mIsLoadTaskRunning = false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            lrcView.updateTime(playController.getCurrentPosition());
            long progress = 0;
            try {

                progress = 100 * playController.getCurrentPosition() / playController.getDuration();
            } catch (Exception e) {
                Log.e(TAG, "run: ", e);
            } finally {
                mSeekBar.setProgress((int) progress);
            }
        }
    }


}
