package com.ra.base_spring_boot.services.file;

import org.springframework.web.multipart.MultipartFile;


public interface IFileStorageService {
    String uploadFile(MultipartFile file);
}
