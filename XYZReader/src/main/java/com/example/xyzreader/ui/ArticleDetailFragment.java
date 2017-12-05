package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.DBCols;
import com.example.xyzreader.data.SingleDataLoader;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";
    private static final float PARALLAX_FACTOR = 1.25f;

    private Cursor mCursor;
    private String mItemId;
    private View mRootView;
    private ImageView home;
    private int vibrantColor = 0xFF333333;
    // private ObservableScrollView mScrollView;
    //private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    private ColorDrawable mStatusBarColorDrawable;
    /*
        private int mTopInset;
        private View mPhotoContainerView;*/
    private ImageView mPhotoView;
    private LinearLayout meta_bar;
    //private int mScrollY;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(String itemId) {
        return new ArticleDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("onCreate","fragmentCreated");

//        if (getArguments().containsKey(ARG_ITEM_ID)) {
        mItemId = getArguments().getString(ARG_ITEM_ID);
        //  }

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
        mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
                R.dimen.detail_card_top_margin);
        setHasOptionsMenu(true);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        Log.v("onActivityCreated","onActivityCreated");
        getLoaderManager().initLoader(1, null, this).forceLoad();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("OncreateView","OnCreateView");
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        home= (ImageView) mRootView.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivityCast().homePressed();
            }
        });
        mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);

        mStatusBarColorDrawable = new ColorDrawable(0);
        meta_bar= (LinearLayout) mRootView.findViewById(R.id.meta_bar);

        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });



        //bindViews();
        //updateStatusBar();
        return mRootView;
    }


    private Date parsePublishedDate() {
        try {

            String date = mCursor.getString(DBCols.PD);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());

            return new Date();
        }
    }

    private void bindViews() {

        final CollapsingToolbarLayout collapsingToolbarLayout=mRootView.findViewById(R.id.collapsing_toolbar_layout);

        TextView titleView = (TextView) mRootView.findViewById(R.id.article_title);
        TextView bylineView = (TextView) mRootView.findViewById(R.id.article_byline);
        TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);


        titleView.setText(mCursor.getString(DBCols.TITLE));
        bylineView.setText(mCursor.getString(DBCols.AUTHOR));

        Date publishedDate = parsePublishedDate();
        bylineView.setText(publishedDate.toString());
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            bylineView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <font color='#ffffff'>"
                            + mCursor.getString(DBCols.AUTHOR)
                            + "</font>"));
        }else {
            bylineView.setText(outputFormat.format(publishedDate)+mCursor.getString(DBCols.AUTHOR));
        }
        String text = mCursor.getString(DBCols.BODY).replaceAll("(\r\n|\n)", "\n");
        bodyView.setText(Html.fromHtml(text));

        Glide.with(getActivity())
                .load(mCursor.getString(DBCols.PHOTO))
                .asBitmap()
                .into(new BitmapImageViewTarget(mPhotoView){
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                // access palette colors here
                                vibrantColor=palette.getVibrantColor(vibrantColor);
                                meta_bar.setBackgroundColor(vibrantColor);
                                collapsingToolbarLayout.setContentScrimColor(vibrantColor);
                                mRootView.findViewById(R.id.progressView).setVisibility(View.GONE);
                            }
                        };
                        if(resource!=null)
                            Palette.from(resource).generate(paletteListener);
                    }
                });
        /*if (mRootView == null) {
            return;
        }


        bylineView.setMovementMethod(new LinkMovementMethod());



        //bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            titleView.setText(mCursor.getString(DBCols.TITLE));
            Log.v("title",mCursor.getString(DBCols.TITLE)+"");
            *//*Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                bylineView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + " by <font color='#ffffff'>"
                                + mCursor.getString(DBCols.AUTHOR)
                                + "</font>"));

            } else {
                // If date is before 1902, just show the string
                bylineView.setText(outputFormat.format(publishedDate)+mCursor.getString(DBCols.AUTHOR));

            }*//*

        } else {
            mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            bylineView.setText("N/A" );
            bodyView.setText("N/A");
        }*/
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
        Log.v("fragLoad","fragmentLoad");
        return new SingleDataLoader(getActivityCast().getApplicationContext(),getActivityCast(),mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v("flf","fragmentLoadFinished");
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        //bindViews();
    }


    public int getUpButtonFloor() {
        /*if (mPhotoContainerView == null || mPhotoView.getHeight() == 0) {
            return Integer.MAX_VALUE;
        }

        // account for parallax
        return mIsCard
                ? (int) mPhotoContainerView.getTranslationY() + mPhotoView.getHeight() - mScrollY
                : mPhotoView.getHeight() - mScrollY;*/
        return 0;
    }
}