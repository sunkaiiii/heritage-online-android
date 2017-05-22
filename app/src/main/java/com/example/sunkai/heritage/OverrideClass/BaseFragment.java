package com.example.sunkai.heritage.OverrideClass;

/**
 * Created by sunkai on 2017-4-28.
 */

public abstract class BaseFragment extends android.support.v4.app.Fragment {
    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            isVisible=true;
            onVisible();
        }
        else{
            isVisible=false;
            onInvisible();
        }
    }

    protected void onVisible(){
        lazyLoad();
    }

    protected void onInvisible(){

    }

    protected abstract void lazyLoad();
}
