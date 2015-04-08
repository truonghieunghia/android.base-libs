package groupbase.vn.thn.baselibs.common;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by nghiath on 4/3/15.
 */
public class AdapterCommon < T > extends ArrayAdapter< T > {

    private int mXmlLayout;
    public AdapterCommon ( Context context, int xmlLayout, List< T > list ) {

        super( context, xmlLayout, list );
        mXmlLayout = xmlLayout;
    }
}
