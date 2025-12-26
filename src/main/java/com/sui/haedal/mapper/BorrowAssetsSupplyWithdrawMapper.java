package com.sui.haedal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sui.haedal.model.bo.BorrowAssetsSupplyWithdrawQueryBo;
import com.sui.haedal.model.entity.BorrowAssetsSupplyWithdraw;
import com.sui.haedal.model.vo.BorrowAssetsSupplyWithdrawVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BorrowAssetsSupplyWithdrawMapper extends BaseMapper<BorrowAssetsSupplyWithdraw> {

    List<BorrowAssetsSupplyWithdrawVo> borrowAssetsSupplyWithdrawPageQuery(IPage<BorrowAssetsSupplyWithdrawVo> page, BorrowAssetsSupplyWithdrawQueryBo queryBo);
}
