package com.onyx.miaosha.service;

import com.onyx.miaosha.dao.GoodsDao;
import com.onyx.miaosha.domain.Goods;
import com.onyx.miaosha.domain.MiaoshaGoods;
import com.onyx.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {


    @Autowired
    private GoodsDao goodsDao;


    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }


    public GoodsVo getById(long id) {
        return goodsDao.getGoodsVoByGoodsId(id);
    }

    //添加判断是否下单成功了...
    public boolean reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        int stock = goodsDao.reduceStock(g);//1个的时候
        return stock>0;
    }

    public void resetStock(List<GoodsVo> goodsList) {
        for(GoodsVo goods : goodsList ) {
            MiaoshaGoods g = new MiaoshaGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDao.resetStock(g);
        }

    }
}
