package com.example.testrecyclerview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends  Activity implements OnScrollToBottomListener{

	protected FootUpdate mFootUpdate = new FootUpdate();

	TopicsAdapter mAdapter;
	RecyclerView mRecyclerView;
	HeaderViewRecyclerAdapter mHeaderAdapter;
	RecyclerView.LayoutManager mLayoutManager;
	SwipeRefreshLayout mSwipeLayout;

	TopicModel mTopic;

	protected int mPage = 1;

	private boolean mIsLoading;

	Handler mHandler = new Handler();

	private boolean mNoMore;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mRecyclerView = (RecyclerView) findViewById(R.id.list_replies);

		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		mAdapter = new TopicsAdapter(this, this);
		mHeaderAdapter = new HeaderViewRecyclerAdapter(mAdapter);

		View  header = LayoutInflater.from(this).inflate(R.layout.listview_header, null);
		RecyclerView.LayoutParams headerParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
				ViewGroup.LayoutParams.MATCH_PARENT);
		header.setLayoutParams(headerParams);

		mHeaderAdapter.addHeaderView(header);

		mRecyclerView.setAdapter(mHeaderAdapter);

		mFootUpdate.init(mHeaderAdapter, LayoutInflater.from(this), new FootUpdate.LoadMore() {
			@Override
			public void loadMore() {
				requestMoreTopics();
			}
		});

		loadDatas();

		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				//	
				mPage = 1;
				requestTopics(true);
			}

		});
		mSwipeLayout.setColorSchemeResources(R.color.holo_blue_bright,
				R.color.holo_green_light,
				R.color.holo_orange_light,
				R.color.holo_red_light);

		mSwipeLayout.setProgressViewOffset(false, 0,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
	}
	@Override
	public void onLoadMore() {
		++mPage; 
		requestMoreTopics();
	}

	private void requestMoreTopics() {
		loadDatas();
	}

	private void requestTopics(boolean refresh) {
		if (mIsLoading)
			return;
		mIsLoading = true;
		loadDatas();
	}

	private void loadDatas() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				ArrayList<TopicModel> list = new ArrayList<>();
				for (int i = 0; i < 10; i++) {
					TopicModel mode = new TopicModel();
					mode.title = "new one object current time :"+System.currentTimeMillis()+"\n" +
							" mPage = "+mPage;
					list.add(mode);
				}
				onSuccess(list);
			}
		}, 1500);
	}
	private void onSuccess(ArrayList<TopicModel> list ) {

		mSwipeLayout.setRefreshing(false);
		mIsLoading = false;
		if(list.size()== 0){
			return ;
		}
		mAdapter.insertAtBack(list, mPage !=1);
		if (mNoMore) {
			mFootUpdate.dismiss();
		} else {
			mFootUpdate.showLoading();
		}
	}
}
