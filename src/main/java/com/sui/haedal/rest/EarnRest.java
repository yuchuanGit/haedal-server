package com.sui.haedal.rest;

import com.sui.haedal.common.R;
import com.sui.haedal.model.bo.EarnTotalBo;
import com.sui.haedal.model.vo.BorrowVo;
import com.sui.haedal.model.vo.StrategyVo;
import com.sui.haedal.model.vo.TimePeriodStatisticsVo;
import com.sui.haedal.model.vo.VaultVo;
import com.sui.haedal.service.BorrowService;
import com.sui.haedal.service.EarnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/earn")
@Tag(name = "vault管理接口", description = "Vault 增删改查接口")
public class EarnRest {


    @Autowired
    private EarnService service;

    @GetMapping("/list")
    @Operation(summary = "查询earn列表", description = "根据ID查询金库完整信息")
    public R<List<VaultVo>> listQuery() {
        return R.data(service.list());
    }

    @GetMapping("/vaultDetail")
    @Operation(summary = "vault详情", description = "vault详情")
    public R<VaultVo> vaultDetail(@RequestParam String vaultId) {
        return R.data(service.vaultDetail(vaultId));
    }

    @GetMapping("/vaultStrategy")
    @Operation(summary = "vault详情Strategy", description = "vault池分配借款池比例资金信息")
    public R<List<StrategyVo>> vaultStrategy(@RequestParam String vaultId) {
        return R.data(service.vaultStrategy(vaultId));
    }


    @PostMapping("/yourDepositsWithdraw")
    @Operation(summary = "用户时间段最新存入/取出数量", description = "用户时间段最新存入/取出数量")
    public R<List<TimePeriodStatisticsVo>> yourDepositsWithdraw(@RequestBody EarnTotalBo bo) {
        return R.data(service.yourDepositsWithdraw(bo));
    }


    @PostMapping("/totalDeposits")
    @Operation(summary = "统计单个Vault池存入数据", description = "统计单个Vault池存入数据")
    public R<List<TimePeriodStatisticsVo>> totalDeposits(@RequestBody EarnTotalBo bo) {
        return R.data(service.totalDeposits(bo));
    }


    @PostMapping("/totalAPY")
    @Operation(summary = "统计单个Vault池APY", description = "统计单个Vault池APY")
    public R<List<TimePeriodStatisticsVo>> totalAPY(@RequestBody EarnTotalBo bo) {
        return R.data(service.totalAPY(bo));
    }




}
