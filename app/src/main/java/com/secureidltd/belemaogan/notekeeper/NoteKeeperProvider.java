package com.secureidltd.belemaogan.notekeeper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.secureidltd.belemaogan.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.secureidltd.belemaogan.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.secureidltd.belemaogan.notekeeper.NoteKeeperProviderContract.Courses;
import com.secureidltd.belemaogan.notekeeper.NoteKeeperProviderContract.CoursesIdColumns;
import com.secureidltd.belemaogan.notekeeper.NoteKeeperProviderContract.Notes;

public class NoteKeeperProvider extends ContentProvider {

    private NoteKeeperOpenHelper mNoteKeeperOpenHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int COURSES = 0;
    private static final int NOTES = 1;
    private static final int NOTES_EXPANDED = 2;

    static {
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Courses.PATH, COURSES);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH, NOTES);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH_EXPANDED, NOTES_EXPANDED);
    }

    public NoteKeeperProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mNoteKeeperOpenHelper = new NoteKeeperOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = mNoteKeeperOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match){
            case COURSES:
                cursor = sqLiteDatabase.query(CourseInfoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case NOTES:
                cursor = sqLiteDatabase.query(NoteInfoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case NOTES_EXPANDED:
                cursor = notesExpandedQuery(sqLiteDatabase, projection, selection, selectionArgs, sortOrder);
                break;
        }

        return cursor;
    }

    private Cursor notesExpandedQuery(SQLiteDatabase sqLiteDatabase, String[] projection, String selection,
                                      String[] selectionArgs, String sortOrder) {
        String[] columns = new String[projection.length];
        for (int index = 0; index < projection.length; index++){
            //if column[index] = projection[index].equals(BaseColumns._ID) do...else
            columns[index] = projection[index].equals(BaseColumns._ID) ||
                    projection[index].equals(CoursesIdColumns.COLUMN_COURSE_ID) ?
                    NoteInfoEntry.getQName(projection[index]) : projection[index];
        }
        String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " + CourseInfoEntry.TABLE_NAME
                + " ON " + NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = "
                + CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);
        return sqLiteDatabase.query(tablesWithJoin, columns, selection, selectionArgs,
                null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
