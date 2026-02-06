# Spring Boot 测试修复总结

## 已修复的问题

### 1. JwtTokenUtilTest - 字段名不匹配 ✅
**问题**: `ReflectionTestUtils.setField` 使用了错误的字段名 `expiration`，实际字段名为 `accessTokenExpiration` 和 `refreshTokenExpiration`

**修复**: 更新字段名以匹配实际类定义

### 2. InvalidUseOfMatchers 问题 ✅
**问题**: Mockito 中混合使用原始值和 `any()` 匹配器

**修复**: 使用 `eq()` 包装原始值
```java
// 错误
when(repository.findById(1L, any())).thenReturn(...)

// 正确
when(repository.findById(eq(1L), any())).thenReturn(...)
```

### 3. WebSocket 循环依赖问题 ✅
**问题**: `ChatChannelInterceptor` 和 `WebSocketService` 之间存在循环依赖

**修复**: 使用 `@Lazy` 延迟注入
```java
@Autowired
public ChatChannelInterceptor(@Lazy WebSocketService webSocketService) {
    this.webSocketService = webSocketService;
}
```

### 4. Service 单元测试 NullPointerException ✅
**问题**: 测试中只 mock 了部分依赖，但 Service 有多个构造函数参数

**修复**: 添加所有必要的 `@Mock` 依赖

### 5. 测试配置问题 ✅
**修复**: 在 `application-test.yml` 中排除 WebSocket 自动配置
```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      - org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration
```

## 测试最佳实践

### 1. 单元测试结构
```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    
    @Mock
    private DependencyRepository dependencyRepository;
    
    @InjectMocks
    private MyServiceImpl myService;
    
    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }
    
    @Test
    void testMethod_Success() {
        // given
        when(dependencyRepository.findById(any())).thenReturn(Optional.of(mockData));
        
        // when
        Result result = myService.method();
        
        // then
        assertNotNull(result);
        verify(dependencyRepository).findById(any());
    }
}
```

### 2. 集成测试结构
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MyControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MyService myService;
    
    @Test
    void testEndpoint() throws Exception {
        when(myService.method()).thenReturn(mockData);
        
        mockMvc.perform(get("/api/endpoint"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data").exists());
    }
}
```

### 3. 常用 Mockito 模式

#### 参数匹配
```java
// 精确匹配
when(repository.findById(1L)).thenReturn(Optional.of(entity));

// 任意值匹配
when(repository.findById(any())).thenReturn(Optional.of(entity));

// 特定类型匹配
when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

// 混合使用 eq()
when(repository.findByIdAndStatus(eq(1L), any())).thenReturn(entity);
```

#### 验证调用
```java
// 验证调用次数
verify(repository, times(1)).save(any());
verify(repository, never()).delete(any());
verify(repository, atLeastOnce()).findById(any());

// 验证调用顺序
InOrder inOrder = inOrder(repository);
inOrder.verify(repository).findById(any());
inOrder.verify(repository).save(any());
```

### 4. 测试数据构建
```java
// 使用 Builder 模式
private User createMockUser(Long id, String username) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setEmail(username + "@example.com");
    return user;
}

// 或使用 Test Data Builder 模式
```

## 待修复的测试清单

### Service 单元测试 (NullPointerException)
- [x] BankruptCaseServiceTest
- [x] CaseTaskServiceTest
- [ ] ArchiveServiceTest
- [ ] CaseAnnouncementServiceTest
- [ ] CaseTaskSubmissionServiceTest
- [ ] ClaimRegistrationServiceTest
- [ ] BankAccountTransactionServiceTest
- [ ] ApprovalServiceRejectedTest
- [ ] ExpenseReimbursementServiceTest
- [ ] DocumentDeliveryServiceTest

### 集成测试 (ApplicationContext 加载失败)
- [ ] 所有 ControllerIntegrationTest 类
- [ ] 需要修复 WebSocket 配置或排除 WebSocket 自动配置

## 建议的修复策略

### 短期方案
1. 为所有 Service 测试添加完整的依赖 mock
2. 修复断言值与实际业务逻辑不匹配的问题
3. 添加必要的 `when(...).thenReturn(...)` 来 mock 依赖方法调用

### 长期方案
1. 使用 `@SpringBootTest` + `@MockBean` 重构集成测试
2. 添加 Testcontainers 支持真实数据库测试
3. 添加 JaCoCo 代码覆盖率工具
4. 建立 CI/CD 流程自动运行测试

## 运行测试命令

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=MyServiceTest

# 运行特定测试方法
mvn test -Dtest=MyServiceTest#testMethod

# 跳过测试
mvn clean install -DskipTests

# 生成测试报告
mvn surefire-report:report
```

## 添加 JaCoCo 代码覆盖率

在 `pom.xml` 中添加：
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

运行覆盖率报告：
```bash
mvn clean test jacoco:report
```
报告位置：`target/site/jacoco/index.html`
