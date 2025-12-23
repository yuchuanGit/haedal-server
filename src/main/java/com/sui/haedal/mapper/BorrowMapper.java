package com.sui.haedal.mapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sui.haedal.model.bo.TimePeriodStatisticsBo;
import com.sui.haedal.model.bo.YourTotalSupplyLineBo;
import com.sui.haedal.model.entity.Borrow;
import com.sui.haedal.model.vo.BorrowRateLineVo;
import com.sui.haedal.model.vo.TimePeriodStatisticsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {

    /**
     * 查询marketId对应Vault地址
     * @param marketId
     * @return
     */
    List<String> queryVaultAddress(String marketId);

    /**
     * borrow借还净值记录时间段统计
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowAssetsRecordTimePeriodStatistics(TimePeriodStatisticsBo bo);

    /**
     * borrow借时间段统计
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowTimePeriodStatistics(TimePeriodStatisticsBo bo);

    /**
     *  borrow借时间段统计 最小时间
     * @param bo
     * @return
     */
    Date borrowTimePeriodMinTime(TimePeriodStatisticsBo bo);

    /**
     *  borrow借记录时间段统计 最小时间
     * @param bo
     * @return
     */
    Date borrowAssetsRecordMinTime(TimePeriodStatisticsBo bo);

    /**
     * borrow借时间段统计 小于TransactionTime
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowTimePeriodStatisticsLTTransactionTime(TimePeriodStatisticsBo bo);

    /**
     * borrow还时间段统计
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowRepayTimePeriodStatistics(TimePeriodStatisticsBo bo);

    /**
     * borrow还时间段统计 小于TransactionTime
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowRepayTimePeriodStatisticsLTTransactionTime(TimePeriodStatisticsBo bo);


    /**
     * borrow存资产/存抵押 market创建时间
     * @return
     */
    Long borrowSupplyOrCollateralMinTimeMarketCreateTime(Integer supplyType,String userAddress);

    /**
     * borrow时间段统计 存资产/存抵押
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowTimePeriodSupplyAssetsRecord(TimePeriodStatisticsBo bo);

    /**
     * borrow时间段统计 存资产/存抵押
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowTimePeriodStatisticsSupplyOrCollateral(TimePeriodStatisticsBo bo);

    /**
     * borrow时间段统计 存资产/存抵押 最小时间
     * @param bo
     * @return
     */
    Date borrowTimePeriodSupplyOrCollateralMinTime(TimePeriodStatisticsBo bo);

    /**
     * borrow时间段统计 存资产记录最小时间
     * @param bo
     * @return
     */
    Date borrowTimePeriodSupplyAssetsRecordMinTime(TimePeriodStatisticsBo bo);

    /**
     *  borrow时间段统计小于TransactionTime 存资产/存抵押
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowTimePeriodStatisticsSupplyOrCollateralLTTransactionTime(TimePeriodStatisticsBo bo);


    /**
     *  borrow时间段统计 取抵押
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowWithdraw(TimePeriodStatisticsBo bo);

    /**
     *  borrow时间段统计小于TransactionTime 取抵押
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowWithdrawLTTransactionTime(TimePeriodStatisticsBo bo);

    /**
     *  borrow时间段统计 取抵押
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowCollateralWithdraw(TimePeriodStatisticsBo bo);

    /**
     *  borrow时间段统计小于TransactionTime 取抵押
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> borrowCollateralWithdrawLTTransactionTime(TimePeriodStatisticsBo bo);

    List<BorrowRateLineVo> queryBorrowDetailLine(YourTotalSupplyLineBo mysqlConditionBo);

    /**
     * borrow 详情借利率时间段统计每天最新利率
     * @param statisticsBo
     * @return
     */
    List<TimePeriodStatisticsVo> queryBorrowDetailRateLine(TimePeriodStatisticsBo statisticsBo);

    /**
     * borrow 详情借利率时间段统计 最小时间
     * @param statisticsBo
     * @return
     */
    Date queryBorrowDetailRateLineMinTime(TimePeriodStatisticsBo statisticsBo);


    /**
     * 池子当前利用率 = 总借款/总供应
     * @param mysqlConditionBo
     * @return
     */
    String marketCurrentU(YourTotalSupplyLineBo mysqlConditionBo);


    /**
     * 利率模型
     * @param mysqlConditionBo
     * @return
     */
    List<JSONObject> queryRateDateGroup(YourTotalSupplyLineBo mysqlConditionBo);

    List<JSONObject> dateGroupSupplyAssets(YourTotalSupplyLineBo mysqlConditionBo);

    List<JSONObject> dateGroupBorrowAssets(YourTotalSupplyLineBo mysqlConditionBo);

}
