package com.born2go.lazzybee.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.adapter.RecyclerViewDrawerListAdapter;
import com.born2go.lazzybee.db.Course;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.Arrays;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;

    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    RecyclerViewDrawerListAdapter recyclerViewDrawerListAdapter;
    private Context context;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        // selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        try {


            this.context = getActivity();
            //init List Object
            @SuppressWarnings("ConstantConditions") final List<String> objects = Arrays.asList(context.getResources().getStringArray(R.array.drawer_list));

//        objects.add(LazzyBeeShare.DRAWER_USER);
//        objects.add(LazzyBeeShare.DRAWER_TITLE_COURSE);
//        objects.add("English Word");
////        objects.add(new Course("Math"));
//        objects.add(LazzyBeeShare.DRAWER_ADD_COURSE);
//        objects.add(LazzyBeeShare.DRAWER_LINES);
//        objects.add(LazzyBeeShare.DRAWER_SETTING);
//        objects.add(LazzyBeeShare.DRAWER_ABOUT);


            //init mRecyclerViewDrawerList
            final RecyclerView mRecyclerViewDrawerList = view.findViewById(R.id.mRecyclerViewDrawerList);
            //init GridLayoutManager
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerViewDrawerList.getContext(), 1);
            //init Adapter
            recyclerViewDrawerListAdapter = new RecyclerViewDrawerListAdapter(context, objects);

            //set version app
            String versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            TextView lbAppVersion = view.findViewById(R.id.mVesionApp);
            lbAppVersion.setText(String.valueOf("Version:" + versionName));

            mRecyclerViewDrawerList.setLayoutManager(gridLayoutManager);
            mRecyclerViewDrawerList.setAdapter(recyclerViewDrawerListAdapter);
            mRecyclerViewDrawerList.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, (view1, position) -> {
                        Object o = objects.get(position);
                        if (o.equals(LazzyBeeShare.DRAWER_ADD_COURSE)) {
                            selectItem(LazzyBeeShare.DRAWER_ADD_COURSE_INDEX);
                        } else if (o.equals(LazzyBeeShare.DRAWER_SETTING)) {
                            selectItem(LazzyBeeShare.DRAWER_SETTINGS_INDEX);
                        } else if (o.equals(getString(R.string.drawer_about))) {
                            selectItem(LazzyBeeShare.DRAWER_ABOUT_INDEX);
                        } else if (o.equals(getString(R.string.drawer_dictionary))) {
                            selectItem(LazzyBeeShare.DRAWER_DICTIONARY_INDEX);
                        } else if (o.equals(LazzyBeeShare.DRAWER_USER)) {
                            selectItem(LazzyBeeShare.DRAWER_USER_INDEX);
                        } else //noinspection ConstantConditions
                            if (o instanceof Course) {
                            selectItem(LazzyBeeShare.DRAWER_COURSE_INDEX);
                        } else if (o.equals(getString(R.string.drawer_subject))) {
                            selectItem(LazzyBeeShare.DRAWER_MAJOR_INDEX);
                        } else if (o.equals(getString(R.string.drawer_help))) {
                            selectItem(LazzyBeeShare.DRAWER_HELP_INDEX);
                        } else if (o.equals(getString(R.string.drawer_statistical))) {
                            selectItem(LazzyBeeShare.DRAWER_STATISTICAL_INDEX);
                        } else if (o.equals(getString(R.string.drawer_home))) {
                            selectItem(LazzyBeeShare.DRAWER_HOME_INDEX);
                        } else if (o.equals(getString(R.string.drawer_test_your_voca))) {
                            selectItem(LazzyBeeShare.DRAWER_TEST_YOUR_VOCA_INDEX);
                        }

                    })
            );
            mRecyclerViewDrawerList.setAdapter(recyclerViewDrawerListAdapter);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "onCreateView", e);
        }
        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param toolbar
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    @SuppressWarnings("ConstantConditions")
    public void setUp(int fragmentId, Toolbar toolbar, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
//        mDrawerToggle = new ActionBarDrawerToggle(
//                getActivity(),                    /* host Activity */
//                mDrawerLayout,                    /* DrawerLayout object */
//                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
//                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
//                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
//        )

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                if (recyclerViewDrawerListAdapter != null)
                    recyclerViewDrawerListAdapter.notifyDataSetChanged();
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(() -> mDrawerToggle.syncState());

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            mCallbacks = (NavigationDrawerCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.home) {
//            mDrawerToggle.onOptionsItemSelected(item);
//            return true;
//        }
        if (item != null && item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }

//        if (item.getItemId() == R.id.action_example) {
//            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @SuppressWarnings("ConstantConditions")
    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }


    static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private final OnItemClickListener mListener;

        interface OnItemClickListener {
            public void onItemClick(View view, int position);
        }

        final GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}

