package com.secureidltd.belemaogan.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder>{

    private Context mContext;
    private List<CourseInfo> mCourseInfoList;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courseInfoList) {
        mContext = context;
        mCourseInfoList = courseInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View itemView = layoutInflater.inflate(R.layout.item_course_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CourseInfo courseInfo = mCourseInfoList.get(position);
        viewHolder.mCourseTextView.setText(courseInfo.getTitle());
        viewHolder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {

        if (mCourseInfoList != null){
            return mCourseInfoList.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mCourseTextView;
        public int mCurrentPosition;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCourseTextView = itemView.findViewById(R.id.text_course);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent courseNotesIntent = new Intent(mContext, NoteListActivity.class);
                    courseNotesIntent.putExtra(NoteListActivity.COURSE_ID,
                            mCourseInfoList.get(mCurrentPosition).getCourseId());
                    mContext.startActivity(courseNotesIntent);
                }
            });
        }
    }
}
