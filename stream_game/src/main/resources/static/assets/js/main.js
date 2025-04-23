// JavaScript 文件 (assets/js/main.js)
document.addEventListener('DOMContentLoaded', function() {
    // 检查用户登录状态并更新导航栏
    checkLoginStatus();

    // 全局API基础URL
    const API_BASE_URL = 'http://localhost:8080/api';

    // 页面过渡逻辑
    const links = document.querySelectorAll('a');
    links.forEach(link => {
        link.addEventListener('click', function(e) {
            if (this.href && !this.href.includes('#')) {
                e.preventDefault();
                document.body.classList.add('fade-out');
                setTimeout(() => {
                    window.location.href = this.href;
                }, 300);
            }
        });
    });

    // 搜索功能
    const searchInput = document.getElementById('search-input');
    if (searchInput) {
        const gameCards = document.querySelectorAll('.game-card');
        
        // 实时搜索功能
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            
            gameCards.forEach(card => {
                const title = card.querySelector('.game-title').textContent.toLowerCase();
                card.style.display = title.includes(searchTerm) ? 'block' : 'none';
            });
        });
    
        // 阻止表单默认提交行为
        const searchForm = document.getElementById('search-form');
        if (searchForm) {
            searchForm.addEventListener('submit', function(e) {
                e.preventDefault();
            });
        }
    }
    
    // 轮播图逻辑
    const carouselContainer = document.querySelector('.carousel-container');
    if(carouselContainer) {
        const slides = document.querySelectorAll('.slide');
        const indicators = document.querySelectorAll('.indicator');
        const prevBtn = document.querySelector('.prev');
        const nextBtn = document.querySelector('.next');
        let currentIndex = 0;
        let autoPlay = true;
        let interval = 5000;
    
        // 切换幻灯片
        function showSlide(index) {
            slides.forEach(slide => slide.classList.remove('active'));
            indicators.forEach(indicator => indicator.classList.remove('active'));
            
            currentIndex = (index + slides.length) % slides.length;
            slides[currentIndex].classList.add('active');
            indicators[currentIndex].classList.add('active');
        }
    
        // 自动播放
        let timer = setInterval(() => {
            if(autoPlay) showSlide(currentIndex + 1);
        }, interval);
    
        // 按钮事件
        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                autoPlay = false;
                clearInterval(timer);
                showSlide(currentIndex - 1);
            });
        }
    
        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                autoPlay = false;
                clearInterval(timer);
                showSlide(currentIndex + 1);
            });
        }
    
        // 指示器点击
        indicators.forEach((indicator, index) => {
            indicator.addEventListener('click', () => {
                autoPlay = false;
                clearInterval(timer);
                showSlide(index);
            });
        });
    
        // 悬停暂停
        carouselContainer.addEventListener('mouseenter', () => autoPlay = false);
        carouselContainer.addEventListener('mouseleave', () => {
            autoPlay = true;
            clearInterval(timer);
            timer = setInterval(() => showSlide(currentIndex + 1), interval);
        });
    
        // 触摸滑动（移动端支持）
        let touchStartX = 0;
        carouselContainer.addEventListener('touchstart', e => {
            touchStartX = e.touches[0].clientX;
        });
    
        carouselContainer.addEventListener('touchend', e => {
            const touchEndX = e.changedTouches[0].clientX;
            const diff = touchStartX - touchEndX;
            if(Math.abs(diff) > 50) {
                diff > 0 ? showSlide(currentIndex + 1) : showSlide(currentIndex - 1);
            }
        });
    }

    // 购物车逻辑
    if (location.search.includes('reset=1')) {
        localStorage.removeItem('cart');
    }
    let cart = JSON.parse(localStorage.getItem('cart')) || {
        count: 0,
        items: [],
        total: 0
    };
    const emptyCart = document.querySelector('.empty-cart');
    const cartHeader = document.querySelector('.cart-header');
    const cartTotal = document.querySelector('.cart-total');
                    
    // 统一更新购物车显示
    function updateCartDisplay() {
        // 更新计数器
        document.querySelectorAll('.cart-counter').forEach(counter => {
            counter.textContent = cart.count;
        });
        
        // 更新总价
        document.querySelectorAll('.cart-total h3').forEach(totalElement => {
            totalElement.textContent = `总计：¥ ${cart.total.toFixed(2)}`;
        });
        
        // 更新购物车商品列表
        renderCartItems();
    }

    // 加入购物车逻辑
    document.querySelectorAll('.buy-btn').forEach(button => {
        button.addEventListener('click', function() {
            const gameCard = this.closest('.game-card');
            const gameInfo = {
                id: gameCard.dataset.gameId,
                title: gameCard.querySelector('.game-title').textContent,
                price: parseFloat(gameCard.querySelector('.price').textContent.replace(/[^0-9.]/g, ''))
            };

            // 添加到购物车
            cart.count++;
            cart.items.push(gameInfo);
            cart.total += gameInfo.price;
            localStorage.setItem('cart', JSON.stringify(cart));
            
            // 飞入动画
            const flyItem = createFlyItem(this, gameCard);
            animateToCart(flyItem, () => {
                updateCartDisplay();
            });
        });
    });

    // 移除商品逻辑
    document.body.addEventListener('click', function(e) {
        if (e.target.classList.contains('remove-btn')) {
            const item = e.target.closest('.cart-item');
            const itemId = item.dataset.itemId;
            
            // 从购物车移除
            const removedItem = cart.items.find(i => i.id === itemId);
            if (removedItem) {
                cart.count--;
                cart.total -= removedItem.price;
                cart.items = cart.items.filter(i => i.id !== itemId);
                localStorage.setItem('cart', JSON.stringify(cart));
                
                // 移除动画
                item.classList.add('removing');
                setTimeout(() => {
                    item.remove();
                    updateCartDisplay();
                }, 400);
            }
        }
    });

    // 初始化显示
    updateCartDisplay();

    // 生成购物车列表
    function renderCartItems() {
        const container = document.querySelector('.cart-items');
        if (!container) return;

        container.innerHTML = cart.items.map(item => `
            <div class="cart-item" data-item-id="${item.id}">
                <img src="${item.id}.jpg">
                <div class="item-info">
                    <h3>${item.title}</h3>
                    <p>¥ ${item.price.toFixed(2)}</p>
                </div>
                <button class="remove-btn">移除</button>
            </div>
        `).join('');
        
        // 显示/隐藏空购物车提示
        document.querySelectorAll('.empty-cart').forEach(empty => {
            empty.style.display = cart.count > 0 ? 'none' : 'block';
        });
    }

    // 结算功能
    const checkoutBtn = document.querySelector('.checkout-btn');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', function(e) {
            // 检查购物车是否为空
            if (cart.count === 0) {
                e.preventDefault();
                alert('购物车为空，请先添加商品');
                return;
            }
            
            // 这里可以添加与后端API的交互，提交订单
            // 但目前使用简单的页面跳转
        });
    }

    // 工具函数
    function createFlyItem(button, gameCard) {
        // 创建飞行元素
        const flyItem = document.createElement('div');
        const imgSrc = gameCard.querySelector('img').src;
        
        // 基础样式
        flyItem.style.cssText = `
            position: fixed;
            width: 80px;
            height: 50px;
            background: url('${imgSrc}');
            background-size: cover;
            border-radius: 4px;
            transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
            pointer-events: none;
            z-index: 9999;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            transform-origin: center;
        `;

        // 初始位置（按钮中心点）
        const buttonRect = button.getBoundingClientRect();
        flyItem.style.left = `${buttonRect.left + buttonRect.width/2 - 40}px`;
        flyItem.style.top = `${buttonRect.top + buttonRect.height/2 - 25}px`;
        
        return flyItem;
    }
    
    function animateToCart(flyItem, callback) {
        // 添加到DOM
        document.body.appendChild(flyItem);

        // 获取购物车位置
        const cartLink = document.querySelector('[href="cart.html"]');
        if (!cartLink) {
            if (callback) callback();
            return;
        }
        
        const cartRect = cartLink.getBoundingClientRect();
        const targetX = cartRect.left + cartRect.width/2 - 40;
        const targetY = cartRect.top + cartRect.height/2 - 25;

        // 第一阶段动画：飞向购物车
        flyItem.style.transform = `
            translate(${targetX - parseFloat(flyItem.style.left)}px, 
                      ${targetY - parseFloat(flyItem.style.top)}px)
            scale(0.5) 
            rotate(360deg)
        `;
        flyItem.style.opacity = '0.5';

        // 第二阶段动画：微调效果
        setTimeout(() => {
            flyItem.style.transition = 'all 0.2s ease-out';
            flyItem.style.transform += 'scale(0)';
            flyItem.style.opacity = '0';
            
            setTimeout(() => {
                flyItem.remove();
                if (callback) callback();
            }, 200);
        }, 600);
    }

    // API交互功能
    function fetchGames() {
        fetch(`${API_BASE_URL}/games`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('网络错误');
                }
                return response.json();
            })
            .then(games => {
                console.log('获取到游戏列表:', games);
                // 这里可以添加动态渲染游戏列表的代码
            })
            .catch(error => {
                console.error('获取游戏列表失败:', error);
            });
    }

    // 用户登录功能
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            if (!username || !password) {
                alert('请输入用户名和密码');
                return;
            }
            
            fetch(`${API_BASE_URL}/players/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('登录失败');
                }
                return response.json();
            })
            .then(player => {
                console.log('登录成功:', player);
                localStorage.setItem('currentPlayer', JSON.stringify(player));
                window.location.href = 'index.html';
            })
            .catch(error => {
                console.error('登录错误:', error);
                alert('登录失败，请检查用户名和密码');
            });
        });
    }
});

// 检查登录状态并更新导航栏
function checkLoginStatus() {
    const currentPlayer = JSON.parse(localStorage.getItem('currentPlayer'));
    const navLinks = document.querySelector('.nav-links');
    
    if (currentPlayer && navLinks) {
        // 找到登录链接元素
        const loginLink = Array.from(navLinks.querySelectorAll('a')).find(link => 
            link.textContent.includes('登录') || link.href.includes('login.html')
        );
        
        if (loginLink) {
            // 替换登录链接为用户信息
            loginLink.innerHTML = `${currentPlayer.username} <span class="logout-btn">[退出]</span>`;
            loginLink.href = "#";
            loginLink.classList.remove('active');
            
            // 添加退出功能
            const logoutBtn = loginLink.querySelector('.logout-btn');
            if (logoutBtn) {
                logoutBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    
                    // 清除登录状态
                    localStorage.removeItem('currentPlayer');
                    alert('您已成功退出登录');
                    
                    // 刷新页面
                    window.location.reload();
                });
            }
        }
    }
}