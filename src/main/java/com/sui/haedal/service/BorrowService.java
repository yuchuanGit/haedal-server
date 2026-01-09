package com.sui.haedal.service;

import com.sui.haedal.model.bo.BorrowTotalBo;
import com.sui.haedal.model.vo.*;

import java.util.List;

public interface BorrowService {

    /**
     * 查询用户清算激励
     * @param userAddress
     * @return
     */
    List<BorrowLiquidateVo> borrowLiquidateList(String userAddress);

    List<BorrowVo> queryList();

    List<BorrowRateLineVo> yourTotalSupplyLine(BorrowTotalBo conditionBo);


    /**
     * borrow 详情
     * @param conditionBo
     * @return
     */
    BorrowVo queryBorrowDetail(BorrowTotalBo conditionBo);

    /**
     * borrow 资产存入统计
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowDetailSupplyStatistics(BorrowTotalBo bo);

    /**
     * borrow 资产存入抵押品统计
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowDetailCollateralStatistics(BorrowTotalBo bo);

    /**
     * borrow 借明细统计
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowDetailStatistics(BorrowTotalBo bo);


    /**
     * borrow 详情借利率统计
     * @param conditionBo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowDetailRateStatistics(BorrowTotalBo conditionBo);

    /**
     * borrow 详情抵押品利率
     * @param conditionBo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowDetailCollateralAPRStatistics(BorrowTotalBo conditionBo);




    List<BorrowRateLineVo> queryBorrowDetailLine(BorrowTotalBo conditionBo);



    RateModelVo queryBorrowDetailRateModel(BorrowTotalBo conditionBo);


}
