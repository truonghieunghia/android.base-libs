package groupbase.vn.thn.baselibs.util;

/**
 * Created by nghiath on 4/3/15.
 */
public class Param {

    private String mParamName;
    private Object mParamValue;

    public Param () {

    }

    public Param ( String paramName, Object paramValue ) {

        setParamName( paramName );
        setParamValue( paramValue );
    }

    public String getParamName () {

        return mParamName;
    }

    public void setParamName ( String paramName ) {

        this.mParamName = paramName;
    }

    public Object getParamValue () {

        return mParamValue;
    }

    public void setParamValue ( Object paramValue ) {

        this.mParamValue = paramValue;
    }
}
