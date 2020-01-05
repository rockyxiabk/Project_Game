package com.bzy.game.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.bzy.game.R;

/**
 * Description : com.bzy.game.view
 *
 * @author : rocky
 * @Create Time : 2018/12/11 9:46 AM
 * @Modified Time : 2018/12/11 9:46 AM
 */
public class ExitGameDialog extends BaseDialog {

    private final ExitGameListener listener;
    private Button btnCancel;
    private Button btnConfirm;

    public ExitGameDialog(Context context, boolean cancelable, boolean cancelableOutside, ExitGameListener listener) {
        super(context, cancelable, cancelableOutside);
        this.listener = listener;
    }

    @Override
    public int getLayout() {
        return R.layout.dialog_exit;
    }

    @Override
    protected void initView() {
        btnCancel = ((Button) findViewById(R.id.btn_exit_cancel));
        btnConfirm = ((Button) findViewById(R.id.btn_confirm_exit));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.confirmExitGame();
            }
        });
    }

    public interface ExitGameListener {
        void confirmExitGame();
    }
}
