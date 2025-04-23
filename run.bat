@echo off
echo 正在启动 Stream游戏商城平台...

echo 编译后端项目...
cd stream_game
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo 编译失败，请检查错误信息！
    pause
    exit /b 1
)

echo 启动后端服务...
java -jar target\stream_game-0.0.1-SNAPSHOT.jar

echo 服务已关闭，按任意键退出...
pause 