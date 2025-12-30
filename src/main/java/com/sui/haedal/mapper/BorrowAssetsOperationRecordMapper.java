package com.sui.haedal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sui.haedal.model.bo.BorrowAssetsSupplyWithdrawQueryBo;
import com.sui.haedal.model.entity.BorrowAssetsOperationRecord;
import com.sui.haedal.model.vo.BorrowAssetsOperationRecordVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BorrowAssetsOperationRecordMapper extends BaseMapper<BorrowAssetsOperationRecord> {

    List<BorrowAssetsOperationRecordVo> borrowAssetsOperationRecordPageQuery(IPage<BorrowAssetsOperationRecordVo> page, BorrowAssetsSupplyWithdrawQueryBo queryBo);
}
