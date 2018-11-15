package com.secureidltd.belemaogan.notekeeper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.secureidltd.belemaogan.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.secureidltd.belemaogan.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;


/**
 * Created by Ogan Belema
 **/

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int NOTES_LOADER = 0;
    public static final int COURSES_LOADER = 1;
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
    private SimpleCursorAdapter mAdapterCourses;
    private boolean mCourseQueryFinished;
    private boolean mNoteQueryFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNoteKeeperOpenHelper = new NoteKeeperOpenHelper(this);

        mSpinnerCourses = findViewById(R.id.spinner_courses);

        mAdapterCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1}, 0);
        mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerCourses.setAdapter(mAdapterCourses);

        //loadCourseData();
        getSupportLoaderManager().restartLoader(COURSES_LOADER, null, this);


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
            //loadNoteData();
            getSupportLoaderManager().restartLoader(NOTES_LOADER, null, this);
        }
    }

    private void loadCourseData() {
        SQLiteDatabase database = mNoteKeeperOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(CourseInfoEntry.TABLE_NAME,
                null, null, null, null, null,
                CourseInfoEntry.COLUMN_COURSE_TITLE);
        mAdapterCourses.changeCursor(cursor);
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteInfoEntry.COLUMN_COURSE_ID, "");
        contentValues.put(NoteInfoEntry.COLUMN_NOTE_TITLE, "");
        contentValues.put(NoteInfoEntry.COLUMN_NOTE_TEXT, "");

        SQLiteDatabase database = mNoteKeeperOpenHelper.getWritableDatabase();
        mNoteId = (int) database.insert(NoteInfoEntry.TABLE_NAME, null, contentValues);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCanceling){
            if (mIsNewNote){
                deleteNotFromDatabase();
            } else {
                //storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
    }

    private void deleteNotFromDatabase() {
        final String selection = NoteInfoEntry._ID +" = ?";
        final String[] selectionArgs = new String[]{String.valueOf(mNoteId)};

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase sqLiteDatabase = mNoteKeeperOpenHelper.getReadableDatabase();
                sqLiteDatabase.delete(NoteInfoEntry.TABLE_NAME, selection, selectionArgs);
                return null;
            }
        };
        task.execute();

    }

    private void storePreviousNoteValues() {
        mNoteInfo.setCourse(DataManager.getInstance().getCourse(mOriginalNoteCourseId));
        mNoteInfo.setTitle(mOriginalNoteTitle);
        mNoteInfo.setText(mOriginalNoteText);
    }

    private void saveNote() {
        String courseId = selectedCourseId();
        String noteTitle = mTextNoteTitle.getText().toString();
        String noteText = mTextNoteText.getText().toString();
        saveNoteToDatabase(courseId, noteTitle, noteText);
    }

    private String selectedCourseId() {
        int selectedPosition = mSpinnerCourses.getSelectedItemPosition();
        Cursor cursor = mAdapterCourses.getCursor();
        cursor.moveToPosition(selectedPosition);
        int courseIdIndex = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        return cursor.getString(courseIdIndex);
    }

    private void saveNoteToDatabase(String noteCourseId, String noteTitle, String noteText){

        final String selection = NoteInfoEntry._ID + " = ?";
        final String[] selectionArgs = new String[]{Integer.toString(mNoteId)};

        final ContentValues contentValues = new ContentValues();
        contentValues.put(NoteInfoEntry.COLUMN_COURSE_ID, noteCourseId);
        contentValues.put(NoteInfoEntry.COLUMN_NOTE_TITLE, noteTitle);
        contentValues.put(NoteInfoEntry.COLUMN_NOTE_TEXT, noteText);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase database = mNoteKeeperOpenHelper.getWritableDatabase();
                database.update(NoteInfoEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                return null;
            }
        };
        task.execute();
    }

    private void displayNote() {

        String courseId = mCursor.getString(mCourseIdColumnIndex);
        String noteTitle = mCursor.getString(mNoteTitleColumnIndex);
        String noteText = mCursor.getString(mNoteTextColumnIndex);


        int courseIndex = getIndexOfCourseId(courseId);
        mSpinnerCourses.setSelection(courseIndex);

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

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor = mAdapterCourses.getCursor();
        int CourseIdColumnIndex = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);

        int courseRowIndex = 0;

        while (cursor.moveToNext()){
            String cursorCourseId = cursor.getString(CourseIdColumnIndex);
            if (cursorCourseId.equals(courseId)){
                break;
            }

            courseRowIndex++;
        }
        return courseRowIndex;
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


    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        CursorLoader loader = null;
        if (id == NOTES_LOADER){
            loader = createNotesLoader();
        } else if (id == COURSES_LOADER){
            loader = createCourseLoader();
        }
        return loader;
    }

    private CursorLoader createCourseLoader() {
        mCourseQueryFinished = false;
        Uri uri = Uri.parse("content://com.secureidltd.belemaogan.notekeeper.provider");

        return new CursorLoader(this, uri, null, null, null,
                CourseInfoEntry.COLUMN_COURSE_TITLE);
    }

    private CursorLoader createNotesLoader() {
        mNoteQueryFinished = false;
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase database = mNoteKeeperOpenHelper.getReadableDatabase();

                String selection = NoteInfoEntry._ID + " =? ";
                String[] selectionArgs = new String[]{Integer.toString(mNoteId)};

                return database.query(NoteInfoEntry.TABLE_NAME, null, selection, selectionArgs,
                        null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == NOTES_LOADER){
            loadFinishedNotes(cursor);
        } else if (loader.getId() == COURSES_LOADER){
            loadFinishedCourses(cursor);
        }
    }

    private void loadFinishedCourses(Cursor cursor) {
        mAdapterCourses.changeCursor(cursor);
        mCourseQueryFinished = true;
        displayNoteWhenQueriesFinished();
    }

    private void loadFinishedNotes(Cursor cursor) {
        mCursor = cursor;

        mCourseIdColumnIndex = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitleColumnIndex = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextColumnIndex = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        mCursor.moveToNext();
        mNoteQueryFinished = true;
        displayNoteWhenQueriesFinished();
    }

    private void displayNoteWhenQueriesFinished() {
        if (mCourseQueryFinished && mNoteQueryFinished){
            displayNote();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(loader.getId() == NOTES_LOADER){
            if (mCursor != null){
                mCursor.close();
            }
        } else if (loader.getId() == COURSES_LOADER){
            mAdapterCourses.changeCursor(null);
        }
    }
}
