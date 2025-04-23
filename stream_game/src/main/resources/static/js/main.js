document.addEventListener('DOMContentLoaded', function() {
    // 轮播图功能
    const slides = document.querySelectorAll('.slide');
    const indicators = document.querySelectorAll('.indicator');
    const prevBtn = document.querySelector('.prev');
    const nextBtn = document.querySelector('.next');
    let currentSlide = 0;
    let slideInterval;

    // 初始化轮播图
    function initCarousel() {
        if (slides.length === 0) return;
        
        slides[0].classList.add('active');
        if (indicators.length > 0) {
            indicators[0].classList.add('active');
        }
        
        startSlideTimer();
    }

    // 切换到指定幻灯片
    function goToSlide(index) {
        // 移除当前活动幻灯片的类
        slides[currentSlide].classList.remove('active');
        if (indicators.length > 0) {
            indicators[currentSlide].classList.remove('active');
        }
        
        // 设置新的当前幻灯片索引
        currentSlide = (index + slides.length) % slides.length;
        
        // 添加新活动幻灯片的类
        slides[currentSlide].classList.add('active');
        if (indicators.length > 0) {
            indicators[currentSlide].classList.add('active');
        }
    }

    // 下一张幻灯片
    function nextSlide() {
        goToSlide(currentSlide + 1);
    }

    // 上一张幻灯片
    function prevSlide() {
        goToSlide(currentSlide - 1);
    }

    // 开始幻灯片计时器
    function startSlideTimer() {
        stopSlideTimer();
        slideInterval = setInterval(nextSlide, 5000); // 5秒切换一次
    }

    // 停止幻灯片计时器
    function stopSlideTimer() {
        if (slideInterval) {
            clearInterval(slideInterval);
        }
    }

    // 绑定轮播图事件
    if (prevBtn) prevBtn.addEventListener('click', function() {
        prevSlide();
        startSlideTimer(); // 重置计时器
    });

    if (nextBtn) nextBtn.addEventListener('click', function() {
        nextSlide();
        startSlideTimer(); // 重置计时器
    });

    // 为指示器添加点击事件
    indicators.forEach((indicator, index) => {
        indicator.addEventListener('click', function() {
            goToSlide(index);
            startSlideTimer(); // 重置计时器
        });
    });

    // 鼠标悬停时停止自动播放，离开时继续
    const carouselContainer = document.querySelector('.carousel-container');
    if (carouselContainer) {
        carouselContainer.addEventListener('mouseenter', stopSlideTimer);
        carouselContainer.addEventListener('mouseleave', startSlideTimer);
    }

    // 初始化轮播图
    initCarousel();

    // 搜索表单提交
    const searchForm = document.getElementById('search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const searchInput = document.querySelector('.search-input');
            if (searchInput && searchInput.value.trim() !== '') {
                alert('搜索: ' + searchInput.value);
                // 这里可以添加实际的搜索功能
                // window.location.href = '/search?q=' + encodeURIComponent(searchInput.value);
            }
        });
    }

    // 购买按钮点击事件
    const buyButtons = document.querySelectorAll('.buy-btn');
    buyButtons.forEach(button => {
        button.addEventListener('click', function() {
            const gameTitle = this.closest('.game-card').querySelector('.game-title').textContent;
            alert('已将 "' + gameTitle + '" 添加到购物车！');
            
            // 更新购物车数量
            updateCartCount(1);
        });
    });

    // 更新购物车数量
    function updateCartCount(add) {
        const cartCounter = document.querySelector('.cart-counter');
        if (cartCounter) {
            let count = parseInt(cartCounter.textContent);
            count += add;
            cartCounter.textContent = count;
            
            // 如果购物车数量为0，则隐藏计数器
            if (count <= 0) {
                cartCounter.style.display = 'none';
            } else {
                cartCounter.style.display = 'inline-block';
            }
        }
    }

    // 如果页面有返回顶部按钮，添加事件处理
    const backToTop = document.querySelector('.back-to-top');
    if (backToTop) {
        window.addEventListener('scroll', function() {
            if (window.pageYOffset > 300) {
                backToTop.style.display = 'block';
            } else {
                backToTop.style.display = 'none';
            }
        });
        
        backToTop.addEventListener('click', function() {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }
}); 