package com.secureidltd.belemaogan.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>{

    private Context mContext;
    private List<NoteInfo> mNoteInfoList;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> noteInfoList) {
        mContext = context;
        mNoteInfoList = noteInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View itemView = layoutInflater.inflate(R.layout.item_note_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        NoteInfo noteInfo = mNoteInfoList.get(position);
        viewHolder.mCourseTextView.setText(noteInfo.getCourse().getTitle());
        viewHolder.mTitleTextView.setText(noteInfo.getTitle());
        viewHolder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {

        if (mNoteInfoList != null){
            return mNoteInfoList.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mCourseTextView;
        public TextView mTitleTextView;
        public int mCurrentPosition;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCourseTextView = itemView.findViewById(R.id.text_course);
            mTitleTextView = itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent noteActivityIntent = new Intent(mContext, NoteActivity.class);
                    noteActivityIntent.putExtra(NoteActivity.NOTE_POSITION, mCurrentPosition);
                    mContext.startActivity(noteActivityIntent);
                }
            });
        }
    }
}
