package com.secureidltd.belemaogan.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.secureidltd.belemaogan.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

/**
 * Created by Ogan Belema
 **/

public class NoteActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_ID = "com.secureidltd.belemaogan.notekeeper.NOTE_ID";
    public static final int ID_NOT_SET = -1;
    private static final String ORIGINAL_COURSE_ID = "com.secureidltd.belemaogan.notekeeper.ORIGINAL_COURSE_ID";
    private static final String ORIGINAL_NOTE_TITLE = "com.secureidltd.belemaogan.notekeeper.ORIGINAL_NOTE_TITLE";
    private static final String ORIGINAL_NOTE_TEXT = "com.secureidltd.belemaogan.notekeeper.ORIGINAL_NOTE_TEXT";
    private NoteInfo mNoteInfo;
    private Boolean mIsNewNote = false;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private Boolean mIsCanceling = false;
    private int mNewNotePosition;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private NoteKeeperOpenHelper mNoteKeeperOpenHelper;
    private Cursor mCursor;
    private int mCourseIdColumnIndex;
    private int mNoteTitleColumnIndex;
    private int mNoteTextColumnIndex;
    private int mNoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNoteKeeperOpenHelper = new NoteKeeperOpenHelper(this);

        mSpinnerCourses = findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);

        mSpinnerCourses.setAdapter(adapterCourses);


        readDisplayStateValues();

        if (savedInstanceState != null){
            restoreOriginalValues(savedInstanceState);
        } else {
            saveOriginalNote();
        }

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if (mIsNewNote){
            createNewNote();
        } else {
            loadNoteData();
        }
    }

    private void loadNoteData() {
        SQLiteDatabase database = mNoteKeeperOpenHelper.getReadableDatabase();

        String selection = NoteInfoEntry._ID + " =? ";
        String[] selectionArgs = new String[]{Integer.toString(mNoteId)};

        mCursor = database.query(NoteInfoEntry.TABLE_NAME, null, selection, selectionArgs, null,
                null, null);

        mCourseIdColumnIndex = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitleColumnIndex = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextColumnIndex = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        mCursor.moveToNext();
        displayNote();
    }

    private void restoreOriginalValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    private void saveOriginalNote() {
        if (mIsNewNote)
            return;

        if (mNoteInfo != null){
            mOriginalNoteCourseId = mNoteInfo.getCourse().getCourseId();
            mOriginalNoteTitle = mNoteInfo.getTitle();
            mOriginalNoteText = mNoteInfo.getText();
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
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
    }

    private void storePreviousNoteValues() {
        mNoteInfo.setCourse(DataManager.getInstance().getCourse(mOriginalNoteCourseId));
        mNoteInfo.setTitle(mOriginalNoteTitle);
        mNoteInfo.setText(mOriginalNoteText);
    }

    private void saveNote() {
        if (mNoteInfo != null){
            mNoteInfo.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
            mNoteInfo.setTitle(mTextNoteTitle.getText().toString());
            mNoteInfo.setText(mTextNoteText.getText().toString());
        }
    }

    private void displayNote() {

        String courseId = mCursor.getString(mCourseIdColumnIndex);
        String noteTitle = mCursor.getString(mNoteTitleColumnIndex);
        String noteText = mCursor.getString(mNoteTextColumnIndex);

        List<CourseInfo> courseInfos = DataManager.getInstance().getCourses();
        CourseInfo courseInfo = DataManager.getInstance().getCourse(courseId);

        int itemPosition = courseInfos.indexOf(courseInfo);
        mSpinnerCourses.setSelection(itemPosition);

        mTextNoteTitle.setText(noteTitle);
        mTextNoteText.setText(noteText);

        mCursor.close();


        /*if (mNoteInfo != null){

            if (mSpinnerCourses.getAdapter() instanceof ArrayAdapter){
                int itemPosition = ((ArrayAdapter) mSpinnerCourses.getAdapter()).getPosition(mNoteInfo.getCourse());
                mSpinnerCourses.setSelection(itemPosition);
            }

            mTextNoteTitle.setText(mNoteInfo.getTitle());

            mTextNoteText.setText(mNoteInfo.getText());
        }*/
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(NOTE_ID)){
            mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
            //mNoteInfo = DataManager.getInstance().getNotes().get(mNoteId);
        } else {
            mIsNewNote = true;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNewNotePosition < lastNoteIndex);

        return super.onPrepareOptionsMenu(menu);
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
        } else if (id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    private void moveNext() {
        saveNote();

        mNewNotePosition = mNewNotePosition + 1;
        mNoteInfo = DataManager.getInstance().getNotes().get(mNewNotePosition);

        saveOriginalNote();
        displayNote();

        invalidateOptionsMenu();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNoteKeeperOpenHelper.close();
    }
}
