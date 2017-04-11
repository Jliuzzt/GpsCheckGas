package com.cug.gpscheckgas.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.cug.gpscheckgas.R;
import com.cug.gpscheckgas.util.ContainerViewPager;
import com.cug.gpscheckgas.util.MyViewPager;
import com.cug.gpscheckgas.util.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class TaskFragment extends Fragment implements View.OnClickListener{

    public static final int TAB_UNDO = 0;
    public static final int TAB_DONE = 1;

    public ContainerViewPager containerViewPager;
    public RadioButton radioUndo;
    public RadioButton radioDone;

    UndoTaskFragment undoTaskFragment;
    DoneTaskFragment doneTaskFragment;
    View taskView = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        taskView = inflater.inflate(R.layout.fragment_task, container, false);
        containerViewPager = (ContainerViewPager) taskView.findViewById(R.id.viewpager_task);
        radioUndo = (RadioButton) taskView.findViewById(R.id.radioUndo);
        radioDone = (RadioButton) taskView.findViewById(R.id.radioDone);

        radioUndo.setOnClickListener(this);
        radioDone.setOnClickListener(this);

        initView();
        addPageChangeListener();
        return taskView;

    }


    private void initView() {

        List<Fragment> fragments = new ArrayList<Fragment>();

        undoTaskFragment = new UndoTaskFragment();
        doneTaskFragment = new DoneTaskFragment();
        fragments.add(undoTaskFragment);
        fragments.add(doneTaskFragment);
        this.containerViewPager.setOffscreenPageLimit(0);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getFragmentManager(), fragments, containerViewPager);


    }

    private void addPageChangeListener() {
        containerViewPager.setOnPageChangeListener(new MyViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int id) {
                switch (id) {
                    case TAB_UNDO:
                        radioUndo.setChecked(true);
                        break;
                    case TAB_DONE:
                        radioDone.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.radioUndo:
                containerViewPager.setCurrentItem(TAB_UNDO, false);
                break;
            case R.id.radioDone:
                containerViewPager.setCurrentItem(TAB_DONE, false);
                break;
        }
    }




}
