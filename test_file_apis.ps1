# File Management API Test Script

$BASE_URL = "http://localhost:8081/api/v1"
$TEST_FILE = "test.txt"
$TEST_FILE2 = "test2.txt"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "File Management API Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Create test files
Write-Host "Creating test files..." -ForegroundColor Yellow
"This is a test file for API testing" | Out-File -FilePath $TEST_FILE -Encoding UTF8
"This is another test file for API testing" | Out-File -FilePath $TEST_FILE2 -Encoding UTF8

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "1. Test File Upload API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$uploadResponse1 = Invoke-RestMethod -Uri "$BASE_URL/file/upload" -Method Post -Form @{
    file = Get-Item $TEST_FILE
    bizType = "test"
    bizId = 1
}
Write-Host "Upload successful!" -ForegroundColor Green
Write-Host "File ID: $($uploadResponse1.data.id)" -ForegroundColor Green
Write-Host "File Name: $($uploadResponse1.data.originalFileName)" -ForegroundColor Green
$FILE_ID_1 = $uploadResponse1.data.id

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "2. Test File Upload API (Second File)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$uploadResponse2 = Invoke-RestMethod -Uri "$BASE_URL/file/upload" -Method Post -Form @{
    file = Get-Item $TEST_FILE2
    bizType = "test"
    bizId = 1
}
Write-Host "Upload successful!" -ForegroundColor Green
Write-Host "File ID: $($uploadResponse2.data.id)" -ForegroundColor Green
Write-Host "File Name: $($uploadResponse2.data.originalFileName)" -ForegroundColor Green
$FILE_ID_2 = $uploadResponse2.data.id

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "3. Test Get File List API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$listResponse = Invoke-RestMethod -Uri "$BASE_URL/file/list?pageNum=1&pageSize=10&bizType=test" -Method Get
Write-Host "Get successful!" -ForegroundColor Green
Write-Host "Total Files: $($listResponse.data.total)" -ForegroundColor Green
Write-Host "File List: $($listResponse.data.list | ConvertTo-Json -Depth 3)" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "4. Test Get File Statistics API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$statsResponse = Invoke-RestMethod -Uri "$BASE_URL/file/statistics?bizType=test" -Method Get
Write-Host "Get successful!" -ForegroundColor Green
Write-Host "Statistics: $($statsResponse.data | ConvertTo-Json -Depth 3)" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "5. Test Get File Info API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$infoResponse = Invoke-RestMethod -Uri "$BASE_URL/file/$FILE_ID_1" -Method Get
Write-Host "Get successful!" -ForegroundColor Green
Write-Host "File Info: $($infoResponse.data | ConvertTo-Json -Depth 3)" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "6. Test File Download API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$downloadPath = "downloaded_file.txt"
Invoke-WebRequest -Uri "$BASE_URL/file/download/$FILE_ID_1" -Method Get -OutFile $downloadPath
Write-Host "Download successful! File saved to: $downloadPath" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "7. Test File Rename API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$renameResponse = Invoke-RestMethod -Uri "$BASE_URL/file/$FILE_ID_1/rename?newFileName=renamed_test.txt" -Method Put
Write-Host "Rename successful!" -ForegroundColor Green
Write-Host "New File Name: $($renameResponse.data.originalFileName)" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "8. Test Update File Status API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$statusResponse = Invoke-RestMethod -Uri "$BASE_URL/file/$FILE_ID_1/status?status=ARCHIVED" -Method Put
Write-Host "Status update successful!" -ForegroundColor Green
Write-Host "New Status: $($statusResponse.data.status)" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "9. Test Batch Update File Status API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$batchStatusBody = @($FILE_ID_1, $FILE_ID_2) | ConvertTo-Json
$batchStatusResponse = Invoke-RestMethod -Uri "$BASE_URL/file/batch/status?status=ACTIVE" -Method Put -Body $batchStatusBody -ContentType "application/json"
Write-Host "Batch status update successful!" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "10. Test File Preview API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
try {
    $previewPath = "preview_file.txt"
    Invoke-WebRequest -Uri "$BASE_URL/file/preview/$FILE_ID_1" -Method Get -OutFile $previewPath
    Write-Host "Preview successful! File saved to: $previewPath" -ForegroundColor Green
} catch {
    Write-Host "Preview failed (maybe file type not supported): $_" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "11. Test Delete File API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$deleteResponse = Invoke-RestMethod -Uri "$BASE_URL/file/$FILE_ID_1" -Method Delete
Write-Host "Delete successful!" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "12. Test Batch Delete Files API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$batchDeleteBody = @($FILE_ID_2) | ConvertTo-Json
$batchDeleteResponse = Invoke-RestMethod -Uri "$BASE_URL/file/batch" -Method Delete -Body $batchDeleteBody -ContentType "application/json"
Write-Host "Batch delete successful!" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Complete" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Clean up test files
Write-Host "Cleaning up test files..." -ForegroundColor Yellow
Remove-Item $TEST_FILE -ErrorAction SilentlyContinue
Remove-Item $TEST_FILE2 -ErrorAction SilentlyContinue
Remove-Item $downloadPath -ErrorAction SilentlyContinue
Remove-Item $previewPath -ErrorAction SilentlyContinue
Write-Host "Cleanup complete!" -ForegroundColor Green

Write-Host ""
Write-Host "All API tests completed!" -ForegroundColor Green
Write-Host ""