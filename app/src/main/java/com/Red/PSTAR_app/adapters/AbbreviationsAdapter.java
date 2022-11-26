package com.Red.PSTAR_app.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Red.PSTAR_app.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Alexey Matrosov on 28.09.2018.
 */
public class AbbreviationsAdapter extends RecyclerView.Adapter<AbbreviationsAdapter.ViewHolder> {
    private final List<String> abbreviations;
    private Activity mActivity;

    public AbbreviationsAdapter(List<String> abbreviations, Activity mActivity) {
        this.abbreviations = abbreviations;
        this.mActivity = mActivity;
        Collections.sort(abbreviations, String::compareToIgnoreCase);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.abbreviations_text_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String text = abbreviations.get(i);

        char symbol = Character.toUpperCase(text.charAt(0));

        viewHolder.itemText.setText(text);
        viewHolder.headerText.setText(Character.toString(symbol));
        viewHolder.headerText.setVisibility(isFirstInGroup(i) ? View.VISIBLE : View.GONE);
        viewHolder.itemDivider.setVisibility(isLastInGroup(i) ? View.INVISIBLE : View.VISIBLE);

        if (i == 0) {
            viewHolder.noteText.setVisibility(View.VISIBLE);
            viewHolder.noteText.setText(mActivity.getString(R.string.abbreviations_note));
        } else {
            viewHolder.noteText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return abbreviations.size();
    }

    private boolean isFirstInGroup(int index) {
        if (index == 0)
            return true;

        char currentSymbol = Character.toUpperCase(abbreviations.get(index).charAt(0));
        char lastSymbol = Character.toUpperCase(abbreviations.get(index - 1).charAt(0));

        return currentSymbol != lastSymbol;
    }

    private boolean isLastInGroup(int index) {
        if (index >= abbreviations.size() - 1)
            return true;

        char currentSymbol = Character.toUpperCase(abbreviations.get(index).charAt(0));
        char lastSymbol = Character.toUpperCase(abbreviations.get(index + 1).charAt(0));

        return currentSymbol != lastSymbol;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemText;
        private TextView headerText;
        private View itemDivider;
        private TextView noteText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemText = itemView.findViewById(R.id.item_text);
            headerText = itemView.findViewById(R.id.header_text);
            itemDivider = itemView.findViewById(R.id.item_divider);
            noteText = itemView.findViewById(R.id.note_text);
        }
    }
}