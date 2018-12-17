package com.secureidltd.belemaogan.notekeeper;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Belema Ogan on 12/17/2018.
 */

public class CourseEventBroadcastHelper {

    //my app defined action
    public static final String ACTION_COURSE_EVENT = "com.secureidltd.belemaogan.notekeeper.action.COURSE_EVENT";

    //the extras for the intent that will be passed to sendBroadcast
    public static final String EXTRA_COURSE_ID = "com.secureidltd.belemaogan.notekeeper.extras.COURSE_ID";
    public static final String EXTRA_COURSE_MESSAGE = "com.secureidltd.belemaogan.notekeeper.extras.COURSE_MESSAGE";

    public static void sendEventBraodcast(Context context, String courseId, String message){

        Intent intent = new Intent(ACTION_COURSE_EVENT);
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_COURSE_MESSAGE, message);

        context.sendBroadcast(intent);
    }

}
