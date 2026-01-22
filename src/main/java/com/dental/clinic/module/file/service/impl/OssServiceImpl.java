package com.dental.clinic.module.file.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.dental.clinic.module.file.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class OssServiceImpl implements OssService {

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    private final OSS ossClient;

    public OssServiceImpl(OSS ossClient) {
        this.ossClient = ossClient;
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 1. 验证文件类型 (可选，增加安全性)
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            throw new RuntimeException("文件类型不支持，仅支持图片格式");
        }

        // 2. 生成唯一的文件名，防止覆盖
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename); // 提取方法
        String uniqueFileName = "avatar/" + UUID.randomUUID().toString() + fileExtension; // 放在 avatar/ 目录下

        try {
            // 3. 上传文件到OSS
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file.getInputStream());
            ossClient.putObject(putObjectRequest);

            // 4. 构造并返回文件访问URL (使用 endpoint 和 bucketName 拼接)
            return endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + uniqueFileName;

        } catch (IOException e) {
            // 5. 处理上传异常
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadAvatarToLocal(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();

        if (contentType == null || (!contentType.startsWith("image/"))) {
            throw new RuntimeException("文件类型不支持，仅支持图片格式");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 设置本地存储路径
        String localPath = "C:\\Users\\22390\\Desktop\\lgx\\";
        File directory = new File(localPath);

        // 确保目录存在
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("无法创建目录: " + localPath + "，请检查权限或磁盘空间");
            }
        }

        try {
            // 保存文件到本地
            Path targetPath = Paths.get(localPath + uniqueFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 返回可以通过后端接口访问的URL
            return "/api/files/local/avatar/" + uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }


    // 提取的方法：获取文件扩展名
    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.lastIndexOf('.') == -1) {
            return ".jpg"; // 默认扩展名
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}