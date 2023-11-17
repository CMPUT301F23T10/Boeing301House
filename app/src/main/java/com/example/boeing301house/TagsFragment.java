package com.example.boeing301house;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.boeing301house.Itemlist.OnCompleteListener;
import com.example.boeing301house.databinding.FragmentTagsBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Fragment for adding tags
 */
public class TagsFragment extends Fragment {
    private Item currentItem;
    /**
     * This function creates an instance of the fragment and passes an {@link Item} to it.
     * Creates fragment via a no-argument constructor
     * @param item Parcelable {@link Item} object given to fragment
     * @return fragment instance
     */
    public static TagsFragment newInstance(Item item) {
        TagsFragment fragment = new TagsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("item_key", item);
        fragment.setArguments(bundle);

        return fragment;
    }

    private FragmentTagsBinding binding;
    private TagsFragment.OnTagsFragmentInteractionListener listener;


    // TODO: finish javadoc
    /**
     * Listener object for Adding/Editing {@link Item}. Uses Observer pattern.
     */
    public interface OnTagsFragmentInteractionListener {
        void onCancel();
    }
    /**
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: figure out multiple item selection vs single
            currentItem = (Item) getArguments().getParcelable("item_key"); // get item from bundle
        }
    }

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTagsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        TextView chipTextView = binding.chipsTextView;
        ChipGroup chipGroup = binding.chipsGroup;
        EditText editText = binding.addTagEditText;
        // TODO: remove testing chips

        Tag tag = new Tag(new DBConnection(getActivity().getApplicationContext()));
        ArrayList<String> arrayList = tag.getTags();
        ArrayList<String> selectedTags = currentItem.getTags();

        Random random = new Random();
        for (String s: arrayList) {
            Chip chip = (Chip) LayoutInflater.from(getActivity()).inflate(R.layout.chip_layout,null);
            chip.setText(s);
            chip.setId(random.nextInt());
            chipGroup.addView(chip);
        }

        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if (checkedIds.isEmpty()) {
                    chipTextView.setText("No tags selected");
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    selectedTags.clear();
                    for (int i: checkedIds) {
                        Chip chip = getActivity().findViewById(i);
                        stringBuilder.append(", ").append(chip.getText());
                        selectedTags.add((String)chip.getText());
                    }
                    chipTextView.setText("Selected Tag(s): " + stringBuilder.toString().replaceFirst(",",""));
                }
            }
        });

        binding.tagsViewTopBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: add functionality and check over
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.tagsViewTopBar.findViewById(R.id.itemListProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.updateItemConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), String.format(Locale.CANADA,selectedTags.toString()),
                                        Toast.LENGTH_SHORT).show(); // for testing
                currentItem.addTags(selectedTags);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        binding.addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().isEmpty()){
                    Chip chip = (Chip) LayoutInflater.from(getActivity()).inflate(R.layout.chip_layout,null);
                    chip.setText(editText.getText().toString());
                    chip.setId(random.nextInt());
                    chipGroup.addView(chip);
//                    tag.addTag(editText.getText().toString(), new OnCompleteListener<String>() {
//                        @Override
//                        public void onComplete(String item, boolean success) {
//                            if (success){
//                                Toast.makeText(getActivity(), String.format(Locale.CANADA,"Tags Added"),
//                                        Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getActivity(), String.format(Locale.CANADA,"Error, try again"),
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
                };
            }
        });
        return view;
}}
