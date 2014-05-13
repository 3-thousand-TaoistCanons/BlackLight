package us.shandian.blacklight.ui.statuses;

import android.app.Fragment;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.animation.TranslateAnimation;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentStatePagerAdapter;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.util.List;

import us.shandian.blacklight.R;
import us.shandian.blacklight.cache.statuses.HomeTimeLineApiCache;
import us.shandian.blacklight.model.MessageModel;
import us.shandian.blacklight.model.MessageListModel;
import us.shandian.blacklight.ui.comments.StatusCommentFragment;

public class SingleActivity extends SwipeBackActivity
{
	private MessageModel mMsg;
	
	private Fragment mMsgFragment;
	private Fragment mCommentFragment;
	private Fragment mRepostFragment;
	
	private ViewPager mPager;
	private View mRoot;
	private View mContent;
	
	private MenuItem mExpand;
	
	private boolean mExpanded = true;
	private boolean mAnimating = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single);
		
		// Action Bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		// Arguments
		mMsg = getIntent().getParcelableExtra("msg");
		
		// Init
		mRoot = findViewById(R.id.single_root);
		mContent = findViewById(R.id.single_content);
		
		mMsgFragment = new HackyFragment();
		mCommentFragment = new StatusCommentFragment(mMsg.id);
		mRepostFragment = new RepostTimeLineFragment(mMsg.id);
		getFragmentManager().beginTransaction().add(R.id.single_content, mMsgFragment).commit();
		
		mPager = (ViewPager) findViewById(R.id.single_pager);
		mPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public Fragment getItem(int position) {
				switch (position) {
					case 0:
						return mCommentFragment;
					case 1:
						return mRepostFragment;
					default:
						return null;
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.single, menu);
		mExpand = menu.findItem(R.id.expand);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.expand:
				expandOrCollapse();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void expandOrCollapse() {
		if (mAnimating) return;
		
		mAnimating = true;
		TranslateAnimation anim;
		if (mExpanded) {
			anim = new TranslateAnimation(0, 0, 0, -mContent.getHeight());
		} else {
			mContent.setVisibility(View.VISIBLE);
			anim = new TranslateAnimation(0, 0, -mContent.getHeight(), 0);
		}
		anim.setDuration(500);
		mRoot.postDelayed(new Runnable() {
			@Override
			public void run() {
				mRoot.clearAnimation();
				mRoot.setTranslationY(0);
				if (mExpanded) {
					mContent.setVisibility(View.GONE);
				}
				mExpanded = !mExpanded;
				mExpand.setIcon(mExpanded ? R.drawable.ic_action_collapse : R.drawable.ic_action_expand);
				mAnimating = false;
			}
		}, 500);
		mRoot.startAnimation(anim);
	}
	
	private class HackyApiCache extends HomeTimeLineApiCache {
		public HackyApiCache(Context context) {
			super(context);
			mMessages = new MessageListModel();
			((List<MessageModel>) mMessages.getList()).add(mMsg);
		}

		@Override
		public void loadFromCache() {
			
		}

		@Override
		public void load(boolean newWeibo) {
			
		}
		
		@Override
		public void cache() {
			
		}
	}
	
	private class HackyFragment extends HomeTimeLineFragment {
		
		@Override
		protected void bindFooterView(LayoutInflater inflater) {
			
		}

		@Override
		protected void bindSwipeToRefresh(ViewGroup v) {
			
		}

		@Override
		protected HomeTimeLineApiCache bindApiCache() {
			return new HackyApiCache(getActivity());
		}
		
		@Override
		protected void initTitle() {
			
		}
	}
}
