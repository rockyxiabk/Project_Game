package com.bzy.sdk;

public class PayInfo {

    /**
     * price : 6 单位（元）
     * serverName : 自由1服
     * serverIndex : 2001
     * roleName : 巫马波峻
     * roleID : 1
     * roleLv : 19
     * goodsName : 6元充值
     * goodsID : 3
     * cpOrder : 15452878526376612321
     * extendstr : pf_ziyou,1,2001,3,G15365844321,15452878526376612321
     */

    private String price;
    private String serverName;
    private String serverIndex;
    private String roleName;
    private String roleID;
    private String roleLv;
    private String goodsName;
    private int goodsID;
    private String cpOrder;
    private String extendstr;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIndex() {
        return serverIndex;
    }

    public void setServerIndex(String serverIndex) {
        this.serverIndex = serverIndex;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getRoleLv() {
        return roleLv;
    }

    public void setRoleLv(String roleLv) {
        this.roleLv = roleLv;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(int goodsID) {
        this.goodsID = goodsID;
    }

    public String getCpOrder() {
        return cpOrder;
    }

    public void setCpOrder(String cpOrder) {
        this.cpOrder = cpOrder;
    }

    public String getExtendstr() {
        return extendstr;
    }

    public void setExtendstr(String extendstr) {
        this.extendstr = extendstr;
    }
}
