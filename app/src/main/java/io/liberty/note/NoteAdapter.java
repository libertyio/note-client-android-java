package io.liberty.note;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import io.liberty.note.protocol.NoteList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private NoteList noteList;

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

    public NoteAdapter(NoteList incomingNoteList) {
        noteList = incomingNoteList;
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
        noteViewHolder.textViewCardTitle.setText(noteList.list.get(i).info.title);
        noteViewHolder.textViewCardBody.setText(noteList.list.get(i).content);
    }

    // Return the size of noteList
    @Override
    public int getItemCount() {
        if (noteList != null && noteList.list != null) {
            return noteList.list.size();
        } else {
            return 0;
        }
    }
}
