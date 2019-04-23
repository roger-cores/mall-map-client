package com.jelliroo.mallmapbeta.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.adapters.LaunchRecyclerAdapter;
import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.bean.Map;
import com.jelliroo.mallmapbeta.endpoints.ClassEndPoint;
import com.jelliroo.mallmapbeta.endpoints.MapEndPoint;
import com.jelliroo.mallmapbeta.fragments.SuperClassFragment;
import com.jelliroo.mallmapbeta.vholders.ClassViewHolder;
import com.jelliroo.mallmapbeta.vholders.LaunchViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LaunchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private Retrofit retrofit;
    private LaunchRecyclerAdapter adapter;
    private String TAG = "LaunchActivity";
    TextView imageName;

    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.auth_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        recyclerView = (RecyclerView) findViewById(R.id.launch_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new LaunchRecyclerAdapter();
        adapter.setActivity(this);

        recyclerView.setAdapter(adapter);
        refresh();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                MapEndPoint mapEndPoint = retrofit.create(MapEndPoint.class);
                final String name = ((LaunchViewHolder) viewHolder).getMapName();
                Call<Object> callForDeleteMap = mapEndPoint.deleteMap(name);
                callForDeleteMap.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if(response.code() == 201){
                            adapter.removeMap(name);
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



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.launch_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        } else if(item.getItemId() == R.id.add_floor_btn){
            View view = getLayoutInflater().inflate(R.layout.add_building_layout, null);
            imageName = (TextView) view.findViewById(R.id.textView5);
            final EditText floorName = (EditText) view.findViewById(R.id.editText2);
            Button button = (Button) view.findViewById(R.id.button3);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
            });
            selectedImage = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this)
                    .setTitle("Add a floor")
                    .setView(view)
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(selectedImage == null || floorName.getText().toString().trim().equalsIgnoreCase("")) return;
                            File imageFile = new File(getRealPathFromURI(LaunchActivity.this, selectedImage));
                            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                            MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
                            RequestBody labelRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), floorName.getText().toString().trim());
                            MapEndPoint mapEndPoint = retrofit.create(MapEndPoint.class);
                            Call<Map> callForCreateMapImage = mapEndPoint.createMapImage(labelRequestBody, body);
                            callForCreateMapImage.enqueue(new Callback<Map>() {
                                @Override
                                public void onResponse(Call<Map> call, Response<Map> response) {
                                    if(response.code() == 201){
                                        refresh();
                                    } else {
                                        Log.d(TAG, "Delete Failed");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Map> call, Throwable t) {
                                    Log.d(TAG, t.getMessage());
                                    t.printStackTrace();
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    File file = new File(getRealPathFromURI(LaunchActivity.this, selectedImage));
                    if(imageName != null) imageName.setText(file.getName());
                }
                break;
        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void refresh(){
        MapEndPoint mapEndPoint = retrofit.create(MapEndPoint.class);
        Call<List<Map>> callForGetAllMaps = mapEndPoint.getAllMaps();

        callForGetAllMaps.enqueue(new Callback<List<Map>>() {
            @Override
            public void onResponse(Call<List<Map>> call, Response<List<Map>> response) {
                if(response.code() == 200){
                    adapter.clear();
                    List<Map> maps = response.body();
                    for(Map map : maps){
                        adapter.addMap(map);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Get all classes failed");
                }
            }

            @Override
            public void onFailure(Call<List<Map>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }
}
