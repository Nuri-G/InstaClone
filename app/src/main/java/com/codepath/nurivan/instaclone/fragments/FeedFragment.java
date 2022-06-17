package com.codepath.nurivan.instaclone.fragments;

import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.nurivan.instaclone.EndlessRecyclerViewScrollListener;
import com.codepath.nurivan.instaclone.Post;
import com.codepath.nurivan.instaclone.PostAdapter;
import com.codepath.nurivan.instaclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {
    public static final String TAG = "FeedActivity";

    private RecyclerView rvFeed;
    private SwipeRefreshLayout swipeContainer;
    private List<Post> postList;
    private PostAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_feed, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvFeed = view.findViewById(R.id.rvFeed);
        postList = new ArrayList<>();
        adapter = new PostAdapter(getContext(), postList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                refreshFeed(page);
            }
        };

        rvFeed.setLayoutManager(layoutManager);
        rvFeed.setAdapter(adapter);
        rvFeed.addOnScrollListener(scrollListener);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(() -> {
            refreshFeed(0);
            swipeContainer.setRefreshing(false);
        });

        refreshFeed(0);

    }
    private void refreshFeed(int page) {
        int ITEMS_PER_PAGE = 10;
        if(page == 0) {
            postList.clear();
        }
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(ITEMS_PER_PAGE);
        query.setSkip(page * ITEMS_PER_PAGE);
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error getting posts", e);
                    return;
                }
                postList.addAll(posts);
                adapter.notifyItemRangeChanged(page * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE + ITEMS_PER_PAGE);
            }
        });
    }
}