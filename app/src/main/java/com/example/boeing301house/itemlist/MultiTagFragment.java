package com.example.boeing301house.itemlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.boeing301house.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultiTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiTagFragment extends DialogFragment {
    private ArrayList<String> newTags;
    private ChipGroup multiTagChipGroup;
    private OnTagInteractionListener listener;

    /**
     * Response listener
     */
    public interface OnTagInteractionListener {
        void onTagOKPressed(ArrayList<String> tags, boolean success);
    }

    /**
     * Required empty no arg public constructor
     */
    public MultiTagFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TagFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MultiTagFragment newInstance() {
        MultiTagFragment fragment = new MultiTagFragment();
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnTagInteractionListener) {
            listener = (OnTagInteractionListener) context;
        } else {
            throw new RuntimeException((context + "OnTagInteractionListener not implemented"));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_multitag, null);

        newTags = new ArrayList<>();

        multiTagChipGroup = view.findViewById(R.id.multiTagChipGroup);

        TextInputLayout updateMultiTag = view.findViewById(R.id.updateMultiTag);

        updateMultiTag.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                return;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (s.charAt(s.length() - 1) == ' ' || s.charAt(s.length() - 1) == '\n') {
                        if (s.length() > 1 && (!newTags.contains(s.toString().trim()))) {
                            newTags.add(s.toString().trim());
                            addChip(s.toString().trim());
                        }
                        s.clear();
                    }
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setTitle("ADD TAGS")
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> {
                    listener.onTagOKPressed(null, false);
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString = updateMultiTag.getEditText().getText().toString();
                        if (!inputString.isEmpty()) {
                            newTags.add(inputString.trim());
                        }
                        listener.onTagOKPressed(newTags, true);
                    }
                }).create();

        // return super.onCreateDialog(savedInstanceState);
    }


    /**
     * Add chip to chip group
     * @param tag tag to add
     */
    private void addChip(String tag) {
        final String name = tag;
        final Chip newChip = new Chip(requireContext());
        newChip.setText(name);
        newChip.setCloseIconResource(R.drawable.ic_close_button_24dp);
        newChip.setCloseIconEnabled(true);
        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTags.remove(name);
                multiTagChipGroup.removeView(newChip);
            }
        });

        multiTagChipGroup.addView(newChip);

    }
}