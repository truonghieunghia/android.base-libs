package groupbase.vn.thn.baselibs.common;

<<<<<<< HEAD
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

import groupbase.vn.thn.baselibs.listener.AdapterBaseListener;
=======
import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;
>>>>>>> 026cf072780b9b484a886813429329da9b93fc58

/**
 * Created by nghiath on 4/3/15.
 */
<<<<<<< HEAD
public class AdapterCommon<T> extends ArrayAdapter<T> {

    protected FragmentManager mFragmentManager;
    protected LayoutInflater mInflater;
    private AdapterBaseListener mAdapterBaseListener;
    private int mLayoutId;
    private Objects mHolderView;
    private List<T>lst;
    public AdapterCommon(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        mLayoutId = resource;
        mInflater = LayoutInflater.from(context);
        lst = objects;
    }

    public AdapterCommon(Context context, int resource, List<T> objects, FragmentManager fm) {
        super(context, resource, objects);
        mFragmentManager = fm;
        mLayoutId = resource;
        mInflater = LayoutInflater.from(context);
        lst = objects;
    }

    public <T,H> void setAdapterBaseListener(AdapterBaseListener<T,H> adapterBaseListener) {
        this.mAdapterBaseListener = adapterBaseListener;

    }

    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setTagHolder(Objects holder){
        this.mHolderView = holder;
    }
    @Override
    public  View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(this.mLayoutId, parent, false);
            mHolderView = (Objects)this.mAdapterBaseListener.setHolderView(view);
            view.setTag(mHolderView);
        } else {
            mHolderView = (Objects) view.getTag();
        }
        mAdapterBaseListener.showData(getItem(position),mHolderView,position);
        return view;
=======
public class AdapterCommon < T > extends ArrayAdapter< T > {

    private int mXmlLayout;
    public AdapterCommon ( Context context, int xmlLayout, List< T > list ) {

        super( context, xmlLayout, list );
        mXmlLayout = xmlLayout;
>>>>>>> 026cf072780b9b484a886813429329da9b93fc58
    }
}
