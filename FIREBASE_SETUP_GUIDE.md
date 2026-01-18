# Hướng dẫn cấu hình Firebase cho Expense Tracker

## Bước 1: Tạo Firebase Project mới

1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Đăng nhập bằng tài khoản Google của bạn
3. Nhấn "Create a project" hoặc "Add project"
4. Đặt tên project (ví dụ: "expense-tracker-[tên-của-bạn]")
5. Tắt Google Analytics nếu không cần thiết
6. Nhấn "Create project"

## Bước 2: Thêm Android App

1. Trong Firebase Console, nhấn biểu tượng Android
2. Điền thông tin:
   - **Android package name**: `com.ptit.expensetracker`
   - **App nickname**: Expense Tracker PTIT
   - **Debug signing certificate SHA-1**: (tùy chọn)
3. Nhấn "Register app"
4. Tải file `google-services.json` và thay thế file cũ trong thư mục `app/`

## Bước 3: Kích hoạt Authentication

1. Trong Firebase Console, vào **Authentication** > **Sign-in method**
2. Kích hoạt các phương thức đăng nhập:
   - **Email/Password**: Enable
   - **Google**: Enable (cần cấu hình OAuth)

### Cấu hình Google Sign-In:
1. Nhấn vào **Google** trong danh sách Sign-in providers
2. Enable Google Sign-In
3. Chọn Project support email
4. Tải file `google-services.json` mới (nếu có thay đổi)

## Bước 4: Kích hoạt Cloud Storage

1. Trong Firebase Console, vào **Storage**
2. Nhấn "Get started"
3. Chọn **Start in test mode** (cho development)
4. Chọn location gần nhất (ví dụ: asia-southeast1)
5. Nhấn "Done"

### Cấu hình Security Rules cho Storage:
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## Bước 5: Cập nhật cấu hình trong code

File `google-services.json` mới sẽ chứa:
- Project ID mới
- API Keys mới  
- Storage bucket mới
- OAuth client IDs mới

## Lưu ý quan trọng:

1. **Backup**: Sao lưu file `google-services.json` cũ trước khi thay thế
2. **Package Name**: Đảm bảo package name trong Firebase khớp với `applicationId` trong `build.gradle.kts`
3. **SHA-1**: Nếu sử dụng Google Sign-In, cần thêm SHA-1 fingerprint
4. **Security Rules**: Cập nhật rules cho production environment

## Kiểm tra cấu hình:

1. Clean và rebuild project
2. Chạy app và test các chức năng:
   - Đăng ký/đăng nhập
   - Upload/download file từ Storage
   - Đồng bộ dữ liệu

## Troubleshooting:

- Nếu gặp lỗi "google-services.json is missing": Đảm bảo file được đặt đúng trong thư mục `app/`
- Nếu Google Sign-In không hoạt động: Kiểm tra SHA-1 fingerprint và OAuth configuration
- Nếu Storage không hoạt động: Kiểm tra Security Rules và permissions