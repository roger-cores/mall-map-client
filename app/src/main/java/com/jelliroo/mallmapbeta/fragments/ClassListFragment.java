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

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.activities.ClassesActivity;
import com.jelliroo.mallmapbeta.adapters.ClassRecyclerAdapter;
import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.bean.MapPin;
import com.jelliroo.mallmapbeta.endpoints.BeaconEndPoint;
import com.jelliroo.mallmapbeta.endpoints.ClassEndPoint;
import com.jelliroo.mallmapbeta.enums.ClassType;
import com.jelliroo.mallmapbeta.enums.PinType;
import com.jelliroo.mallmapbeta.vholders.BeaconViewHolder;
import com.jelliroo.mallmapbeta.vholders.ClassViewHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roger on 2/14/2017.
 */

public class ClassListFragment extends Fragment {

    private RecyclerView recyclerView;

    private ClassRecyclerAdapter classRecyclerAdapter;
    private Retrofit retrofit;

    private String TAG = "ClassListFragment";

    private ClassType classType;


    public ClassListFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classType = ClassType.valueOf(getArguments().getString("classType"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_class_list, container, false);
        recyclerView = (RecyclerView) v;

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.auth_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        classRecyclerAdapter = new ClassRecyclerAdapter(R.layout.class_item);
        recyclerView.setAdapter(classRecyclerAdapter);

        refresh();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ClassEndPoint classEndPoint = retrofit.create(ClassEndPoint.class);
                final String name = ((ClassViewHolder) viewHolder).getClassName();
                Call<Object> classForDeleteClassRecord = classEndPoint.deleteClassRecord(name);
                classForDeleteClassRecord.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if(response.code() == 201){
                            classRecyclerAdapter.removeClass(name);
                            classRecyclerAdapter.notifyDataSetChanged();
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

        return v;
    }

    public void refresh(){
        ClassEndPoint classEndPoint = retrofit.create(ClassEndPoint.class);
        Call<List<ClassRecord>> callForGetAllClasses = classEndPoint.getAllClassRecordsByType(classType.name());

        callForGetAllClasses.enqueue(new Callback<List<ClassRecord>>() {
            @Override
            public void onResponse(Call<List<ClassRecord>> call, Response<List<ClassRecord>> response) {
                if(response.code() == 200){
                    classRecyclerAdapter.clear();
                    List<ClassRecord> classRecords = response.body();
                    for(ClassRecord classRecord : classRecords){
                        classRecyclerAdapter.addClass(classRecord);
                    }
                    classRecyclerAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Get all classes failed");
                }
            }

            @Override
            public void onFailure(Call<List<ClassRecord>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }


}
