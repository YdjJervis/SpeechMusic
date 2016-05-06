package com.aispeech.aios.music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.music.helper.VolleyHelper;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music2.R;

import java.util.List;

public class MusicRepoAdapter extends BaseAdapter {

    private List<MusicInfo> mMusicInfoList;

    public MusicRepoAdapter(Context context, List<MusicInfo> musicInfoList) {
        super();
        mMusicInfoList = musicInfoList;
    }

    @Override
    public int getItemCount() {
        return mMusicInfoList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AILog.i("MusicAdapter", mMusicInfoList.get(position));
        Holder holder = (Holder) viewHolder;
        MusicInfo musicInfo = mMusicInfoList.get(position);

        holder.title.setText(new StringBuilder(musicInfo.getArtist()).append(" - ").append(musicInfo.getName()));
        VolleyHelper.getInstance().getImage(holder.image, musicInfo.picture);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_repo, viewGroup, false);

        return new Holder(v);
    }

    public class Holder extends ViewHolder implements View.OnClickListener {

        public ImageView image;
        public TextView title;

        public Holder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.iv_music_image);
            title = (TextView) view.findViewById(R.id.tv_music_title);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onItemClick(view, getPosition());
            }
        }
    }
}
