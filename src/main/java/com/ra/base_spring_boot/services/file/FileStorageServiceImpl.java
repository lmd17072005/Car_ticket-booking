package com.ra.base_spring_boot.services.file;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements IFileStorageService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        if(file == null || file.isEmpty()) {
            throw new HttpBadRequest("File is empty");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new HttpBadRequest("File upload failed: " + e.getMessage());
        }
    }
}
