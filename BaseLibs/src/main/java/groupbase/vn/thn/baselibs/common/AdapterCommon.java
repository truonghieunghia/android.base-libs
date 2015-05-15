package groupbase.vn.thn.baselibs.common;

import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.List;
import groupbase.vn.thn.baselibs.listener.AdapterBaseListener;
import groupbase.vn.thn.baselibs.listener.AdapterDropDownBaseListener;


/**
 * Created by nghiath on 4/3/15.
 */
public class AdapterCommon<T> extends ArrayAdapter<T> {

    protected FragmentManager mFragmentManager;
    protected LayoutInflater mInflater;
    private AdapterBaseListener mAdapterBaseListener;
    private AdapterDropDownBaseListener mAdapterDropDownBaseListener;
    private int mLayoutDropDownId;
    private int mLayoutId;
    private List<T> lst;

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

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
        mLayoutDropDownId = resource;
    }

    public <T, H> void setAdapterBaseListener(AdapterBaseListener<T, H> adapterBaseListener) {
        this.mAdapterBaseListener = adapterBaseListener;

    }
    public <T, H> void setAdapterDropDownBaseListener(AdapterDropDownBaseListener<T, H> adapterBaseDropDownListener) {
        this.mAdapterDropDownBaseListener = adapterBaseDropDownListener;

    }
    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (mAdapterDropDownBaseListener !=null) {
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(this.mLayoutDropDownId, parent, false);
                view.setTag(this.mAdapterBaseListener.setHolderView(view));
                mAdapterDropDownBaseListener.showData(getItem(position), this.mAdapterDropDownBaseListener.setHolderView(view), position);
            } else {
                mAdapterDropDownBaseListener.showData(getItem(position), view.getTag(), position);
            }
            return view;
        }else {
            return super.getDropDownView(position,convertView,parent);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(this.mLayoutId, parent, false);
            view.setTag(this.mAdapterBaseListener.setHolderView(view));
            mAdapterBaseListener.showData(getItem(position), this.mAdapterBaseListener.setHolderView(view), position);
        } else {
            mAdapterBaseListener.showData(getItem(position), view.getTag(), position);
        }
        return view;
    }

}
