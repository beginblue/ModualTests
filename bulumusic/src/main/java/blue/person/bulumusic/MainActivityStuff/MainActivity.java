package blue.person.bulumusic.MainActivityStuff;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import blue.person.bulumusic.ListStuff.MusicListListActivity;
import blue.person.bulumusic.ListStuff.listAdapter;
import blue.person.bulumusic.MusicPlayDetailAcitivityStuff.MusicPlayDetailActivity;
import blue.person.bulumusic.R;
import blue.person.bulumusic.ShareDataApplication;
import blue.person.music.Music;
import blue.person.musicplaystuff.musicControl.BroadcastReceivers;
import blue.person.musicplaystuff.musicControl.PlayController;
import blue.person.musicstuff.DBStuff.DBController;
import blue.person.musicstuff.musicUtils.muscScan;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    RecyclerView recyclerView;
    DBController mDBController;
    Handler mOnSearchReturnedHandler;
    List<Music> mMusicList;
    recyclerViewAdapter mListAdapter;
    AlertDialog mLoadingDialog;
    PlayController mPlayController;
    BroadcastReceivers mBroadcastReceivers;
    NavigationView navigationView;
    String listName;
    final int START_LIST_ACTIVITY = 21;
    boolean pause = false;

    /**
     * 初始化后台组件
     */
    private void initialStuff() {

        mMusicList = new ArrayList<>();
        mListAdapter = new recyclerViewAdapter(this, (ShareDataApplication) getApplication());
        mOnSearchReturnedHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == muscScan.SEARCH_MUSIC_SUCCESS) {
                    try {
                        mMusicList = muscScan.getLocalMusicList();
                        mDBController.addMusicListToTable("localMusic", mMusicList, 0);
                        mDBController.addNewMusicList("localMusic", mMusicList.size());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == muscScan.MUSIC_ALREADY_SEARCHED) {
                    mMusicList = mDBController.getMusicList("localMusic");
                } else if (msg.what == muscScan.MUSIC_SEARCH_ERROR) {
                    Toast.makeText(MainActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Universal.newInstance(MainActivity.this).preparePlayController(mMusicList);

                mListAdapter.setMusicList(mMusicList, "localMusic");
                listName = "localMusic";

                //RecyclerView Stuff
                recyclerView = (RecyclerView) findViewById(R.id.recycle_items);
                recyclerView.setAdapter(mListAdapter);
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new itemDecoration());
                mPlayController = ((ShareDataApplication) getApplication()).getPlayController(mMusicList);
                //TODO:刷新完的下一步

                if(mMusicList==null && mMusicList.size() == 0) {
                    mLoadingDialog =
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Loading")
                                    .show();

                    mDBController.scanMusic(this);

                }Music music = mMusicList.get(0);
                headerImage.setImageBitmap(music.getCover());
                headerTitle.setText(music.getTitle());
                headerAuthor.setText(music.getArtist());

                mLoadingDialog.cancel();


            }
        };
        mDBController = ((ShareDataApplication) getApplication()).getDBController();
        mLoadingDialog =
                new AlertDialog.Builder(this)
                        .setTitle("Loading")
                        .show();
        mDBController.scanMusic(mOnSearchReturnedHandler);

        mBroadcastReceivers = new BroadcastReceivers(this);
        mBroadcastReceivers.setOnStartActionIndexListener(new onStartListenerIndex());
        mBroadcastReceivers.setOnPauseListener(new onPauseListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bulu Music");
        setSupportActionBar(toolbar);

        setTitle("Bulu Music");

        //Floating Action Bar Stuff
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDBController.removeMusicList("localMusic");
                initialStuff();

            }
        });


        //Drawer stuff
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Navigation Stuff

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initHeader();
        initialStuff();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitConfirm();
            // super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBroadcastReceivers.unregisterAllBroadcastReceivers();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myMusic) {
            // Handle the camera action
        } else if (id == R.id.nav_musicLists) {
            Intent intent = new Intent(this, MusicListListActivity.class);
            intent.putExtra("mode", listAdapter.MODE_SELECT);
            startActivityForResult(intent, START_LIST_ACTIVITY);
        } else if (id == R.id.nav_exit) {
            exitConfirm();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    static boolean confirm = false;

    private void exitConfirm() {
        Snackbar.make(drawer, "要停止播放嘛(不停止请再按一次退出)", Snackbar.LENGTH_LONG)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (confirm) {
                            MainActivity.this.finish();
                            return;
                        }
                        mPlayController.stop();
                        MainActivity.this.finish();
                        confirm = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                confirm = false;
                            }
                        }).start();
                    }
                }).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void header_start_onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imageBtn_header_change_order:
                mPlayController.changeOrder();
                break;
            case R.id.imageBtn_header_next:
                mPlayController.next();
                break;
            case R.id.imageBtn_header_start:
                //TODO:存储上次播放的列表名和index
                mPlayController.pause();
                break;
            default:
                break;
        }
    }


    //header部分的组件
    ImageButton headerStart;
    ImageButton headerChange;
    ImageButton headerNext;
    TextView headerTitle;
    TextView headerAuthor;
    ImageView headerImage;

    private void initHeader() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);

        headerStart = (ImageButton) inflate.findViewById(R.id.imageBtn_header_start);
        headerChange = (ImageButton) inflate.findViewById(R.id.imageBtn_header_change_order);
        headerNext = (ImageButton) inflate.findViewById(R.id.imageBtn_header_next);
        headerTitle = (TextView) inflate.findViewById(R.id.tv_header_title);
        headerAuthor = (TextView) inflate.findViewById(R.id.tv_header_author);
        headerImage = (ImageView) inflate.findViewById(R.id.header_imageView);
        headerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicPlayDetailActivity.class);
                startActivity(intent);
            }
        });
        navigationView.addHeaderView(inflate);
    }

    /**
     * 收到开始播放信号的时候执行的内容
     */
    private class onStartListener extends BroadcastReceivers.BroadcastResolver {
        @Override
        public void run() {
            headerStart.setImageResource(R.drawable.ic_menu_gallery);
            if (mIntent.hasExtra("Music")) {
                Music music = (Music) mIntent.getSerializableExtra("Music");
                headerTitle.setText(music.getTitle());
                headerAuthor.setText(music.getArtist());
                headerImage.setImageBitmap(music.getCover());
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == START_LIST_ACTIVITY) {
            if (data != null) {
                String title = data.getStringExtra("title");
                mMusicList = mDBController.getMusicList(title);
                listName = title;
                mListAdapter.setMusicList(mMusicList, title);
                // mMusicList = list;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class onStartListenerIndex extends BroadcastReceivers.BroadcastResolver {

        @Override
        public void run() {
            headerStart.setImageResource(android.R.drawable.ic_media_pause);
            int index = mIntent.getIntExtra("Index", 0);
            Music music = mMusicList.get(index);
            headerTitle.setText(music.getTitle());
            headerAuthor.setText(music.getArtist());
            headerImage.setImageBitmap(music.getCover());
            pause = false;

        }
    }

    /**
     * 收到暂停播放信号的时候执行的内容
     */
    private class onPauseListener extends BroadcastReceivers.BroadcastResolver {
        @Override
        public void run() {
            if (!pause) {
                headerStart.setImageResource(android.R.drawable.ic_media_play);
                pause = true;
            } else {
                headerStart.setImageResource(android.R.drawable.ic_media_pause);
                pause = false;
            }
        }
    }

    /**
     * 收到停止播放信号的时候执行的内容
     */
    private class onStopListener extends BroadcastReceivers.BroadcastResolver {

        @Override
        public void run() {

        }
    }
}
