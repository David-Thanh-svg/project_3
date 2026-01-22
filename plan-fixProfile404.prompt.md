Plan: Sửa lỗi 404 cho /profile/{id}

Tóm tắt nhanh — Vì `profile.html` đã có sẵn, nhưng dự án hiện thiếu handler trả view cho `GET /profile/{id}`. Thêm một handler (controller) sẽ truy vấn `Userprofile` từ `UserprofileRepository`, đặt vào `Model` dưới tên `user`, rồi trả view `profile`.

### Steps
1. Tạo/ cập nhật controller: thêm `@GetMapping("/profile/{id}")` trong `src/main/java/com/example/controller/UserController.java` (hoặc `ProfileController` mới) để:
   - Inject `UserprofileRepository` (từ `src/main/java/com/example/repository/UserprofileRepository.java`).
   - Lấy `Optional<Userprofile>` bằng `findById(id)`.
   - Nếu tồn tại, `model.addAttribute("user", user)` và `return "profile"`.
   - Nếu không, trả về `404` hoặc redirect về trang tổng (ví dụ `redirect:/`).
2. Sử dụng entity hiện có: dùng `com.example.entity.Userprofile` (file: `src/main/java/com/example/entity/Userprofile.java`) làm model để Thymeleaf `profile.html` dùng các thuộc tính `user.id`, `user.username`, `user.email`, `user.bio`.
3. Kiểm tra `Avatar` URL: `profile.html` dùng `th:src="@{/avatar/object/{id}(id=${user.id})}"` — endpoint này có trong `src/main/java/com/example/controller/AvatarController.java` (`@GetMapping("/object/{userId}")`) và trả redirect đến MinIO. Không thay đổi nếu hoạt động; nếu không tìm thấy avatar, `AvatarController` trả 404 — trong template xử lý `onerror` để hiển thị avatar mặc định.
4. Xác nhận cấu hình bảo mật: `src/main/java/com/example/config/SecurityConfig.java` hiện đã `permitAll()` cho `/profile/**` và `/avatar/**`. Nếu bạn dùng Keycloak/OAuth khác, kiểm tra thêm rules (nếu lỗi 401/403 thay cho 404).
5. Khởi động lại ứng dụng và kiểm tra: mở `http://localhost:8082/profile/1` (hoặc port trong `application.yml`/`application.properties`). Nếu vẫn lỗi, thu thập log (stacktrace / Spring log) để debug tiếp.

### Further Considerations
1. Trường hợp dữ liệu: Nếu DB chưa có user với id=1, sẽ vẫn báo 404 — muốn fallback hiển thị user giả hay redirect? Option A: hiển thị trang "Không tìm thấy" (hiện hành). Option B: redirect về `/`.
2. DTO vs Entity: hiện `profile.html` dùng tên `user` trỏ tới `Userprofile` — nếu bạn muốn tách view model, tạo `UserProfileDto` và map trước khi thêm vào `Model`.
3. Trang avatar: nếu muốn `<img>` luôn trả URL (thay vì redirect), có thể thêm endpoint `GET /avatar/user/{id}` trả view fragment (đã có) hoặc trả binary stream.

Bạn muốn tôi viết nội dung `@GetMapping` cụ thể (chỉ plan hay luôn tạo file/ chỉnh sửa giúp)? Tôi sẽ chờ xác nhận (với lựa chọn A/B cho fallback) rồi chuyển sang bước thực hiện.
