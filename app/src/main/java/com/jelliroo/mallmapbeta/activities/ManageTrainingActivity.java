package com.jelliroo.mallmapbeta.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.adapters.TrainingRecyclerAdapter;
import com.jelliroo.mallmapbeta.bean.ClassCountResponse;
import com.jelliroo.mallmapbeta.endpoints.BeaconEndPoint;
import com.jelliroo.mallmapbeta.endpoints.KNNEndPoint;
import com.jelliroo.mallmapbeta.vholders.BeaconViewHolder;
import com.jelliroo.mallmapbeta.vholders.ClassViewHolder;
import com.jelliroo.mallmapbeta.vholders.TrainingViewHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageTrainingActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private Retrofit retrofit;
    TrainingRecyclerAdapter adapter;
    private String TAG = "ManageTrainingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_training);

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.auth_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        recyclerView = (RecyclerView) findViewById(R.id.training_recycler);
        adapter = new TrainingRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
                KNNEndPoint knnEndPoint = retrofit.create(KNNEndPoint.class);
                final String name = ((TrainingViewHolder) viewHolder).linkName.getText().toString();
                Call<Object> callForDeleteTrainingByClass = knnEndPoint.deleteClasses(name);
                callForDeleteTrainingByClass.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if(response.code() == 201){
                            adapter.removeItem(name);
                            adapter.notifyDataSetChanged();
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


        refresClassCount();
    }

    public void refresClassCount(){
        KNNEndPoint knnEndPoint = retrofit.create(KNNEndPoint.class);
        Call<List<ClassCountResponse>> callForClassCount = knnEndPoint.getClassCount();
        callForClassCount.enqueue(new Callback<List<ClassCountResponse>>() {
            @Override
            public void onResponse(Call<List<ClassCountResponse>> call, Response<List<ClassCountResponse>> response) {
                if(response.code() == 200){
                    adapter.clear();
                    adapter.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Load ClassCount Failed");
                }
            }

            @Override
            public void onFailure(Call<List<ClassCountResponse>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_manage_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_delete_all:
                KNNEndPoint knnEndPoint = retrofit.create(KNNEndPoint.class);
                Call<Object> callForDeleteAllTraining = knnEndPoint.deleteClasses();
                callForDeleteAllTraining.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if(response.code() == 201){
                            adapter.clear();
                            adapter.notifyDataSetChanged();
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
                break;

            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
