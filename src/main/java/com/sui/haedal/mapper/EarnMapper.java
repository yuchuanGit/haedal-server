package com.sui.haedal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sui.haedal.model.bo.TimePeriodStatisticsBo;
import com.sui.haedal.model.entity.Vault;
import com.sui.haedal.model.vo.StrategyVo;
import com.sui.haedal.model.vo.TimePeriodStatisticsVo;
import com.sui.haedal.model.vo.VaultVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EarnMapper extends BaseMapper<Vault> {

    /**
     * vault池分配借款池比例资金信息
     * @param vaultId
     * @return
     */
    List<StrategyVo> vaultStrategy(String vaultId);

    /**
     * Vault和borrow关联详情
     * @param vaultId
     * @return
     */
    List<VaultVo> vaultDetail(String vaultId);


    /**
     * earn 统计用户时间段存入数量
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo>  userDeposit(TimePeriodStatisticsBo bo);

    /**
     * earn 统计用户时间段取出数量
     * @param bo
     * @return
     */
    List<TimePeriodStatisticsVo>  userWithdraw(TimePeriodStatisticsBo bo);
}
