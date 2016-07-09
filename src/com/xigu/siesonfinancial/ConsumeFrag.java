package com.xigu.siesonfinancial;

import java.util.ArrayList;

import org.json.JSONObject;

import com.xigu.siesonfinancial.view.FinancialDataListInfoAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

public class ConsumeFrag extends Fragment {
	private View mRootView;
	private ListView listView;
	private TextView emptyView;
	private FinancialDataListInfoAdapter adapter;
	private ArrayList<JSONObject> mDataList = new ArrayList<JSONObject>();

	private int mType;
	private int mFirstVisibleItem;
	private int mVisibleItemCount;
	private int mTotalItemCount;

	private IListViewLoadListener listViewLoadListener;

	public void setListViewLoadListener(IListViewLoadListener listViewLoadListener) {
		this.listViewLoadListener = listViewLoadListener;
	}

	public ConsumeFrag(int type, ArrayList<JSONObject> dataList) {
		super();
		this.mType = type;
		this.mDataList = dataList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.consume_info, container, false);
		listView = (ListView) mRootView.findViewById(R.id.consume_info_lv);
		emptyView = (TextView) mRootView.findViewById(R.id.consume_info_empty_view);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case SCROLL_STATE_IDLE:
					if ((mFirstVisibleItem + mVisibleItemCount) == mTotalItemCount)
						listViewLoadListener.loadMore(mType);
					break;

				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				mFirstVisibleItem = firstVisibleItem;
				mVisibleItemCount = visibleItemCount;
				mTotalItemCount = totalItemCount;
			}
		});
		initData(mDataList);
		return mRootView;
	}

	public ArrayList<JSONObject> getmDataList() {
		return mDataList;
	}

	public FinancialDataListInfoAdapter getAdapter() {
		return adapter;
	}

	public void initData(ArrayList<JSONObject> dataList) {
		adapter = new FinancialDataListInfoAdapter(getActivity(), mDataList);
		listView.setAdapter(adapter);
		listView.setEmptyView(emptyView);
	}
}
