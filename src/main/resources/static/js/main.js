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

searchButton.addEventListener('click', function () {
  if (!expanded) {
    inputWrapper.classList.add('expanded');
    searchInput.focus();
    expanded = true;
  } else {
    if (searchInput.value.trim() !== '') {
      searchForm.submit();
    } else {
      searchInput.focus();
    }
  }
});

searchInput.addEventListener('blur', function () {
  if (searchInput.value.trim() === '') {
    inputWrapper.classList.remove('expanded');
    expanded = false;
  }
});
function previewImage(event) {
  var reader = new FileReader();
  reader.onload = function(){
    var output = document.getElementById('previewImg');
    output.src = reader.result;
  };
  reader.readAsDataURL(event.target.files[0]);
  
}


