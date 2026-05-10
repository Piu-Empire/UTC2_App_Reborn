package com.utc2.appreborn.ui.dormitory.exception;

/**
 * Ngoại lệ tùy chỉnh cho module Kí túc xá.
 *
 * [Chương 4 - Xử lý ngoại lệ]
 *  - Custom exception (extends Exception)
 *  - Được ném (throw) và khai báo (throws) trong DormitoryRepository
 *  - Được bắt (catch) trong DormitoryActivity
 */
public class DormitoryException extends Exception {

    public DormitoryException(String message) {
        super(message);
    }

    public DormitoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
