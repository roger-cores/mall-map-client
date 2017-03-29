package com.jelliroo.mallmapbeta.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.adapters.LinkRecyclerAdapter;
import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.bean.Link;
import com.jelliroo.mallmapbeta.endpoints.BeaconEndPoint;
import com.jelliroo.mallmapbeta.endpoints.ClassEndPoint;
import com.jelliroo.mallmapbeta.endpoints.LinkEndPoint;
import com.jelliroo.mallmapbeta.vholders.BeaconViewHolder;
import com.jelliroo.mallmapbeta.vholders.LinkViewHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roger on 2/19/2017.
 */

public class LinkFragment extends Fragment {

    private String TAG = "LinkFragment";

    private Retrofit retrofit;

    private Spinner sourceSpinner, destinationSpinner;
    private TextView distanceTextView;
    private SeekBar distanceSeekBar;
    private RecyclerView recyclerView;
    private Button saveButton;

    private ArrayAdapter<ClassRecord> sourceClassAdapter, destinationClassAdapter;
    private LinkRecyclerAdapter linkRecyclerAdapter;



    public LinkFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.auth_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        final View view = inflater.inflate(R.layout.fragment_link, null);

        sourceSpinner = (Spinner) view.findViewById(R.id.source_class_spinner);
        destinationSpinner = (Spinner) view.findViewById(R.id.destination_class_spinner);

        distanceTextView = (TextView) view.findViewById(R.id.link_distance_tv);
        distanceSeekBar = (SeekBar) view.findViewById(R.id.lik_distance_seek);
        distanceTextView.setText("Distance: " + distanceSeekBar.getProgress());
        recyclerView = (RecyclerView) view.findViewById(R.id.link_recycler);
        saveButton = (Button) view.findViewById(R.id.link_save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sourceSpinner.getSelectedItem().equals(destinationSpinner.getSelectedItem())) return;

                Link link = new Link();
                link.setSourceLabel((ClassRecord) sourceSpinner.getSelectedItem());
                link.setDestinationLabel((ClassRecord) destinationSpinner.getSelectedItem());
                link.setDistance(distanceSeekBar.getProgress());

                LinkEndPoint linkEndPoint = retrofit.create(LinkEndPoint.class);
                Call<Link> callCreateLink = linkEndPoint.createLink(link);
                callCreateLink.enqueue(new Callback<Link>() {
                    @Override
                    public void onResponse(Call<Link> call, Response<Link> response) {
                        if(response.code() == 201){
                            linkRecyclerAdapter.addItem(response.body());
                            linkRecyclerAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Link Creation Failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<Link> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                        t.printStackTrace();
                    }
                });

            }
        });

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceTextView.setText("Distance: " + progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                LinkEndPoint linkEndPoint = retrofit.create(LinkEndPoint.class);
                final int identifier = linkRecyclerAdapter.getItem(viewHolder.getAdapterPosition()).getIdentifier();
                Call<Object> callForDeleteLink = linkEndPoint.deleteLinks(identifier);
                callForDeleteLink.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if(response.code() == 201){
                            linkRecyclerAdapter.removeItem(identifier);
                            linkRecyclerAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Delete Failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                        t.printStackTrace();
                    }
                });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        linkRecyclerAdapter = new LinkRecyclerAdapter();
        recyclerView.setAdapter(linkRecyclerAdapter);

        sourceClassAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_dropdown_item_1line);
        sourceSpinner.setAdapter(sourceClassAdapter);

        destinationClassAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_dropdown_item_1line);
        destinationSpinner.setAdapter(destinationClassAdapter);

        refreshClasses();
        refreshLinks();



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void refreshClasses(){
        ClassEndPoint classEndPoint = retrofit.create(ClassEndPoint.class);
        Call<List<ClassRecord>> callForGetAllClassRecords = classEndPoint.getAllClassRecords();
        callForGetAllClassRecords.enqueue(new Callback<List<ClassRecord>>() {
            @Override
            public void onResponse(Call<List<ClassRecord>> call, Response<List<ClassRecord>> response) {
                if(response.code() == 200){

                    destinationClassAdapter.clear();
                    destinationClassAdapter.addAll(response.body());
                    destinationClassAdapter.notifyDataSetChanged();

                    sourceClassAdapter.clear();
                    sourceClassAdapter.addAll(response.body());
                    sourceClassAdapter.notifyDataSetChanged();

                } else {
                    Log.d(TAG, "Get All ClassRecords Failed");
                }
            }

            @Override
            public void onFailure(Call<List<ClassRecord>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void refreshLinks(){
        LinkEndPoint linkEndPoint = retrofit.create(LinkEndPoint.class);
        Call<List<Link>> callForGetAllLinks = linkEndPoint.getAllLinks();
        callForGetAllLinks.enqueue(new Callback<List<Link>>() {
            @Override
            public void onResponse(Call<List<Link>> call, Response<List<Link>> response) {

                if(response.code() == 200){
                    linkRecyclerAdapter.clear();
                    linkRecyclerAdapter.addAll(response.body());
                    linkRecyclerAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Get All Links Failed");
                }


            }

            @Override
            public void onFailure(Call<List<Link>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }




}
