document.addEventListener('DOMContentLoaded', () => {
    const gameId = new URLSearchParams(window.location.search).get('id');
    
    // 仅处理上传游戏
    if (!gameId || !gameId.startsWith('game_')) {
        // 对于非上传游戏，不做额外处理，页面使用静态HTML
        return;
    }

    // 查找游戏数据
    const game = GameManager.getGameById(gameId);
    
    if (!game) {
        renderError();
        return;
    }

    renderGameDetail(game);
    setupBuyButton(game);
});

// 渲染游戏详情
function renderGameDetail(game) {
    // 更新页面标题
    document.title = `${game.title} - Stream`;
    
    // 更新游戏横幅
    const bannerImg = document.querySelector('.game-banner');
    if (bannerImg && game.banner) {
        bannerImg.src = game.banner;
    }
    
    // 更新价格
    const priceElement = document.querySelector('.price');
    if (priceElement) {
        priceElement.textContent = `¥ ${game.price.toFixed(2)}`;
    }
    
    // 更新标签
    const tagsContainer = document.querySelector('.game-tags');
    if (tagsContainer && game.tags) {
        tagsContainer.innerHTML = '';
        game.tags.forEach(tag => {
            const tagElement = document.createElement('span');
            tagElement.className = 'game-tag';
            tagElement.textContent = tag;
            tagsContainer.appendChild(tagElement);
        });
    }
    
    // 更新游戏介绍
    const descriptionElement = document.querySelector('.game-content p');
    if (descriptionElement) {
        descriptionElement.textContent = game.description;
    }
    
    // 更新系统需求
    if (game.requirements) {
        const requirementsList = document.querySelector('.system-requirements');
        if (requirementsList) {
            requirementsList.innerHTML = '';
            
            for (const [key, value] of Object.entries(game.requirements)) {
                if (value) {
                    const li = document.createElement('li');
                    const label = {
                        os: '操作系统',
                        cpu: '处理器',
                        ram: '内存',
                        gpu: '显卡',
                        storage: '存储空间'
                    }[key] || key;
                    
                    li.textContent = `${label}: ${value}`;
                    requirementsList.appendChild(li);
                }
            }
        }
    }
}

// 设置购买按钮逻辑
function setupBuyButton(game) {
    const buyButton = document.querySelector('.buy-btn');
    if (!buyButton) return;
    
    buyButton.addEventListener('click', () => {
        // 获取购物车数据
        let cart = JSON.parse(localStorage.getItem('cart')) || {
            count: 0,
            items: [],
            total: 0
        };
        
        // 检查游戏是否已在购物车中
        if (cart.items.some(item => item.id === game.id)) {
            alert('该游戏已在购物车中');
            return;
        }
        
        // 添加到购物车
        cart.count++;
        cart.items.push({
            id: game.id,
            title: game.title,
            price: game.price,
            banner: game.banner
        });
        cart.total += game.price;
        
        // 保存购物车
        localStorage.setItem('cart', JSON.stringify(cart));
        
        // 更新购物车计数器
        document.querySelectorAll('.cart-counter').forEach(counter => {
            counter.textContent = cart.count;
        });
        
        alert('已添加到购物车');
    });
}

// 显示错误信息
function renderError() {
    const container = document.querySelector('.game-detail');
    if (container) {
        container.innerHTML = `
            <div class="error-message">
                <h2>游戏不存在</h2>
                <p>找不到对应的游戏信息，可能已被删除或链接错误。</p>
                <a href="../index.html" class="btn">返回商城</a>
            </div>
        `;
    }
}