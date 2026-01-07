package com.sui.haedal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sui.haedal.model.bo.TimePeriodStatisticsBo;
import com.sui.haedal.model.entity.Vault;
import com.sui.haedal.model.vo.StrategyVo;
import com.sui.haedal.model.vo.TimePeriodStatisticsVo;
import com.sui.haedal.model.vo.VaultVo;
import com.sui.haedal.model.vo.VaultYieldVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface EarnMapper extends BaseMapper<Vault> {


    /**
     * Vault池最小和最大时间收益
     * @param minTime
     * @param maxTime
     * @return
     */
    List<VaultYieldVo> vaultMinOrMaxTimeYield(String  minTime, String maxTime);
    /**
     * 获取Vault往前24小时最小时间和最大时间
     * @return
     */
    VaultYieldVo vaultYield24HourTimeRangeMixAndMaxTime();

    /**
     * 所有Vault收益
     * @return
     */
    List<VaultYieldVo> allVaultYield();

    /**
     * Vault apy查询
     * @param vaultId
     * @return
     */
    List<VaultVo> vaultApy(String vaultId);
    /**
     * vault池分配借款池比例资金信息
     * @param vaultId
     * @return
     */
    List<StrategyVo> vaultStrategy(String vaultId);

    List<StrategyVo> allVaultStrategy();

    /**
     * 所有Vault最新Curator
     * @return
     */
    List<VaultVo> allVaultNewCurator();

    /**
     * 所有Vault最新Allocator
     * @return
     */
    List<VaultVo> allVaultNewAllocator();

    /**
     * Vault和borrow关联详情
     * @param vaultId
     * @return
     */
    List<VaultVo> vaultDetail(String vaultId);


    /**
     * earn 统计vault时间段存入数量
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo>  vaultDeposit(TimePeriodStatisticsBo bo);

    /**
     *  earn 统计vault时间段tvl
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> vaultTvlTimePeriodStatistics(TimePeriodStatisticsBo bo);

    /**
     * earn 统计vault时间段存入 最小时间
     * @param bo
     * @return
     */
    Date vaultDepositMinTime(TimePeriodStatisticsBo bo);

    /**
     *  earn 统计vault时间段存入 最小时间
     * @param bo
     * @return
     */
    Date vaultTvlMinTime(TimePeriodStatisticsBo bo);

    /**
     * 获取存入最小时间找到对应vault创建时间
     * @param bo
     * @return
     */
    Long depositMinTimeVaultCreateTime(TimePeriodStatisticsBo bo);

    /**
     * earn 统计vault时间段小于TransactionTime存入数量
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo>  vaultDepositLTTransactionTime(TimePeriodStatisticsBo bo);

    /**
     * earn 统计vault时间段取出数量
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo>  vaultWithdraw(TimePeriodStatisticsBo bo);

    /**
     *  earn 统计vault时间段小于TransactionTime取出数量
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo>  vaultWithdrawLTTransactionTime(TimePeriodStatisticsBo bo);

    /**
     * earn 统计vault时间段APY
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> vaultAPYStatistics(TimePeriodStatisticsBo bo);

    /**
     * earn 统计vault时间段APY 最小时间
     * @param bo
     * @return
     */
    Date vaultAPYStatisticsMinTime(TimePeriodStatisticsBo bo);

    /**
     * earn 统计vault小于TransactionTime 份额价格
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> vaultSharePriceLTTransactionTime(TimePeriodStatisticsBo bo);

    /**
     * earn 统计vault小于TransactionTime 收益
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> vaultYieldEarnedLTTransactionTime(TimePeriodStatisticsBo bo);
}
