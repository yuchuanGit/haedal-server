package com.sui.haedal.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileCopyRenameUtil {


    /**
     * 复制源文件夹到目标目录，并修改目标文件夹名称
     * @param sourceDirPath 源文件夹路径（如 "D:/test/source"）
     * @param targetParentDir 目标父目录（如 "D:/test/target_parent"）
     * @param newDirName 新的文件夹名称（如 "new_source_name"）
     * @throws IOException 读写/目录操作异常
     */
    public static void copyDirAndRename(String sourceDirPath, String targetParentDir, String newDirName) throws IOException {
        // 1. 校验源文件夹是否存在且是目录
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists()) {
            throw new IOException("源文件夹不存在：" + sourceDirPath);
        }
        if (!sourceDir.isDirectory()) {
            throw new IOException("路径不是文件夹：" + sourceDirPath);
        }

        // 2. 构建目标文件夹完整路径（父目录 + 新名称）
        File targetDir = new File(targetParentDir, newDirName);
        // 若目标文件夹已存在，先删除（可选：根据业务决定是否覆盖/报错）
        if (targetDir.exists()) {
            deleteDir(targetDir); // 递归删除已有文件夹
        }

        // 3. 递归复制源文件夹到目标文件夹（含所有子文件/子文件夹）
        copyDirectory(sourceDir, targetDir);
        System.out.println("文件夹复制并重命名完成：\n源路径：" + sourceDirPath + "\n目标路径：" + targetDir.getAbsolutePath());
    }

    /**
     * 递归复制文件夹（核心方法）
     */
    private static void copyDirectory(File sourceDir, File targetDir) throws IOException {
        // 创建目标文件夹
        if (!targetDir.mkdirs()) {
            throw new IOException("创建目标文件夹失败：" + targetDir.getAbsolutePath());
        }

        // 遍历源文件夹下的所有文件/子文件夹
        File[] files = sourceDir.listFiles();
        if (files == null) {
            throw new IOException("读取源文件夹内容失败：" + sourceDir.getAbsolutePath());
        }

        for (File file : files) {
            File targetFile = new File(targetDir, file.getName());
            if (file.isDirectory()) {
                // 递归复制子文件夹
                copyDirectory(file, targetFile);
            } else {
                // 复制文件（使用 NIO 高效复制）
                copyFile(file, targetFile);
            }
        }
    }

    /**
     * 复制单个文件（NIO 通道方式，高效且稳定）
     */
    private static void copyFile(File sourceFile, File targetFile) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
             FileChannel targetChannel = new FileOutputStream(targetFile).getChannel()) {
            // 传输文件内容（JDK 优化的零拷贝方式）
            targetChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
        // 可选：保留文件最后修改时间
        targetFile.setLastModified(sourceFile.lastModified());
    }

    /**
     * 递归删除文件夹（含子文件/子文件夹）
     */
    private static void deleteDir(File dir) throws IOException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDir(file); // 递归删除子文件/子文件夹
                }
            }
        }
        // 删除文件/空文件夹
        if (!dir.delete()) {
            throw new IOException("删除文件/文件夹失败：" + dir.getAbsolutePath());
        }
    }

    // 简化版：仅重命名已有文件夹（如果不需要复制，仅重命名）
    public static void renameDir(String oldDirPath, String newDirPath) throws IOException {
        File oldDir = new File(oldDirPath);
        File newDir = new File(newDirPath);
        if (!oldDir.exists()) {
            throw new IOException("待重命名的文件夹不存在：" + oldDirPath);
        }
        // 重命名（若目标已存在，需先删除）
        if (newDir.exists()) {
            deleteDir(newDir);
        }
        if (!oldDir.renameTo(newDir)) {
            throw new IOException("文件夹重命名失败：" + oldDirPath + " -> " + newDirPath);
        }
    }

    // 测试示例
    public static void main(String[] args) {
        try {
            // 示例1：复制文件夹并修改名称
            String sourceDir = "E:\\sui-file\\htoken-template";       // 源文件夹
            String targetParent = "E:\\sui-file\\";               // 目标父目录
            String newName = generateUniqueFileName();            // 新文件夹名称
            copyDirAndRename(sourceDir, targetParent, newName);
            Map<String, String> replaceMap = new HashMap<>();
            replaceMap.put("MODULE_NAME","asset");
            replaceMap.put("COIN_TYPE","ASSET");
            replaceMap.put("COIN_SYMBOL","hSUI-USDC");
            replaceMap.put("COIN_DECIMALS","6");
            ResourceFileUtil.replaceFileContent("E:\\sui-file\\12091931-a45295b818464b6ebaf280c09a1e947b\\sources\\asset.move", replaceMap, "E:\\sui-file\\12091931-a45295b818464b6ebaf280c09a1e947b\\sources\\asset.move");
            // 示例2：仅重命名已有文件夹（不复制）
            // renameDir("D:/test/old_folder", "D:/test/renamed_folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成 UUID + 时间戳 组合的文件名（无后缀）
     * @return 示例：20251209153020-550e8400-e29b-41d4-a716-446655440000
     */
    public static String generateUniqueFileName() {
        // 1. 时间戳（精确到秒，便于溯源）
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
        String timeStr = sdf.format(new Date());
        // 2. UUID（保证唯一性）
        String uuid = UUID.randomUUID().toString();
        // 3. 组合（可去掉横线简化）
        return timeStr + "-" + uuid.replace("-", "");
    }

}
