package com.bzy.game.presenter;

import android.content.Context;

import com.bzy.game.Constants;
import com.bzy.game.ipresenter.ICheckUpdate;
import com.bzy.game.service.CopyIntentService;
import com.bzy.game.service.DownloadFileService;
import com.bzy.game.util.LogUtil;
import com.bzy.game.version.VersionManager;

import java.util.Map;

/**
 * Description : com.bzy.game.presenter
 *
 * @author : rocky
 * @Create Time : 2018/12/17 2:51 PM
 * @Modified Time : 2018/12/17 2:51 PM
 */
public class CheckUpdatePresenter {

    private final Context context;
    private final ICheckUpdate iCheckUpdate;

    public CheckUpdatePresenter(Context context, ICheckUpdate iCheckUpdate) {
        this.context = context;
        this.iCheckUpdate = iCheckUpdate;
    }

    public void initUpdate() {
        Map<String, Integer> updateMap = VersionManager.get().getUpdateMap();
        if (null != updateMap && updateMap.size() > 0) {
            int dataSize = 0;
            for (Map.Entry<String, Integer> entry : updateMap.entrySet()) {
//                LogUtil.d("path:" + entry.getKey() + "  size:" + entry.getValue());
                dataSize += entry.getValue();
            }
            iCheckUpdate.setUpdateInfo(updateMap.size(), dataSize);
            CopyIntentService.startActionStart(context);
        } else {
            iCheckUpdate.setUpdateInfo(0, 0);
        }
    }
}
