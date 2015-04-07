package groupbase.vn.thn.baselibs.service.callback;

/**
 * Created by nghiath on 4/6/15.
 */
public interface ProgressRequestCallBack {

    public void onStart(int contentLength);
    public void onUpdate(int downloadSize);
    public void onComplete(int totalSize);
}
