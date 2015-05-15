package groupbase.vn.thn.baselibs.listener;

import android.view.View;

/**
 * Created by nghiath on 5/15/15.
 */
public interface AdapterDropDownBaseListener<T,H> {
    public H setHolderView(View view);
    public void showData(T data,H holderView,int position);
}
