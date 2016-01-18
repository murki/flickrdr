package com.murki.flckrdr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FlickrListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String CLASSNAME = FlickrListFragment.class.getCanonicalName();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(CLASSNAME, "onCreate()");
        Toast.makeText(getContext(), "onCreate()", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getContext(), "onCreateView()", Toast.LENGTH_SHORT).show();
        View view = inflater.inflate(R.layout.fragment_flickr_list, container, false);

        Log.d(CLASSNAME, "onCreateView()");

        setupView(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(CLASSNAME, "onActivityCreated()");
        Toast.makeText(getContext(), "onCreateView()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        Log.d(CLASSNAME, "onRefresh()");
    }

    private void setupView(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.flickr_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }
}
