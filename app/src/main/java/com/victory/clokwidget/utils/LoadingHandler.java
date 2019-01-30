package com.victory.clokwidget.utils;

import android.content.Context;

public class LoadingHandler {
    private LoadingDialog _loadingDialog;
    private Context _context;

    public LoadingHandler(Context context) {
        _context = context;
    }

    public void showLoading() {
        showLoading("Loading…");
    }

    public void showLoading(String message) {

        if (_loadingDialog != null)
            return;
        _loadingDialog = new LoadingDialog(_context, message);
        _loadingDialog.setMessage(message);
        _loadingDialog.show();
    }

    public void updateLoading(String message) {
        if (_loadingDialog != null)
            _loadingDialog.setMessage(message);
    }

    public void hideLoading() {
        if (_loadingDialog != null&&_loadingDialog.isShowing()) {
            _loadingDialog.cancel();
            _loadingDialog = null;
        }
    }
}
