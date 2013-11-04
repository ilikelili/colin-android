//package com.windo.common.view;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.graphics.Color;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.animation.LinearInterpolator;
//import android.view.animation.RotateAnimation;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.colin.android.R;
//
//public class PullToRefreshListView extends ListView implements OnScrollListener {
//	
////	private static final String TAG = "PullToRefreshListView";
//
//	public static final int IDLE = 0;
//
//	public static final int PULL_REFRESH = 1;
//
//	public static final int LOAD_MORE = 2;
//
//	private final static int RELEASE_TO_REFRESH = 0;
//	private final static int PULL_TO_REFRESH = 1;
//	private final static int REFRESHING = 2;
//	private final static int DONE = 3;
//
//	private final static float RATIO = (float) 2.9;
//
//	private LayoutInflater mInflater;
//
//	private LinearLayout mHeadView;
//
//	private TextView mTvTips;
//
//	private TextView mTvUpdateTime;
//
//	private ImageView mIvArrow;
//
//	private ProgressBar mHeadProgress;
//
//	private RotateAnimation mAnimation;
//	private RotateAnimation mReverseAnimation;
//
//	private boolean mIsRecored;
//
//	private String mLastUpdateTime = "";
//	
//	private String mLastUpdateTime_12 = "";
//
//	private int mHeadContentHeight = -1;
//
//	private int mImageHeight;
//
//	private int mStartY;
//
//	private int mFirstItemIndex;
//
//	private int mLastItemIndex;
//
//	private BaseAdapter mAdapter;
//
//	private int mHeadState;
//
//	private int mState;
//
//	private boolean mIsBack;
//
//	private OnRefreshListener mRefreshListener;
//	private OnLoadDataListener mLoadDataListener;
//
//	private boolean mIsRefreshable;
//
////	private boolean mIsLoadMore;
//
//	private View mFootView;
//
//	private ProgressBar mFootProgress;
//
//	private TextView mFootText;
//
////	private OnItemClickListener mOnItemClickListener;
//
////	private Animation alphaAnim_show, alphaAnim_hide;
//
//	private Context mContext;
//	
//
//	public PullToRefreshListView(Context context) {
//		super(context);
////		init(context,false);
//	}
//
//	public PullToRefreshListView(Context context, AttributeSet attrs) {
//		super(context, attrs);
////		init(context,false);
//	}
//	
//	boolean showView;
//
//	public void init(Context context, boolean mIfViewHead) {
//		mContext = context;
//		showView = mIfViewHead;
//		
//		mInflater = LayoutInflater.from(context);
//
//		mHeadView = (LinearLayout) mInflater.inflate(R.layout.refresh_item,
//				null);
//
//		mIvArrow = (ImageView) mHeadView
//				.findViewById(R.id.pull_to_refresh_image);
//		mIvArrow.setMinimumWidth(70);
//		mIvArrow.setMinimumHeight(50);
//		mHeadProgress = (ProgressBar) mHeadView
//				.findViewById(R.id.pull_to_refresh_progress);
//		mTvTips = (TextView) mHeadView.findViewById(R.id.pull_to_refresh_text);
//		mTvUpdateTime = (TextView) mHeadView
//				.findViewById(R.id.pull_to_refresh_updated_at);
//
//		mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
//		this.setHeaderDividersEnabled(false);
//		addHeaderView(mHeadView);
//
//		mFootView = mInflater.inflate(R.layout.loading_item, null);
//		mFootView.setBackgroundColor(Color.TRANSPARENT);
//		mFootProgress = (ProgressBar) mFootView.findViewById(R.id.progress);
//		mFootText = (TextView) mFootView.findViewById(R.id.loading);
//
//		setOnScrollListener(this);
//		mAnimation = new RotateAnimation(0, -180,
//				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//		mAnimation.setInterpolator(new LinearInterpolator());
//		mAnimation.setDuration(250);
//		mAnimation.setFillAfter(true);
//
//		mReverseAnimation = new RotateAnimation(-180, 0,
//				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//		mReverseAnimation.setInterpolator(new LinearInterpolator());
//		mReverseAnimation.setDuration(200);
//		mReverseAnimation.setFillAfter(true);
//
//		mHeadState = DONE;
//		mIsRefreshable = false;
//		//修改卡超市 上次更新时间 的展示
////		String time = MocamSetting.getInstance(mContext).getLastUpdateTime();
////		if (time!=null && time.substring(0, 4).equals("最后更新")) {
////			MocamSetting.getInstance(mContext).setLastUpdateTime(null);
////			time = "";
////		}
//////		String time = MocamSetting.getInstance(mContext).getLastUpdateTime();
////		ContentResolver cv = mContext.getContentResolver();
////		String strTimeFormat = android.provider.Settings.System.getString(cv,
////				android.provider.Settings.System.TIME_12_24);
////		if (time == null || time.equals("")) {
////			if (strTimeFormat != null && strTimeFormat.equals("24")) {
////				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
////				mLastUpdateTime = sdf.format(new Date());
////				String last_update_time = String.format(
////						mContext.getString(R.string.last_update_time),
////						mLastUpdateTime);
////				mTvUpdateTime.setText(last_update_time);
////			} else {
////				SimpleDateFormat sdd = new SimpleDateFormat("yyyy-MM-dd hh:mm");
////				mLastUpdateTime = sdd.format(new Date());
////				String last_update_time = String.format(
////						mContext.getString(R.string.last_update_time),
////						mLastUpdateTime);
////				mTvUpdateTime.setText(last_update_time);
////			}
////		} else {
////			if (strTimeFormat != null && strTimeFormat.equals("24")) {
////				String last_update_time = String.format(
////						mContext.getString(R.string.last_update_time), time);
////				mTvUpdateTime.setText(last_update_time);
////			} else {
////				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
////				SimpleDateFormat sdd = new SimpleDateFormat("yyyy-MM-dd hh:mm");
////				try {
////					Date lastTime = sdf.parse(time);
////					String lastUpdateTime = sdd.format(lastTime);
////					String last_update_time = String.format(
////							mContext.getString(R.string.last_update_time),
////							lastUpdateTime);
////					mTvUpdateTime.setText(last_update_time);
////				} catch (ParseException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////			}
////		}
//	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		
//		if (mHeadContentHeight <= 0) {
//			mHeadContentHeight = mHeadView.getMeasuredHeight();
//			
////			PalLog.e(TAG, "onMeasure mHeadContentHeight " + mHeadContentHeight);
//		}
//		
//		if(!showView && mHeadContentHeight >0){
//			showView = !showView;
//			mHeadState = DONE;
//			changeHeaderViewByState();
//		}
//		
////		PalLog.e(TAG, "onMeasure");
//	}
//
//	public void onScroll(AbsListView arg0, int firstVisiableItem,
//			int visiableItemCount, int totalItemCount) {
//		mFirstItemIndex = firstVisiableItem;
//		mLastItemIndex = visiableItemCount + firstVisiableItem - 2;
//
//	}
//
//	public void onScrollStateChanged(AbsListView arg0, int arg1) {
//		if (mAdapter != null) {
////			PalLog.e("onScrollStateChanged", mLastItemIndex + "");
////			PalLog.e(
////					"onScrollStateChanged",
////					(mLastItemIndex == mAdapter.getCount() && arg1 == OnScrollListener.SCROLL_STATE_IDLE)
////							+ "");
//			if (mLastItemIndex == mAdapter.getCount()
//					&& arg1 == OnScrollListener.SCROLL_STATE_IDLE) {
//				if (mLoadDataListener != null) {
//					setFootProgressVisible(true);
//					mState = LOAD_MORE;
//					mLoadDataListener.onLoadMore();
//				}
//
//			}
//		}
//
//	}
//
//	public void enableRefresh() {
//		mIsRefreshable = true;
//	}
//
//	public void disableRefresh() {
//		mIsRefreshable = false;
//	}
//
////	public void enableLoadMore() {
////		mIsLoadMore = true;
////	}
////
////	public void disableLoadMore() {
////		mIsLoadMore = false;
////	}
//
//	public void doRefresh() {
//
//		mHeadState = REFRESHING;
//		changeHeaderViewByState();
//		onRefresh();
//
//	}
//
//	public void onRefreshComplete() {
//		refreshUpdateTime();
//		cancelPullRefreshState();
//	}
//
//	public boolean onTouchEvent(MotionEvent event) {
//		
//		if (!mIsRefreshable || mHeadState == REFRESHING) {
//			return super.onTouchEvent(event);
//		}
//
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN: {
//			if (mFirstItemIndex == 0 && !mIsRecored) {
//
//				mIsRecored = true;
//				mStartY = (int) event.getY();
//			}
//			break;
//		}
//		case MotionEvent.ACTION_UP: {
//			if (mHeadState == PULL_TO_REFRESH) {
//
//				mHeadState = DONE;
//				changeHeaderViewByState();
//			} else if (mHeadState == RELEASE_TO_REFRESH) {
//
//				mHeadState = REFRESHING;
//				changeHeaderViewByState();
//
//				if (mState == LOAD_MORE) {
//					cancelLoadMoreState();
//				}
//				mState = PULL_REFRESH;
//				if (mLoadDataListener != null)
//					mLoadDataListener.onPullRefresh();
//				onRefresh();
//			}
//
//			mIsRecored = false;
//			mIsBack = false;
//			break;
//		}
//		case MotionEvent.ACTION_MOVE: {
//			int tempY = (int) event.getY();
//			if (!mIsRecored && mFirstItemIndex == 0) {
//				mIsRecored = true;
//				mStartY = tempY;
//			}
//
//			if (mIsRecored) {
//				int deltaY = tempY - mStartY;
//				int headMiddleHeight = mHeadContentHeight - mImageHeight;
//
//				if (mHeadState == RELEASE_TO_REFRESH) {
//
//					setSelection(0);
//
//					if (deltaY > 0 && ((deltaY / RATIO) < headMiddleHeight)) {
//						mHeadState = PULL_TO_REFRESH;
//						changeHeaderViewByState();
//					}
//
//					else if (tempY - mStartY <= 0) {
//						mHeadState = DONE;
//						changeHeaderViewByState();
//					}
//
//					else {
//
//					}
//				}
//
//				else if (mHeadState == PULL_TO_REFRESH) {
//					setSelection(0);
//
//					if ((deltaY / RATIO) >= headMiddleHeight) {
//						mHeadState = RELEASE_TO_REFRESH;
//						mIsBack = true;
//						changeHeaderViewByState();
//					}
//
//					else if (tempY - mStartY <= 0) {
//						mHeadState = DONE;
//						changeHeaderViewByState();
//					}
//				}
//
//				else if (mHeadState == DONE) {
//					if (deltaY > 0) {
//						mHeadState = PULL_TO_REFRESH;
//						changeHeaderViewByState();
//					}
//				}
//
//				if (mHeadState == PULL_TO_REFRESH
//						|| mHeadState == RELEASE_TO_REFRESH) {
//					int paddingTop = (int) (-1 * mHeadContentHeight + deltaY
//							/ RATIO);
//					mHeadView.setPadding(0, paddingTop, 0, 0);
//				}
//			}
//			break;
//		}
//		}
//
//		return super.onTouchEvent(event);
//	}
//
//	public void setToIdleState() {
//		if (mState == PULL_REFRESH) {
//			cancelPullRefreshState();
//		} else if (mState == LOAD_MORE) {
//			cancelLoadMoreState();
//		}
//
//		mState = IDLE;
//	}
//
//	private void changeHeaderViewByState() {
//		switch (mHeadState) {
//		case RELEASE_TO_REFRESH:
//			if(getHeaderViewsCount()==0)
//				addHeaderView(mHeadView);
//			mIvArrow.setVisibility(View.VISIBLE);
//			mHeadProgress.setVisibility(View.GONE);
//			mTvTips.setVisibility(View.VISIBLE);
//			mTvUpdateTime.setVisibility(View.VISIBLE);
//
//			mIvArrow.clearAnimation();
//			mIvArrow.startAnimation(mAnimation);
//
//			mTvTips.setText(R.string.pull_to_refresh_release_label);
//
//			break;
//		case PULL_TO_REFRESH:
//			if(getHeaderViewsCount()==0)
//				addHeaderView(mHeadView);
//			mHeadProgress.setVisibility(View.GONE);
//			mTvTips.setVisibility(View.VISIBLE);
//			mTvUpdateTime.setVisibility(View.VISIBLE);
//			mIvArrow.clearAnimation();
//			mIvArrow.setVisibility(View.VISIBLE);
//			
//			if (mIsBack) {
//				mIsBack = false;
//				mIvArrow.clearAnimation();
//				mIvArrow.startAnimation(mReverseAnimation);
//
//				mTvTips.setText(R.string.pull_to_refresh_pull_label);
//			} else {
//				mTvTips.setText(R.string.pull_to_refresh_pull_label);
//			}
//
//			break;
//		case REFRESHING:
//			if(getHeaderViewsCount()==0)
//				addHeaderView(mHeadView);
//			mHeadView.setPadding(0, 0, 0, 0);
//			mHeadProgress.setVisibility(View.VISIBLE);
//			mIvArrow.clearAnimation();
//			mIvArrow.setVisibility(View.GONE);
//			mTvTips.setText(R.string.loading);
//			mTvUpdateTime.setVisibility(View.VISIBLE);
//
//			break;
//		case DONE:
//			mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
//			mHeadProgress.setVisibility(View.GONE);
//			mIvArrow.clearAnimation();
//
//			mTvTips.setText(R.string.pull_to_refresh_pull_label);
//			mTvUpdateTime.setVisibility(View.VISIBLE);
//			if(getHeaderViewsCount()>0)
//			removeHeaderView(mHeadView);
//			break;
//		}
//	}
//
//	private void refreshUpdateTime() {
//		// 修改卡超市 上次修改时间 的展示
//		ContentResolver cv = mContext.getContentResolver();
//		String strTimeFormat = android.provider.Settings.System.getString(cv,
//				android.provider.Settings.System.TIME_12_24);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		mLastUpdateTime = MocamSetting.getInstance(mContext)
//				.getLastUpdateTime();
//		SimpleDateFormat sdd = new SimpleDateFormat("yyyy-MM-dd hh:mm");
//		try {
//			
//			if (mLastUpdateTime != null && !mLastUpdateTime.equals("")) {
//				if (strTimeFormat != null && strTimeFormat.equals("24")) {
//					String last_update_time = String.format(
//							mContext.getString(R.string.last_update_time),
//							mLastUpdateTime);
//					mTvUpdateTime.setText(last_update_time);
//				} else {
//					Date mLastUpdateTime_24 = sdf.parse(mLastUpdateTime);
//					mLastUpdateTime_12 = sdd.format(mLastUpdateTime_24);
//					String last_update_time = String.format(
//							mContext.getString(R.string.last_update_time),
//							mLastUpdateTime_12);
//					mTvUpdateTime.setText(last_update_time);
//				}
//			}
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public void setOnRefreshListener(OnRefreshListener refreshListener) {
//		mRefreshListener = refreshListener;
//		mIsRefreshable = true;
////		mIsLoadMore = true;
//	}
//
//	public interface OnRefreshListener {
//		public void onRefresh();
//	}
//
//	public interface OnLoadDataListener {
//		public void onPullRefresh();
//
//		public void onLoadMore();
//	}
//
//	public void setOnLoadDataListener(OnLoadDataListener listener) {
//		mLoadDataListener = listener;
//		mIsRefreshable = true;
////		mIsLoadMore = true;
//	}
//
//	public void onLoadDataComplete(boolean loadFinish) {
//		if (mState == IDLE) {
//			refreshUpdateTime();
//			resetFooterView(!loadFinish);
//		} else if (mState == PULL_REFRESH) {
//			refreshUpdateTime();
//			cancelPullRefreshState();
//			resetFooterView(!loadFinish);
//		} else if (mState == LOAD_MORE) {
//			cancelLoadMoreState();
//			if (loadFinish) {
//				resetFooterView(false);
//			} else {
//				setFootProgressVisible(false);
//			}
//		}
//
//		mState = IDLE;
//	}
//
//	private void onRefresh() {
//		if (mRefreshListener != null) {
//			mRefreshListener.onRefresh();
//		}
//	}
//
//	public int getState() {
//		return mState;
//	}
//
//	public void setAdapter(BaseAdapter adapter, boolean needFootView) {
//		super.setAdapter(adapter);
//		mAdapter = adapter;
//		if (adapter.getCount() > 0) {
//			if (getFooterViewsCount() > 0) {
//				removeFooterView(mFootView);
//			}
//			if (needFootView) {
//				addFooterView(mFootView);
//			}
//		}
//
//	}
//
//	public View getFooterView() {
//		return mFootView;
//	}
//
//	public void resetFooterView(boolean showFooterView) {
//		if (showFooterView && getFooterViewsCount() == 0) {
//			mFootProgress.setVisibility(View.GONE);
//			addFooterView(mFootView);
//		} else if (!showFooterView && getFooterViewsCount() > 0) {
//			removeFooterView(mFootView);
//		}
//	}
//
//	public void setFootProgressVisible(boolean v) {
//		if (v) {
//			mFootProgress.setVisibility(View.VISIBLE);
//			mFootText.setText(R.string.get_more);
//		} else {
//			mFootProgress.setVisibility(View.GONE);
//			mFootText.setText(R.string.get_more_success);
//		}
//	}
//
//	public void cancelPullRefreshState() {
//		if (mHeadState == REFRESHING) {
//			mHeadState = DONE;
//		}
//		changeHeaderViewByState();
//		
//	}
//	
//	public void  cancelRefreshState(){
//		if(mHeadState != REFRESHING){
//			mHeadState = DONE;
//		}
//		changeHeaderViewByState();
//	}
//
//	public void cancelLoadMoreState() {
//		mFootProgress.setVisibility(View.GONE);
//		mFootText.setText(R.string.get_more_success);
//	}
//}
