package com.onyx.miaosha.vo;

import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.domain.model.User;

public class GoodsDetail {

    private GoodsVo goods;
    private int miaoshaStatus ;
    private long remainSeconds;
    private MiaoshaUser user;

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "GoodsDetail{" +
                "goods=" + goods +
                ", miaoshaStatus=" + miaoshaStatus +
                ", remainSeconds=" + remainSeconds +
                ", user=" + user +
                '}';
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public long getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(long remainSeconds) {
        this.remainSeconds = remainSeconds;
    }



}
