package com.sui.haedal.mapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sui.haedal.model.bo.BorrowTotalBo;
import com.sui.haedal.model.bo.YourTotalSupplyLineBo;
import com.sui.haedal.model.entity.Borrow;
import com.sui.haedal.model.vo.BorrowRateLineVo;
import com.sui.haedal.model.vo.RateModelDetailVo;
import com.sui.haedal.model.vo.UserTotalCollateralVo;
import netscape.javascript.JSObject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {

    List<UserTotalCollateralVo> userCollateralSupply(YourTotalSupplyLineBo bo);

    List<UserTotalCollateralVo> userCollateralWithdraw(YourTotalSupplyLineBo bo);

    List<BorrowRateLineVo> queryBorrowDetailLine(YourTotalSupplyLineBo mysqlConditionBo);

    List<BorrowRateLineVo> queryBorrowDetailRateLine(YourTotalSupplyLineBo mysqlConditionBo);

    List<JSONObject> queryRateDateGroup(YourTotalSupplyLineBo mysqlConditionBo);

    List<JSONObject> dateGroupSupplyAssets(YourTotalSupplyLineBo mysqlConditionBo);

    List<JSONObject> dateGroupBorrowAssets(YourTotalSupplyLineBo mysqlConditionBo);

    /**
     * 池子当前利用率 = 总借款/总供应
     * @param mysqlConditionBo
     * @return
     */
    String marketCurrentU(YourTotalSupplyLineBo mysqlConditionBo);
}
