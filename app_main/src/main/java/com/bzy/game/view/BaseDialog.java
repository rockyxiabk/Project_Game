package com.bzy.game.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.bzy.game.R;

/**
 * Description :  自定义弹窗样式
 *
 * @author : rocky
 * @Create Time : 2018/12/11 9:45 AM
 * @Modified By: rocky
 * @Modified Time : 2018/12/11 9:45 AM
 */
public abstract class BaseDialog extends Dialog {

    protected final Context context;

    public BaseDialog(Context context, boolean cancelable, boolean cancelableOutside) {
        super(context, R.style.CustomBaseDialog);
        this.context = context;
        getWindow().setWindowAnimations(R.style.iosalertdialog);
        setCancelable(cancelable);
        setCanceledOnTouchOutside(cancelableOutside);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initView();
    }

    public abstract int getLayout();

    protected abstract void initView();
}
