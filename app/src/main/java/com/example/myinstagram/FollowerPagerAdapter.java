package com.example.myinstagram;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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
                    FollowerFragment followerFragment2 = new FollowerFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("mode", FOLLOING_MODE);
                    followerFragment2.setArguments(bundle2);
                    return followerFragment2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mPageCount;
        }
    }
