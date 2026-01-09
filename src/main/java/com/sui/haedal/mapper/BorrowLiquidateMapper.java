package com.sui.haedal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sui.haedal.model.bo.BorrowLiquidateBo;
import com.sui.haedal.model.entity.BorrowLiquidate;
import com.sui.haedal.model.vo.BorrowLiquidateVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BorrowLiquidateMapper extends BaseMapper<BorrowLiquidate> {

    List<BorrowLiquidateVo> borrowLiquidateList(BorrowLiquidateBo bo);
}
