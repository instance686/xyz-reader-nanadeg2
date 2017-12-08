package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.example.xyzreader.R;
import com.example.xyzreader.data.DBCols;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.ReadAllData;

import java.util.ArrayList;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    /*private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private int mTopInset;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;*/
    /*private View mUpButtonContainer;
    private View mUpButton;*/
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private ArrayList<String> idList;
    private int selectedPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);

        idList=getIntent().getStringArrayListExtra("IDLIST");
        selectedPos=getIntent().getIntExtra("Selected Pos",0);

        Log.d("pos",selectedPos+"");

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageTransformer(true,new ZoomOutPageTransformer());
        mPager.setOffscreenPageLimit(1);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.setCurrentItem(selectedPos);



        /*getLoaderManager().initLoader(1, null, this).forceLoad();

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(1);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }
                mSelectedItemId = mCursor.getLong(DBCols.ID);

                //updateUpButtonPosition();
            }
        });


        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                Log.v("mStartId",""+mStartId);
                mSelectedItemId = mStartId;
                Log.v("mSelectedItemId",""+mSelectedItemId);

            }
        }*/
    }

   /* @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
       // return ArticleLoader.newAllArticlesInstance(this);
        Log.v("onlf","onloadCreated");
        return new ReadAllData(getApplicationContext(),ArticleDetailActivity.this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v("onlf","onloadfinished");
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(DBCols.ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    Log.v("pos",""+position);
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }*/

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
/*
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
            if (fragment != null) {
                //mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
                //updateUpButtonPosition();
            }
        }*/

        @Override
        public Fragment getItem(int position) {
            Bundle arguments = new Bundle();
            arguments.putString("item_id", idList.get(position));

            ArticleDetailFragment articleDetailFragment= ArticleDetailFragment.newInstance(idList.get(position));
            articleDetailFragment.setArguments(arguments);
            return articleDetailFragment;
        }

        @Override
        public int getCount() {
            return idList.size();
        }
    }

    public void homePressed(){
        getSupportFragmentManager().popBackStack();
    }
}