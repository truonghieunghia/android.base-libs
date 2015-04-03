package groupbase.vn.thn.baselibs.service.callback;

import java.util.ArrayList;

/**
 * Created by nghiath on 4/3/15.
 */
public interface RequestCallBack <T>{

    public void onResult ( T data );

    public void onResultArray ( ArrayList< T > data );
}
