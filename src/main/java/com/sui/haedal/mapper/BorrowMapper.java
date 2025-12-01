package com.sui.haedal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sui.haedal.model.bo.BorrowTotalBo;
import com.sui.haedal.model.bo.YourTotalSupplyLineBo;
import com.sui.haedal.model.entity.Borrow;
import com.sui.haedal.model.vo.BorrowRateLineVo;
import com.sui.haedal.model.vo.UserTotalCollateralVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {

    List<UserTotalCollateralVo> userCollateralSupply(YourTotalSupplyLineBo bo);

    List<UserTotalCollateralVo> userCollateralWithdraw(YourTotalSupplyLineBo bo);

    List<BorrowRateLineVo> queryBorrowDetailLine(YourTotalSupplyLineBo mysqlConditionBo);

    List<BorrowRateLineVo> queryBorrowDetailRateLine(YourTotalSupplyLineBo mysqlConditionBo);
}
