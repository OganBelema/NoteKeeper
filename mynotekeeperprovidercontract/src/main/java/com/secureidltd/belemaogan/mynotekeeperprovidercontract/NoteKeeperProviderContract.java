package com.secureidltd.belemaogan.mynotekeeperprovidercontract;

import android.net.Uri;
import android.provider.BaseColumns;


public final class NoteKeeperProviderContract {

    private NoteKeeperProviderContract(){}

    public static final String AUTHORITY = "com.secureidltd.belemaogan.notekeeper.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public interface CoursesIdColumns {
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    public interface CoursesColumns {
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }

    public interface NoteColumns {
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
    }

    public static final class Courses implements CoursesColumns, BaseColumns, CoursesIdColumns {
        public static final String PATH = "courses";
        //content://com.secureidltd.belemaogan.notekeeper.provider/courses
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Notes implements NoteColumns, BaseColumns, CoursesIdColumns, CoursesColumns{
        public static final String PATH = "notes";
        //content://com.secureidltd.belemaogan.notekeeper.provider/notes
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
        public static final String PATH_EXPANDED = "notes_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);
    }
}
