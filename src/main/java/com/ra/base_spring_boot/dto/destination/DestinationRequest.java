package com.ra.base_spring_boot.dto.destination;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationRequest {

    @NotBlank(message = "Tên điểm đến không được để trống")
    @Size(max = 255, message = "Tên điểm đến không được quá 255 ký tự")
    private String name;

    @Size(max = 255, message = "Tên địa điểm không được quá 255 ký tự")
    private String location;

    @Size(max = 500, message = "URL hình ảnh không được quá 500 ký tự")
    private String image;

    @Size(max = 500, message = "URL wallpaper không được quá 500 ký tự")
    private String wallpaper;

    private String description;

    @Min(value = 0, message = "Số bài viết phải >= 0")
    private Integer reviewCount;

    private Boolean isFeatured;
}