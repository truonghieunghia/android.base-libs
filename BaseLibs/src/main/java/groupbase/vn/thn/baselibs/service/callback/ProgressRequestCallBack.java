package groupbase.vn.thn.baselibs.service.callback;

/**
 * Created by nghiath on 4/6/15.
 */
public interface ProgressRequestCallBack {

    public void onStart();
    public void doInBackground();
    public void onComplete();
}
