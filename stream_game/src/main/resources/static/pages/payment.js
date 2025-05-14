// payment.js - 支付页面专用逻辑
document.addEventListener('DOMContentLoaded', () => {
    // 全局API基础URL
    const API_BASE_URL = 'http://localhost:8080/api';
    
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
        payButton.addEventListener('click', async () => {
            const cart = getCartData();
            
            if (cart.count === 0) {
                alert('错误：购物车为空！');
                return;
            }

            // 获取当前用户信息
            const currentUser = JSON.parse(localStorage.getItem('currentPlayer'));
            if (!currentUser) {
                alert('请先登录后再购买游戏');
                window.location.href = 'login.html';
                return;
            }

            try {
                // 打印购物车内容，帮助调试
                console.log("购物车内容:", cart.items);
                
                // 使用批量购买API，由后端处理游戏名称匹配和购买流程
                const response = await fetch(`${API_BASE_URL}/transactions/purchase-cart`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        playerId: currentUser.playerId,
                        items: cart.items
                    })
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error('购买失败:', errorText);
                    throw new Error('批量购买处理失败');
                }
                
                // 处理返回结果
                const result = await response.json();
                console.log('购买结果:', result);
                
                // 向用户显示购买结果
                if (result.success) {
                    alert(`购买成功！${result.successCount}个游戏已添加到您的游戏库`);
                } else {
                    alert(`部分游戏购买失败：${result.message}\n成功：${result.successCount}，失败：${result.failCount}`);
                    console.error('失败详情:', result.errors);
                }
                
                // 为每个成功购买的游戏添加到玩家游戏库
                const successfulTransactions = result.transactions || [];
                for (const transaction of successfulTransactions) {
                    const gameId = transaction.game.gameId;
                    
                    try {
                        // 添加到用户游戏库
                        const playerGameResponse = await fetch(`${API_BASE_URL}/player-games`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({
                                player: {
                                    playerId: currentUser.playerId
                                },
                                game: {
                                    gameId: gameId
                                },
                                playTimeMinutes: 0
                            })
                        });
                        
                        if (!playerGameResponse.ok) {
                            console.error(`添加到游戏库失败: ${await playerGameResponse.text()}`);
                        }
                    } catch (error) {
                        console.error(`添加游戏${gameId}到库时出错:`, error);
                    }
                }

                // 创建订单记录
                const order = {
                    id: 'ORDER-' + Date.now().toString(36),
                    items: [...cart.items],
                    total: cart.total,
                    date: new Date().toISOString(),
                    successCount: result.successCount,
                    failCount: result.failCount
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
            } catch (error) {
                console.error('支付处理错误:', error);
                alert('购买处理时出错: ' + error.message);
            }
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