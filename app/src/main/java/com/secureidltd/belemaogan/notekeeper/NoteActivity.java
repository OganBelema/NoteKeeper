package com.secureidltd.belemaogan.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_INFO = "com.secureidltd.belemaogan.notekeeper.NOTE_INFO";
    private NoteInfo mNoteInfo;
    private Boolean mIsNewNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinnerCourses = findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);

        spinnerCourses.setAdapter(adapterCourses);


        readDisplayStateValues();

        EditText textNoteTitle = findViewById(R.id.text_note_title);
        EditText textNoteText = findViewById(R.id.text_note_text);

        if (!mIsNewNote){
            displayNote(spinnerCourses, textNoteTitle, textNoteText);
        }
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        if (mNoteInfo != null){

            if (spinnerCourses.getAdapter() instanceof ArrayAdapter){
                int itemPosition = ((ArrayAdapter) spinnerCourses.getAdapter()).getPosition(mNoteInfo.getCourse());
                spinnerCourses.setSelection(itemPosition);
            }

            textNoteTitle.setText(mNoteInfo.getTitle());

            textNoteText.setText(mNoteInfo.getText());
        }
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(NOTE_INFO)){
            mNoteInfo = intent.getParcelableExtra(NOTE_INFO);
        } else {
            mIsNewNote = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
