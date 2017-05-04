package blue.person.bulumusic.ListStuff;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import blue.person.bulumusic.R;
import blue.person.bulumusic.Universal;
import blue.person.music.Music;
import blue.person.musicstuff.DBStuff.DBController;

/**
 * Created by getbl on 2017/4/22.
 */

public class listAdapter extends RecyclerView.Adapter<listAdapter.listHolder> {


    private Context mContext;
    private DBController mDBController;
    private List<String> mTitle;
    private List<Integer> mCounts;
    private Map<String, Integer> mStringIntegerMap;
    private Intent mIntent;

    private onClickListener mOnClickListener;

    public static final int MODE_SELECT = 1;
    public static final int MODE_ADD = 2;
    public static final int MODE_DELETE = 3;

    private int mode;

    public listAdapter(Context context, int mode, Intent intent) {
        mContext = context;
        this.mode = mode;
        mIntent = intent;
        mDBController = Universal.newInstance(mContext).getDBController();
        mStringIntegerMap = mDBController.getMusicLists();
        initData();
    }

    private void initData() {
        mTitle = new ArrayList<>();
        mCounts = new ArrayList<>();
        mStringIntegerMap = mDBController.getMusicLists();
        for (String title :
                mStringIntegerMap.keySet()) {
            mTitle.add(title);
            mCounts.add(mStringIntegerMap.get(title));
        }
    }

    @Override
    public listHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(mContext, R.layout.list_listitem, null);
        return new listHolder(v);
    }

    @Override
    public void onBindViewHolder(listHolder holder, int position) {
        holder.title.setText(mTitle.get(position));
        holder.description.setText(mCounts.get(position) + " songs");
        holder.setStrTitle(mTitle.get(position));
    }

    @Override
    public int getItemCount() {
        return Math.min(mTitle.size(), mCounts.size());
    }

    public void addNewList(String listName) {
        try {
            mDBController.addNewMusicList(listName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData();
        notifyDataSetChanged();
    }

    public void removeList(String listName) {
        mDBController.removeMusicList(listName);
        initData();
        notifyDataSetChanged();
    }

    public void setOnClickListener(onClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public class listHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        FrameLayout images;
        TextView title;
        TextView description;

        public listHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            images = (FrameLayout) itemView.findViewById(R.id.framelayout_images);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        String strTitle;

        public void setStrTitle(String strTitle) {
            this.strTitle = strTitle;
        }

        @Override
        public void onClick(View v) {
            int currentSize = mDBController.getSizeOfMusicList(strTitle);
            switch (mode) {
                case MODE_ADD: //添加歌曲到播放列表
                    // Log.i("aaa", "onClick: clicked");
                    //Music music = (Music) mIntent.getBundleExtra("object").get("music");
                    String musicName = mIntent.getStringExtra("musicName");
                    String listName = mIntent.getStringExtra("listName");
                    Music music = mDBController.getSpecificMusicFromSpecificList(musicName, listName);
                    if (music == null) {
                        Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
                    } else {
                        mDBController.addMusicToList(strTitle, music, currentSize);
                        Toast.makeText(mContext, "finished", Toast.LENGTH_SHORT).show();
                    }
                    mOnClickListener.run_add();

                    break;
                case MODE_SELECT: //选择播放列表
                    mOnClickListener.run_select(title.getText().toString());
                    break;
                case MODE_DELETE: //从列表中删除歌曲
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            MenuInflater inflater = new MenuInflater(mContext);
            inflater.inflate(R.menu.list_activity_item_long_click, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.removeList:
                            removeList(title.getText().toString());
                    }
                    return true;
                }
            });
            popupMenu.show();
            return true;
        }
    }

    public static abstract class onClickListener {

        public abstract void run_select(String title);

        public abstract void run_add();

    }
}
