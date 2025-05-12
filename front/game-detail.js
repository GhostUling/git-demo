document.addEventListener('DOMContentLoaded', () => {
    const gameId = new URLSearchParams(window.location.search).get('id');
    
    // 仅处理上传游戏
    if (!gameId || !gameId.startsWith('uploaded_')) {
        window.location.href = 'index.html';
        return;
    }

    // 查找游戏数据
    const game = GameManager.getUploadedGames().find(g => g.id === gameId);
    
    if (!game) {
        renderError();
        return;
    }

    renderGameDetail(game);
    setupBuyButton(game);
});

function renderGameDetail(game) {
    const container = document.getElementById('dynamicContent');
    
    const html = `
        <img src="${game.banner}" class="game-banner" alt="${game.title}">
        
        <div class="detail-content">
            <div class="main-info">
                <h1>${game.title}</h1>
                <div class="price-tag">¥ ${game.price.toFixed(2)}</div>
                <div class="tags">${game.tags.map(t => `<span>${t}</span>`).join('')}</div>
                <p class="description">${game.description}</p>
            </div>
            
            <div class="sidebar">
                <div class="requirements">
                    <h2>系统需求</h2>
                    <ul>
                        ${Object.entries(game.requirements)
                            .map(([key, val]) => `<li><strong>${key}:</strong> ${val}</li>`)
                            .join('')}
                    </ul>
                </div>
                <button class="buy-btn">加入购物车</button>
            </div>
        </div>
    `;

    container.innerHTML = html;
}

function setupBuyButton(game) {
    document.querySelector('.buy-btn').addEventListener('click', () => {
        addToCart(game.id);
        alert('已加入购物车！');
    });
}

function renderError() {
    document.getElementById('dynamicContent').innerHTML = `
        <div class="error-message">
            <h2>游戏不存在</h2>
            <a href="index.html" class="back-btn">返回商城</a>
        </div>
    `;
}