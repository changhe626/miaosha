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

    public void reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        goodsDao.reduceStock(g);
    }
}
