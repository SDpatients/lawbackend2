@echo off
echo ========================================
echo 文件管理API功能测试
echo ========================================
echo.

set BASE_URL=http://localhost:8081/api/v1
set TEST_FILE=test.txt
set TEST_FILE2=test2.txt

echo 创建测试文件...
echo This is a test file for API testing > %TEST_FILE%
echo This is another test file for API testing > %TEST_FILE2%

echo.
echo ========================================
echo 1. 测试文件上传API
echo ========================================
curl -X POST "%BASE_URL%/file/upload" -F "file=@%TEST_FILE%" -F "bizType=test" -F "bizId=1"
echo.
echo.

echo ========================================
echo 2. 测试文件上传API (第二个文件)
echo ========================================
curl -X POST "%BASE_URL%/file/upload" -F "file=@%TEST_FILE2%" -F "bizType=test" -F "bizId=1"
echo.
echo.

echo ========================================
echo 3. 测试获取文件列表API
echo ========================================
curl -X GET "%BASE_URL%/file/list?pageNum=1&pageSize=10&bizType=test"
echo.
echo.

echo ========================================
echo 4. 测试获取文件统计信息API
echo ========================================
curl -X GET "%BASE_URL%/file/statistics?bizType=test"
echo.
echo.

echo ========================================
echo 5. 测试获取文件信息API (需要手动替换FILE_ID)
echo ========================================
echo 请从上面的输出中复制fileId，然后运行以下命令：
echo curl -X GET "%BASE_URL%/file/{FILE_ID}"
echo.

echo ========================================
echo 6. 测试文件下载API (需要手动替换FILE_ID)
echo ========================================
echo 请从上面的输出中复制fileId，然后运行以下命令：
echo curl -X GET "%BASE_URL%/file/download/{FILE_ID}" --output downloaded_file.txt
echo.

echo ========================================
echo 7. 测试文件重命名API (需要手动替换FILE_ID)
echo ========================================
echo 请从上面的输出中复制fileId，然后运行以下命令：
echo curl -X PUT "%BASE_URL%/file/{FILE_ID}/rename?newFileName=renamed_test.txt"
echo.

echo ========================================
echo 8. 测试更新文件状态API (需要手动替换FILE_ID)
echo ========================================
echo 请从上面的输出中复制fileId，然后运行以下命令：
echo curl -X PUT "%BASE_URL%/file/{FILE_ID}/status?status=ARCHIVED"
echo.

echo ========================================
echo 9. 测试批量更新文件状态API (需要手动替换FILE_ID1,FILE_ID2)
echo ========================================
echo 请从上面的输出中复制fileId，然后运行以下命令：
echo curl -X PUT "%BASE_URL%/file/batch/status?status=ACTIVE" -H "Content-Type: application/json" -d "[FILE_ID1, FILE_ID2]"
echo.

echo ========================================
echo 10. 测试文件预览API (需要手动替换FILE_ID)
echo ========================================
echo 请从上面的输出中复制fileId，然后运行以下命令：
echo curl -X GET "%BASE_URL%/file/preview/{FILE_ID}" --output preview_file.txt
echo.

echo ========================================
echo 11. 测试删除文件API (需要手动替换FILE_ID)
echo ========================================
echo 请从上面的输出中复制fileId，然后运行以下命令：
echo curl -X DELETE "%BASE_URL%/file/{FILE_ID}"
echo.

echo ========================================
echo 12. 测试批量删除文件API (需要手动替换FILE_ID1,FILE_ID2)
echo ========================================
echo 请从上面的输出中复制fileId，然后运行以下命令：
echo curl -X DELETE "%BASE_URL%/file/batch" -H "Content-Type: application/json" -d "[FILE_ID1, FILE_ID2]"
echo.

echo.
echo ========================================
echo 测试完成
echo ========================================
echo.
echo 注意事项：
echo 1. 部分API需要手动替换FILE_ID参数
echo 2. 请确保应用正在运行
echo 3. 测试完成后可以删除生成的测试文件
echo.

pause