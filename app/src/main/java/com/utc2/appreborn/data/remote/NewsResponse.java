package com.utc2.appreborn.data.remote;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * NewsResponse — FIXED (khớp với JSON thực tế của UTC2 API)
 * ──────────────────────────────────────────────────────────────
 * Cấu trúc JSON thực tế:
 * {
 *   "message": "ok",
 *   "responseData": {
 *     "count": 1371,
 *     "rows": [ { "id": "...", "title": "...", ... } ],
 *     "totalPages": 138,
 *     "currentPage": "1"
 *   },
 *   "status": "success"
 * }
 *
 * Package: com.utc2.appreborn.data.remote
 */
public class NewsResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    /** Wrapper object chứa danh sách và metadata phân trang */
    @SerializedName("responseData")
    private ResponseData responseData;

    public String       getMessage()      { return message;      }
    public String       getStatus()       { return status;       }
    public ResponseData getResponseData() { return responseData; }

    /** Shortcut tiện lợi — trả về list bài đăng hoặc null */
    public List<PostDto> getData() {
        return responseData != null ? responseData.getRows() : null;
    }

    // ══════════════════════════════════════════════════════════
    //  ResponseData — lớp wrapper chứa "rows" + phân trang
    // ══════════════════════════════════════════════════════════
    public static class ResponseData {

        @SerializedName("count")
        private int count;

        @SerializedName("totalPages")
        private int totalPages;

        @SerializedName("currentPage")
        private String currentPage;

        /** Mảng bài đăng — tên field thực tế là "rows" */
        @SerializedName("rows")
        private List<PostDto> rows;

        public int           getCount()       { return count;       }
        public int           getTotalPages()  { return totalPages;  }
        public String        getCurrentPage() { return currentPage; }
        public List<PostDto> getRows()        { return rows;        }
    }

    // ══════════════════════════════════════════════════════════
    //  PostDto — một bài đăng trong danh sách
    //  (list API không trả về "content", chỉ có metadata)
    // ══════════════════════════════════════════════════════════
    public static class PostDto {

        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        /** slug dùng để build URL chi tiết trên web */
        @SerializedName("seo_text")
        private String seoText;

        @SerializedName("sub_title")
        private String subTitle;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("type")
        private String type;

        @SerializedName("display")
        private boolean display;

        @SerializedName("send_app")
        private boolean sendApp;

        @SerializedName("view")
        private int view;

        // ── Getters ───────────────────────────────────────────
        public String  getId()      { return id;      }
        public String  getTitle()   { return title;   }
        public String  getSeoText() { return seoText; }
        public String  getSubTitle(){ return subTitle;}
        public String  getType()    { return type;    }
        public boolean isDisplay()  { return display; }
        public boolean isSendApp()  { return sendApp; }
        public int     getView()    { return view;    }

        /**
         * Ngày đăng định dạng "yyyy-MM-dd".
         * createdAt từ API: "2026-04-24T08:34:21.617Z"
         */
        public String getDisplayDate() {
            if (createdAt != null && createdAt.length() >= 10) {
                return createdAt.substring(0, 10);
            }
            return createdAt != null ? createdAt : "";
        }

        /**
         * Summary ngắn — dùng sub_title nếu có, nếu không để trống.
         * Content đầy đủ cần gọi API detail riêng.
         */
        public String getSummary() {
            return subTitle != null ? subTitle : "";
        }

        /**
         * URL trang chi tiết trên web UTC2.
         * Dùng để mở WebView hoặc browser khi xem nội dung.
         * VD: https://utc2.edu.vn/sinh-vien/thong-bao/thong-bao-kiem-tra-bang-tot-nghiep-...
         */
        public String getDetailUrl() {
            if (seoText != null && !seoText.isEmpty()) {
                return "https://utc2.edu.vn/sinh-vien/thong-bao/" + seoText;
            }
            return "";
        }
    }
}