package com.em_projects.callerapp.intro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.em_projects.callerapp.MainActivity;
import com.em_projects.callerapp.R;
import com.em_projects.callerapp.intro.viewpagerindicator.CirclePageIndicator;


/**
 * Created by USER on 25/04/2017.
 */

public class IntroActivity extends Activity {
    private static final String TAG = "IntroActivity";

    private ViewPager introViewPager;
    private IntroItemsAdapter introItemsAdapter;
    private CirclePageIndicator indicator;
    private TextView skipIntroButton;

    private int[] titles = new int[]{
            R.string.first_intro_title,
            R.string.second_intro_title,
            R.string.third_intro_title,
            R.string.fourth_intro_title};
    private int[] images = new int[]{
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round};
    private int[] messages = new int[]{
            R.string.first_intro_message,
            R.string.second_intro_message,
            R.string.third_intro_message,
            R.string.fourth_intro_message};

    private Context context;

    private View mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mLayout = findViewById(R.id.introViewPager);
        context = this;


        introViewPager = (ViewPager) findViewById(R.id.introViewPager);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        skipIntroButton = (TextView) findViewById(R.id.skipIntroButton);

        introItemsAdapter = new IntroItemsAdapter();
        introViewPager.setAdapter(introItemsAdapter);

        indicator.setViewPager(introViewPager);
        indicator.setSnap(true);

//        indicator.setBackgroundResource(R.drawable.intro_bg);

        skipIntroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class); //MainScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
            }
        });
    }


    private class IntroItemsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(View collection, int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.app_intro_item, null);

            TextView introTitleTextView = (TextView) view.findViewById(R.id.introTitleTextView);
            ImageView introImageImageView = (ImageView) view.findViewById(R.id.introImageImageView);
            TextView introMessageTextView = (TextView) view.findViewById(R.id.introMessageTextView);

            introTitleTextView.setText(titles[position]);
            introImageImageView.setImageResource(images[position]);
            introMessageTextView.setText(messages[position]);

            ((ViewPager) collection).addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }


}
