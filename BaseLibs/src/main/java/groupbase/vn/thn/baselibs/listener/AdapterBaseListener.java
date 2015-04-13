package groupbase.vn.thn.baselibs.listener;

import android.view.View;

/**
 * Created by TruongHieuNghia on 4/13/15.
 */
public interface AdapterBaseListener<T,H> {
    public H setHolderView(View view);
    public void showData(T data,H holderView,int position);
}
