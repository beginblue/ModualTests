package com.blue.modualtests;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import blue.person.music.Music;

/**
 * Created by getbl on 2017/2/3.
 */

public class listAdapter extends BaseAdapter {

    private List<Music> mMusicList ;

    public listAdapter(List<Music> musicList){
        mMusicList=musicList;
    }

    public listAdapter(){
        mMusicList= new ArrayList<>();
    }

    public void setMusicList(List<Music> musicList){
        mMusicList.addAll(musicList);
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View viewToRtn;
        if(convertView==null){
            viewToRtn=View.inflate(parent.getContext(),R.layout.listitem,null);
        }else {
            viewToRtn=convertView;
        }

        ImageView imageView = (ImageView) viewToRtn.findViewById(R.id.iv_album);
        Bitmap cover =mMusicList.get(position).getCover();
        if(cover != null) imageView.setImageBitmap(cover);

        TextView textViewTitle = (TextView) viewToRtn.findViewById(R.id.musicTitle);
        textViewTitle.setText(mMusicList.get(position).getTitle());
        TextView textViewAuthor = (TextView) viewToRtn.findViewById(R.id.author);
        textViewAuthor.setText(mMusicList.get(position).getArtist());

        viewToRtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("onClick", "onClick: clicked");
                if(Universal.controller!=null){
                    Universal.controller.play(position);
                }
                else {
                    Log.i("onClick", "onClick: controller is null");
                }
            }
        });

        viewToRtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        return viewToRtn;
    }
}
