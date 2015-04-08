package groupbase.vn.thn.baselibs.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import groupbase.vn.thn.baselibs.R;

/**
 * Created by nghiath on 4/3/15.
 */
public abstract class FragmentCommon extends Fragment {

    protected FragmentManager mFragmentManager;
    protected LayoutInflater mInflater;
    protected FragmentTransaction mFragmentTransaction;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        mInflater = ( LayoutInflater ) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        mFragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {

        View view = inflater.inflate( R.layout.common_fragment, container, false );
        return view;
    }

    @Override
    public void onActivityCreated ( @Nullable Bundle savedInstanceState ) {

        super.onActivityCreated( savedInstanceState );
        this.init();
    }

    protected void setLayout ( int layoutResID ) {

        FrameLayout window = ( FrameLayout ) getView().findViewById( R.id.frag_parent );
        window.removeAllViews();
        View view = mInflater.inflate( layoutResID, window, false );
        window.addView( view );
    }

    protected abstract void init ();

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
}
