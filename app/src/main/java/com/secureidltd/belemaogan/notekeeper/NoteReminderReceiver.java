package com.secureidltd.belemaogan.notekeeper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NoteReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_NOTE_TITLE = "com.secureidltd.belemaogan.notekeeper.extras.NOTE_TITLE";
    public static final String EXTRA_NOTE_TEXT = "com.secureidltd.belemaogan.notekeeper.extras.NOTE_TEXt";
    public static final String EXTRA_NOTE_ID = "com.secureidltd.belemaogan.notekeeper.extras.NOTE_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            String noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE);
            String noteText = intent.getStringExtra(EXTRA_NOTE_TEXT);
            int noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0);

            NoteReminderNotification.notify(context, noteTitle, noteText, noteId);
        }
    }
}
