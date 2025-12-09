package com.sui.haedal.common;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class ResourceFileUtil {



    /**
     * 读取 resources 下文件并替换占位符，兼容 JAR 环境
     * @param classPathFile resources 下的文件路径（如 "template.txt"）
     * @param replaceMap 占位符替换映射（key: 占位符，value: 替换值）
     * @param outputPath 输出文件路径（生产环境需指定本地磁盘路径，如 "./temp/template.txt"）
     * @throws IOException 读写异常
     */
    public static void replaceFileContent(String classPathFile, Map<String, String> replaceMap, String outputPath) throws IOException {
        // 1. 读取 resources 下的文件内容
        String content = readFilePath(classPathFile);

        // 2. 替换占位符
        String replacedContent = replacePlaceholders(content, replaceMap);

        // 3. 写入文件（开发环境可直接写回 resources，生产环境写本地目录）
        writeFile(replacedContent, outputPath);
    }

    /**
     * 读取 resources 下文件内容
     */
    private static String readClassPathFile(String classPathFile) throws IOException {
        Resource resource = new ClassPathResource(classPathFile);
        // 读取文件内容为字符串（指定 UTF-8 编码）
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }


    private static String readFilePath(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在：" + filePath);
        }
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }
    /**
     * 替换字符串中的占位符（如 ${name}）
     */
    private static String replacePlaceholders(String content, Map<String, String> replaceMap) {
        String result = content;
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            result = result.replace(placeholder, entry.getValue());
        }
        return result;
    }

    /**
     * 写入文件到指定路径（自动创建父目录）
     */
    private static void writeFile(String content, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        // 创建父目录（如果不存在）
        if (!outputFile.getParentFile().exists()) {
            boolean mkdirs = outputFile.getParentFile().mkdirs();
            if (!mkdirs) {
                throw new IOException("创建目录失败：" + outputFile.getParentFile().getPath());
            }
        }
        // 写入内容
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(outputFile, false),  // true = 追加写入
                StandardCharsets.UTF_8
        )) {
            writer.write(content);
        }
    }
}
