package com.sui.haedal.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sui.haedal.common.R;
import com.sui.haedal.model.bo.BorrowAssetsSupplyWithdrawQueryBo;
import com.sui.haedal.model.bo.EarnTotalBo;
import com.sui.haedal.model.bo.HTokenBo;
import com.sui.haedal.model.bo.VaultDepositWithdrawQueryBo;
import com.sui.haedal.model.vo.*;
import com.sui.haedal.service.EarnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/earn")
@Tag(name = "vault管理接口", description = "Vault 增删改查接口")
public class EarnRest {


    @Autowired
    private EarnService service;


    @GetMapping("/getAllVaultTotal")
    @Operation(summary = "查询所有Vault统计", description = "查询所有Vault统计")
    public R<VaultTotalVo> getAllVaultTotal() {
        return R.data(service.getAllVaultTotal());
    }

    @GetMapping("/coinConfigList")
    @Operation(summary = "查询coin列表", description = "查询coin列表")
    public R<List<CoinConfigVo>> coinConfigList(@RequestParam(required = false) String coinType) {
        return R.data(service.coinConfigList(coinType));
    }

    @PostMapping("/borrowAssetsOperationRecordPageQuery")
    @Operation(summary = "borrow资产操作记录分页查询", description = "borrow资产操作记录分页查询")
    public R<IPage<BorrowAssetsOperationRecordVo>> borrowAssetsOperationRecordPageQuery(@RequestBody @Valid BorrowAssetsSupplyWithdrawQueryBo bo) {
        return R.data(service.borrowAssetsOperationRecordPageQuery(bo));
    }


    @PostMapping("/vaultDepositWithdrawPageQuery")
    @Operation(summary = "Vault存取分页查询", description = "Vault存取分页查询")
    public R<IPage<VaultDepositWithdrawVo>> vaultDepositWithdrawPageQuery(@RequestBody  @Valid VaultDepositWithdrawQueryBo bo) {
        return R.data(service.vaultDepositWithdrawPageQuery(bo));
    }


    @GetMapping("/userVaultPermission")
    @Operation(summary = "获取用户Vault权限", description = "获取用户Vault权限")
    public R<UserVaultPermissionVo> userVaultPermission(@RequestParam String userAddress) {
        return R.data(service.userVaultPermission(userAddress));
    }

    @GetMapping("/list")
    @Operation(summary = "查询earn列表", description = "根据ID查询金库完整信息")
    public R<List<VaultVo>> listQuery(@RequestParam(required = false) String userAddress,
                                      @RequestParam(required = false) Integer roleType) {
        return R.data(service.list(userAddress,roleType));
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

    @PostMapping("/yieldEarned")
    @Operation(summary = "vault统计获得收益", description = "vault统计获得收益")
    public R<TimePeriodStatisticsGrowthVo> yieldEarned(@RequestBody EarnTotalBo bo) {
        return R.data(service.yieldEarned(bo));
    }


    @PostMapping("/vaultSharePrice")
    @Operation(summary = "vault统计份额价格", description = "vault统计份额价格")
    public R<TimePeriodStatisticsGrowthVo> vaultSharePrice(@RequestBody EarnTotalBo bo) {
        return R.data(service.vaultSharePrice(bo));
    }



    @PostMapping("/geHTokenInfo")
    @Operation(summary = "geHTokenInfo", description = "geHTokenInfo")
    public R<HTokenVo> geHTokenInfo(@RequestBody @Valid HTokenBo tokenBo){
        return R.data(service.geHTokenInfo(tokenBo));
    }


}
