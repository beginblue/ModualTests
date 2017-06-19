package blue.person.bulumusic.MusicPlayDetailAcitivityStuff.selectLRCStuff;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

import blue.person.bulumusic.R;
import blue.person.internetstuff.LRCRequests;
import blue.person.internetstuff.RequestListEntity;

public class selectLRCActivity extends AppCompatActivity {

    listResultAdapter adapter;
    EditText songName;
    ListView mListView;
    public static final String SONG_DURATION = "song duration";
    public static final String SONG_NAME = "song name";
    public static final String ACCESS_KEY = "access key";
    public static final String ID = "id";
    public static final int RESULT_CODE = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lrc);
        songName = (EditText) findViewById(R.id.songName);
        mListView = (ListView) findViewById(R.id.resultList);
        setTitle("搜索歌词");
        adapter = new listResultAdapter(this);


    }

    String name;
    long duration;

    @Override
    protected void onResume() {
        Intent intent = getIntent();
        name = intent.getStringExtra(SONG_NAME);
        duration = intent.getLongExtra(SONG_DURATION, 0);
        songName.setText(name);
        new Thread(new search(name, duration)).start();
        super.onResume();
    }


    private class search implements Runnable {

        String mName;
        long mDuration;

        public search(String name, long duration) {
            mName = name;
            mDuration = duration;
        }

        @Override
        public void run() {

            List<RequestListEntity.CandidatesBean> candidates = null;
            try {
                candidates = new LRCRequests()
                        .requestList(mName, mDuration)
                        .getCandidates();
                Log.e("bulumusicCandidate", "run: " + candidates.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

            final List<RequestListEntity.CandidatesBean> finalCandidates = candidates;
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    if (finalCandidates != null && finalCandidates.size() != 0)
                        adapter.setResultList(finalCandidates);
                    else adapter.setResultList(null);
                    adapter.setOnItemClickListener(new listResultAdapter.onItemClickListener() {

                        @Override
                        public void onClick(String accessKey, String Id) {
                            Intent result = new Intent();
                            result.putExtra(ACCESS_KEY, accessKey);
                            result.putExtra(ID, Id);
                            selectLRCActivity.this.setResult(RESULT_CODE, result);
                            selectLRCActivity.this.finish();

                        }
                    });
                    mListView.setAdapter(adapter);
                }
            });

        }
    }

    public void searchClick(View view) {
        String songName = this.songName.getText().toString();
        new Thread(new search(songName, duration)).start();
    }
}
