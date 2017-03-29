package com.jelliroo.mallmapbeta.fragments;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.activities.ClassesActivity;
import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.bean.MapPin;
import com.jelliroo.mallmapbeta.customviews.PinView;
import com.jelliroo.mallmapbeta.endpoints.ClassEndPoint;
import com.jelliroo.mallmapbeta.enums.ClassType;
import com.jelliroo.mallmapbeta.enums.PinType;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roger on 2/14/2017.
 */

public class ClassesMapFragment extends Fragment {

    private PinView imageView;
    private EditText className;
    private Button placeMarkerButton;

    private Retrofit retrofit;

    private String TAG = "ClassesMapFragment";

    private ClassType classType;


    public ClassesMapFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classType = ClassType.valueOf(getArguments().getString("classType"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_place_class_marker, container, false);

        imageView = (PinView) v.findViewById(R.id.imageView);
        className = (EditText) v.findViewById(R.id.editText);
        placeMarkerButton = (Button) v.findViewById(R.id.button);

        imageView.setImage(ImageSource.resource(R.drawable.floor_3r));

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.auth_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        refresh();

        placeMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(className.getText().toString().trim().equals(""))
                    return;

                float x =imageView.getX() + imageView.getWidth()  / 2;
                x -= 48;
                float y =imageView.getY() + imageView.getHeight() / 2;
                y -= 48;
                PointF point = imageView.viewToSourceCoord(x,y);
//                imageView.addPin(new MapPin(point.x, point.y, 1, PinType.CLASS));

                final ClassEndPoint classEndPoint = retrofit.create(ClassEndPoint.class);
                final ClassRecord classRecord = new ClassRecord(className.getText().toString(), point.x, point.y, classType);
                Call<ClassRecord> callForCreateClassRecord = classEndPoint.createClassRecord(classRecord);

                callForCreateClassRecord.enqueue(new Callback<ClassRecord>() {
                    @Override
                    public void onResponse(Call<ClassRecord> call, Response<ClassRecord> response) {
                        if(response.code() == 201){
                            imageView.addPin(new MapPin(response.body().getX(), response.body().getY(), 1, PinType.CLASS, response.body().getLabel(), response.body().getClassType()));
                            className.setText("");
                        } else {
                            Log.d(TAG, "Create Failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ClassRecord> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                        t.printStackTrace();
                    }
                });


            }
        });

        return v;
    }

    public void refresh(){
        ClassEndPoint classEndPoint = retrofit.create(ClassEndPoint.class);
        Call<List<ClassRecord>> callForGetAllClasses = classEndPoint.getAllClassRecords();

        callForGetAllClasses.enqueue(new Callback<List<ClassRecord>>() {
            @Override
            public void onResponse(Call<List<ClassRecord>> call, Response<List<ClassRecord>> response) {
                if(response.code() == 200){
                    List<ClassRecord> classRecords = response.body();
                    for(ClassRecord classRecord : classRecords){
                        imageView.addPin(new MapPin(classRecord.getX(), classRecord.getY(), 1, PinType.CLASS, classRecord.getLabel(), classRecord.getClassType()));
                    }
                } else {
                    Log.d(TAG, "Get all classes failed");
                }
            }

            @Override
            public void onFailure(Call<List<ClassRecord>> call, Throwable t) {
                if(t.getMessage()!=null)
                    Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }


}
