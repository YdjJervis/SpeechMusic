package com.aispeech.aios.music.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aispeech.ailog.AILog;
import com.aispeech.aimusic.model.MusicSearchParam;
import com.aispeech.aios.music.AIMusicApp;
import com.aispeech.aios.music.adapter.BaseAdapter;
import com.aispeech.aios.music.adapter.MusicRepoAdapter;
import com.aispeech.aios.music.db.MusicLocalDaoImpl;
import com.aispeech.aios.music.model.DownloadData;
import com.aispeech.aios.music.model.MusicData;
import com.aispeech.aios.music.model.MusicKeywordData;
import com.aispeech.aios.music.pojo.MusicInfo;
import com.aispeech.aios.music.ui.view.CustomEditText;
import com.aispeech.aios.music2.R;

import java.util.ArrayList;
import java.util.List;

public class RepoMusicFragment extends Fragment implements MusicData.OnMusicListener,
        CustomEditText.OnClearListener, TextView.OnEditorActionListener,
        MusicKeywordData.OnMusicListener, View.OnKeyListener, BaseAdapter.OnItemClickListener, DownloadData.OnDownloadListener {

    private static final String TAG = "AIOS-Music-RepoMusicFragment";

    private RecyclerView mRecyclerView;
    private MusicRepoAdapter mMusicRepoAdapter;

    private MusicData mMusicData;
    private MusicKeywordData mKeywordData;

    private EditText mEditTextSearch;

    private List<MusicInfo> mMusicInfoList = new ArrayList<>();

    private ProgressBar mProgressBarDownload;
    private OnRepoMusicFragmentListener mOnRepoMusicFragmentListener;

    public RepoMusicFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_repo, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_local_repo);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        mMusicRepoAdapter = new MusicRepoAdapter(getActivity(), mMusicInfoList);
        mMusicRepoAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mMusicRepoAdapter);

        AILog.i(TAG, "init custom edittext");
        mEditTextSearch = (EditText) view.findViewById(R.id.iv_music_search);
        mEditTextSearch.setOnEditorActionListener(this);
        mEditTextSearch.setOnKeyListener(this);
//        mEditTextSearch.setOnClearListener(this);

        MusicSearchParam param = new MusicSearchParam();
        param.setTitle("爱情");
        mMusicData = new MusicData(getActivity(), param);
        mMusicData.setListener(this);
        mMusicData.queryCloudByTitleArtist();

        mKeywordData = new MusicKeywordData(getActivity());
        mKeywordData.setListener(this);

        mProgressBarDownload = (ProgressBar) view.findViewById(R.id.pb_music_download);
        dismissProgress();
    }

    @Override
    public void onSearchSuccess(List<MusicInfo> list, String keyword) {
        AILog.i(TAG, "show cloud and local search list");
        notifyData(list);
    }

    @Override
    public void onSearchFailure(List<MusicInfo> list, String keyword) {
        AILog.i(TAG, "show local search list");
        notifyData(list);
    }

    private void notifyData(List<MusicInfo> musicInfoList) {
        mMusicInfoList.clear();
        mMusicInfoList.addAll(musicInfoList);
        mMusicRepoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchFailure() {
        Toast.makeText(getActivity(), "获取乐库失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClear() {
        mEditTextSearch.setText("");
    }

    @Override
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == EditorInfo.IME_ACTION_SEARCH || id == EditorInfo.IME_ACTION_UNSPECIFIED) {
            hidenKeybord();
            searchMusic(mEditTextSearch.getText().toString());
            return true;
        }
        return false;
    }

    private void searchMusic(String keyword) {
        mKeywordData.queryCloudByKeyword(keyword);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            hidenKeybord();
            searchMusic(mEditTextSearch.getText().toString());
            return true;
        }

        return false;
    }

    private void hidenKeybord() {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onItemClick(View view, int position) {
        AILog.i(TAG, "onItemClick::position=" + position);

        DownloadData downloadData = new DownloadData(AIMusicApp.getContext());
        downloadData.setOnDownloadListener(this);
        downloadData.downloadMusic(mMusicInfoList.get(position), position);
        showProgress();
    }

    @Override
    public void onLoading() {
        AILog.i(TAG, "onLoading...");
    }

    @Override
    public void onSuccessed(MusicInfo musicInfo) {
        AILog.i(TAG, "onSuccessed...");
        AILog.i(TAG, musicInfo);
        dismissProgress();

        MusicLocalDaoImpl musicLocalDao = new MusicLocalDaoImpl(AIMusicApp.getContext());
        musicLocalDao.add(musicInfo);
        if (mOnRepoMusicFragmentListener != null) {
            mOnRepoMusicFragmentListener.onDownload(musicInfo);
        }
    }

    @Override
    public void onFailed(String msg) {
        AILog.i(TAG, "onFailed::" + msg);
    }

    @Override
    public void onResult(int position) {
        AILog.i(TAG, "onResult::position=" + position);
    }

    public void showProgress() {
        mProgressBarDownload.setVisibility(View.VISIBLE);
    }

    public void dismissProgress() {
        mProgressBarDownload.setVisibility(View.GONE);
    }

    public void setOnRepoMusicFragmentListener(OnRepoMusicFragmentListener onRepoMusicFragmentListener) {
        mOnRepoMusicFragmentListener = onRepoMusicFragmentListener;
    }

    public interface OnRepoMusicFragmentListener {

        void onDownload(MusicInfo musicInfo);
    }
}
