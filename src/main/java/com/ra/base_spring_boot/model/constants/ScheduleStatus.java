package com.ra.base_spring_boot.model.constants;

public enum ScheduleStatus {
    UPCOMING,    // Sắp chạy (thay cho AVAILABLE)
    RUNNING,     // Đang chạy (khi thời gian hiện tại nằm giữa giờ đi và giờ đến)
    COMPLETED,   // Hoàn thành (khi đã qua giờ đến)
    CANCELLED,   // Đã hủy
    FULL         // Hết vé (có thể là một trạng thái phụ của UPCOMING)
}