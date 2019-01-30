package com.victory.clokwidget.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.victory.clokwidget.R;

public class LoadingDialog extends Dialog {
    private Context mContext;
    private LayoutInflater inflater;
    private WindowManager.LayoutParams lp;
    private TextView loadingText;

    public LoadingDialog(Context context, String content) {
        super(context, R.style.Dialog);

        this.mContext = context;
        // 设置点击对话框之外能消失
        setCanceledOnTouchOutside(true);
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.loading, null);
        loadingText = (TextView) layout.findViewById(R.id.progress_msg);

        if (null != content) {
            loadingText.setText(content);
            loadingText.setVisibility(View.VISIBLE);
        }else{
            loadingText.setVisibility(View.INVISIBLE);

        }
        setContentView(layout);

        // 设置window属性
        lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.dimAmount = 0; // 去背景遮盖
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }


    public void setMessage(String message){
        this.loadingText.setText(message);
    }

}
