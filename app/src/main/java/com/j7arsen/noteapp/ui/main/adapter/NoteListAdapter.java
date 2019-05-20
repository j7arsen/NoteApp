package com.j7arsen.noteapp.ui.main.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j7arsen.noteapp.R;
import com.j7arsen.noteapp.dataclasses.NoteModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ItemViewHolder> {

    private ArrayList<NoteModel> noteModelList;

    private NoteListListener noteListListener;

    private boolean isLoading = false;
    protected boolean isMoreItems = true;

    public NoteListAdapter() {
        noteModelList = new ArrayList<>();
    }

    public void setNoteListListener(NoteListListener noteListListener) {
        this.noteListListener = noteListListener;
    }

    public void finishLoadMore() {
        isLoading = false;
        isMoreItems = false;
    }

    public void reset() {
        isLoading = false;
        isMoreItems = true;
    }

    public void updateData(ArrayList<NoteModel> noteModelList) {
        this.noteModelList = noteModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_note, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {
        if (position != 0 && !isLoading && isMoreItems && getItemCount() - position <= 3) {
            loadData();
        }
        if (noteModelList != null && noteModelList.size() != 0) {
            NoteModel noteModel = noteModelList.get(position);
            if (noteModel != null) {
                itemViewHolder.tvNoteDescription.setText(noteModel.getDescription());
                NoteFormatterTime noteFormatterTime = getFormatNoteTime(noteModel.getTime());
                itemViewHolder.tvNoteDate.setText(noteFormatterTime.getDate());
                itemViewHolder.tvNoteTime.setText(noteFormatterTime.getTime());
            }
        }
    }

    private NoteFormatterTime getFormatNoteTime(long time) {
        NoteFormatterTime noteFormatterTime = new NoteFormatterTime();
        SimpleDateFormat resultTimeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat resultDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        noteFormatterTime.setDate(resultDateFormat.format(calendar.getTime()));
        noteFormatterTime.setTime(resultTimeFormat.format(calendar.getTime()));
        return noteFormatterTime;
    }

    private void loadData() {
        isLoading = true;
        if (noteListListener != null) {
            noteListListener.loadData();
        }
    }

    @Override
    public int getItemCount() {
        return noteModelList == null ? 0 : noteModelList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvNoteDate;
        private TextView tvNoteTime;
        private TextView tvNoteDescription;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoteDate = (TextView) itemView.findViewById(R.id.tv_note_date);
            tvNoteTime = (TextView) itemView.findViewById(R.id.tv_note_time);
            tvNoteDescription = (TextView) itemView.findViewById(R.id.tv_note_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (noteListListener != null) {
                noteListListener.onItemClick((noteModelList != null && noteModelList.size() != 0) ? noteModelList.get(getAdapterPosition()) : null);
            }
        }
    }

    public interface NoteListListener {

        void onItemClick(NoteModel noteModel);

        void loadData();

    }

    class NoteFormatterTime {

        private String time;
        private String date;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

}
