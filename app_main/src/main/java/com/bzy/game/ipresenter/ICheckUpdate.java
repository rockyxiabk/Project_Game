package com.bzy.game.ipresenter;

/**
 * Description : com.bzy.game.ipresenter
 *
 * @author : rocky
 * @Create Time : 2018/12/17 2:55 PM
 * @Modified Time : 2018/12/17 2:55 PM
 */
public interface ICheckUpdate {
    void setUpdateInfo(int size, int dataSize);

    void updateProgress(int dataLen);
}
