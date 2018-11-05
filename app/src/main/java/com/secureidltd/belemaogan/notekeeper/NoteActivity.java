package com.secureidltd.belemaogan.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

/**
 * Created by Ogan Belema
 **/

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_POSITION = "com.secureidltd.belemaogan.notekeeper.NOTE_POSITION";
    public static final int DEFAULT_POSITION_VALUE = -1;
    private NoteInfo mNoteInfo;
    private Boolean mIsNewNote = false;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private Boolean mIsCanceling = false;
    private int mNewNotePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerCourses = findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);

        mSpinnerCourses.setAdapter(adapterCourses);


        readDisplayStateValues();

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if (mIsNewNote){
            createNewNote();
        } else {
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
        }
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNewNotePosition = dm.createNewNote();
        mNoteInfo = dm.getNotes().get(mNewNotePosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCanceling){
            if (mIsNewNote){
                DataManager.getInstance().removeNote(mNewNotePosition);
            }
        } else {
            saveNote();
        }
    }

    private void saveNote() {
        mNoteInfo.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNoteInfo.setTitle(mTextNoteTitle.getText().toString());
        mNoteInfo.setText(mTextNoteText.getText().toString());
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

        if (intent != null && intent.hasExtra(NOTE_POSITION)){
            int position = intent.getIntExtra(NOTE_POSITION, DEFAULT_POSITION_VALUE);
            mNoteInfo = DataManager.getInstance().getNotes().get(position);
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
        if (id == R.id.action_send_mail) {
            sendMail();
            return true;
        } else if (id == R.id.action_cancel){
            mIsCanceling = true;
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMail() {

        CourseInfo courseInfo = (CourseInfo) mSpinnerCourses.getSelectedItem();

        String subject = mTextNoteTitle.getText().toString();

        String text = "Learning \""+ courseInfo.getTitle() +"\" on Pluralsight\n"
                + mTextNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
}
