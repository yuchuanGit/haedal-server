package com.sui.haedal.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sui.haedal.common.*;
import com.sui.haedal.common.page.Condition;
import com.sui.haedal.config.HTokenConfig;
import com.sui.haedal.mapper.*;
import com.sui.haedal.model.bo.*;
import com.sui.haedal.model.entity.CoinConfig;
import com.sui.haedal.model.entity.Vault;
import com.sui.haedal.model.enums.DecimalType;
import com.sui.haedal.model.vo.*;
import com.sui.haedal.service.EarnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service("earnService")
public class EarnServiceImpl implements EarnService {

    @Resource
    private EarnMapper earnMapper;

    @Resource
    private CoinConfigMapper coinConfigMapper;

    @Resource
    private HTokenConfig hTokenConfig;

    @Resource
    private VaultDepositWithdrawMapper depositWithdrawMapper;

    @Resource
    private BorrowAssetsOperationRecordMapper borrowAssetsOperationRecordMapper;



    /**
     * 币种列表
     * @param coinType
     * @return
     */
    @Override
    public List<CoinConfigVo> coinConfigList(String coinType){
        List<CoinConfigVo> data = new ArrayList<>();
        LambdaQueryWrapper<CoinConfig> queryWrapper = Wrappers.<CoinConfig>query().lambda();
        if(coinType!=null&&!"".equals(coinType)){
            queryWrapper.eq(CoinConfig::getCoinType,coinType);
        }
        List<CoinConfig> coinList = coinConfigMapper.selectList(queryWrapper);
        for (CoinConfig coinConfig : coinList) {
            CoinConfigVo vo = new CoinConfigVo();
            BeanUtils.copyProperties(coinConfig, vo);
            data.add(vo);
        }
        return data;
    }


    /**
     * borrow 资产存取分页查询
     * @param queryBo
     * @return
     */
    @Override
    public IPage<BorrowAssetsOperationRecordVo> borrowAssetsOperationRecordPageQuery(BorrowAssetsSupplyWithdrawQueryBo queryBo){
        IPage<BorrowAssetsOperationRecordVo> page = Condition.getPage(queryBo);
        queryBo.setOperationTypes(Arrays.asList("Deposit","Withdraw"));
        if(queryBo.getIsBorrow()){
            queryBo.setOperationTypes(Arrays.asList("Collateral","CollateralWithdraw","Borrow","Repay","Liquidation"));
        }
        List<BorrowAssetsOperationRecordVo>  records = borrowAssetsOperationRecordMapper.borrowAssetsOperationRecordPageQuery(page,queryBo);
        page.setRecords(records);
        return page;
    }

    /**
     * Vault存取分页查询
     * @param queryBo
     * @return
     */
    @Override
    public IPage<VaultDepositWithdrawVo> vaultDepositWithdrawPageQuery(VaultDepositWithdrawQueryBo queryBo){
        IPage<VaultDepositWithdrawVo> page = Condition.getPage(queryBo);
        List<VaultDepositWithdrawVo>  records = depositWithdrawMapper.vaultDepositWithdrawPageQuery(page,queryBo);
        page.setRecords(records);
        return page;
    }
    /**
     * 获取用户Vault权限
     * @param userAddress
     * @return
     */
    @Override
    public UserVaultPermissionVo userVaultPermission(String userAddress){
        UserVaultPermissionVo vo = new UserVaultPermissionVo();
        List<Vault> list = earnMapper.selectList(Wrappers.<Vault>query().lambda());
        List<String> ownerVaultAddress = new ArrayList<>();
        List<String> curatorVaultAddress = new ArrayList<>();
        List<String> guardianVaultAddress = new ArrayList<>();
        for (Vault vault : list) {
            if(userAddress.equals(vault.getOwner())){
                ownerVaultAddress.add(vault.getVaultId());
            }
            if(userAddress.equals(vault.getCurator())){
                curatorVaultAddress.add(vault.getVaultId());
            }
            if(userAddress.equals(vault.getGuardian())){
                guardianVaultAddress.add(vault.getVaultId());
            }
        }
        vo.setOwnerVaultAddress(ownerVaultAddress);
        vo.setCuratorVaultAddress(curatorVaultAddress);
        vo.setGuardianVaultAddress(guardianVaultAddress);
        return vo;
    }
    /**
     * earn vault列表
     * roleType=1 owner curator guardian
     * roleType=2 owner curator
     * roleType=3 owner guardian
     * roleType=4 curator guardian
     * roleType=5 owner
     * roleType=6 curator
     * roleType=7 guardian
     * @return
     */
    @Override
    public List<VaultVo> list(String userAddress,Integer roleType){
        List<VaultVo> vos = new ArrayList<>();
        LambdaQueryWrapper<Vault> queryWrapper = Wrappers.<Vault>query().lambda();
        if(userAddress!=null&&roleType!=null&& userAddress!=""){
            switch (roleType){
                case 1:
                    queryWrapper.eq(Vault::getOwner, userAddress).or().eq(Vault::getCurator, userAddress).or().eq(Vault::getGuardian, userAddress);
                    break;
                case 2:
                    queryWrapper.eq(Vault::getOwner,userAddress).or().eq(Vault::getCurator,userAddress);
                    break;
                case 3:
                    queryWrapper.eq(Vault::getOwner,userAddress).or().eq(Vault::getGuardian,userAddress);
                    break;
                case 4:
                    queryWrapper.eq(Vault::getCurator,userAddress).or().eq(Vault::getGuardian,userAddress);
                    break;
                case 5:
                    queryWrapper.eq(Vault::getOwner,userAddress);
                    break;
                case 6:
                    queryWrapper.eq(Vault::getCurator,userAddress);
                    break;
                case 7:
                    queryWrapper.eq(Vault::getGuardian,userAddress);
                    break;
            }
        }
        List<Vault> list = earnMapper.selectList(queryWrapper);
        List<StrategyVo> strategyVos = earnMapper.allVaultStrategy();
        List<VaultVo> vaultApyVos = earnMapper.vaultApy(null);
        List<VaultVo> allVaultNewCuratorVos = earnMapper.allVaultNewCurator();
        List<VaultVo> allVaultNewAllocatorVos = earnMapper.allVaultNewAllocator();
        Map<String,List<StrategyVo>> vaultMaps = strategyVos.stream().collect(Collectors.groupingBy(StrategyVo::getVaultId));
        Map<String,VaultVo> vaultApyMaps =vaultApyVos.stream().collect(Collectors.toMap(VaultVo::getVaultId,Function.identity(),(v1,v2)->v1));
        Map<String,VaultVo> newCuratorMaps =allVaultNewCuratorVos.stream().collect(Collectors.toMap(VaultVo::getVaultId,Function.identity(),(v1,v2)->v1));
        Map<String,VaultVo> newAllocatorMaps =allVaultNewAllocatorVos.stream().collect(Collectors.toMap(VaultVo::getVaultId,Function.identity(),(v1,v2)->v1));
        Map<String,CoinConfig> coinConfigMap = getCoinConfigMap();
        for (Vault vault : list) {
            VaultVo vo = new VaultVo();
            BeanUtils.copyProperties(vault, vo);
            VaultVo vaultApy = vaultApyMaps.get(vault.getVaultId());
            if(vaultApy!=null){
                vo.setApy(vaultApy.getApy()+"%");
            }
            vo.setTvl(vault.getTotalAsset());
            setTvlCapacity(vo);//设置剩余容量
            CoinConfig coinConfig = coinConfigMap.get(vault.getAssetType());
            if(coinConfig!=null){
                vo.setAssetTypeFeedId(coinConfig.getFeedId());
                vo.setAssetTypeFeedObjectId(coinConfig.getFeedObjectId());
            }
            vo.setStrategyVos(vaultMaps.get(vo.getVaultId()));
            VaultVo newCurator = newCuratorMaps.get(vault.getVaultId());
            if(newCurator!=null){
                vo.setCurator(newCurator.getCurator());
            }
            VaultVo newAllocator = newAllocatorMaps.get(vault.getVaultId());
            if(newAllocator!=null){
                vo.setAllocator(newAllocator.getAllocator());
            }
            vos.add(vo);
        }
        return vos;
    }


    private void setTvlCapacity(VaultVo vo){
        if(vo.getSupplyCap()!=null){
            BigDecimal supplyCap = new BigDecimal(vo.getSupplyCap());
            if(supplyCap.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal tvlCapacity = BigDecimalUtil.calculate(DecimalType.SUBTRACT.getValue(),supplyCap,
                        new BigDecimal(vo.getTotalAsset()),0,RoundingMode.DOWN);
                vo.setTvlCapacity(tvlCapacity.toString());//该池子总存款-剩余容量
            }
        }
    }
    /**
     * Vault详情
     * @param vaultId
     * @return
     */
    @Override
    public VaultVo vaultDetail(String vaultId){
        VaultVo vaultVo = new VaultVo();
        List<VaultVo> list = earnMapper.vaultDetail(vaultId);
        if(list.size()>0){
            vaultVo = list.get(0);
            Map<String,CoinConfig> coinConfigMap = getCoinConfigMap();
            CoinConfig coinConfig = coinConfigMap.get(vaultVo.getAssetType());
            if(coinConfig!=null){
                vaultVo.setAssetTypeFeedId(coinConfig.getFeedId());
                vaultVo.setAssetTypeFeedObjectId(coinConfig.getFeedObjectId());
            }

            vaultVo.setTvl(vaultVo.getTotalAsset());
            vaultVo.setTvlCapacity("0");
            setTvlCapacity(vaultVo);//设置剩余容量
            List<VaultVo> vaultApyVos = earnMapper.vaultApy(vaultId);
            if(vaultApyVos.size()>0){
                vaultVo.setApy(vaultApyVos.get(0).getApy()+"%");
            }
        }
        return vaultVo;
    }

    /**
     * vault池分配借款池比例资金信息
     * @param vaultId
     * @return
     */
    @Override
    public List<StrategyVo> vaultStrategy(String vaultId){
        List<StrategyVo> data = earnMapper.vaultStrategy(vaultId);
        BigDecimal precision = decimalPower(16);
        BigDecimal baseVal = new BigDecimal(0.01);
        for (StrategyVo vo : data) {
            if(vo.getApy()!=null && vo.getApy()!=""){
                BigDecimal supplyRate = new BigDecimal(vo.getApy()).divide(precision);
                String apy = TimePeriodUtil.roundingModeStr(supplyRate, 2,RoundingMode.UP);
                if(supplyRate.compareTo(baseVal)<0){
                    apy = "<0.01";
                }
                vo.setApy(apy+"%");
            }
        }
        return data;
    }

    private BigDecimal decimalPower(int power){
        return new BigDecimal(Math.pow(10,power));
    }
    /**
     * 用户时间段最新存入/去除数量
     * 1.
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> yourDepositsWithdraw(EarnTotalBo bo){
        /**获取时间段类型 时间段数据**/
        TimePeriodStatisticsBo statisticsRoleBo = new TimePeriodStatisticsBo();
        setStatisticalUserRole(statisticsRoleBo,bo);
        Long poolTransactionTime = earnMapper.depositMinTimeVaultCreateTime(statisticsRoleBo);
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(bo.getTimePeriodType(),poolTransactionTime);
        setStatisticalUserRole(statisticsBo,bo);
        Date timePeriodMinTime = earnMapper.vaultDepositMinTime(statisticsBo);
        if(null==timePeriodMinTime) return new ArrayList<>();
        statisticsBo.setTimePeriodMinTime(timePeriodMinTime);
        /**根据时间段查询存/取数据**/
        List<TimePeriodStatisticsVo> depositVos = earnMapper.vaultDeposit(statisticsBo);
        List<TimePeriodStatisticsVo> depositLtVos = earnMapper.vaultDepositLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> withdrawVos = earnMapper.vaultWithdraw(statisticsBo);
        List<TimePeriodStatisticsVo> withdrawLtVos = earnMapper.vaultWithdrawLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> resultData = TimePeriodUtil.getTimePeriodData(statisticsBo,depositVos,depositLtVos,withdrawVos,withdrawLtVos,true);
        return resultData;
    }

    private void setStatisticalUserRole(TimePeriodStatisticsBo statisticsBo,EarnTotalBo bo){
        statisticsBo.setUserAddress(bo.getUserAddress());
        statisticsBo.setStatisticalRole(true);//用户查询
    }
    /**
     * 统计单个Vault池存入数据
     * @param bo
     * @return
     */

    public List<TimePeriodStatisticsVo> totalDepositsOld(EarnTotalBo bo){
        /**获取时间段类型 时间段数据**/
        Vault vault = getVaultId(bo.getVaultId());
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(bo.getTimePeriodType(),vault.getTransactionTimeUnix());
        statisticsBo.setBusinessPoolId(bo.getVaultId());
        statisticsBo.setStatisticalRole(false);//vault池子查询
        Date timePeriodMinTime = earnMapper.vaultDepositMinTime(statisticsBo);
        if(null==timePeriodMinTime) return new ArrayList<>();
        statisticsBo.setTimePeriodMinTime(timePeriodMinTime);
        /**根据时间段查询存/取数据**/
        List<TimePeriodStatisticsVo> depositVos = earnMapper.vaultDeposit(statisticsBo);
        List<TimePeriodStatisticsVo> depositLtVos = earnMapper.vaultDepositLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> withdrawVos = earnMapper.vaultWithdraw(statisticsBo);
        List<TimePeriodStatisticsVo> withdrawLtVos = earnMapper.vaultWithdrawLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> resultData = TimePeriodUtil.getTimePeriodData(statisticsBo,depositVos,depositLtVos,withdrawVos,withdrawLtVos,false);
        return resultData;
    }

    @Override
    public List<TimePeriodStatisticsVo> totalDeposits(EarnTotalBo bo){
        /**获取时间段类型 时间段数据**/
        Vault vault = getVaultId(bo.getVaultId());
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(bo.getTimePeriodType(),vault.getTransactionTimeUnix());
        statisticsBo.setBusinessPoolId(bo.getVaultId());
        Date timePeriodMinTime = earnMapper.vaultTvlMinTime(statisticsBo);
        List<TimePeriodStatisticsVo> tvlVos = earnMapper.vaultTvlTimePeriodStatistics(statisticsBo);
        List<TimePeriodStatisticsVo> resultData = TimePeriodUtil.getTimePeriodData(statisticsBo,timePeriodMinTime,tvlVos);
        return resultData;
    }


    private Vault getVaultId(String vaultId){
        Vault vault = null;
        LambdaQueryWrapper<Vault>  queryWrapper = Wrappers.<Vault>query().lambda();
        queryWrapper.eq(Vault::getVaultId,vaultId);
        List<Vault> list = earnMapper.selectList(queryWrapper);
        if(list.size()>0){
            vault = list.get(0);
        }
        return vault;
    }

    /**
     * 统计单个Vault池APY
     * @param bo
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> totalAPY(EarnTotalBo bo){
        List<TimePeriodStatisticsVo> resultData = new ArrayList<>();
        Vault vault = getVaultId(bo.getVaultId());
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(bo.getTimePeriodType(),vault.getTransactionTimeUnix());
        statisticsBo.setBusinessPoolId(bo.getVaultId());
        Date timePeriodMinTime = earnMapper.vaultAPYStatisticsMinTime(statisticsBo);
        if(null==timePeriodMinTime) return new ArrayList<>();
        statisticsBo.setTimePeriodMinTime(timePeriodMinTime);
        List<TimePeriodStatisticsVo> aprVos = earnMapper.vaultAPYStatistics(statisticsBo);
        if(aprVos.size()>0){
            Map<String,TimePeriodStatisticsVo> dateUnitKeys = aprVos.stream().collect(Collectors.toMap(TimePeriodStatisticsVo::getDateUnit,Function.identity(),(v1,v2)-> v1));
            List<TimePeriodStatisticsVo> virtualTimePeriodData = DateUtil.timePeriodDayGenerateNew(statisticsBo.getStartLD(),statisticsBo.getEndLD(),statisticsBo.getIsWeek(),TimePeriodUtil.getCreatePoolTimeHours(statisticsBo));
            /**虚拟时间数据匹配虚拟时间最近点dateUnitKeys(apr数据)**/
            TimePeriodUtil.virtualTimePeriodMatchValue(virtualTimePeriodData,dateUnitKeys,statisticsBo,resultData);
        }
        return resultData;
    }

    /**
     * vault统计获得收益
     * @param bo
     * @return
     */
    @Override
    public TimePeriodStatisticsGrowthVo yieldEarned(EarnTotalBo bo){
        TimePeriodStatisticsGrowthVo vo = new TimePeriodStatisticsGrowthVo();
        Vault vault = getVaultId(bo.getVaultId());
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(1,vault.getTransactionTimeUnix());
        statisticsBo.setBusinessPoolId(bo.getVaultId());
        List<TimePeriodStatisticsVo> yieldEarnedVos = earnMapper.vaultYieldEarnedLTTransactionTime(statisticsBo);
        vo.setStatisticsData(yieldEarnedVos);
        vo.setGrowthData(dayGrowthCalculate(yieldEarnedVos));
        return vo;
    }

    /**
     * vault统计份额价格
     * @param bo
     * @return
     */
    @Override
    public TimePeriodStatisticsGrowthVo vaultSharePrice(EarnTotalBo bo){
        TimePeriodStatisticsGrowthVo vo = new TimePeriodStatisticsGrowthVo();
        Vault vault = getVaultId(bo.getVaultId());
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(1,vault.getTransactionTimeUnix());
        statisticsBo.setBusinessPoolId(bo.getVaultId());
        List<TimePeriodStatisticsVo> sharePriceVos = earnMapper.vaultSharePriceLTTransactionTime(statisticsBo);
        vo.setStatisticsData(sharePriceVos);
        vo.setGrowthData(dayGrowthCalculate(sharePriceVos));
        return vo;
    }

    private List<GrowthStatisticsVo> dayGrowthCalculate(List<TimePeriodStatisticsVo> vos){
        List<GrowthStatisticsVo> growthData = new ArrayList<>();
        List<Integer> dayGrowths = dayGrowthRules();
        if(vos.size()>0){
            Map<String,TimePeriodStatisticsVo> dateUnitKeys = vos.stream().collect(Collectors.toMap(TimePeriodStatisticsVo::getDateUnit,Function.identity(),(v1,v2)->v1));
            LocalDateTime now = LocalDateTime.now();
            String thatDayKey = DateUtil.dateFormat(new Date(),DateUtil.DATE_FORMAT_YMD);//当天日期
            TimePeriodStatisticsVo thatDayVo = dateUnitKeys.get(thatDayKey);
            for(int i=0;i<dayGrowths.size();i++){
                LocalDateTime fewDaysAgo = now.minusDays(dayGrowths.get(i)).with(LocalTime.of(23, 59, 59, 999_000_000));//几天前
                if(dayGrowths.get(i)>1){
                    fewDaysAgo = fewDaysAgo.plusNanos(1000 * 1000000);
                }
                Date fewDaysAgoDate = Date.from(fewDaysAgo.atZone(ZoneId.systemDefault()).toInstant());
                String fewDaysAgoKey = DateUtil.dateFormat(fewDaysAgoDate,DateUtil.DATE_FORMAT_YMD);//前几天日期
                TimePeriodStatisticsVo fewDaysAgoVo = dateUnitKeys.get(fewDaysAgoKey);
                if(thatDayVo!=null&&fewDaysAgoVo!=null){
                    GrowthStatisticsVo growthStatisticsVo = new GrowthStatisticsVo();
                    BigDecimal thatDayB= new BigDecimal(thatDayVo.getVal());
                    BigDecimal fewDaysAgoB= new BigDecimal(fewDaysAgoVo.getVal());
                    /**dayGrowth=(thatDayB-fewDaysAgoB)/fewDaysAgoB*100**/
                    BigDecimal calculateVal = BigDecimalUtil.calculate(DecimalType.DIVIDE.getValue(),
                            thatDayB.subtract(fewDaysAgoB),fewDaysAgoB,4,RoundingMode.UP).multiply(new BigDecimal(100));
                    growthStatisticsVo.setGrowthDay(dayGrowths.get(i));
                    growthStatisticsVo.setGrowthRate(calculateVal.toString()+"%");
                    growthData.add(growthStatisticsVo);
                }else {
                    break;
                }
            }
        }
        return growthData;
    }

    private List<Integer> dayGrowthRules(){
        List<Integer> dayGrowths = new ArrayList<>();
        dayGrowths.add(1);
        dayGrowths.add(7);
        dayGrowths.add(60);
        dayGrowths.add(180);
        return dayGrowths;
    }


    @Override
    public HTokenVo geHTokenInfo(HTokenBo tokenBo){
        HTokenVo vo = new HTokenVo();
        try {
            Map<String, String> replaceMap = new HashMap<>();
            replaceMap.put("MODULE_NAME",tokenBo.getModuleName());
            replaceMap.put("COIN_TYPE",tokenBo.getModuleName().toUpperCase());
            replaceMap.put("COIN_SYMBOL",tokenBo.getCoinSymbol());
            replaceMap.put("COIN_DECIMALS",tokenBo.getCoinDecimals());
            replaceMap.put("COIN_URL",tokenBo.getCoinUrl());
            String htokenReplace = hTokenConfig.getHtokenReplace();
            String htokenReplaceFileName = FileCopyRenameUtil.generateUniqueFileName();
            FileCopyRenameUtil.copyDirAndRename(hTokenConfig.getHtokenTemplate(), htokenReplace, htokenReplaceFileName);
            String htokenReplacePath = htokenReplace+htokenReplaceFileName+"/sources";
            String htokenReplaceFile = htokenReplacePath+"/asset.move";
            ResourceFileUtil.replaceFileContent(htokenReplaceFile, replaceMap, htokenReplaceFile);
            CommandResult commandResult =  executeSuiMoveBuild(htokenReplacePath,null);
            if(commandResult.getExitCode()==0){
                vo = JSONObject.parseObject(commandResult.getStdout(),HTokenVo.class);
            }else{
                log.error("sui 执行失败...");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return vo;
    }

    /**
     * 执行 sui move build 命令（指定工作目录）
     * @param workDir 执行命令的目录（等效于 cd 到该目录）
     * @param suiExecutablePath sui 可执行文件路径（如 Windows: C:/sui/sui.exe; Linux: /usr/local/bin/sui）
     * @return 命令执行结果（退出码、标准输出、错误输出）
     * @throws IOException IO异常
     * @throws InterruptedException 线程中断异常
     */
    public static CommandResult executeSuiMoveBuild(String workDir, String suiExecutablePath) throws IOException, InterruptedException {
        // 1. 校验参数
        File workDirFile = new File(workDir);
        Assert.isTrue(workDirFile.exists() && workDirFile.isDirectory(), "执行目录不存在或非目录：" + workDir);
//        File suiFile = new File(suiExecutablePath);
//        Assert.isTrue(suiFile.exists() || isCommandInPath(suiExecutablePath), "sui 命令不存在：" + suiExecutablePath);

        // 2. 构建命令列表（避免字符串拼接，兼容多系统）
        List<String> command = new ArrayList<>();
//        command.add(suiExecutablePath);       // sui 命令路径（优先绝对路径）
        command.add("sui");       // sui 命令路径（优先绝对路径）
        command.add("move");
        command.add("build");
        command.add("--silence-warnings");
        command.add("--dump-bytecode-as-base64");

        // 3. 构建进程：指定工作目录 + 分离输出/错误流
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workDirFile); // 核心：指定命令执行的目录（等效 cd workDir）
        processBuilder.redirectErrorStream(false); // 分离stdout/stderr，便于分别捕获
        // 设置环境变量（可选：若sui依赖特定环境变量）
        processBuilder.environment().put("PATH", System.getenv("PATH"));

        // 4. 启动进程并读取流（异步读取避免缓冲区阻塞）
        Process process = processBuilder.start();
        String stdout = readStream(process.getInputStream());
        String stderr = readStream(process.getErrorStream());
        int exitCode = process.waitFor(); // 等待命令执行完成

        // 5. 返回封装结果
        return new CommandResult(exitCode, stdout, stderr);
    }

    /**
     * 读取输入流（stdout/stderr）为字符串
     */
    private static String readStream(java.io.InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        return sb.toString().trim();
    }

    /**
     * 检查命令是否在系统PATH中（兼容未指定绝对路径的情况）
     */
    private static boolean isCommandInPath(String command) {
        String path = System.getenv("PATH");
        String[] pathDirs = path.split(File.pathSeparator);
        String executable = command;
        // Windows 下自动补全 .exe 后缀
        if (System.getProperty("os.name").toLowerCase().contains("win") && !executable.endsWith(".exe")) {
            executable += ".exe";
        }
        for (String pathDir : pathDirs) {
            File file = new File(pathDir, executable);
            if (file.exists() && file.canExecute()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 命令执行结果封装类
     */
    public static class CommandResult {
        private final int exitCode;    // 退出码（0=成功，非0=失败）
        private final String stdout;   // 标准输出（Base64字节码）
        private final String stderr;   // 错误输出（异常信息）

        public CommandResult(int exitCode, String stdout, String stderr) {
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
        }

        // Getter
        public int getExitCode() { return exitCode; }
        public String getStdout() { return stdout; }
        public String getStderr() { return stderr; }
        public boolean isSuccess() { return exitCode == 0; }
    }



    private Map<String,CoinConfig> getCoinConfigMap(){
        List<CoinConfig> coinList = coinConfigMapper.selectList(Wrappers.<CoinConfig>query().lambda());
        return coinList.stream().collect(Collectors.toMap(CoinConfig::getCoinType, Function.identity(),(v1, v2)->v1));
    }

}
