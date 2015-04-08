package groupbase.vn.thn.baselibs.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import groupbase.vn.thn.baselibs.R;

/**
 * Created by nghiath on 4/3/15.
 */
public abstract class ActivityCommon extends FragmentActivity {

    protected FragmentManager mFragmentManager;
    protected LayoutInflater mInflater;
    protected FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        mInflater = (LayoutInflater)getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mFragmentManager = getSupportFragmentManager();
        setContentView( R.layout.common_activity );
        this.init();
    }

    protected  void setLayout ( int layoutResID ){

        FrameLayout window = (FrameLayout)findViewById( R.id.window );
        window.removeAllViews();
        View view = mInflater.inflate( layoutResID,window,false );
        window.addView( view );
    }

    protected abstract void init();

    protected void setAnimations ( int enter, int exit, int popEnter, int popExit ) {

        this.mFragmentTransaction.setCustomAnimations( enter, exit, popEnter, popExit );
    }

    protected void setAnimations ( int enter, int exit ) {

        this.mFragmentTransaction.setCustomAnimations( enter, exit );
    }

    protected void beginReplaceFragment () {

        this.mFragmentTransaction = mFragmentManager.beginTransaction();
    }

    protected void commitReplaceFragment () {

        this.mFragmentTransaction.commit();
    }

    protected void replaceFragment ( Fragment fragment, int idReplace ) {

        this.mFragmentTransaction.replace( idReplace, fragment );
        this.mFragmentTransaction.addToBackStack( fragment.getClass().getName() );
    }

    protected void replaceFragment ( ArrayList< Fragment > fragments, int[] idReplace ) {

        int index = 0;
        for ( Fragment fragment : fragments ) {

            this.mFragmentTransaction.replace( idReplace[ index ], fragment );
            this.mFragmentTransaction.addToBackStack( fragment.getClass().getName() );
            index = index++;
        }
    }

    protected void replaceFragmentNoBackStack ( Fragment fragment, int idReplace ) {

        this.mFragmentTransaction.replace( idReplace, fragment );
    }

    protected void replaceFragmentNoBackStack ( ArrayList< Fragment > fragments, int[] idReplace ) {

        int index = 0;
        for ( Fragment fragment : fragments ) {

            this.mFragmentTransaction.replace( idReplace[ index ], fragment );
            index = index++;
        }
    }
    @Override
    public void onBackPressed () {

        final int backCount = mFragmentManager.getBackStackEntryCount();
        if ( backCount == 0 ){
            finish();
        }else {
            super.onBackPressed();
        }
    }
}
