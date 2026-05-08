package com.utc2.appreborn.ui.courseregistration.exception;

/**
 * Ngoại lệ nghiệp vụ cho chức năng Đăng ký học phần.
 *
 * [Chương 4 - Xử lý ngoại lệ]
 *  - Extends RuntimeException → unchecked exception
 *  - Dùng để ném khi: trùng môn, đủ tín chỉ, môn không tồn tại, v.v.
 */
public class CourseException extends RuntimeException {

    public CourseException(String message) {
        super(message);
    }

    public CourseException(String message, Throwable cause) {
        super(message, cause);
    }
}
