package com.aispeech.aios.music.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music2.R;

import java.util.List;

public class MusicLocalAdapter extends BaseAdapter {

    private List<MusicInfo> mMusicInfoList;

    public MusicLocalAdapter(List<MusicInfo> musicInfoList) {
        super();
        mMusicInfoList = musicInfoList;
    }

    @Override
    public int getItemCount() {
        return mMusicInfoList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AILog.i("MusicAdapter",mMusicInfoList.get(position));
        Holder holder = (Holder)viewHolder;
        holder.title.setText(mMusicInfoList.get(position).getArtist() + " - " + mMusicInfoList.get(position).getName());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_music, viewGroup, false);

        return new Holder(v);
    }

    public class Holder extends ViewHolder implements View.OnClickListener {

        public TextView title;

        public Holder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tv_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(null!=mOnItemClickListener){
                mOnItemClickListener.onItemClick(view,getPosition());
            }
        }

    }
}
