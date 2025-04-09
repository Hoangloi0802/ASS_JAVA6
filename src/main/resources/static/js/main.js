(function () {
  "use strict";

  // Utility functions
  const utils = {
    toggleClass: (element, className, condition) => element?.classList.toggle(className, condition),
    formatVND: (number) => new Intl.NumberFormat('vi-VN', { style: 'decimal', minimumFractionDigits: 0, maximumFractionDigits: 0 }).format(number) + ' VNĐ',
    fetchData: async (url, errorMessage) => {
      const response = await fetch(url);
      if (!response.ok) throw new Error(errorMessage);
      return response.json();
    },
    showAlert: (title, text, icon, options = {}) => Swal.fire({ title, text, icon, ...options })
  };

  // Core functionality
  const app = {
    // Scroll handling
    handleScroll() {
      const scrollY = window.scrollY > 100;
      utils.toggleClass(document.body, 'scrolled', scrollY);
      utils.toggleClass(document.querySelector('.scroll-top'), 'active', scrollY);
    },

    // Swiper initialization
    initSwipers() {
      const thumbsSwiper = new Swiper('.thumbs-slider', {
        loop: true,
        spaceBetween: 10,
        slidesPerView: 4,
        freeMode: true,
        watchSlidesProgress: true,
        centeredSlides: true,
        slideToClickedSlide: true,
        breakpoints: { 0: { slidesPerView: 4 }, 768: { slidesPerView: 6 }, 992: { slidesPerView: 8 } }
      });

      new Swiper('.main-slider', {
        loop: true,
        spaceBetween: 10,
        navigation: { nextEl: '.swiper-button-next', prevEl: '.swiper-button-prev' },
        thumbs: { swiper: thumbsSwiper }
      });
    },

    // Cart display update
    updateCartDisplay(data) {
      document.getElementById('subtotal').textContent = utils.formatVND(data.subtotal);
      document.getElementById('voucher-discount').textContent = utils.formatVND(data.discount);
      document.getElementById('total-amount').textContent = utils.formatVND(data.total);
    },

    // Search handling
    initSearch() {
      const searchForm = document.getElementById('searchForm');
      const searchInput = document.getElementById('searchInput');
      const searchButton = document.getElementById('searchButton');
      const inputWrapper = document.querySelector('.input-wrapper');
      let expanded = false;

      searchButton?.addEventListener('click', () => {
        if (!expanded) {
          inputWrapper.classList.add('expanded');
          searchInput.focus();
          expanded = true;
        } else if (searchInput.value.trim()) {
          searchForm.submit();
        } else {
          searchInput.focus();
        }
      });

      searchInput?.addEventListener('blur', () => {
        if (!searchInput.value.trim()) {
          inputWrapper.classList.remove('expanded');
          expanded = false;
        }
        document.getElementById('searchButton').addEventListener('click', function () {
          document.getElementById('searchForm').submit();
        });
      });
      
    },

    // Event listeners setup
    setupEventListeners() {
      window.addEventListener('scroll', this.handleScroll);
      window.addEventListener('load', () => { this.handleScroll(); this.initSwipers(); });

      const mobileNavToggle = document.querySelector('.mobile-nav-toggle');
      mobileNavToggle?.addEventListener('click', () => {
        document.body.classList.toggle('mobile-nav-active');
        mobileNavToggle.classList.toggle('bi-list');
        mobileNavToggle.classList.toggle('bi-x');
      });

      document.querySelector('.scroll-top')?.addEventListener('click', () => 
        window.scrollTo({ top: 0, behavior: 'smooth' })
      );

      document.querySelector('#preloader')?.addEventListener('load', (e) => e.target.remove());
    },

    // DOMContentLoaded handler
    initDOMContentLoaded() {
      // Password toggle
      document.querySelectorAll('.toggle-password').forEach(button => {
        button.addEventListener('click', () => {
          const input = document.getElementById(button.dataset.target);
          const icon = button.querySelector('i');
          input.type = input.type === 'password' ? 'text' : 'password';
          icon.classList.toggle('fa-eye', input.type === 'password');
          icon.classList.toggle('fa-eye-slash', input.type === 'text');
        });
      });

      // Cart updates
      document.querySelectorAll('.update-cart').forEach(button => {
        button.addEventListener('click', async () => {
          const { productId, change } = button.dataset;
          const quantityInput = button.parentElement.querySelector('.quantity-display');
          const newQuantity = parseInt(quantityInput.value) + parseInt(change);

          if (newQuantity < 1) return;

          quantityInput.value = newQuantity;
          try {
            const data = await utils.fetchData(`/cart/update-cart?productId=${productId}&quantity=${newQuantity}`, 'Failed to update cart');
            this.updateCartDisplay(data);
          } catch (error) {
            console.error('Cart update error:', error);
            utils.showAlert('Lỗi!', 'Không thể cập nhật giỏ hàng!', 'error');
          }
        });
      });

      // Cart removal
      document.querySelectorAll('.remove-cart-item').forEach(button => {
        button.addEventListener('click', async () => {
            const productId = button.dataset.productId;
    
            // Hiển thị hộp thoại xác nhận
            const result = await utils.showAlert('Bạn có chắc chắn?', 'Sản phẩm sẽ bị xóa khỏi giỏ hàng!', 'warning', {
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Xóa',
                cancelButtonText: 'Hủy'
            });
    
            if (!result.isConfirmed) return; // Nếu người dùng bấm "Hủy" thì dừng lại
    
            try {
                // Gửi request xóa sản phẩm
                const response = await fetch(`/cart?action=remove&productId=${productId}`, { method: 'GET' });
    
                if (!response.ok) {
                    throw new Error('Không thể xóa sản phẩm.');
                }
    
                // Hiển thị thông báo thành công
                await utils.showAlert('Đã xóa!', 'Sản phẩm đã bị xóa khỏi giỏ hàng.', 'success', { 
                    timer: 1500, 
                    showConfirmButton: false 
                });
    
                // Reload lại trang
                location.reload();
            } catch (error) {
                console.error('Lỗi xóa sản phẩm:', error);
                utils.showAlert('Lỗi!', 'Không thể xóa sản phẩm!', 'error');
            }
        });
    });
    

      // Voucher application
      document.querySelectorAll('.apply-voucher').forEach(button => {
        button.addEventListener('click', async () => {
          const voucherCode = button.dataset.code;
          try {
            const data = await utils.fetchData(`/cart/apply-voucher?voucherCode=${voucherCode}`, 'Failed to apply voucher');
            if (data.error) {
              document.getElementById('voucher-discount').textContent = utils.formatVND(0);
              utils.showAlert('Lỗi!', data.error, 'error');
            } else {
              this.updateCartDisplay(data);
              bootstrap.Modal.getInstance(document.getElementById('voucherModal'))?.hide();
              utils.showAlert('Thành công!', 'Voucher đã được áp dụng!', 'success', { timer: 1500, showConfirmButton: false });
            }
          } catch (error) {
            console.error('Voucher error:', error);
            utils.showAlert('Lỗi!', 'Có lỗi xảy ra khi áp dụng voucher!', 'error');
          }
        });
      });
   
      document.getElementById('submitPaymentBtn').addEventListener('click', async (event) => {
        event.preventDefault(); // Ngăn chặn form submit mặc định
    
        const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
        const amount = document.getElementById('total-amount').textContent.replace(/\D/g, ''); // Lấy số tiền tổng cộng
    
        if (paymentMethod === 'momo') {
            try {
                const response = await fetch(`/thanhtoan/momo?amount=${amount}`); // Dùng GET thay vì POST
                const data = await response.json();
    
                if (data.payUrl) {
                    window.location.href = data.payUrl; // Chuyển hướng đến MoMo
                } else {
                    utils.showAlert('Lỗi!', 'Thanh toán MoMo thất bại: ' + data.error, 'error');
                }
            } catch (error) {
                console.error('Lỗi thanh toán MoMo:', error);
                utils.showAlert('Lỗi!', 'Có lỗi xảy ra khi thanh toán qua MoMo!', 'error');
            }
        } else {
            document.getElementById('paymentForm').submit(); // Xử lý thanh toán thường
        }
    });
    
    },
    changeQuantity(change, inputId) {
      var input = document.getElementById(inputId);
      var maxQuantity = parseInt(input.getAttribute("max"));
  
      var currentValue = parseInt(input.value);
      var newValue = currentValue + change;
  
      if (newValue >= 1 && newValue <= maxQuantity) {
          input.value = newValue;
      }
  },
  

    
    // Global functions
     changeQuantity(change, inputId) {
      const input = document.getElementById(inputId);
      const maxQuantity = parseInt(input.getAttribute("max"));
      let newValue = parseInt(input.value) + change;
  
      // Kiểm tra giới hạn số lượng
      if (newValue >= 1 && newValue <= maxQuantity) {
          input.value = newValue;
      }
  
      // Cập nhật trạng thái của nút tăng/giảm
      const minusButton = input.parentElement.querySelector("button[onclick*='changeQuantity(-1']");
      const plusButton = input.parentElement.querySelector("button[onclick*='changeQuantity(1']");
  
      minusButton.disabled = (parseInt(input.value) <= 1);
      plusButton.disabled = (parseInt(input.value) >= maxQuantity);
  },

    openAddressModal() {
      new bootstrap.Modal(document.getElementById('addressModal')).show();
    },

    saveAddress() {
      document.getElementById('addressInput').value = document.getElementById('modalAddressDetail').value;
      bootstrap.Modal.getInstance(document.getElementById('addressModal')).hide();
    },

    previewImage(event) {
      const reader = new FileReader();
      reader.onload = () => document.getElementById('previewImg').src = reader.result;
      reader.readAsDataURL(event.target.files[0]);
    }
  };

  // Initialize
  const init = () => {
    app.setupEventListeners();
    app.initSearch();
    document.addEventListener('DOMContentLoaded', () => app.initDOMContentLoaded());
    Object.assign(window, {
      changeQuantity: app.changeQuantity,
      openAddressModal: app.openAddressModal,
      saveAddress: app.saveAddress,
      previewImage: app.previewImage
    });
  };

  init();
})();
