package com.example.lazyvision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ShowAlldataActivity extends AppCompatActivity {

    RecyclerView recyclerView,recyclerView2;
    String path;
    String cno;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_alldata);


        String myUriString = getIntent().getStringExtra("imageUri");
         cno = getIntent().getStringExtra("caseno");
        String imgUrl = getIntent().getStringExtra("imageUrl");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView2=(RecyclerView)findViewById(R.id.recyclerView1);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager((this)));

        firebaseDatabase = FirebaseDatabase.getInstance();

        path = "caseno/c" + cno.substring(0, 3) + "/evidence/labelVision    ";
        Log.e("caseno :...", path + "...................");
        mRef = firebaseDatabase.getReference(path);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<JavaModelClassForShowingAllData, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<JavaModelClassForShowingAllData, ViewHolder>(
                        JavaModelClassForShowingAllData.class,
                        R.layout.showall_recycler_view,
                        ViewHolder.class,
                        mRef
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, JavaModelClassForShowingAllData javaModelClassForShowingAllData, int i) {
                        viewHolder.setDetails(getApplicationContext(), javaModelClassForShowingAllData.getImageURL(), javaModelClassForShowingAllData.getLabel());


                    }
                };
        path = "caseno/c" + cno.substring(0, 3) + "/evidence/textVision";
        mRef=FirebaseDatabase.getInstance().getReference(path);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        textVisionSetData();

    }
        public void textVisionSetData()
        {
            FirebaseRecyclerAdapter<JavaModelClassForShowingAllData, ViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<JavaModelClassForShowingAllData, ViewHolder>(
                            JavaModelClassForShowingAllData.class,
                            R.layout.showall_recycler_view,
                            ViewHolder.class,
                            mRef
                    ) {
                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, JavaModelClassForShowingAllData javaModelClassForShowingAllData, int i) {
                            viewHolder.setDetails(getApplicationContext(), javaModelClassForShowingAllData.getImageURL(), javaModelClassForShowingAllData.getLabel());


                        }
                    };


            recyclerView2.setAdapter(firebaseRecyclerAdapter);


        }

}

