package com.sui.haedal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sui.haedal.model.bo.BorrowAssetsSupplyWithdrawQueryBo;
import com.sui.haedal.model.bo.EarnTotalBo;
import com.sui.haedal.model.bo.HTokenBo;
import com.sui.haedal.model.bo.VaultDepositWithdrawQueryBo;
import com.sui.haedal.model.vo.*;

import java.util.List;

public interface EarnService {


    /**
     * borrow 资产存取分页查询
     * @param queryBo
     * @return
     */
    IPage<BorrowAssetsOperationRecordVo> borrowAssetsOperationRecordPageQuery(BorrowAssetsSupplyWithdrawQueryBo queryBo);

    /**
     * Vault存取分页查询
     * @param queryBo
     * @return
     */
    IPage<VaultDepositWithdrawVo> vaultDepositWithdrawPageQuery(VaultDepositWithdrawQueryBo queryBo);

    /**
     * 获取用户Vault权限
     * @param userAddress
     * @return
     */
    UserVaultPermissionVo userVaultPermission(String userAddress);

    /**
     * earn vault列表
     * @return
     */
    List<VaultVo> list(String userAddress,Integer roleType);

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


    /**
     * vault统计获得收益
     * @param bo
     * @return
     */
    TimePeriodStatisticsGrowthVo yieldEarned(EarnTotalBo bo);

    /**
     * vault统计份额价格
     * @param bo
     * @return
     */
    TimePeriodStatisticsGrowthVo vaultSharePrice(EarnTotalBo bo);

    HTokenVo geHTokenInfo(HTokenBo tokenBo);

}
