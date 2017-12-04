    package com.example.xyzreader.ui;
    import android.arch.persistence.room.Dao;
    import android.content.Intent;
    import android.content.res.Configuration;
    import android.database.Cursor;
    import android.graphics.Color;
    import android.graphics.Typeface;
    import android.net.ConnectivityManager;
    import android.net.NetworkInfo;
    import android.os.Bundle;
    import android.os.Parcelable;
    import android.os.PersistableBundle;
    import android.support.design.widget.AppBarLayout;
    import android.support.design.widget.CollapsingToolbarLayout;
    import android.support.design.widget.CoordinatorLayout;
    import android.support.design.widget.Snackbar;
    import android.support.v4.view.ViewCompat;
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
    import com.example.xyzreader.data.ItemsContract;
    import com.example.xyzreader.data.MyApplication;
    import com.example.xyzreader.data.OnLoadFinished;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
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
        Snackbar snackbar,hsnackbar;
        CoordinatorLayout coordinatorLayout,coordinatorLayout1;
        Typeface rosario;
        boolean checkAsync=false;
        private ArrayList<Data> viewData=new ArrayList<>();
        int choice;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
        // Use default locale format
        private SimpleDateFormat outputFormat = new SimpleDateFormat();
        // Most time functions can only handle 1902 - 2037
        CollapsingToolbarLayout toolbarContainerView;
        private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
            @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_article_list);

            mToolbar = (Toolbar) findViewById(R.id.toolbar);

            coordinatorLayout=findViewById(R.id.coordinator);


             toolbarContainerView = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);


                mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
            appBarLayout= (AppBarLayout) findViewById(R.id.toolbar_container);

            mRecyclerView = (GridRecyclerView) findViewById(R.id.recycler_view);
            appDatabase=((MyApplication)getApplicationContext()).getDatabase();
            rosario=Typeface.createFromAsset(getAssets(),"Rosario-Regular.ttf");
            toolbarContainerView.setTitle("XYZ-READER");
            toolbarContainerView.setCollapsedTitleTypeface(rosario);
            toolbarContainerView.setExpandedTitleTypeface(rosario);

                if (savedInstanceState == null) {
                mSwipeRefreshLayout.post(new Runnable() {   //start refreshing if screen orientation not changed
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        refresh();
                    }
                });
            }
            else{
                         choice=savedInstanceState.getInt("choice");
                         checkAsync=savedInstanceState.getBoolean("checkAsync");
                        if(checkAsync && choice==1) {
                            mSwipeRefreshLayout.post(new Runnable() {   //start refreshing if screen orientation changed
                                @Override
                                public void run() {
                                    mSwipeRefreshLayout.setRefreshing(true);
                                    refresh();
                                }
                            });
                        }
                        else {
                            new DownloadTask(getApplicationContext(), ArticleListActivity.this, 2).execute();
                        }
            }


        }



        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){


            }
            else if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE) {

            }
        }

        public void Snack(View view,String text,int lenght) {
            try {
                snackbar = Snackbar.make(view, text, lenght);
                snackbar.show();
            } catch (Exception e) {
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
            if(checkConnectivity()) {
                Snack(coordinatorLayout,"Refreshing",Snackbar.LENGTH_LONG);
                new DownloadTask(getApplicationContext(), ArticleListActivity.this,1).execute();
            }else {
                Snack(coordinatorLayout,"Not Connected to Internet",Snackbar.LENGTH_LONG);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            mSwipeRefreshLayout.setEnabled(true);
       }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putBoolean("checkAsync",checkAsync);
            outState.putInt("choice",choice);
        }



        @Override
        public void loadFinished(List<Data> da) {
            mSwipeRefreshLayout.setRefreshing(false);
            if (da == null) {
                Snack(coordinatorLayout,"Data not found",Snackbar.LENGTH_LONG);
                mRecyclerView.setAdapter(null);
            }else {
                viewData= (ArrayList<Data>) da;
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(ArticleListActivity.this, R.anim.grid_layout_animation_from_bottom);
                mRecyclerView.setLayoutAnimation(animationController);
                Adapter adapter=new Adapter(viewData,rosario);
                adapter.setHasStableIds(true);
                mRecyclerView.setAdapter(adapter);

            }
        }

        @Override
        public void isRunning(boolean val,int ch) {
            checkAsync=val;
            choice=ch;
        }

        @Override
        public void isRunning(boolean val) {

        }

        @Override
        public void loadFinished(Cursor cursor) {

        }


        public boolean checkConnectivity() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null || !ni.isConnected()) {
                return false;
            }
            return true;
        }

        private class Adapter extends GridRecyclerView.Adapter<ViewHolder> {
            private List<Data> data;
            int pos;
            Typeface rr;

            public Adapter(List<Data> data,Typeface rr) {
                this.data=data;
                this.rr=rr;
            }


            @Override
            public long getItemId(int position) {
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
                      startActivity(new Intent(Intent.ACTION_VIEW,
                                ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
                    }
                });
                return vh;
            }

            private Date parsePublishedDate() {
                try {
//
                     return dateFormat.parse(data.get(pos).getPublished_date());

                } catch (ParseException ex) {
                    Log.e(TAG, ex.getMessage());
                    Log.i(TAG, "passing today's date");
                    return new Date();
                }
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {

                holder.titleView.setText(data.get(position).getTitle());
                holder.titleView.setTypeface(rr);
                Date publishedDate = parsePublishedDate();
                if (!publishedDate.before(START_OF_EPOCH.getTime())) {

                    holder.subtitleView.setText(Html.fromHtml(
                            DateUtils.getRelativeTimeSpanString(
                                    publishedDate.getTime(),
                                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                    DateUtils.FORMAT_ABBREV_ALL).toString()
                                    + "<br/>" + " by "
                                    + data.get(position).getAuthor() ));
                } else {
                    holder.subtitleView.setText(Html.fromHtml(
                            outputFormat.format(publishedDate)
                                    + "<br/>" + " by "
                                   + data.get(position).getAuthor() ));

                }
                holder.subtitleView.setTypeface(rr);

                Glide.with(ArticleListActivity.this)
                       .load(data.get(position).getThumb())
                        .centerCrop()
                        .crossFade()
                        .into(holder.thumbnailView);
            }

            @Override
            public int getItemCount() {
                return data.size();
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