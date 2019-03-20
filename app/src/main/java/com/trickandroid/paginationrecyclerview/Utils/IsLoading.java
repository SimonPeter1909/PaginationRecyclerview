package com.trickandroid.paginationrecyclerview.Utils;

public class IsLoading {
    private boolean boo = false;
    private OnLoadingListener listener;

    public boolean isLoading() {
        return boo;
    }

    public void setLoading(boolean boo) {
        this.boo = boo;
        if (listener != null) listener.onChange();
    }

    public OnLoadingListener getListener() {
        return listener;
    }

    public void setListener(OnLoadingListener listener) {
        this.listener = listener;
    }

    public interface OnLoadingListener {
        void onChange();
    }
}
