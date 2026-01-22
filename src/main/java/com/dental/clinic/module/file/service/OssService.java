package com.dental.clinic.module.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {
    /**
     * 上传头像到OSS
     * @param file 上传的文件
     * @return 文件在OSS上的URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 上传文件到 本地
     * @param file 上传的文件
     * @return 文件在OSS上的URL
     */
    String uploadAvatarToLocal(MultipartFile file);
}