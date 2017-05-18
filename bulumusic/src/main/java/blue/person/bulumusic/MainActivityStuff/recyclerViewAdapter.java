package blue.person.bulumusic.MainActivityStuff;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import blue.person.bulumusic.ListStuff.MusicListListActivity;
import blue.person.bulumusic.ListStuff.listAdapter;
import blue.person.bulumusic.R;
import blue.person.bulumusic.ShareDataApplication;
import blue.person.music.Music;
import blue.person.musicplaystuff.musicControl.PlayController;

/**
 * The adapter of RecyclerView
 * Created by getbl on 2017/3/27.
 */

class recyclerViewAdapter extends RecyclerView.Adapter<recyclerViewAdapter.itemHolder> {

    private List<Music> mMusicList;
    private Context mContext;
    private PlayController mPlayController;
    private String listName;
    private ShareDataApplication mapp;

    recyclerViewAdapter(Context context, ShareDataApplication app) {
        mContext = context;
        mapp = app;
//        mPlayController = Universal.newInstance(mContext).getPlayController();
    }


    void setMusicList(List<Music> musicList, String listName) {
        mMusicList = musicList;
        this.listName = listName;
        if (mPlayController == null) {
            mPlayController = mapp.getPlayController(mMusicList);
        } else mPlayController.setMusicList(mMusicList);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    private static final int VIEW_TYPE_ITEM = 0;

    @Override
    public itemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemHolder holder = null;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                View view = LayoutInflater.from(mContext).inflate(R.layout.listitem, parent, false);
                holder = new itemHolder(view);

        }
        return holder;
    }


    @Override
    public void onBindViewHolder(itemHolder holder, final int position) {
        Music currentMusic = mMusicList.get(position);
        holder.setPosition(position);
        holder.mTitle.setText(currentMusic.getTitle());
        holder.mSinger.setText(currentMusic.getArtist());
        holder.mImageView.setImageBitmap(currentMusic.getCover());

    }


    @Override
    public int getItemCount() {
        return mMusicList.size();
    }


    public class itemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        ImageView mImageView;
        TextView mTitle;
        TextView mSinger;


        public itemHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_album);
            mTitle = (TextView) itemView.findViewById(R.id.musicTitle);
            mSinger = (TextView) itemView.findViewById(R.id.author);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public int position;

        void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Log.d("tests", "onClick: clicked");
            mPlayController.play(position);
        }

        @Override
        public boolean onLongClick(View v) {
            final PopupMenu popupMenu = new PopupMenu(mContext, mTitle);
            MenuInflater inflater = new MenuInflater(mContext);
            inflater.inflate(R.menu.main_activity_item_long_click, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.addToList:
                            Intent intent = new Intent(mContext, MusicListListActivity.class);
                            intent.putExtra("mode", listAdapter.MODE_ADD);
                            intent.putExtra("size", 0);
                            intent.putExtra("musicName", mTitle.getText().toString());
                            intent.putExtra("listName", listName);
                            //too big
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("music", mMusicList.get(position));
//                            intent.putExtra("object", bundle);
                            mContext.startActivity(intent);
                            break;
                        case R.id.removeFromList:
                            mapp.getDBController()
                                    .removeMusicFromList(listName
                                            , mMusicList
                                                    .get(position)
                                                    .getTitle()
                                            , mMusicList.size()
                                    );

                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
            return false;
        }
    }
}
