(function () {
  "use strict";

  /** Toggle class when scrolling **/
  function toggleScrolled() {
    document.body.classList.toggle('scrolled', window.scrollY > 100);
  }
  document.addEventListener('scroll', toggleScrolled);
  window.addEventListener('load', toggleScrolled);

  /** Mobile nav toggle **/
  const mobileNavToggleBtn = document.querySelector('.mobile-nav-toggle');
  if (mobileNavToggleBtn) {
    mobileNavToggleBtn.addEventListener('click', () => {
      document.body.classList.toggle('mobile-nav-active');
      mobileNavToggleBtn.classList.toggle('bi-list');
      mobileNavToggleBtn.classList.toggle('bi-x');
    });
  }

  /** Preloader removal **/
  const preloader = document.querySelector('#preloader');
  if (preloader) {
    window.addEventListener('load', () => preloader.remove());
  }

  /** Scroll to top **/
  const scrollTop = document.querySelector('.scroll-top');
  if (scrollTop) {
    function toggleScrollTop() {
      scrollTop.classList.toggle('active', window.scrollY > 100);
    }
    scrollTop.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));
    window.addEventListener('scroll', toggleScrollTop);
    window.addEventListener('load', toggleScrollTop);
  }

  /** Swiper Initialization after load **/
  window.addEventListener('load', () => {
    // Khởi tạo Thumbs Swiper
    const thumbsSwiper = new Swiper('.thumbs-slider', {
      loop: true,
      spaceBetween: 10,
      slidesPerView: 4,
      freeMode: true,
      watchSlidesProgress: true,
      centeredSlides: true,
      slideToClickedSlide: true,
      breakpoints: {
        0: {
          slidesPerView: 4,
        },
        768: {
          slidesPerView: 6,
        },
        992: {
          slidesPerView: 8,
        },
      },
    });

    // Khởi tạo Main Swiper
    const mainSwiper = new Swiper('.main-slider', {
      loop: true,
      spaceBetween: 10,
      navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
      },
      thumbs: {
        swiper: thumbsSwiper,
      },
    });
  });

})();


document.addEventListener('DOMContentLoaded', function () {
  const toggleButtons = document.querySelectorAll('.toggle-password');

  toggleButtons.forEach(button => {
    button.addEventListener('click', function () {
      const targetId = this.getAttribute('data-target');
      const input = document.getElementById(targetId);
      const icon = this.querySelector('i');

      if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
      } else {
        input.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
      }
    });
  });
});
const searchForm = document.getElementById('searchForm');
const searchInput = document.getElementById('searchInput');
const searchButton = document.getElementById('searchButton');
const inputWrapper = document.querySelector('.input-wrapper');

let expanded = false;




function previewImage(event) {
  var reader = new FileReader();
  reader.onload = function(){
    var output = document.getElementById('previewImg');
    output.src = reader.result;
  };
  reader.readAsDataURL(event.target.files[0]);
  
}


function changeQuantity(change, inputId) {
  let input = document.getElementById(inputId);
  let currentValue = parseInt(input.value);
  let newValue = currentValue + change;

  if (newValue >= 1) {
      input.value = newValue;
  }
}
document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll('.update-cart').forEach(button => {
      button.addEventListener('click', function () {
          let productId = this.getAttribute('data-product-id');
          let change = parseInt(this.getAttribute('data-change'));
          let inputField = this.closest('.d-flex')?.querySelector('.quantity-display');
          let priceDisplay = this.closest('.row')?.querySelector('.price-display');
          let unitPrice = parseInt(this.getAttribute('data-unit-price'));

          if (!inputField || !priceDisplay || isNaN(unitPrice)) return; // Kiểm tra nếu bị lỗi truy vấn

          let newQuantity = parseInt(inputField.value) + change;
          if (newQuantity >= 1) {
              inputField.value = newQuantity;
              priceDisplay.textContent = (unitPrice * newQuantity).toLocaleString() + ' VNĐ';

              fetch('/giohang/update', {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                  body: `productId=${productId}&quantity=${newQuantity}`
              })
          }
      });
  });

  // Xử lý xóa sản phẩm khỏi giỏ hàng
  document.querySelectorAll('.remove-cart-item').forEach(button => {
    button.addEventListener('click', function () {
        let productId = this.getAttribute('data-product-id');

        Swal.fire({
            title: "Bạn có chắc chắn?",
            text: "Sản phẩm sẽ bị xóa khỏi giỏ hàng!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: "#6c757d",
            confirmButtonText: "Xóa",
            cancelButtonText: "Hủy"
        }).then((result) => {
            if (result.isConfirmed) {
                fetch(`/giohang?action=remove&productId=${productId}`, {
                    method: 'GET'
                }).then(response => {
                    if (response.ok) {
                        Swal.fire({
                            title: "Đã xóa!",
                            text: "Sản phẩm đã bị xóa khỏi giỏ hàng.",
                            icon: "success",
                            timer: 1500,
                            showConfirmButton: false
                        }).then(() => {
                            location.reload();
                        });
                    } else {
                        Swal.fire("Lỗi!", "Không thể xóa sản phẩm. Vui lòng thử lại!", "error");
                    }
                });
            }
        });
    });
});
});
function openAddressModal() {
  var addressModal = new bootstrap.Modal(document.getElementById('addressModal'));
  addressModal.show();
}

function saveAddress() {
  var addressDetail = document.getElementById('modalAddressDetail').value;
  document.getElementById('addressInput').value = addressDetail;
  var addressModal = bootstrap.Modal.getInstance(document.getElementById('addressModal'));
  addressModal.hide();
}

function previewImage(event) {
  var reader = new FileReader();
  reader.onload = function() {
    var output = document.getElementById('previewImg');
    output.src = reader.result;
  };
  reader.readAsDataURL(event.target.files[0]);
}



