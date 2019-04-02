package com.example.myinstagram.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.myinstagram.FollowerFragment;
import com.example.myinstagram.FollowingFragment;

public class FollowerPagerAdapter extends FragmentStatePagerAdapter {

     final int FOLLOWER_MODE=1;
     final int FOLLOING_MODE=2;

        private int mPageCount;

        public FollowerPagerAdapter(FragmentManager fm, int pageCount) {
            super(fm);
            this.mPageCount = pageCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FollowerFragment followerFragment = new FollowerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("mode", FOLLOWER_MODE);
                    followerFragment.setArguments(bundle);
                    return followerFragment;
                case 1:
                    FollowingFragment followingFragment = new FollowingFragment();
                    return followingFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mPageCount;
        }
    }
