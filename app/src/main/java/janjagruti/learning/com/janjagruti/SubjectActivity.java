package janjagruti.learning.com.janjagruti;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import janjagruti.learning.com.janjagruti.config.AppController;

public class SubjectActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT_ID = "subjectId";
    public static final String EXTRA_SUBJECT_TITLE = "subjectName";

    private Toolbar toolbar;
    private TabLayout tablayout;
    private ViewPager vp_subject;

    private FragmentPagerAdapter subjectPagerAdapter;
    private int subjectId;
    private String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        Intent comingIntent = getIntent();
        if (comingIntent != null){
            subjectId = comingIntent.getIntExtra(EXTRA_SUBJECT_ID, 0);
            subjectName = comingIntent.getStringExtra(EXTRA_SUBJECT_TITLE);
        }

        if (subjectId == 0) {
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(TextUtils.isEmpty(subjectName) ? getString(R.string.subject) : subjectName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subjectPagerAdapter = new SubjectPagerAdapter(getSupportFragmentManager(), subjectId);
        vp_subject = findViewById(R.id.vp_subject);
        vp_subject.setAdapter(subjectPagerAdapter);

        tablayout = findViewById(R.id.tablayout);
        tablayout.setupWithViewPager(vp_subject);
    }

    public class SubjectPagerAdapter extends FragmentPagerAdapter {

        private SubjectDetailsFragment subjectDetailsFragment;
        private MaterialListFragment materialListFragment;

        private int subjectId;

        public SubjectPagerAdapter(FragmentManager fragmentManager, int subjectId){
            super(fragmentManager);
            this.subjectId = subjectId;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {

            Bundle argumentsBundle = new Bundle();

            switch (position){
                case 0:
                    if (materialListFragment == null) {
                        materialListFragment = new MaterialListFragment();
                        argumentsBundle.putInt(SubjectDetailsFragment.EXTRA_SUBJECT_ID, this.subjectId);
                        materialListFragment.setArguments(argumentsBundle);
                    }
                    return materialListFragment;

                case 1:
                    if (subjectDetailsFragment == null) {
                        subjectDetailsFragment = new SubjectDetailsFragment();
                        argumentsBundle.putInt(SubjectDetailsFragment.EXTRA_SUBJECT_ID, this.subjectId);
                        subjectDetailsFragment.setArguments(argumentsBundle);
                    }
                    return subjectDetailsFragment;
            }

            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0:
                    return getResources().getString(R.string.material);

                case 1:
                    return getResources().getString(R.string.details);
            }
            return super.getPageTitle(position);
        }
    }

}
