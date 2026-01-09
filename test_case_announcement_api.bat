@echo off
setlocal enabledelayedexpansion

echo ========================================
echo 案件公告API测试脚本
echo ========================================
echo.

set BASE_URL=http://localhost:8081/api/v1
set ANNOUNCEMENT_URL=%BASE_URL%/case-announcement
set VIEW_RECORD_URL=%BASE_URL%/announcement-view-record

echo 测试1: 创建案件公告 - 正常情况
echo.
curl -X POST "%ANNOUNCEMENT_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"caseId\": 1, \"title\": \"测试公告标题\", \"content\": \"这是测试公告的内容\", \"announcementType\": \"NOTICE\"}"
echo.
echo ========================================
echo.

echo 测试2: 创建案件公告 - 缺少必填字段
echo.
curl -X POST "%ANNOUNCEMENT_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"caseId\": 1, \"title\": \"\", \"content\": \"这是测试公告的内容\", \"announcementType\": \"NOTICE\"}"
echo.
echo ========================================
echo.

echo 测试3: 创建案件公告 - 特殊字符
echo.
curl -X POST "%ANNOUNCEMENT_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"caseId\": 1, \"title\": \"测试公告<>\\\"\\'\\&\\<\\>\", \"content\": \"这是测试公告的内容\\n包含换行符\", \"announcementType\": \"NOTICE\"}"
echo.
echo ========================================
echo.

echo 测试4: 获取案件公告列表
echo.
curl -X GET "%ANNOUNCEMENT_URL%/list?pageNum=1&pageSize=10"
echo.
echo ========================================
echo.

echo 测试5: 获取案件公告列表 - 按案件ID筛选
echo.
curl -X GET "%ANNOUNCEMENT_URL%/list?pageNum=1&pageSize=10&caseId=1"
echo.
echo ========================================
echo.

echo 测试6: 获取案件公告列表 - 按状态筛选
echo.
curl -X GET "%ANNOUNCEMENT_URL%/list?pageNum=1&pageSize=10&status=DRAFT"
echo.
echo ========================================
echo.

echo 测试7: 获取案件公告列表 - 边界值测试（页码为0）
echo.
curl -X GET "%ANNOUNCEMENT_URL%/list?pageNum=0&pageSize=10"
echo.
echo ========================================
echo.

echo 测试8: 获取案件公告列表 - 边界值测试（每页大小为负数）
echo.
curl -X GET "%ANNOUNCEMENT_URL%/list?pageNum=1&pageSize=-1"
echo.
echo ========================================
echo.

echo 测试9: 获取公告详情 - 正常情况
echo.
curl -X GET "%ANNOUNCEMENT_URL%/1"
echo.
echo ========================================
echo.

echo 测试10: 获取公告详情 - 不存在的ID
echo.
curl -X GET "%ANNOUNCEMENT_URL%/99999"
echo.
echo ========================================
echo.

echo 测试11: 获取公告详情 - 非数字ID
echo.
curl -X GET "%ANNOUNCEMENT_URL%/abc"
echo.
echo ========================================
echo.

echo 测试12: 更新案件公告 - 正常情况
echo.
curl -X PUT "%ANNOUNCEMENT_URL%/1" ^
  -H "Content-Type: application/json" ^
  -d "{\"title\": \"更新后的标题\", \"content\": \"更新后的内容\", \"announcementType\": \"URGENT\"}"
echo.
echo ========================================
echo.

echo 测试13: 更新案件公告 - 部分字段更新
echo.
curl -X PUT "%ANNOUNCEMENT_URL%/1" ^
  -H "Content-Type: application/json" ^
  -d "{\"title\": \"只更新标题\"}"
echo.
echo ========================================
echo.

echo 测试14: 更新案件公告 - 不存在的ID
echo.
curl -X PUT "%ANNOUNCEMENT_URL%/99999" ^
  -H "Content-Type: application/json" ^
  -d "{\"title\": \"更新后的标题\"}"
echo.
echo ========================================
echo.

echo 测试15: 发布公告 - 正常情况
echo.
curl -X POST "%ANNOUNCEMENT_URL%/1/publish" ^
  -H "Content-Type: application/json" ^
  -d "{\"topExpireTime\": \"2026-02-08T10:00:00\"}"
echo.
echo ========================================
echo.

echo 测试16: 发布公告 - 缺少必填字段
echo.
curl -X POST "%ANNOUNCEMENT_URL%/1/publish" ^
  -H "Content-Type: application/json" ^
  -d "{}"
echo.
echo ========================================
echo.

echo 测试17: 发布公告 - 重复发布
echo.
curl -X POST "%ANNOUNCEMENT_URL%/1/publish" ^
  -H "Content-Type: application/json" ^
  -d "{\"topExpireTime\": \"2026-02-08T10:00:00\"}"
echo.
echo ========================================
echo.

echo 测试18: 置顶公告 - 正常情况
echo.
curl -X POST "%ANNOUNCEMENT_URL%/1/top" ^
  -H "Content-Type: application/json" ^
  -d "{\"topExpireTime\": \"2026-02-08T10:00:00\"}"
echo.
echo ========================================
echo.

echo 测试19: 删除案件公告 - 正常情况（草稿状态）
echo.
curl -X DELETE "%ANNOUNCEMENT_URL%/2"
echo.
echo ========================================
echo.

echo 测试20: 删除案件公告 - 已发布的公告
echo.
curl -X DELETE "%ANNOUNCEMENT_URL%/1"
echo.
echo ========================================
echo.

echo 测试21: 删除案件公告 - 不存在的ID
echo.
curl -X DELETE "%ANNOUNCEMENT_URL%/99999"
echo.
echo ========================================
echo.

echo ========================================
echo 测试完成
echo ========================================
pause
