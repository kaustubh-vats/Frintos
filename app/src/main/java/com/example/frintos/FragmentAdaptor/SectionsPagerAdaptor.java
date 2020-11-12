package com.example.frintos.FragmentAdaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.frintos.ChatsFragment;
import com.example.frintos.RequestFragment;
import com.example.frintos.TossFragment;

public class SectionsPagerAdaptor extends FragmentStateAdapter {

    public SectionsPagerAdaptor(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new RequestFragment();
            case 1:
                return new ChatsFragment();
            case 2:
                return new TossFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
