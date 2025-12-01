package com.sui.haedal.service;

import com.sui.haedal.model.bo.BorrowTotalBo;
import com.sui.haedal.model.entity.Borrow;
import com.sui.haedal.model.vo.BorrowRateLineVo;
import com.sui.haedal.model.vo.BorrowVo;

import java.util.List;

public interface BorrowService {

    List<BorrowVo> queryList();

    List<BorrowRateLineVo> yourTotalSupplyLine(BorrowTotalBo conditionBo);


    BorrowVo queryBorrowDetail(BorrowTotalBo conditionBo);

    List<BorrowRateLineVo> queryBorrowDetailLine(BorrowTotalBo conditionBo);

    List<BorrowRateLineVo> queryBorrowDetailRateLine(BorrowTotalBo conditionBo);


}
