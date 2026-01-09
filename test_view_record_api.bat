@echo off
setlocal enabledelayedexpansion

echo ========================================
echo 公告查看记录API测试脚本
echo ========================================
echo.

set BASE_URL=http://localhost:8081/api/v1
set VIEW_RECORD_URL=%BASE_URL%/announcement-view-record

echo 测试1: 创建公告查看记录 - 正常情况
echo.
curl -X POST "%VIEW_RECORD_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"announcementId\": 1, \"announcementTitle\": \"测试公告\", \"caseId\": 1, \"caseName\": \"测试案件\", \"viewerId\": 1, \"viewerName\": \"测试用户\", \"viewerType\": \"CREDITOR\", \"ipAddress\": \"192.168.1.1\", \"userAgent\": \"Mozilla/5.0\", \"viewDuration\": 60, \"deviceType\": \"PC\", \"browserType\": \"Chrome\", \"osType\": \"Windows\", \"location\": \"北京\"}"
echo.
echo ========================================
echo.

echo 测试2: 创建公告查看记录 - 缺少必填字段
echo.
curl -X POST "%VIEW_RECORD_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"announcementTitle\": \"测试公告\", \"caseId\": 1, \"caseName\": \"测试案件\", \"viewerId\": 1, \"viewerName\": \"测试用户\", \"viewerType\": \"CREDITOR\"}"
echo.
echo ========================================
echo.

echo 测试3: 创建公告查看记录 - 不存在的公告ID
echo.
curl -X POST "%VIEW_RECORD_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"announcementId\": 99999, \"announcementTitle\": \"测试公告\", \"caseId\": 1, \"caseName\": \"测试案件\", \"viewerId\": 1, \"viewerName\": \"测试用户\", \"viewerType\": \"CREDITOR\"}"
echo.
echo ========================================
echo.

echo 测试4: 创建公告查看记录 - 特殊字符
echo.
curl -X POST "%VIEW_RECORD_URL%" ^
  -H "Content-Type: application/json" ^
  -d "{\"announcementId\": 1, \"announcementTitle\": \"测试公告<>\\\"\\'\\&\\<\\>\", \"caseId\": 1, \"caseName\": \"测试案件\", \"viewerId\": 1, \"viewerName\": \"测试用户<>\\\"\\'\\&\\<\\>\", \"viewerType\": \"CREDITOR\", \"ipAddress\": \"192.168.1.1\", \"userAgent\": \"Mozilla/5.0\", \"viewDuration\": 60, \"deviceType\": \"PC\", \"browserType\": \"Chrome\", \"osType\": \"Windows\", \"location\": \"北京<>\\\"\\'\\&\\<\\>\"}"
echo.
echo ========================================
echo.

echo 测试5: 获取公告查看记录详情 - 正常情况
echo.
curl -X GET "%VIEW_RECORD_URL%/1"
echo.
echo ========================================
echo.

echo 测试6: 获取公告查看记录详情 - 不存在的ID
echo.
curl -X GET "%VIEW_RECORD_URL%/99999"
echo.
echo ========================================
echo.

echo 测试7: 获取公告查看记录详情 - 非数字ID
echo.
curl -X GET "%VIEW_RECORD_URL%/abc"
echo.
echo ========================================
echo.

echo 测试8: 获取公告查看记录列表 - 正常情况
echo.
curl -X GET "%VIEW_RECORD_URL%/list?page=1&size=10"
echo.
echo ========================================
echo.

echo 测试9: 获取公告查看记录列表 - 按公告ID筛选
echo.
curl -X GET "%VIEW_RECORD_URL%/list?page=1&size=10&announcementId=1"
echo.
echo ========================================
echo.

echo 测试10: 获取公告查看记录列表 - 按案件ID筛选
echo.
curl -X GET "%VIEW_RECORD_URL%/list?page=1&size=10&caseId=1"
echo.
echo ========================================
echo.

echo 测试11: 获取公告查看记录列表 - 按查看人ID筛选
echo.
curl -X GET "%VIEW_RECORD_URL%/list?page=1&size=10&viewerId=1"
echo.
echo ========================================
echo.

echo 测试12: 获取公告查看记录列表 - 边界值测试（页码为0）
echo.
curl -X GET "%VIEW_RECORD_URL%/list?page=0&size=10"
echo.
echo ========================================
echo.

echo 测试13: 获取公告查看记录列表 - 边界值测试（每页大小为负数）
echo.
curl -X GET "%VIEW_RECORD_URL%/list?page=1&size=-1"
echo.
echo ========================================
echo.

echo 测试14: 获取公告查看记录列表 - 组合筛选
echo.
curl -X GET "%VIEW_RECORD_URL%/list?page=1&size=10&announcementId=1&caseId=1"
echo.
echo ========================================
echo.

echo 测试15: 获取公告查看次数 - 按公告ID
echo.
curl -X GET "%VIEW_RECORD_URL%/count/announcement/1"
echo.
echo ========================================
echo.

echo 测试16: 获取公告查看次数 - 不存在的公告ID
echo.
curl -X GET "%VIEW_RECORD_URL%/count/announcement/99999"
echo.
echo ========================================
echo.

echo 测试17: 获取案件公告查看次数 - 按案件ID
echo.
curl -X GET "%VIEW_RECORD_URL%/count/case/1"
echo.
echo ========================================
echo.

echo 测试18: 获取案件公告查看次数 - 不存在的案件ID
echo.
curl -X GET "%VIEW_RECORD_URL%/count/case/99999"
echo.
echo ========================================
echo.

echo 测试19: 获取用户查看次数 - 按用户ID
echo.
curl -X GET "%VIEW_RECORD_URL%/count/viewer/1"
echo.
echo ========================================
echo.

echo 测试20: 获取用户查看次数 - 不存在的用户ID
echo.
curl -X GET "%VIEW_RECORD_URL%/count/viewer/99999"
echo.
echo ========================================
echo.

echo 测试21: 删除公告查看记录 - 正常情况
echo.
curl -X DELETE "%VIEW_RECORD_URL%/1"
echo.
echo ========================================
echo.

echo 测试22: 删除公告查看记录 - 不存在的ID
echo.
curl -X DELETE "%VIEW_RECORD_URL%/99999"
echo.
echo ========================================
echo.

echo ========================================
echo 测试完成
echo ========================================
pause
