package com.frintos.frintos.FragmentAdaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.frintos.frintos.ChatsFragment;
import com.frintos.frintos.RequestFragment;
import com.frintos.frintos.TossFragment;

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
            case 2:
                return new TossFragment();
            default:
                return new ChatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
