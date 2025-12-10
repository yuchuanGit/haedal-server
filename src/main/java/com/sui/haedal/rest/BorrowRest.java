package com.sui.haedal.rest;

import com.sui.haedal.common.R;
import com.sui.haedal.model.bo.BorrowTotalBo;
import com.sui.haedal.model.vo.BorrowRateLineVo;
import com.sui.haedal.model.vo.BorrowVo;
import com.sui.haedal.model.vo.RateModelVo;
import com.sui.haedal.model.vo.TimePeriodStatisticsVo;
import com.sui.haedal.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/borrow")
public class BorrowRest {

    @Autowired
    private BorrowService service;

    @GetMapping("/GetBorrowList")
    public R<List<BorrowVo>> listQuery() {
        return R.data(service.queryList());
    }

    @PostMapping("/YourTotalSupplyLine")
    public R<List<BorrowRateLineVo>> yourTotalSupplyLine(@RequestBody BorrowTotalBo bo) {
        return R.data(service.yourTotalSupplyLine(bo));
    }

    @PostMapping("/QueryBorrowDetail")
    public R<BorrowVo> queryBorrowDetail(@RequestBody BorrowTotalBo bo) {
        return R.data(service.queryBorrowDetail(bo));
    }


    @PostMapping("/borrowDetailSupplyStatistics")
    @Operation(summary = "资产存入统计", description = "资产存入统计")
    public R<List<TimePeriodStatisticsVo>> borrowDetailSupplyStatistics(@RequestBody BorrowTotalBo bo) {
        return R.data(service.borrowDetailSupplyStatistics(bo));
    }

    @PostMapping("/borrowDetailCollateralStatistics")
    @Operation(summary = "资产存入抵押品统计", description = "资产存入抵押品统计")
    public R<List<TimePeriodStatisticsVo>> borrowDetailCollateralStatistics(@RequestBody BorrowTotalBo bo) {
        return R.data(service.borrowDetailCollateralStatistics(bo));
    }

    @PostMapping("/borrowDetailStatistics")
    @Operation(summary = "借明细统计", description = "借明细统计")
    public R<List<TimePeriodStatisticsVo>> borrowDetailStatistics(@RequestBody BorrowTotalBo bo) {
        return R.data(service.borrowDetailStatistics(bo));
    }

    @PostMapping("/borrowDetailRateStatistics")
    @Operation(summary = "borrow详情 借利率统计", description = "borrow详情 借利率统计")
    public R<List<TimePeriodStatisticsVo>> borrowDetailRateStatistics(@RequestBody BorrowTotalBo bo) {
        return R.data(service.borrowDetailRateStatistics(bo));
    }

    @PostMapping("/borrowDetailCollateralAPRStatistics")
    @Operation(summary = "borrow详情 抵押品利率", description = "borrow详情 抵押品利率")
    public R<List<TimePeriodStatisticsVo>> borrowDetailCollateralAPRStatistics(@RequestBody BorrowTotalBo bo) {
        return R.data(service.borrowDetailCollateralAPRStatistics(bo));
    }


    @PostMapping("/QueryBorrowDetailLine")
    public R<List<BorrowRateLineVo>> queryBorrowDetailLine(@RequestBody BorrowTotalBo bo) {
        return R.data(service.queryBorrowDetailLine(bo));
    }


    @PostMapping("/QueryBorrowDetailRateModel")
    public R<RateModelVo> queryBorrowDetailRateModel(@RequestBody BorrowTotalBo bo) {
        return R.data(service.queryBorrowDetailRateModel(bo));
    }
}
