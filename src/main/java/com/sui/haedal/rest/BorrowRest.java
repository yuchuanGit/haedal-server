package com.sui.haedal.rest;

import com.sui.haedal.common.R;
import com.sui.haedal.model.bo.BorrowTotalBo;
import com.sui.haedal.model.vo.BorrowRateLineVo;
import com.sui.haedal.model.vo.BorrowVo;
import com.sui.haedal.model.vo.RateModelVo;
import com.sui.haedal.service.BorrowService;
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

    @PostMapping("/QueryBorrowDetailLine")
    public R<List<BorrowRateLineVo>> queryBorrowDetailLine(@RequestBody BorrowTotalBo bo) {
        return R.data(service.queryBorrowDetailLine(bo));
    }

    @PostMapping("/QueryBorrowDetailRateLine")
    public R<List<BorrowRateLineVo>> queryBorrowDetailRateLine(@RequestBody BorrowTotalBo bo) {
        return R.data(service.queryBorrowDetailRateLine(bo));
    }

    @PostMapping("/QueryBorrowDetailRateModel")
    public R<RateModelVo> queryBorrowDetailRateModel(@RequestBody BorrowTotalBo bo) {
        return R.data(service.queryBorrowDetailRateModel(bo));
    }
}
