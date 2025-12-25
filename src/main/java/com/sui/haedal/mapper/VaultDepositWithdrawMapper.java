package com.sui.haedal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sui.haedal.model.bo.VaultDepositWithdrawQueryBo;
import com.sui.haedal.model.entity.VaultDepositWithdraw;
import com.sui.haedal.model.vo.VaultDepositWithdrawVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VaultDepositWithdrawMapper extends BaseMapper<VaultDepositWithdraw> {

    List<VaultDepositWithdrawVo> vaultDepositWithdrawPageQuery(IPage<VaultDepositWithdrawVo> page, VaultDepositWithdrawQueryBo queryBo);
}
