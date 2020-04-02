package io.liberty.note;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private ArrayList<Note> notes;

    // Provide a reference to the views for each data item
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewNote;
        TextView textViewCardTitle;
        TextView textViewCardBody;

        public NoteViewHolder(View itemView) {
            super(itemView);
            cardViewNote = itemView.findViewById(R.id.cardViewNote);
            textViewCardTitle = itemView.findViewById(R.id.textViewCardTitle);
            textViewCardBody = itemView.findViewById(R.id.textViewCardBody);
        }
    }

    public NoteAdapter(ArrayList<Note> incomingNotes) {
        notes = incomingNotes;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_layout, viewGroup, false);
        NoteViewHolder nvh = new NoteViewHolder(v);
        return nvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NoteViewHolder noteViewHolder, int i) {
        noteViewHolder.cardViewNote.setElevation(8);
        noteViewHolder.textViewCardTitle.setText(notes.get(i).title);
        noteViewHolder.textViewCardBody.setText(notes.get(i).body);
    }

    // Return the size of notes
    @Override
    public int getItemCount() {
        return notes.size();
    }
}
