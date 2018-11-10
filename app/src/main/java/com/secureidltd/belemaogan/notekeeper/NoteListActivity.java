package com.secureidltd.belemaogan.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

/**
 * Created by Ogan Belema
 **/

public class NoteListActivity extends AppCompatActivity {
    private NoteRecyclerAdapter mNoteRecyclerAdapter;

    public static final String COURSE_ID = "com.secureidltd.belemaogan.notekeeper.COURSE_ID";
    private List<NoteInfo> mNoteInfoList;

    //private ArrayAdapter<NoteInfo> mAdapterNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab_add_new_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });

        initializeDisplayContent();
    }

    private void initializeDisplayContent() {

        final RecyclerView recyclerViewNotes = findViewById(R.id.list_items);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewNotes.setLayoutManager(layoutManager);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(COURSE_ID)){
            String courseId = intent.getStringExtra(COURSE_ID);
            CourseInfo courseInfo = DataManager.getInstance().getCourse(courseId);

            setTitle(courseInfo.getTitle());

            mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);
            recyclerViewNotes.setAdapter(mNoteRecyclerAdapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
