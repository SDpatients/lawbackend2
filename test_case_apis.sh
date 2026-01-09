# 案件管理API功能测试脚本
# 测试日期：2026-01-08

# 基础URL
BASE_URL="http://localhost:8081/api/v1"

# 测试结果记录
echo "========================================" > test_results.txt
echo "案件管理API功能测试报告" >> test_results.txt
echo "测试时间：$(date)" >> test_results.txt
echo "========================================" >> test_results.txt
echo "" >> test_results.txt

# 1. 测试案件管理API
echo "【1. 案件管理API测试】" >> test_results.txt
echo "" >> test_results.txt

# 1.1 创建案件
echo "1.1 创建案件 - POST /case" >> test_results.txt
curl -X POST "${BASE_URL}/case" \
  -H "Content-Type: application/json" \
  -d '{"caseNumber":"TEST001","caseName":"测试案件1","acceptanceDate":"2024-01-01","caseSource":"法院移送","acceptanceCourt":"北京市第一中级人民法院","designatedInstitution":"北京破产管理人协会","mainResponsiblePerson":"张三","isSimplifiedTrial":0,"caseReason":"经营困难","caseProgress":"FIRST","debtClaimDeadline":"2024-12-31T23:59:59","filingDate":"2024-01-15","remarks":"这是一个测试案件"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 1.2 获取案件详情
echo "1.2 获取案件详情 - GET /case/{caseId}" >> test_results.txt
curl -X GET "${BASE_URL}/case/1" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 1.3 案件列表（分页）
echo "1.3 案件列表（分页） - GET /case/list" >> test_results.txt
curl -X GET "${BASE_URL}/case/list?pageNum=1&pageSize=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 1.4 更新案件信息
echo "1.4 更新案件信息 - PUT /case/{caseId}" >> test_results.txt
curl -X PUT "${BASE_URL}/case/1" \
  -H "Content-Type: application/json" \
  -d '{"caseName":"测试案件1-更新","caseReason":"经营困难-更新","remarks":"这是一个测试案件-更新"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 1.5 案件状态流转
echo "1.5 案件状态流转 - PUT /case/{caseId}/status" >> test_results.txt
curl -X PUT "${BASE_URL}/case/1/status" \
  -H "Content-Type: application/json" \
  -d '{"caseStatus":"IN_PROGRESS","reviewOpinion":"同意审核"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 1.6 案件进度更新
echo "1.6 案件进度更新 - PUT /case/{caseId}/progress" >> test_results.txt
curl -X PUT "${BASE_URL}/case/1/progress" \
  -H "Content-Type: application/json" \
  -d '{"caseProgress":"SECOND"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 1.7 案件审核
echo "1.7 案件审核 - POST /case/{caseId}/review" >> test_results.txt
curl -X POST "${BASE_URL}/case/1/review" \
  -H "Content-Type: application/json" \
  -d '{"reviewStatus":"APPROVED","reviewOpinion":"审核通过"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 1.8 查询案件审核状态
echo "1.8 查询案件审核状态 - GET /case/{caseId}/review-status" >> test_results.txt
curl -X GET "${BASE_URL}/case/1/review-status" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 2. 测试案件进度API
echo "【2. 案件进度API测试】" >> test_results.txt
echo "" >> test_results.txt

# 2.1 创建案件进度
echo "2.1 创建案件进度 - POST /api/v1/case-progress" >> test_results.txt
curl -X POST "${BASE_URL}/case-progress" \
  -H "Content-Type: application/json" \
  -d '{"caseId":1,"caseName":"测试案件1","caseNumber":"TEST001","progressStage":"FIRST","stageName":"第一阶段","stageDescription":"案件受理阶段","startDate":"2024-01-01","expectedEndDate":"2024-01-31","progressStatus":"IN_PROGRESS","completionPercentage":50,"keyTasks":"完成受理登记","responsiblePerson":"张三","responsiblePersonId":1}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 2.2 获取案件进度详情
echo "2.2 获取案件进度详情 - GET /api/v1/case-progress/{progressId}" >> test_results.txt
curl -X GET "${BASE_URL}/case-progress/1" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 2.3 获取案件进度列表
echo "2.3 获取案件进度列表 - GET /api/v1/case-progress/list" >> test_results.txt
curl -X GET "${BASE_URL}/case-progress/list?page=1&size=10&caseId=1" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 2.4 更新案件进度
echo "2.4 更新案件进度 - PUT /api/v1/case-progress/{progressId}" >> test_results.txt
curl -X PUT "${BASE_URL}/case-progress/1" \
  -H "Content-Type: application/json" \
  -d '{"completionPercentage":80,"keyTasks":"完成受理登记-更新"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 2.5 完成案件进度
echo "2.5 完成案件进度 - POST /api/v1/case-progress/{progressId}/complete" >> test_results.txt
curl -X POST "${BASE_URL}/case-progress/1/complete" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 2.6 获取案件的所有进度记录
echo "2.6 获取案件的所有进度记录 - GET /api/v1/case-progress/case/{caseId}" >> test_results.txt
curl -X GET "${BASE_URL}/case-progress/case/1" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 2.7 获取进行中的案件进度
echo "2.7 获取进行中的案件进度 - GET /api/v1/case-progress/case/{caseId}/in-progress" >> test_results.txt
curl -X GET "${BASE_URL}/case-progress/case/1/in-progress" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 2.8 获取已完成的案件进度
echo "2.8 获取已完成的案件进度 - GET /api/v1/case-progress/case/{caseId}/completed" >> test_results.txt
curl -X GET "${BASE_URL}/case-progress/case/1/completed" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 2.9 获取案件整体进度百分比
echo "2.9 获取案件整体进度百分比 - GET /api/v1/case-progress/case/{caseId}/overall-percentage" >> test_results.txt
curl -X GET "${BASE_URL}/case-progress/case/1/overall-percentage" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3. 测试案件搜索API
echo "【3. 案件搜索API测试】" >> test_results.txt
echo "" >> test_results.txt

# 3.1 关键词搜索案件
echo "3.1 关键词搜索案件 - GET /api/v1/case-search/keyword" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/keyword?keyword=测试&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.2 关键词和状态搜索案件
echo "3.2 关键词和状态搜索案件 - GET /api/v1/case-search/keyword-and-status" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/keyword-and-status?keyword=测试&caseStatus=PENDING&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.3 关键词和进度搜索案件
echo "3.3 关键词和进度搜索案件 - GET /api/v1/case-search/keyword-and-progress" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/keyword-and-progress?keyword=测试&caseProgress=FIRST&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.4 按案件编号搜索
echo "3.4 按案件编号搜索 - GET /api/v1/case-search/case-number" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/case-number?caseNumber=TEST001&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.5 按案件名称搜索
echo "3.5 按案件名称搜索 - GET /api/v1/case-search/case-name" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/case-name?caseName=测试案件&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.6 按受理法院搜索
echo "3.6 按受理法院搜索 - GET /api/v1/case-search/acceptance-court" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/acceptance-court?acceptanceCourt=北京市&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.7 按指定机构搜索
echo "3.7 按指定机构搜索 - GET /api/v1/case-search/designated-institution" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/designated-institution?designatedInstitution=破产管理人&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.8 按主要负责人搜索
echo "3.8 按主要负责人搜索 - GET /api/v1/case-search/main-responsible-person" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/main-responsible-person?mainResponsiblePerson=张三&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.9 按受理日期范围搜索
echo "3.9 按受理日期范围搜索 - GET /api/v1/case-search/acceptance-date-range" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/acceptance-date-range?startDate=2024-01-01&endDate=2024-12-31&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.10 按案件来源搜索
echo "3.10 按案件来源搜索 - GET /api/v1/case-search/case-source" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/case-source?caseSource=法院移送&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.11 按案件原因搜索
echo "3.11 按案件原因搜索 - GET /api/v1/case-search/case-reason" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/case-reason?caseReason=经营&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 3.12 按指定法官搜索
echo "3.12 按指定法官搜索 - GET /api/v1/case-search/designated-judge" >> test_results.txt
curl -X GET "${BASE_URL}/case-search/designated-judge?designatedJudge=法官&page=1&size=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 4. 测试案件统计API
echo "【4. 案件统计API测试】" >> test_results.txt
echo "" >> test_results.txt

# 4.1 获取案件统计数据
echo "4.1 获取案件统计数据 - GET /api/v1/case-statistics" >> test_results.txt
curl -X GET "${BASE_URL}/case-statistics" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 4.2 按日期范围统计
echo "4.2 按日期范围统计 - GET /api/v1/case-statistics?startDate=2024-01-01&endDate=2024-12-31" >> test_results.txt
curl -X GET "${BASE_URL}/case-statistics?startDate=2024-01-01&endDate=2024-12-31" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 4.3 按状态统计
echo "4.3 按状态统计 - GET /api/v1/case-statistics?caseStatus=PENDING" >> test_results.txt
curl -X GET "${BASE_URL}/case-statistics?caseStatus=PENDING" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 5. 测试案件公告API
echo "【5. 案件公告API测试】" >> test_results.txt
echo "" >> test_results.txt

# 5.1 创建案件公告
echo "5.1 创建案件公告 - POST /case-announcement" >> test_results.txt
curl -X POST "${BASE_URL}/case-announcement" \
  -H "Content-Type: application/json" \
  -d '{"caseId":1,"title":"案件公告1","content":"这是案件公告内容","announcementType":"NOTICE"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 5.2 案件公告列表
echo "5.2 案件公告列表 - GET /case-announcement/list" >> test_results.txt
curl -X GET "${BASE_URL}/case-announcement/list?pageNum=1&pageSize=10" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 5.3 获取公告详情
echo "5.3 获取公告详情 - GET /case-announcement/{announcementId}" >> test_results.txt
curl -X GET "${BASE_URL}/case-announcement/1" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 5.4 更新案件公告
echo "5.4 更新案件公告 - PUT /case-announcement/{announcementId}" >> test_results.txt
curl -X PUT "${BASE_URL}/case-announcement/1" \
  -H "Content-Type: application/json" \
  -d '{"title":"案件公告1-更新","content":"这是案件公告内容-更新"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 5.5 发布公告
echo "5.5 发布公告 - POST /case-announcement/{announcementId}/publish" >> test_results.txt
curl -X POST "${BASE_URL}/case-announcement/1/publish" \
  -H "Content-Type: application/json" \
  -d '{"publishTime":"2024-01-15T10:00:00"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 5.6 置顶公告
echo "5.6 置顶公告 - POST /case-announcement/{announcementId}/top" >> test_results.txt
curl -X POST "${BASE_URL}/case-announcement/1/top" \
  -H "Content-Type: application/json" \
  -d '{"topExpireTime":"2024-01-20T10:00:00"}' \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

# 5.7 删除案件公告
echo "5.7 删除案件公告 - DELETE /case-announcement/{announcementId}" >> test_results.txt
curl -X DELETE "${BASE_URL}/case-announcement/1" \
  -H "Content-Type: application/json" \
  2>&1 | tee -a test_results.txt
echo "" >> test_results.txt

echo "========================================" >> test_results.txt
echo "测试完成" >> test_results.txt
echo "========================================" >> test_results.txt

echo "测试完成！结果已保存到 test_results.txt"
