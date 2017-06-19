package blue.person.bulumusic.MusicPlayDetailAcitivityStuff.selectLRCStuff;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import blue.person.bulumusic.R;
import blue.person.internetstuff.RequestListEntity;

/**
 * Created by getbl on 2017/6/4.
 */

public class listResultAdapter implements ListAdapter {

    private List<RequestListEntity.CandidatesBean> resultList;
    private Context mContext;
    private onItemClickListener mListener;

    public listResultAdapter(Context context) {
        mContext = context;
    }

    public void setResultList(@Nullable List<RequestListEntity.CandidatesBean> list) {
        resultList = list;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return resultList == null ? 1 : resultList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (resultList == null) {
            convertView  = new TextView(mContext);
            ((TextView)convertView).setText("没有结果");
        } else {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.list_listitem, null);
            }
            TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            TextView tv_singer = (TextView) convertView.findViewById(R.id.tv_description);
            tv_title.setText(resultList.get(position).getSong());
            tv_singer.setText(resultList.get(position).getSinger());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(
                            resultList.get(position).getAccesskey(),
                            resultList.get(position).getId()
                    );
                }
            });
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return resultList.size() == 0;
    }

    public abstract static class onItemClickListener {
        public abstract void onClick(String accessKey, String Id);
    }
}
