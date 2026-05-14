package com.utc2.appreborn.ui.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.utc2.appreborn.databinding.FragmentQrBinding;
import com.utc2.appreborn.utils.MockHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * QrFragment — CRASH FIXES
 * ──────────────────────────────────────────────────────────────
 *
 * FIX A — Back button crash (emulator):
 *   Root cause: calling getOnBackPressedDispatcher().onBackPressed()
 *   INSIDE the btn_back click AND having a registered
 *   OnBackPressedCallback that also calls the same dispatcher →
 *   infinite loop → StackOverflowError crash.
 *
 *   Fix: btn_back click calls getSupportFragmentManager()
 *   .popBackStack() DIRECTLY (no dispatcher).
 *   The OnBackPressedCallback disables itself before popping so
 *   hardware/gesture back never double-fires.
 *
 * FIX B — Share crash:
 *   Root cause: FileProvider authority not declared in
 *   AndroidManifest.xml → IllegalArgumentException: "Failed to
 *   find configured root that contains …"
 *
 *   Fix: see AndroidManifest.xml and res/xml/file_provider_paths.xml
 *   snippets at the bottom of this file's Javadoc. The share code
 *   itself is unchanged; the crash is 100% a manifest/config issue.
 *
 * ──────────────────────────────────────────────────────────────
 * AndroidManifest.xml — add inside <application>:
 *
 *   <provider
 *       android:name="androidx.core.content.FileProvider"
 *       android:authorities="${applicationId}.provider"
 *       android:exported="false"
 *       android:grantUriPermissions="true">
 *       <meta-data
 *           android:name="android.support.FILE_PROVIDER_PATHS"
 *           android:resource="@xml/file_provider_paths" />
 *   </provider>
 *
 * res/xml/file_provider_paths.xml (create this file):
 *
 *   <?xml version="1.0" encoding="utf-8"?>
 *   <paths>
 *       <cache-path name="images" path="images/" />
 *   </paths>
 * ──────────────────────────────────────────────────────────────
 */
public class QrFragment extends Fragment {

    public static final String TAG = "tag_qr";

    private static final String ARG_FULL_NAME    = "arg_full_name";
    private static final String ARG_STUDENT_CODE = "arg_student_code";
    private static final int    QR_PX            = 700;

    private FragmentQrBinding binding;
    private String studentName;
    private String studentCode;

    // ── Factory ───────────────────────────────────────────────

    public static QrFragment newInstance(String fullName, String studentCode) {
        QrFragment f   = new QrFragment();
        Bundle     args = new Bundle();
        args.putString(ARG_FULL_NAME,    fullName    != null ? fullName    : "");
        args.putString(ARG_STUDENT_CODE, studentCode != null ? studentCode : "");
        f.setArguments(args);
        return f;
    }

    // ── Lifecycle ─────────────────────────────────────────────

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        studentName = (args != null)
                ? args.getString(ARG_FULL_NAME,    MockHelper.getMockFullName())
                : MockHelper.getMockFullName();
        studentCode = (args != null)
                ? args.getString(ARG_STUDENT_CODE, MockHelper.getMockStudentCode())
                : MockHelper.getMockStudentCode();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQrBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applyWindowInsets();
        bindHeader();
        renderQrCode();
        setupClickListeners();
        registerHardwareBack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Status bar inset ──────────────────────────────────────

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.toolbarContainer,
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(v.getPaddingLeft(), bars.top,
                            v.getPaddingRight(), v.getPaddingBottom());
                    return WindowInsetsCompat.CONSUMED;
                });
    }

    // ── UI ────────────────────────────────────────────────────

    private void bindHeader() {
        binding.tvQrStudentName.setText(studentName);
        binding.tvQrStudentCode.setText(studentCode);
    }

    private void renderQrCode() {
        Bitmap qr = generateQrBitmap(studentCode, QR_PX);
        if (qr != null) {
            binding.ivQrCode.setImageBitmap(qr);
        } else {
            Toast.makeText(requireContext(),
                    "Không thể tạo mã QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        // FIX A: pop back stack DIRECTLY — no dispatcher → no infinite loop
        binding.btnBack.setOnClickListener(v -> popBack());

        binding.btnCopyCode.setOnClickListener(v -> copyCodeToClipboard());

        binding.btnSaveQr.setOnClickListener(v -> {
            Bitmap bmp = generateQrBitmap(studentCode, QR_PX);
            if (bmp != null) saveQrToGallery(bmp);
        });

        binding.btnShareQr.setOnClickListener(v -> {
            Bitmap bmp = generateQrBitmap(studentCode, QR_PX);
            if (bmp != null) shareQrBitmap(bmp);
        });
    }

    /**
     * FIX A: Register hardware/gesture back.
     * The callback disables itself BEFORE popping so it cannot
     * be called twice.
     */
    private void registerHardwareBack() {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        setEnabled(false); // disable self FIRST to prevent re-entry
                        popBack();
                    }
                });
    }

    /** Single pop method used by both the button and the callback. */
    private void popBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    // ── QR generation ─────────────────────────────────────────

    @Nullable
    private Bitmap generateQrBitmap(String content, int sizePx) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx);
            return bitMatrixToBitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    private Bitmap bitMatrixToBitmap(@NonNull BitMatrix matrix) {
        int w = matrix.getWidth(), h = matrix.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                pixels[y * w + x] = matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
        return bmp;
    }

    // ── Actions ───────────────────────────────────────────────

    private void copyCodeToClipboard() {
        ClipboardManager cm = (ClipboardManager)
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("MSSV", studentCode));
            Toast.makeText(requireContext(),
                    "Đã sao chép: " + studentCode, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveQrToGallery(@NonNull Bitmap bitmap) {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_" + studentCode + ".png");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cv.put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/UTC2");
        }
        Uri uri = requireContext().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        if (uri == null) {
            Toast.makeText(requireContext(), "Lưu thất bại", Toast.LENGTH_SHORT).show();
            return;
        }
        try (OutputStream out =
                     requireContext().getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(requireContext(),
                    "Đã lưu ảnh QR vào thư viện", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * FIX B: Share works ONLY after FileProvider is declared in
     * AndroidManifest.xml and res/xml/file_provider_paths.xml exists.
     * See class Javadoc above for the exact XML snippets to add.
     */
    private void shareQrBitmap(@NonNull Bitmap bitmap) {
        try {
            File cacheDir = new File(requireContext().getCacheDir(), "images");
            //noinspection ResultOfMethodCallIgnored
            cacheDir.mkdirs();
            File file = new File(cacheDir, "qr_share.png");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    file
            );
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, "Mã QR sinh viên: " + studentCode);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Chia sẻ mã QR"));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi khi chia sẻ", Toast.LENGTH_SHORT).show();
        }
    }
}