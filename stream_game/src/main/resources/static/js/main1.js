// JavaScript 文件 (assets/js/main.js)
document.addEventListener('DOMContentLoaded', function() {
    // 全局API基础URL
    const API_BASE_URL = 'http://localhost:8080/api';

    // 全局变量
    let cart = JSON.parse(localStorage.getItem('cart')) || {
        count: 0,
        items: [],
        total: 0
    };

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
    const emptyCart = document.querySelector('.empty-cart');
    const cartHeader = document.querySelector('.cart-header');
    const cartTotal = document.querySelector('.cart-total');
                    
    // 更新购物车显示
    function updateCartDisplay() {
        try {
            // 更新购物车计数器
        document.querySelectorAll('.cart-counter').forEach(counter => {
            counter.textContent = cart.count;
        });
        
            // 更新购物车总价
        document.querySelectorAll('.cart-total h3').forEach(totalElement => {
            totalElement.textContent = `总计：¥ ${cart.total.toFixed(2)}`;
        });
        
            // 更新购物车列表
            const cartItems = document.querySelector('.cart-items');
            if (cartItems) {
                cartItems.innerHTML = cart.items.map(item => {
                    // 处理图片路径
                    let imgSrc;
                    if (item.banner) {
                        if (item.banner.startsWith('data:')) {
                            imgSrc = item.banner;
                        } else if (item.banner.startsWith('assets/')) {
                            imgSrc = item.banner;
                        } else {
                            imgSrc = item.banner;
                        }
                    } else {
                        // 如果没有banner，使用游戏标题作为图片文件名
                        imgSrc = `${item.title}.jpg`;
                    }

                    return `
                        <div class="cart-item" data-item-id="${item.id}">
                            <img src="${imgSrc}" alt="${item.title}">
                            <div class="item-info">
                                <h3>${item.title}</h3>
                                <p>¥${item.price.toFixed(2)}</p>
                            </div>
                            <button class="remove-btn">移除</button>
                        </div>
                    `;
                }).join('') || '<div class="empty-cart">购物车是空的</div>';
            }
        } catch (error) {
            console.error('更新购物车显示失败:', error);
        }
    }

    // 加入购物车逻辑
    document.querySelectorAll('.buy-btn').forEach(button => {
        button.addEventListener('click', function() {
            const gameCard = this.closest('.game-card');
            if (!gameCard) return;
            
            const gameInfo = {
                id: gameCard.dataset.gameId,
                title: gameCard.querySelector('.game-title')?.textContent || '未知游戏',
                price: parseFloat(gameCard.querySelector('.price')?.textContent.replace(/[^0-9.]/g, '') || '0')
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


// ================= 导航栏动态控制 =================
function updateNavbar() {
    const navLinks = document.getElementById('nav-links');
    if (!navLinks) return; // 如果元素不存在，直接返回
    
    const currentUser = JSON.parse(localStorage.getItem('currentPlayer'));
    
    // 基础链接
    let linksHtml = `
        <a href="index.html" class="${location.pathname.endsWith('index.html') ? 'active' : ''}">商城</a>
        <a href="cart.html" class="${location.pathname.endsWith('cart.html') ? 'active' : ''}">购物车 <span class="cart-counter">0</span></a>
        <a href="about.html" class="${location.pathname.endsWith('about.html') ? 'active' : ''}">关于</a>
    `;

    // 根据登录状态显示不同内容
    if (currentUser) {
        linksHtml += `
            <a href="profile.html" class="${location.pathname.endsWith('profile.html') ? 'active' : ''}">个人中心</a>
            <a href="#" id="logout-btn">退出</a>
        `;
    } else {
        linksHtml += `<a href="login.html" class="${location.pathname.endsWith('login.html') ? 'active' : ''}">登录/注册</a>`;
    }

    navLinks.innerHTML = linksHtml;
    
    // 绑定退出事件
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            sessionStorage.removeItem('currentPlayer');
            window.location.href = 'login.html';
        });
    }
}



// 初始化调用
document.addEventListener('DOMContentLoaded', updateNavbar);

// 动态设置导航栏激活状态
document.addEventListener('DOMContentLoaded', function() {
    // 获取当前路径（处理参数和大小写）
    const currentPage = window.location.pathname.split('/').pop().toLowerCase();
    const currentPath = currentPage.split('?')[0]; // 移除查询参数

    // 遍历所有导航链接
    document.querySelectorAll('.nav-links a').forEach(link => {
        const linkHref = link.getAttribute('href').toLowerCase();
        const linkPath = linkHref.split('/').pop().split('?')[0];

        // 清除所有激活状态
        link.classList.remove('active');

        // 匹配路径（排除首页特殊处理）
        if (linkPath === currentPath) {
            link.classList.add('active');
        }
    });

    // 特殊处理首页激活状态
    if (currentPath === 'index.html' || currentPath === '') {
        document.querySelector('.nav-links a[href="index.html"]').classList.add('active');
    }
});

// 在文件顶部添加
const UserGameManager = {
    getUploadedGames: () => GameManager.getAllGames(),
    
    renderUserGames: function() {
        const container = document.getElementById('userGamesContainer');
        if (!container) return;
        
        const games = this.getUploadedGames();
        
        container.innerHTML = games.length ? 
            games.map(game => `
                <div class="game-card" data-game-id="${game.id}">
                        <img src="${game.banner}" alt="${game.title}">
                    <h3 class="game-title">${game.title}</h3>
                    <p class="price">¥${game.price}</p>
                        <button class="buy-btn">加入购物车</button>
                </div>
            `).join('') : 
            '<div class="empty-tip">期待您的推荐！</div>';
        
        // 绑定事件
        container.querySelectorAll('.buy-btn').forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault(); // 阻止默认行为
                const gameCard = button.closest('.game-card');
                if (gameCard) {
                    const gameId = gameCard.dataset.gameId;
                    CartManager.addToCart(gameId);
                }
            });
        });
    }
};

// 添加游戏管理器
const GameManager = {
    uploadGame: function(gameData) {
        return new Promise(async (resolve, reject) => {
            try {
                // 压缩图片
                if (gameData.banner) {
                    gameData.banner = await this.compressImage(gameData.banner);
                }
                
                // 获取已上传的游戏列表
                const uploadedGames = this.getUploadedGames();
                
                // 生成新的游戏ID
                const newGameId = 'game_' + Date.now();
                
                // 创建新的游戏对象
                const newGame = {
                    id: newGameId,
                    ...gameData,
                    uploadDate: new Date().toISOString()
                };
                
                // 添加到已上传游戏列表
                uploadedGames.push(newGame);
                
                // 保存到localStorage
                await this.saveGames(uploadedGames);
                resolve(newGame);
            } catch (error) {
                reject(error);
            }
        });
    },

    getUploadedGames: function() {
        try {
            // 首先尝试获取新的分块存储数据
            const index = JSON.parse(localStorage.getItem('uploadedGames_index') || '{"totalChunks":0,"totalGames":0}');
            let allGames = [];
            
            // 获取所有分块数据
            for (let i = 0; i < index.totalChunks; i++) {
                const chunk = JSON.parse(localStorage.getItem(`uploadedGames_${i}`) || '[]');
                allGames = allGames.concat(chunk);
            }

            // 如果没有分块数据，尝试获取旧格式数据
            if (allGames.length === 0) {
                const oldGames = JSON.parse(localStorage.getItem('uploadedGames') || '[]');
                if (oldGames.length > 0) {
                    // 将旧数据转换为新格式
                    this.saveGames(oldGames);
                    return oldGames;
                }
            }
            
            return allGames;
        } catch (error) {
            console.error('获取游戏列表失败:', error);
            // 如果出错，尝试获取旧格式数据
            try {
                return JSON.parse(localStorage.getItem('uploadedGames') || '[]');
            } catch (e) {
                return [];
            }
        }
    },

    saveGames: async function(games) {
        try {
            // 如果数据太大，只保留最新的10个游戏
            if (games.length > 10) {
                games = games.slice(-10);
            }

            // 清理所有相关的localStorage项
            for (let i = 0; i < 10; i++) {
                localStorage.removeItem(`uploadedGames_${i}`);
            }
            localStorage.removeItem('uploadedGames_index');
            localStorage.removeItem('uploadedGames');
            
            // 分批保存数据
            const chunkSize = 5;
            for (let i = 0; i < games.length; i += chunkSize) {
                const chunk = games.slice(i, i + chunkSize);
                localStorage.setItem(`uploadedGames_${i/chunkSize}`, JSON.stringify(chunk));
            }
            
            // 保存索引信息
            localStorage.setItem('uploadedGames_index', JSON.stringify({
                totalChunks: Math.ceil(games.length / chunkSize),
                totalGames: games.length
            }));
        } catch (error) {
            console.error('保存游戏失败:', error);
            throw new Error('保存游戏失败，可能是存储空间不足');
        }
    },

    compressImage: function(base64String) {
        return new Promise((resolve, reject) => {
            const img = new Image();
            img.src = base64String;
            
            img.onload = function() {
                const canvas = document.createElement('canvas');
                let width = img.width;
                let height = img.height;
                
                // 计算新的尺寸，保持宽高比
                const maxSize = 800;
                if (width > height && width > maxSize) {
                    height = Math.round((height * maxSize) / width);
                    width = maxSize;
                } else if (height > maxSize) {
                    width = Math.round((width * maxSize) / height);
                    height = maxSize;
                }
                
                canvas.width = width;
                canvas.height = height;
                
                const ctx = canvas.getContext('2d');
                ctx.drawImage(img, 0, 0, width, height);
                
                // 压缩图片质量
                const compressedBase64 = canvas.toDataURL('image/jpeg', 0.6);
                resolve(compressedBase64);
            };
            
            img.onerror = function() {
                reject(new Error('图片加载失败'));
            };
        });
    },

    // 获取所有游戏数据
    getAllGames: function() {
        try {
            // 首先尝试获取新的分块存储数据
            const index = JSON.parse(localStorage.getItem('uploadedGames_index') || '{"totalChunks":0,"totalGames":0}');
            let allGames = [];
            
            // 获取所有分块数据
            for (let i = 0; i < index.totalChunks; i++) {
                const chunk = JSON.parse(localStorage.getItem(`uploadedGames_${i}`) || '[]');
                allGames = allGames.concat(chunk);
            }

            // 如果没有分块数据，尝试获取旧格式数据
            if (allGames.length === 0) {
                const oldGames = JSON.parse(localStorage.getItem('uploadedGames') || '[]');
                if (oldGames.length > 0) {
                    // 将旧数据转换为新格式
                    this.saveGames(oldGames);
                    return oldGames;
                }
            }
            
            return allGames;
        } catch (error) {
            console.error('获取游戏列表失败:', error);
            // 如果出错，尝试获取旧格式数据
            try {
                return JSON.parse(localStorage.getItem('uploadedGames') || '[]');
            } catch (e) {
                return [];
            }
        }
    }
};

// 全局购物车管理器
const CartManager = {
    cart: JSON.parse(localStorage.getItem('cart')) || {
        count: 0,
        items: [],
        total: 0
    },

    updateDisplay: function() {
        try {
            // 更新购物车计数器
            document.querySelectorAll('.cart-counter').forEach(counter => {
                counter.textContent = this.cart.count;
            });

            // 更新购物车总价
            document.querySelectorAll('.cart-total h3').forEach(totalElement => {
                totalElement.textContent = `总计：¥ ${this.cart.total.toFixed(2)}`;
            });

            // 更新购物车列表
            const cartItems = document.querySelector('.cart-items');
            if (cartItems) {
                cartItems.innerHTML = this.cart.items.map(item => {
                    // 处理图片路径
                    let imgSrc;
                    if (item.banner) {
                        if (item.banner.startsWith('data:')) {
                            imgSrc = item.banner;
                        } else if (item.banner.startsWith('assets/')) {
                            imgSrc = item.banner;
                        } else {
                            imgSrc = item.banner;
                        }
                    } else {
                        // 如果没有banner，使用游戏标题作为图片文件名
                        imgSrc = `${item.title}.jpg`;
                    }

                    return `
                        <div class="cart-item" data-item-id="${item.id}">
                            <img src="${imgSrc}" alt="${item.title}">
                            <div class="item-info">
                                <h3>${item.title}</h3>
                                <p>¥${item.price.toFixed(2)}</p>
                            </div>
                            <button class="remove-btn">移除</button>
                        </div>
                    `;
                }).join('') || '<div class="empty-cart">购物车是空的</div>';
            }
        } catch (error) {
            console.error('更新购物车显示失败:', error);
        }
    },

    createFlyItem: function(button, gameCard) {
        const flyItem = document.createElement('div');
        const imgSrc = gameCard.querySelector('img').src;
        
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

        const buttonRect = button.getBoundingClientRect();
        flyItem.style.left = `${buttonRect.left + buttonRect.width/2 - 40}px`;
        flyItem.style.top = `${buttonRect.top + buttonRect.height/2 - 25}px`;
        
        return flyItem;
    },

    animateToCart: function(flyItem, callback) {
        document.body.appendChild(flyItem);

        const cartLink = document.querySelector('[href="cart.html"]');
        if (!cartLink) {
            if (callback) callback();
            return;
        }
        
        const cartRect = cartLink.getBoundingClientRect();
        const targetX = cartRect.left + cartRect.width/2 - 40;
        const targetY = cartRect.top + cartRect.height/2 - 25;

        flyItem.style.transform = `
            translate(${targetX - parseFloat(flyItem.style.left)}px, 
                      ${targetY - parseFloat(flyItem.style.top)}px)
            scale(0.5) 
            rotate(360deg)
        `;
        flyItem.style.opacity = '0.5';

        setTimeout(() => {
            flyItem.style.transition = 'all 0.2s ease-out';
            flyItem.style.transform += 'scale(0)';
            flyItem.style.opacity = '0';
            
            setTimeout(() => {
                flyItem.remove();
                if (callback) callback();
            }, 200);
        }, 600);
    },

    addToCart: function(gameId) {
        try {
            const games = GameManager.getAllGames();
            const game = games.find(g => g.id === gameId);
            
            if (!game) {
                console.error('游戏不存在:', gameId);
                return;
            }

            const existingItem = this.cart.items.find(item => item.id === gameId);
            if (existingItem) {
                alert('该游戏已在购物车中');
                return;
            }

            const buyBtn = document.querySelector(`.game-card[data-game-id="${gameId}"] .buy-btn`);
            const gameCard = buyBtn?.closest('.game-card');
            
            if (!buyBtn || !gameCard) {
                console.error('未找到游戏卡片元素');
                return;
            }

            // 获取游戏卡片中的图片元素
            const gameImage = gameCard.querySelector('img');
            if (!gameImage) {
                console.error('未找到游戏图片元素');
                return;
            }

            const flyItem = this.createFlyItem(buyBtn, gameCard);
            
            this.animateToCart(flyItem, () => {
                this.cart.count++;
                // 使用游戏卡片中的图片路径，如果不存在则使用默认图片
                const banner = gameImage.src || 'assets/images/default-game.jpg';
                
                this.cart.items.push({
                    id: game.id,
                    title: game.title,
                    price: game.price,
                    banner: banner
                });
                this.cart.total += game.price;

                localStorage.setItem('cart', JSON.stringify(this.cart));
                this.updateDisplay();
            });
        } catch (error) {
            console.error('添加到购物车失败:', error);
            alert('添加到购物车失败，请重试');
        }
    }
};

// 在DOMContentLoaded事件中调用
document.addEventListener('DOMContentLoaded', () => {
    updateNavbar();
    UserGameManager.renderUserGames();
    CartManager.updateDisplay(); // 初始化购物车显示
});
