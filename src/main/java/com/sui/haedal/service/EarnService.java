package com.sui.haedal.service;

import com.sui.haedal.model.bo.EarnTotalBo;
import com.sui.haedal.model.vo.StrategyVo;
import com.sui.haedal.model.vo.TimePeriodStatisticsVo;
import com.sui.haedal.model.vo.VaultVo;

import java.util.List;

public interface EarnService {

    /**
     * earn vault列表
     * @return
     */
    List<VaultVo> list();

    /**
     * Vault详情
     * @param vaultId
     * @return
     */
    VaultVo vaultDetail(String vaultId);

    /**
     * vault池分配借款池比例资金信息
     * @param vaultId
     * @return
     */
    List<StrategyVo> vaultStrategy(String vaultId);

    /**
     * 用户时间段最新存入/取出数量
     * @return
     */
    List<TimePeriodStatisticsVo> yourDepositsWithdraw(EarnTotalBo bo);

    /**
     * 统计单个Vault池存入数据
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> totalDeposits(EarnTotalBo bo);


    /**
     * 统计单个Vault池APY
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo> totalAPY(EarnTotalBo bo);

}
