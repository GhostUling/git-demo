#!/bin/bash
echo "正在启动 Stream游戏商城平台..."

echo "编译后端项目..."
cd stream_game
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "编译失败，请检查错误信息！"
    exit 1
fi

echo "启动后端服务..."
java -jar target/stream_game-0.0.1-SNAPSHOT.jar

echo "服务已关闭" 