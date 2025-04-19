// JavaScript 文件 (assets/js/main.js)
document.addEventListener('DOMContentLoaded', function() {
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

    // 购物车动画逻辑
    document.querySelectorAll('.buy-btn').forEach(button => {
        button.addEventListener('click', function() {
            const gameCard = this.closest('.game-card');
            const imgSrc = gameCard.querySelector('img').src;
            
            // 创建飞行元素
            const flyItem = document.createElement('div');
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
            `;

            const rect = this.getBoundingClientRect();
            flyItem.style.left = `${rect.left}px`;
            flyItem.style.top = `${rect.top}px`;
            document.body.appendChild(flyItem);

            // 动画到购物车
            setTimeout(() => {
                const cartLink = document.querySelector('[href="cart.html"]');
                const cartRect = cartLink.getBoundingClientRect();
                flyItem.style.left = `${cartRect.left - 30}px`;
                flyItem.style.top = `${cartRect.top}px`;
                flyItem.style.opacity = '0.5';
                flyItem.style.transform = 'scale(0.3) rotate(360deg)';
            }, 10);

            // 移除元素并更新计数器
            setTimeout(() => {
                flyItem.remove();
                updateCartCounter();
            }, 600);
        });
    });

    // 更新购物车计数器
    function updateCartCounter() {
        const counter = document.querySelector('.cart-counter');
        if (counter) {
            // 简单递增，实际应连接后端
            const current = parseInt(counter.textContent) || 0;
            counter.textContent = current + 1;
            
            // 触发动画
            counter.style.animation = 'none';
            void counter.offsetWidth;
            counter.style.animation = 'cartBounce 0.5s ease';
        }
    }

    // 购物车移除功能
    document.querySelectorAll('.remove-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const item = this.closest('.cart-item');
            item.classList.add('removing');
            setTimeout(() => {
                item.remove();
                updateTotal();
            }, 400);
        });
    });

    // 更新总价
    function updateTotal() {
        // 实际应重新计算所有商品价格
        const totalElement = document.querySelector('.cart-total h3');
        if (totalElement) {
            const currentTotal = parseFloat(totalElement.textContent.replace('总计：¥ ', ''));
            totalElement.textContent = `总计：¥ ${currentTotal - 198}`;
        }
    }
});