(function() {
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
  
    /** Init Swiper Sliders **/
    function initSwiper() {
      document.querySelectorAll(".init-swiper").forEach(swiperElement => {
        const config = JSON.parse(swiperElement.querySelector(".swiper-config").innerHTML.trim());
        new Swiper(swiperElement, config);
      });
    }
    window.addEventListener("load", initSwiper);
  })();