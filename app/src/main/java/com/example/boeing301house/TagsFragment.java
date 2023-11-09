package com.example.boeing301house;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * Fragment for adding tags
 */
public class TagsFragment extends Fragment {
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
}
