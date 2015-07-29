package com.born2go.lazzybee.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.fragment.FragmentStudy;
import com.born2go.lazzybee.shared.LazzyBeeShare;

public class StudyActivity extends ActionBarActivity implements FragmentStudy.FragmentStudyListener {

    FragmentStudy fragmentStudy;
    boolean learn_more;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        //int fragment study
        fragmentStudy = (FragmentStudy) getSupportFragmentManager().findFragmentById(R.id.fragmentStudy);

        //get lean_more form intern
        learn_more = getIntent().getBooleanExtra(LazzyBeeShare.LEARN_MORE, false);
//        Bundle bundle=new Bundle();
//        bundle.putBoolean(LazzyBeeShare.LEARN_MORE, learn_more);
//        fragmentStudy.setArguments(bundle);


    }

    public boolean isLearn_more() {
        return learn_more;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_study, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Hoan thanh khoa hoc rui quay tro lai DetailCourse
     */
    @Override
    public void completeCourse() {
        onBackPressed();
    }
}
