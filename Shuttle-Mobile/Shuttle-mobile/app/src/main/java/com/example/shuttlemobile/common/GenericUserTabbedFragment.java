package com.example.shuttlemobile.common;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>GenericUserTabbedFragment</code> is a <code>GenericUserFragment</code>
 * with a <code>TabLayout</code> (support for tabs inside the fragment) and a
 * <code>FragmentContainerView</code> which displays one of the tabs.
 * <br/>
 * To put it simply, when you need a fragment with tabs, your fragment should extend this.
 * <br/>
 * All you have to define yourself are 3 methods for fetching the proper layout ID
 * and a method that maps each tab onto a fragment instance (be careful about the order!).
 */
public abstract class GenericUserTabbedFragment extends GenericUserFragment {
    protected TabLayout tabLayout;
    protected FragmentContainerView fragmentContainerView;
    protected List<Fragment> fragments = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutID(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (fragments.size() == 0) {
            initFragmentList();
            buildTabLayout();
        }
        tabLayout.getTabAt(getDefaultTab()).select();
        setVisibleFragment(fragments.get(getDefaultTab()));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // HACK: Is this the best way to do this? The fragments won't show up otherwise.
        fragments.clear();
        initFragmentList();
        buildTabLayout();
        tabLayout.getTabAt(getDefaultTab()).select();
        setVisibleFragment(fragments.get(getDefaultTab()));
    }

    protected final void setVisibleFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .replace(fragmentContainerView.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void buildTabLayout() {
        tabLayout = getActivity().findViewById(getTabLayoutID());
        fragmentContainerView = getActivity().findViewById(getFragmentContainerID());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                final int index = tab.getPosition();
                setVisibleFragment(fragments.get(index));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * @return LayoutID = R.layout.XYZ, the layout made when creating this fragment.
     */
    protected abstract int getLayoutID();

    /**
     *
     * @return R.layout.XYZ, which is defined in the layout for this fragment as
     * the ID of the tab layout element.
     */
    protected abstract int getTabLayoutID();

    /**
     *
     * @return R.layout.XYZ, which is defined in the layout for this fragment as
     * the ID of the fragment container view.
     */
    protected abstract int getFragmentContainerID();

    /**
     * Bind fragments to tab layout.
     * <br/>
     * How to use:
     * <pre>
     * this.fragments.add([Fragment1].newInstance(...));
     * this.fragments.add([Fragment2].newInstance(...));
     * </pre>
     * Order of insertion matters (it must match the one defined in this
     * fragment's layout).
     */
    protected abstract void initFragmentList();

    /**
     * @return Index of which tab should be open by default (default is 0).
     */
    protected int getDefaultTab() {
        return 0;
    }
}
