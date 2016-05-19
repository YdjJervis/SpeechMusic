package com.aispeech.aios.music.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MusicCatViewPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> mFragmentList;
	private String[] titleList = { "本地", "乐库" };

	public MusicCatViewPagerAdapter(FragmentManager fm,List<Fragment> list) {
		super(fm);
		mFragmentList = list;
	}

	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		mFragmentList.remove(position);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return super.isViewFromObject(view, object);
	}

	
}
