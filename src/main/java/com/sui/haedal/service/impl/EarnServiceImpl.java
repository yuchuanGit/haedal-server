package com.sui.haedal.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sui.haedal.common.*;
import com.sui.haedal.config.HTokenConfig;
import com.sui.haedal.mapper.BorrowMapper;
import com.sui.haedal.mapper.CoinConfigMapper;
import com.sui.haedal.mapper.EarnMapper;
import com.sui.haedal.model.bo.EarnTotalBo;
import com.sui.haedal.model.bo.HTokenBo;
import com.sui.haedal.model.bo.TimePeriodStatisticsBo;
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
    /**
     * earn vault列表
     * @return
     */
    @Override
    public List<VaultVo> list(){
        List<VaultVo> vos = new ArrayList<>();
        List<Vault> list = earnMapper.selectList(Wrappers.<Vault>query().lambda());
        Map<String,CoinConfig> coinConfigMap = getCoinConfigMap();
        for (Vault vault : list) {
            VaultVo vo = new VaultVo();
            BeanUtils.copyProperties(vault, vo);
            vo.setApy("12.24%");
            vo.setTvl(vault.getTotalAsset());
            BigDecimal tvlCapacity = BigDecimalUtil.calculate(DecimalType.SUBTRACT.getValue(),new BigDecimal(vault.getSupplyCap()),
                    new BigDecimal(vault.getTotalAsset()),0,RoundingMode.DOWN);
            vo.setTvlCapacity(tvlCapacity.toString());//该池子总存款-剩余容量
            CoinConfig coinConfig = coinConfigMap.get(vault.getAssetType());
            if(coinConfig!=null){
                vo.setAssetTypeFeedId(coinConfig.getFeedId());
                vo.setAssetTypeFeedObjectId(coinConfig.getFeedObjectId());
            }
            vos.add(vo);
        }
        return vos;
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
            vaultVo.setApy("12.24%");
            vaultVo.setTvl(vaultVo.getTotalAsset());
            BigDecimal tvlCapacity = BigDecimalUtil.calculate(DecimalType.SUBTRACT.getValue(),new BigDecimal(vaultVo.getSupplyCap()),
                    new BigDecimal(vaultVo.getTotalAsset()),0,RoundingMode.DOWN);
            vaultVo.setTvlCapacity(tvlCapacity.toString());//该池子总存款-剩余容量
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
        List<TimePeriodStatisticsVo> resultData = new ArrayList<>();
        Map<String,TimePeriodStatisticsVo> dateUnitKeys = new HashMap<>();//存/取所有数据
        List<String> depositTransactionTimes = new ArrayList<>();//存交易时间
        List<String> withdrawTransactionTimes = new ArrayList<>();//取交易时间
        /**获取时间段类型 时间段数据**/
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(bo.getTimePeriodType());
        statisticsBo.setBusinessPoolId(bo.getVaultId());
        statisticsBo.setUserAddress(bo.getUserAddress());
        /**根据时间段查询存/取数据**/
        List<TimePeriodStatisticsVo> userDepositVos = earnMapper.userDeposit(statisticsBo);
        List<TimePeriodStatisticsVo> userWithdrawVos = earnMapper.userWithdraw(statisticsBo);
        Map<String,String> feedIds = TimePeriodUtil.getInputAndOutFeedIds(userDepositVos,userWithdrawVos);
        /**存/取币种所有feedId查询Pyth价格**/
        Map<String, PythCoinFeedPriceVo> coinPrice = PythOracleUtil.getPythPrice(feedIds);
        Map<String, TimePeriodStatisticsVo> dateUnitRemoveWithdrawMaps = new HashMap<>();// 取map数据,用于dateUnit删除
        /**存/取list转map计算usd**/
        Map<String, TimePeriodStatisticsVo> dateUnitDepositMap = TimePeriodUtil.timePeriodDataConvertDateUnitMaps(userDepositVos,new ArrayList<>(),coinPrice,depositTransactionTimes,null);
        Map<String, TimePeriodStatisticsVo> dateUnitWithdrawMap = TimePeriodUtil.timePeriodDataConvertDateUnitMaps(userWithdrawVos,new ArrayList<>(),coinPrice,withdrawTransactionTimes,dateUnitRemoveWithdrawMaps);
        /**循环存时间点匹配对应取时间点 计算当前剩余数量**/
        TimePeriodUtil.matchDepositTimeCalculate(dateUnitDepositMap,withdrawTransactionTimes,dateUnitWithdrawMap,dateUnitRemoveWithdrawMaps,statisticsBo.getIsWeek(),dateUnitKeys);
        /**循环取时间点匹配对应取时间点 计算当前剩余数量**/
        TimePeriodUtil.matchWithdrawTimeCalculate(dateUnitRemoveWithdrawMaps,dateUnitDepositMap,dateUnitKeys);
        /**虚拟时间段生成**/
        List<TimePeriodStatisticsVo> virtualTimePeriodData = DateUtil.timePeriodDayGenerateNew(statisticsBo.getStartLD(),statisticsBo.getEndLD(),statisticsBo.getIsWeek());
        /**虚拟时间数据匹配虚拟时间最近点dateUnitKeys(所有存/取数据)**/
        TimePeriodUtil.virtualTimePeriodMatchValue(virtualTimePeriodData,dateUnitKeys,statisticsBo.getIsWeek(),resultData);
        return resultData;
    }


    /**
     * 统计单个Vault池存入数据
     * @param bo
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> totalDeposits(EarnTotalBo bo){
        List<TimePeriodStatisticsVo> data = new ArrayList<>();

        return data;
    }


    /**
     * 统计单个Vault池APY
     * @param bo
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> totalAPY(EarnTotalBo bo){
        List<TimePeriodStatisticsVo> data = new ArrayList<>();

        return data;
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
