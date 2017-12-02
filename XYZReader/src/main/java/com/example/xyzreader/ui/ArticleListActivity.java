    package com.example.xyzreader.ui;
    import android.database.Cursor;
    import android.net.ConnectivityManager;
    import android.net.NetworkInfo;
    import android.os.Bundle;
    import android.support.design.widget.AppBarLayout;
    import android.support.design.widget.CollapsingToolbarLayout;
    import android.support.design.widget.CoordinatorLayout;
    import android.support.design.widget.Snackbar;
    import android.support.v4.widget.SwipeRefreshLayout;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.GridLayoutManager;
    import android.support.v7.widget.RecyclerView;
    import android.support.v7.widget.Toolbar;
    import android.text.Html;
    import android.text.format.DateUtils;
    import android.util.Log;
    import android.view.View;
    import android.view.ViewAnimationUtils;
    import android.view.ViewGroup;
    import android.view.animation.AnimationUtils;
    import android.view.animation.LayoutAnimationController;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.bumptech.glide.Glide;
    import com.example.xyzreader.R;
    import com.example.xyzreader.data.AppDatabase;
    import com.example.xyzreader.data.ArticleLoader;
    import com.example.xyzreader.data.Data;
    import com.example.xyzreader.data.DownloadTask;
    import com.example.xyzreader.data.MyApplication;
    import com.example.xyzreader.data.OnLoadFinished;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.GregorianCalendar;
    import java.util.List;

    /**
     * An activity representing a list of Articles. This activity has different presentations for
     * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
     * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
     * activity presents a grid of items as cards.
     */
    public class ArticleListActivity extends AppCompatActivity implements
          //  LoaderManager.LoaderCallbacks<Cursor> ,
            AppBarLayout.OnOffsetChangedListener,OnLoadFinished{

        private static final String TAG = ArticleListActivity.class.toString();
        private Toolbar mToolbar;
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private GridRecyclerView mRecyclerView;
        private AppBarLayout appBarLayout;
        AppDatabase appDatabase;
        Snackbar snackbar;
        CoordinatorLayout coordinatorLayout;

        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
        // Use default locale format
        private SimpleDateFormat outputFormat = new SimpleDateFormat();
        // Most time functions can only handle 1902 - 2037
        private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_article_list);

            mToolbar = (Toolbar) findViewById(R.id.toolbar);

            coordinatorLayout=findViewById(R.id.coordinator);


            CollapsingToolbarLayout toolbarContainerView = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

            snackbar=Snackbar.make(coordinatorLayout,"Loading Data ,Please wait.",Snackbar.LENGTH_INDEFINITE);

            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
            appBarLayout= (AppBarLayout) findViewById(R.id.toolbar_container);

            mRecyclerView = (GridRecyclerView) findViewById(R.id.recycler_view);
        //    getLoaderManager().initLoader(0, null, this);
            appDatabase=((MyApplication)getApplicationContext()).getDatabase();


            if (savedInstanceState == null) {
               refresh();
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            appBarLayout.addOnOffsetChangedListener(this);
        }

        @Override
        protected void onPause() {
            super.onPause();
            appBarLayout.removeOnOffsetChangedListener(this);
        }

        private void refresh() {
            mRecyclerView.setAdapter(null);
            if(checkConnectivity()==1){
                mIsRefreshing=true;
                updateRefreshingUI();
                snackbar.setText("Please Wait Loading Data.");
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            new DownloadTask(getApplicationContext(),ArticleListActivity.this).execute();
            }
            else{
                mIsRefreshing=false;
                updateRefreshingUI();
                snackbar.setText("No Internet Connection!");
                snackbar.setDuration(Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            //startService(new Intent(this, UpdaterService.class));
        }

        @Override
        protected void onStart() {
            super.onStart();

     //      registerReceiver(mRefreshingReceiver,
       //             new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
        }


        @Override
        protected void onStop() {
            super.onStop();
//            unregisterReceiver(mRefreshingReceiver);
        }

        private boolean mIsRefreshing = false;

//        private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
//                    mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
//                    updateRefreshingUI();
//                }
//            }
//        };

        private void updateRefreshingUI() {
            mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
        }

//        @Override
//        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//            return ArticleLoader.newAllArticlesInstance(this);
//        }
//
//        @Override
//        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//            Adapter adapter = new Adapter(cursor);
//            adapter.setHasStableIds(true);
//            mRecyclerView.setAdapter(adapter);
//           /* int columnCount = getResources().getInteger(R.integer.list_column_count);
//            StaggeredGridLayoutManager sglm =
//                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
//          */  mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
//        }
//
//        @Override
//        public void onLoaderReset(Loader<Cursor> loader) {
//            mRecyclerView.setAdapter(null);
//        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            mSwipeRefreshLayout.setEnabled(verticalOffset==0);
        }

        @Override
        public void loadFinished(List<Data> data) {
            if(data==null){
                snackbar.setText("Bad Internet Connection!");
                snackbar.setDuration(Snackbar.LENGTH_LONG);
                snackbar.show();
                mIsRefreshing = false;
                updateRefreshingUI();
                mRecyclerView.setAdapter(null);

            }
            else {
                snackbar.dismiss();
                mIsRefreshing = false;
                updateRefreshingUI();
                Adapter adapter = new Adapter(data);
                adapter.setHasStableIds(true);
                                mRecyclerView.setAdapter(adapter);
           /* int columnCount = getResources().getInteger(R.integer.list_column_count);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
          */
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                LayoutAnimationController animationController=AnimationUtils.loadLayoutAnimation(ArticleListActivity.this,
                        R.anim.grid_layout_animation_from_bottom);
                mRecyclerView.setLayoutAnimation(animationController);
            }
        }

        public int checkConnectivity(){
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null || !ni.isConnected()) {
                return 0;
            }
            return 1;
        }

        private class Adapter extends GridRecyclerView.Adapter<ViewHolder> {
            private Cursor mCursor;
            private List<Data> data;
            int pos;
            public Adapter(Cursor cursor) {
                mCursor = cursor;
            }

            public Adapter(List<Data> data) {
                this.data=data;
            }


            @Override
            public long getItemId(int position) {
                //mCursor.moveToPosition(position);

               // return mCursor.getLong(ArticleLoader.Query._ID);
                pos=position;
                return Long.parseLong(data.get(position).getId());
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
                final ViewHolder vh = new ViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //startActivity(new Intent(Intent.ACTION_VIEW,
                          //      ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
                    }
                });
                return vh;
            }

            private Date parsePublishedDate() {
                try {
//                    String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
//
//                    return dateFormat.parse(date);
                     return dateFormat.parse(data.get(pos).getPublished_date());

                } catch (ParseException ex) {
                    Log.e(TAG, ex.getMessage());
                    Log.i(TAG, "passing today's date");
                    return new Date();
                }
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
               // mCursor.moveToPosition(position);
              //  holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
                holder.titleView.setText(data.get(position).getTitle());
                Date publishedDate = parsePublishedDate();
                if (!publishedDate.before(START_OF_EPOCH.getTime())) {

                    holder.subtitleView.setText(Html.fromHtml(
                            DateUtils.getRelativeTimeSpanString(
                                    publishedDate.getTime(),
                                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                    DateUtils.FORMAT_ABBREV_ALL).toString()
                                    + "<br/>" + " by "
                                    +// mCursor.getString(ArticleLoader.Query.AUTHOR)));
                            data.get(position).getAuthor() ));
                } else {
                    holder.subtitleView.setText(Html.fromHtml(
                            outputFormat.format(publishedDate)
                                    + "<br/>" + " by "
                                    + //mCursor.getString(ArticleLoader.Query.AUTHOR)));
                                    data.get(position).getAuthor() ));

                }

                Glide.with(ArticleListActivity.this)
                       // .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                       .load(data.get(position).getThumb())
                        .centerCrop()
                        .crossFade()
                        .into(holder.thumbnailView);
                /*.setImageUrl(
                        mCursor.getString(ArticleLoader.Query.THUMB_URL),
                        ImageLoaderHelper.getInstance(ArticleListActivity.this).getImageLoader());
                holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));*/

            }

            @Override
            public int getItemCount() {
                return data.size();
                //    return mCursor.getCount();
            }
        }

        public static class ViewHolder extends GridRecyclerView.ViewHolder {
            public ImageView thumbnailView;
            public TextView titleView;
            public TextView subtitleView;

            public ViewHolder(View view) {
                super(view);
                thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
                titleView = (TextView) view.findViewById(R.id.article_title);
                subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
            }
        }


    }