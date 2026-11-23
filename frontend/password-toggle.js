/* Nút con mắt hiện/ẩn mật khẩu, dùng chung cho các trang đăng nhập / đăng ký. */
(function () {
    "use strict";

    function doiTrangThai(input, nut) {
        const dangAn = input.type === "password";
        let viTriConTro = null;
        try {
            viTriConTro = input.selectionStart;
        } catch (e) {
            viTriConTro = null;
        }

        input.type = dangAn ? "text" : "password";

        nut.classList.toggle("showing", dangAn);
        nut.setAttribute("aria-pressed", String(dangAn));
        nut.setAttribute("aria-label", dangAn ? "Ẩn mật khẩu" : "Hiện mật khẩu");

        input.focus();
        if (viTriConTro !== null) {
            try {
                input.setSelectionRange(viTriConTro, viTriConTro);
            } catch (e) {
                // Một số trình duyệt không cho đặt vị trí con trỏ, bỏ qua.
            }
        }
    }

    document.addEventListener("click", function (event) {
        const nut = event.target.closest(".toggle-password");
        if (!nut) return;

        event.preventDefault();
        const input = nut.closest(".input-password").querySelector("input");
        if (input) doiTrangThai(input, nut);
    });
})();
