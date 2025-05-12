// payment.js - 支付页面专用逻辑
document.addEventListener('DOMContentLoaded', () => {
    // 获取购物车数据（与商城统一结构）
    const getCartData = () => {
        const defaultCart = { count: 0, items: [], total: 0 };
        try {
            return JSON.parse(localStorage.getItem('cart')) || defaultCart;
        } catch {
            return defaultCart;
        }
    };

    // 动态渲染订单
    const renderOrder = (cart) => {
        const orderItems = document.getElementById('order-items');
        const totalAmount = document.getElementById('total-amount');

        if (!orderItems || !totalAmount) return;

        // 清空旧数据
        orderItems.innerHTML = '';
        totalAmount.textContent = '';

        if (cart.count === 0) {
            orderItems.innerHTML = '<p class="empty-cart">购物车为空</p>';
            return;
        }

        // 生成商品列表
        const itemsFragment = document.createDocumentFragment();
        cart.items.forEach((item, index) => {
            const itemDiv = document.createElement('div');
            itemDiv.className = 'order-item';
            itemDiv.innerHTML = `
                <div class="item-info">
                    <span class="item-index">${index + 1}.</span>
                    <span class="item-title">${item.title}</span>
                    <span class="item-price">¥ ${item.price.toFixed(2)}</span>
                </div>
            `;
            itemsFragment.appendChild(itemDiv);
        });

        // 插入统计信息
        const statsDiv = document.createElement('div');
        statsDiv.className = 'order-stats';
        statsDiv.innerHTML = `<span>共计 ${cart.count} 件商品</span>`;
        
        orderItems.appendChild(itemsFragment);
        orderItems.appendChild(statsDiv);
        totalAmount.textContent = `总计：¥ ${cart.total.toFixed(2)}`;
    };

    // 初始化渲染
    const initialCart = getCartData();
    renderOrder(initialCart);

    // 支付按钮逻辑
    const payButton = document.getElementById('pay-now-btn');
    if (payButton) {
        payButton.addEventListener('click', () => {
            const cart = getCartData();
            
            if (cart.count === 0) {
                alert('错误：购物车为空！');
                return;
            }

            // 创建订单记录
            const order = {
                id: 'ORDER-' + Date.now().toString(36),
                items: [...cart.items],
                total: cart.total,
                date: new Date().toISOString()
            };

            // 保存订单历史
            const orders = JSON.parse(localStorage.getItem('orders') || '[]');
            orders.push(order);
            localStorage.setItem('orders', JSON.stringify(orders));

            // 清空购物车
            localStorage.setItem('cart', JSON.stringify({
                count: 0,
                items: [],
                total: 0
            }));

            // 显示支付成功
            document.querySelector('.payment-success').style.display = 'block';
            document.querySelectorAll('.cart-counter').forEach(c => c.textContent = '0');
        });
    }

    // 实时同步购物车变化
    window.addEventListener('storage', (e) => {
        if (e.key === 'cart') {
            const newCart = JSON.parse(e.newValue);
            renderOrder(newCart);
        }
    });
});