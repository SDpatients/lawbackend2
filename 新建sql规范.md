# MySQL数据库表新增规范与格式标准

## 1 概述

本文档旨在为项目数据库表的新增、修改和维护工作制定统一的规范与标准。通过遵循这些规范，可以确保数据库结构的一致性、可读性和可维护性，同时提高开发团队的协作效率。所有参与数据库设计与开发的人员都应严格遵守本规范中的各项要求。

本规范基于对现有数据库结构（`law_schema.sql`）的分析总结，涵盖了从表命名到SQL语句格式的各个方面。这些规范不仅考虑了数据库的性能优化需求，也兼顾了实际业务场景的复杂性，力求在规范性与灵活性之间取得平衡。

---

## 2 表命名规范

### 2.1 基本命名规则

表名是数据库对象的重要组成部分，良好的表命名能够直观地反映表的用途和数据特征。表名必须全部使用小写字母，并采用下划线分隔单词，这种命名方式具有以下优势：首先，小写字母可以避免因大小写敏感导致的跨平台兼容性问题；其次，下划线分隔的方式便于阅读和理解，尤其对于包含多个单词的表名效果更为明显。

表名的命名应当遵循以下核心原则：**表名必须以`tb_`前缀开头**，这是本项目数据库表命名的统一前缀，用于区分不同项目的表并便于识别和管理。例如，用户表命名为`tb_user`，角色表命名为`tb_role`，权限表命名为`tb_permission`。这种前缀命名方式源自现有数据库的实践，能够有效避免表名冲突，特别是在多个系统共用数据库或未来需要进行数据库合并的场景下尤为重要。

### 2.2 表名命名约定

表名的单词选择应当准确反映表的业务含义，避免使用模糊或过于简略的名称。每个单词都应具有明确的含义，不建议使用缩写形式，除非该缩写已成为业界通用的标准术语（如`info`代表`information`、`desc`代表`description`等）。表名的长度应控制在合理范围内，建议不超过30个字符，过长的表名不仅书写不便，还会影响SQL语句的可读性。

对于表示实体对象的表，应使用单数名词形式，如`tb_user`而非`tb_users`，因为一张表代表的是用户这一类实体的集合，而非多个独立的用户。这种命名方式与主流的ORM框架（如MyBatis、Hibernate）的默认映射规则相一致，可以减少配置工作量。对于关联表（表示多对多关系的中间表），应包含两个关联实体的名称，名称之间用下划线分隔，顺序按照字母排序或按照主从关系排列，例如表示用户和角色关联的表命名为`tb_user_role`。

### 2.3 特殊表命名规则

某些特殊功能的表需要遵循额外的命名约定。关联表用于存储两个实体之间的多对多关系，表名应包含两个关联实体的名称，顺序按照字母先后排列，例如用户角色关联表`tb_user_role`、角色权限关联表`tb_role_permission`。日志表用于记录系统操作历史，表名应包含`log`或`record`等后缀以表明其用途，如`tb_login_record`（登录记录表）、`tb_fund_operation_log`（资金操作日志表）。

配置表用于存储系统运行所需的配置信息，应包含`config`或`setting`等后缀，如`tb_system_config`（系统配置表）。字典表或枚举表用于存储可选值列表，应包含`dict`或`type`等后缀，如`tb_dictionary_type`（字典类型表）。统计表或报表表用于存储统计汇总数据，应包含`statistics`、`report`或`summary`等后缀，以明确区分于业务数据表。

---

## 3 字段设计规范

### 3.1 主键字段设计

每个表都必须定义一个主键，主键字段统一命名为`id`，数据类型为`bigint`，并设置自增属性`AUTO_INCREMENT`。使用`bigint`作为主键类型可以满足绝大多数业务场景的需求，即使在高并发、大数据量的环境下也能提供充足的主键空间。自增属性确保了主键值的唯一性和顺序性，同时由数据库自动管理可以避免应用层手动赋值可能带来的重复或冲突问题。

对于主键字段，添加`NOT NULL`约束是必须的，因为主键的本质要求就是非空且唯一。主键字段应当作为表的第一个字段，这不仅符合常规的阅读习惯，也有利于数据库优化器进行查询优化。主键字段必须添加详细的中文注释，说明其含义和用途，注释内容应简洁明了，如`COMMENT '主键ID'`。

### 3.2 通用字段设计

为了满足审计追踪和数据管理的需求，所有业务表都应包含以下通用字段，这些字段的设计在现有数据库中得到了充分体现。`create_time`字段用于记录数据创建时间，数据类型为`datetime`，默认值设为`CURRENT_TIMESTAMP`，表示记录创建时自动填充当前时间。`update_time`字段用于记录数据最后修改时间，数据类型为`datetime`，默认值设为`CURRENT_TIMESTAMP`，并添加`ON UPDATE CURRENT_TIMESTAMP`修饰，表示记录更新时自动更新为当前时间。

`create_user_id`字段记录创建者的用户ID，数据类型为`bigint`，允许为空，表示创建记录的操作人员。`update_user_id`字段记录最后修改者的用户ID，数据类型为`bigint`，允许为空。这两个字段的设计支持后续的业务审计需求，可以通过关联查询追溯每条记录的创建者和修改者。`is_deleted`字段用于实现软删除功能，数据类型为`tinyint(1)`，默认值设为`0`，通过该字段标记记录是否已被删除，而非物理删除数据，这种设计可以保留历史数据便于追溯和分析。

### 3.3 状态字段设计

状态字段用于表示记录的业务状态，命名应使用`status`、`state`或`is_xxx`等形式。`status`字段命名适用于表示多状态的情况，如`ACTIVE`（激活）、`INACTIVE`（停用）、`DELETED`（删除）等。数据类型建议使用`varchar(20)`，以支持状态值的扩展和描述性命名。字段注释必须详细说明所有可能的状态值及其含义，例如`COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除'`。

对于布尔类型的状态，使用`is_`前缀命名，数据类型为`tinyint(1)`，0和1分别表示两种相反的状态。注释中应明确说明0和1的具体含义，例如`COMMENT '是否删除: 0-否, 1-是'`。状态字段应当建立索引以支持状态的快速筛选和统计查询，索引命名遵循`idx_status`的形式。

### 3.4 外键字段设计

外键字段用于建立表与表之间的关联关系，命名应准确反映所关联的表名。外键字段名通常由关联表名的核心部分加`_id`后缀组成，例如关联用户表的字段命名为`user_id`，关联角色表的字段命名为`role_id`，关联案件表的字段命名为`case_id`。这种命名方式直观明了，便于理解字段的业务含义。

外键字段的数据类型必须与被关联表的主键类型一致，本项目中统一使用`bigint`类型。外键字段的注释应说明其所关联的表和字段，例如`COMMENT '用户ID'`。在定义外键约束时，应合理设置级联操作规则，`ON DELETE`和`ON UPDATE`行为需要根据具体的业务需求决定。常用的设置包括`ON DELETE CASCADE`（级联删除）和`ON UPDATE CASCADE`（级联更新），以及`ON DELETE SET NULL`（删除置空）等。

---

## 4 数据类型选择标准

### 4.1 整数类型选择

整数类型的的选择应根据字段的实际取值范围和业务需求来确定。`bigint`是本项目中使用最广泛的整数类型，适用于主键（`id`）、用户ID（`user_id`）、角色ID（`role_id`）等需要大范围取值的字段，其取值范围为-9,223,372,036,854,775,808到9,223,372,036,854,775,807，足以满足绝大多数业务场景的需求。

`int`适用于取值范围相对较小的场景，如序号（`sort_order`）、登录次数（`login_count`）、审核次数（`review_count`）等字段。`tinyint`适用于只需要小范围取值的场景，如布尔标记字段（`is_deleted`、`is_valid`）和状态标记字段，其取值范围为-128到127，对于`tinyint(1)`在MySQL中等同于布尔值，可以存储0或1。

### 4.2 字符串类型选择

字符串类型的选择需要综合考虑存储效率、字符集支持和对检索需求的影响。`varchar`是本项目中使用最广泛的字符串类型，适用于长度不固定或较短的字符串字段。选择`varchar`时需要指定合理的长度，长度设置应当基于业务需求而非随意设置。

不同长度的`varchar`字段适用于不同的场景：长度20适用于手机号（`mobile`）、电话（`phone`）等短字符串；长度50适用于用户名（`username`）、姓名（`real_name`）、角色代码（`role_code`）等中等长度字符串；长度100适用于邮箱（`email`）、简称（`short_name`）等稍长字符串；长度255适用于名称类字段（`enterprise_name`、`full_name`）和地址类字段；长度500或更长适用于描述类字段（`description`、`remarks`）和路径类字段（`file_path`）。

对于长度超过1000的长文本，应使用`text`类型，如债权事实（`claim_facts`）、证据材料（`evidence_materials`）等字段。`text`类型在存储和检索上有其特殊性，需要注意相关的性能影响。

### 4.3 日期时间类型选择

日期时间类型的选择取决于字段的精度需求和存储特性。`datetime`类型适用于需要存储精确时间的场景，如创建时间（`create_time`）、修改时间（`update_time`）、登录时间（`login_time`）等。`datetime`类型的取值范围为'1000-01-01 00:00:00'到'9999-12-31 23:59:59'，精度可达秒级，可以存储日期和时间信息。

`date`类型适用于只需要存储日期的场景，如成立日期（`establishment_date`）、受理日期（`acceptance_date`）、开户日期（`opening_date`）等。`date`类型占用存储空间更小，查询效率更高，适合只需要日期信息的场景。

对于需要存储时间戳的场景，如过期时间（`expire_time`）、登录时间（`last_login_time`）等，使用`datetime`类型可以直观地表示具体的时间点。在设置默认值时，`CURRENT_TIMESTAMP`表示使用当前时间戳，`DEFAULT NULL`表示允许为空。

### 4.4 数值类型选择

数值类型用于存储需要精确计算的数值数据。`decimal`类型是存储货币金额和需要精确计算数值的不二选择，如注册资本（`registered_capital`）、本金（`principal`）、利息（`interest`）、余额（`current_balance`）等字段。`decimal`类型的精度由长度和小数位数共同决定，格式为`decimal(precision, scale)`，其中`precision`表示总位数，`scale`表示小数位数。

本项目中金额字段统一使用`decimal(18,2)`，表示总共18位数字，其中2位小数，可以精确存储最大999,999,999,999,999.99的金额，满足破产清算等业务场景中大额资金的管理需求。使用`decimal`类型而非`float`或`double`可以避免浮点数运算中的精度丢失问题，确保财务数据的准确性。

---

## 5 字符集与排序规则

### 5.1 字符集选择

本项目统一使用`utf8mb4`字符集，这是MySQL中最完整的Unicode字符集支持。`utf8mb4`支持存储所有Unicode字符，包括表情符号（Emoji）和一些特殊符号，这对于处理用户输入的多样性至关重要。相比早期的`utf8`字符集，`utf8mb4`解决了4字节UTF-8字符的存储问题，是现代MySQL应用的首选字符集。

字符集应在表级别或数据库级别统一设置，确保整个数据库的一致性。在创建表时显式指定字符集可以避免依赖默认配置带来的不确定性。字符集的设置应在`CREATE TABLE`语句中明确声明，如`DEFAULT CHARSET=utf8mb4`。

### 5.2 排序规则选择

排序规则（Collation）决定了字符数据的比较和排序方式。本项目主要使用两种排序规则：`utf8mb4_bin`和`utf8mb4_unicode_ci`。`utf8mb4_bin`将字符串按照二进制方式比较，区分大小写，适用于需要精确匹配的字段，如用户名（`username`）、邮箱（`email`）等登录凭证相关的字段，确保用户名的唯一性和区分大小写的验证。

`utf8mb4_unicode_ci`按照Unicode规范进行不区分大小写的比较，适用于名称类字段的模糊查询，如企业名称（`enterprise_name`）、法院全称（`full_name`）等。`unicode_ci`排序规则在处理中文字符时具有良好的排序效果，按照拼音顺序排列中文字符，便于用户浏览和查找。

在选择排序规则时，需要权衡区分大小写的需求和查询便利性。对于需要精确匹配的认证字段，建议使用`bin`排序规则；对于需要进行模糊搜索的名称类字段，建议使用`unicode_ci`排序规则。在表定义中，排序规则应与字符集一起明确指定，如`COLLATE=utf8mb4_bin`。

---

## 6 索引创建原则

### 6.1 索引命名规范

索引命名应当清晰反映索引的用途和类型，便于后续的维护和优化。本项目采用统一的索引命名规范：唯一索引使用`uk_`前缀（Unique Key的缩写），普通索引使用`idx_`前缀（Index的缩写）。索引名应包含所涉及的字段名，当包含多个字段时，字段名之间用下划线分隔。

常见的索引命名示例：`uk_username`表示username字段的唯一索引，`uk_email`表示email字段的唯一索引，`idx_status`表示status字段的普通索引，`idx_user_id`表示user_id字段的普通索引。对于联合索引，索引名应包含所有涉及的字段名，如`uk_user_role`表示user_id和role_id的联合唯一索引，`idx_case_id_create_time`表示case_id和create_time的联合普通索引。

### 6.2 主键索引

主键自动创建唯一索引，索引名为`PRIMARY`。主键索引是表中最重要的索引，所有查询都应尽可能利用主键索引进行数据检索。主键应选择能够唯一标识记录的字段，本项目中统一使用自增的`id`字段作为主键，这种设计具有以下优势：主键值有序插入，避免了随机插入导致的页分裂问题；主键索引紧凑高效，占用存储空间小；自增主键的B+树结构稳定，查询性能稳定。

### 6.3 唯一索引创建

唯一索引用于保证字段或字段组合的唯一性，防止重复数据的产生。唯一索引的创建应基于业务需求，对业务上要求唯一性的字段建立唯一索引。用户相关表中的`username`、`mobile`、`email`等登录凭证字段应建立唯一索引，确保每个用户账号、手机号、邮箱的唯一性。配置表中的`config_key`字段应建立唯一索引，防止配置键重复。编号类字段如`case_number`（案号）、`plan_number`（单据号）应建立唯一索引。

### 6.4 普通索引创建

普通索引用于加速查询速度，应建立在经常用于查询条件的字段上。状态字段（`status`）应建立索引，支持状态的快速筛选。创建时间字段（`create_time`）应建立索引，支持按时间排序和范围查询。外键字段（如`user_id`、`case_id`）应建立索引，支持关联查询的性能优化。频繁出现在WHERE子句、ORDER BY子句、GROUP BY子句中的字段应考虑建立索引。

### 6.5 联合索引创建

联合索引是指包含多个字段的索引，适用于多条件组合查询的场景。创建联合索引时，应遵循最左前缀原则，将选择性高（区分度高）的字段放在前面，将等值查询的字段放在范围查询字段前面。例如，查询条件经常包含`status`和`create_time`时，可以创建联合索引`idx_status_create_time`。

关联表中的外键组合应建立联合唯一索引，如用户角色关联表的`uk_user_role(user_id, role_id)`，既保证了关联的唯一性，也加速了基于用户或角色的关联查询。在设计联合索引时，应避免创建过多的单列索引，评估查询模式后创建适当的联合索引可以减少索引数量，提高查询效率。

---

## 7 约束条件设置要求

### 7.1 主键约束

主键约束是表最基本的约束，定义了记录的唯一标识符。每个表必须有且只有一个主键，主键字段的值不能为空且必须唯一。主键字段应放在表定义的第一位，这既是阅读习惯的要求，也有利于数据库优化器进行查询优化。

主键的选择应当考虑以下因素：值不应频繁更新；值应该是稳定的；值应该尽可能简短以减少索引占用空间。本项目使用自增的`bigint`类型作为主键，符合上述所有要求，是业界推荐的通用做法。

### 7.2 唯一约束

唯一约束确保字段或字段组合的值在表中唯一，但允许有空值（NULL）。唯一约束通过唯一索引实现，与唯一索引的命名规则保持一致。创建唯一约束时，应评估业务需求确定哪些字段或字段组合需要唯一性。

常见的需要唯一约束的字段包括：用户名、手机号、邮箱等登录凭证；配置键、编号等业务编码；关联表中的外键组合用于确保关联关系的唯一性。在设置唯一约束时，应考虑空值的处理逻辑，MySQL中多个空值不会被视为重复，这在某些业务场景下需要特别注意。

### 7.3 外键约束

外键约束用于维护表之间的引用完整性，确保子表中引用的父表记录存在。外键约束的命名采用`fk_`前缀，加上关联信息，格式为`fk_表名_关联表名`或`fk_表名_字段名`。例如，`fk_user_role_user`表示`tb_user_role`表关联`tb_user`表的外键约束，`fk_fund_account_case`表示`tb_fund_account`表关联`tb_bankrupt_case`表的外键约束。

外键约束的级联行为需要根据业务需求谨慎设置。`ON DELETE CASCADE`表示当父表记录被删除时，自动删除子表中引用该记录的关联记录，适用于关联表和从属表的场景。`ON DELETE SET NULL`表示当父表记录被删除时，将子表的外键字段设为NULL，适用于外键字段允许为空的场景。`ON UPDATE CASCADE`表示当父表主键更新时，自动更新子表的外键值。

### 7.4 非空约束

非空约束（NOT NULL）用于确保字段必须有值，不允许为空。对于业务上必须有值的字段，如用户名、密码、角色代码等，应设置`NOT NULL`约束。对于允许为空的字段，如可选的联系方式、描述信息等，不应设置`NOT NULL`约束。

合理使用非空约束可以提高数据质量，减少空值判断的代码复杂度。在设计表结构时，应明确每个字段是否允许为空，并在建表时通过`NOT NULL`或`DEFAULT NULL`明确声明。默认值（DEFAULT）可以与`NOT NULL`配合使用，为字段提供默认值填充。

### 7.5 默认值约束

默认值约束为字段提供默认值，当插入新记录时如果未指定该字段的值，则使用默认值填充。默认值的使用场景包括：状态字段的默认值（如`DEFAULT 'ACTIVE'`）；标记字段的默认值（如`is_deleted`默认`0`）；时间戳字段的默认值（如`DEFAULT CURRENT_TIMESTAMP`）；数值字段的默认值（如`login_count`默认`0`）。

默认值应设置为符合业务逻辑的合理值。对于状态类字段，默认值应为最常见或最安全的值；对于时间戳字段，`CURRENT_TIMESTAMP`是最佳选择；对于数值字段，0通常是合理的默认值。默认值应与字段类型匹配，避免类型转换带来的潜在问题。

---

## 8 注释编写规范

### 8.1 表注释规范

每个表都应添加表级别的注释，说明表的用途和业务含义。表注释应在`CREATE TABLE`语句的末尾添加，格式为`COMMENT='表注释'`。表注释应当简洁明了，用一到两句话准确描述表的业务用途，例如`COMMENT='用户表'`、`COMMENT='案件信息表'`、`COMMENT='资金流水表'`。

表注释应当随着表结构的变化及时更新，确保注释始终准确反映表的当前状态和用途。在团队协作中，清晰的表注释可以帮助其他开发人员快速理解表的功能和数据结构，降低沟通成本。

### 8.2 字段注释规范

每个字段都应添加注释，说明字段的含义和取值规范。字段注释紧跟在字段定义后面，格式为`COMMENT '字段注释'`。字段注释应当详细准确，特别对于枚举类字段，应列出所有可能的取值及其含义。

示例：
```sql
`status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
`is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
```

对于外键字段，注释应说明关联的表和字段，例如`COMMENT '用户ID'`。对于金额字段，注释应说明计量单位，例如`COMMENT '金额（元）'`。对于日期时间字段，注释应说明时间的含义和格式，例如`COMMENT '创建时间'`。

### 8.3 索引注释规范

索引注释不是MySQL的标准功能，但可以在CREATE TABLE语句中通过COMMENT关键字为索引添加注释。在复杂的数据库设计中，为重要索引添加注释有助于后续的维护和优化工作。索引注释应当说明索引的用途和创建原因，例如`KEY idx_user_id (user_id) COMMENT '用于用户相关查询'`。

### 8.4 约束注释规范

外键约束是重要的数据库对象，为外键约束添加注释可以清晰地说明表之间的关联关系和业务规则。虽然外键约束的注释需要通过ALTER TABLE语句单独添加，但在设计文档中应当记录约束的业务含义和级联规则，便于开发人员理解数据关系。

---

## 9 存储引擎与表属性

### 9.1 存储引擎选择

本项目统一使用`InnoDB`存储引擎。InnoDB是MySQL的默认存储引擎，具有以下优势：支持事务（ACID特性），确保数据的一致性和可靠性；支持行级锁定，在高并发场景下提供更好的性能；支持外键约束，便于维护数据的引用完整性；支持崩溃恢复，异常情况下可以恢复到一致状态。

在CREATE TABLE语句中，应明确指定存储引擎：`ENGINE=InnoDB`。虽然InnoDB是MySQL 5.7以后的默认引擎，但显式指定可以避免因配置变更导致的不确定性。

### 9.2 表注释

表注释是表属性的一部分，用于描述表的用途和相关信息。表注释应当包含以下信息：表的业务功能描述；创建日期和创建者（可以在注释中注明）；表的用途说明和特殊注意事项。表注释应在CREATE TABLE语句中指定，格式为`COMMENT='表注释'`。

### 9.3 表的字符集和排序规则

表的字符集和排序规则应在CREATE TABLE语句中统一指定，确保表内所有字符类型字段的一致性。推荐使用以下配置：

```sql
DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
```

字符集和排序规则的选择应当与项目的整体设计保持一致，避免同一表中混用不同的字符集和排序规则，这可能导致比较和排序行为不一致的问题。

---

## 10 SQL语句格式规范

### 10.1 建表语句模板

为确保建表语句的一致性和规范性，提供以下标准模板：

```sql
-- 表结构描述注释
DROP TABLE IF EXISTS `表名`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `表名` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  -- 业务字段定义
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  -- 索引定义
  UNIQUE KEY `uk_xxx` (`xxx`),
  KEY `idx_xxx` (`xxx`),
  -- 外键约束
  CONSTRAINT `fk_xxx` FOREIGN KEY (`xxx_id`) REFERENCES `关联表` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='表注释';
/*!40101 SET character_set_client = @saved_cs_client */;
```

### 10.2 关键字大小写规范

SQL关键字应使用大写，以提高可读性和代码的规范性。常见的需要大写的关键字包括：`CREATE`、`TABLE`、`DROP`、`IF`、`EXISTS`、`NOT`、`NULL`、`AUTO_INCREMENT`、`DEFAULT`、`COMMENT`、`PRIMARY`、`KEY`、`UNIQUE`、`CONSTRAINT`、`FOREIGN`、`REFERENCES`、`ENGINE`、`CHARSET`、`COLLATE`等。

表名和字段名使用反引号（`）括起来，这是MySQL的标识符引用方式。反引号可以避免关键字冲突，同时允许使用保留字作为标识符（如`status`在某些数据库中是保留字）。

### 10.3 缩进与换行规范

良好的代码格式可以显著提高SQL语句的可读性。字段定义应每个字段占一行，缩进2个空格。字段之间用逗号分隔，逗号放在字段定义的末尾。括号内第一个字段不缩进，后续字段缩进2个空格。索引定义和约束定义各占一行，缩进2个空格。

```sql
CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '用户账号',
  `password` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '用户密码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户表';
```

### 10.4 注释规范

SQL语句中的注释用于说明代码的意图和业务规则。单行注释使用`--`，多行注释使用`/* */`。表结构开始处应添加注释说明表的用途。复杂字段或约束应添加注释说明其含义。

示例：
```sql
-- 用户表
DROP TABLE IF EXISTS `tb_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
```

### 10.5 字符集与兼容性设置

为了确保SQL脚本的兼容性和正确执行，应在脚本开头包含必要的MySQL兼容性设置。这些设置确保脚本在不同版本的MySQL之间可以正确执行，同时设置正确的字符集环境。

```sql
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
```

在脚本末尾恢复原始设置：
```sql
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
```

---

## 11 关联表设计规范

### 11.1 多对多关系关联表

多对多关系需要通过中间关联表来实现。关联表的设计应遵循以下规范：表名由两个关联实体的名称组成，顺序按字母排列或按主从关系排列；必须包含两个外键字段分别引用关联的两个主表；建立联合唯一索引确保关联关系的唯一性；包含标准的审计字段（create_time、update_time等）。

用户角色关联表示例：
```sql
CREATE TABLE `tb_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户角色关联表';
```

### 11.2 一对多关系外键设计

一对多关系在多的一方表中外键指向一的一方。外键字段允许为空时，使用`ON DELETE SET NULL`策略；外键字段不允许为空时，使用`ON DELETE CASCADE`策略。`ON UPDATE CASCADE`通常保持一致。

案件与管理人关系示例：
```sql
KEY `idx_case_id` (`case_id`),
CONSTRAINT `fk_administrator_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
```

---

## 12 审计字段规范

### 12.1 审计字段清单

所有业务表应包含以下审计字段，这些字段共同构成了完整的审计追踪体系：

| 字段名 | 数据类型 | 默认值 | 用途 |
|--------|----------|--------|------|
| `id` | bigint | AUTO_INCREMENT | 主键ID |
| `create_time` | datetime | CURRENT_TIMESTAMP | 创建时间 |
| `update_time` | datetime | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 修改时间 |
| `create_user_id` | bigint | NULL | 创建者ID |
| `update_user_id` | bigint | NULL | 修改者ID |
| `is_deleted` | tinyint(1) | 0 | 软删除标记 |
| `status` | varchar(20) | 'ACTIVE' | 状态 |

### 12.2 审计字段排列顺序

审计字段在表定义中的位置应遵循以下顺序：首先放置主键字段（`id`）；然后放置业务字段；最后放置审计字段（`create_time`、`update_time`、`create_user_id`、`update_user_id`、`is_deleted`、`status`）。这种排列顺序将核心标识字段和业务核心字段放在前面，便于阅读和理解。

### 12.3 审计字段说明

`create_time`和`update_time`字段使用`CURRENT_TIMESTAMP`作为默认值，可以自动记录数据的时间信息。`update_time`额外添加`ON UPDATE CURRENT_TIMESTAMP`，确保每次数据更新时该字段都会自动更新。这两个字段共同提供了完整的时间线信息，可以追溯每条记录的生命周期。

`create_user_id`和`update_user_id`字段记录操作人员的用户ID，便于审计追踪。这两个字段允许为空，因为历史数据可能没有这些信息。通过关联查询`tb_user`表，可以获取操作人员的详细信息。

`is_deleted`字段实现软删除功能，这是现代应用推荐的数据删除方式。相比物理删除，软删除保留了数据的历史记录，便于数据追溯和恢复。查询时需要添加`is_deleted = 0`的条件来排除已删除的记录。

`status`字段表示记录的当前状态，通过状态值的变化来管理记录的生命周期。常见的状态值包括`ACTIVE`（激活/有效）、`INACTIVE`（停用/无效）、`DELETED`（已删除）等。通过状态值的筛选可以实现数据的多状态管理。

---

## 13 最佳实践总结

### 13.1 设计阶段最佳实践

在数据库设计阶段，应当遵循以下最佳实践：首先进行充分的业务分析，明确数据实体、属性和关系；绘制ER图可视化数据模型，便于沟通和评审；为每个字段选择合适的数据类型和长度，避免过度设计；预估数据量和查询性能，提前规划索引策略；考虑未来的扩展性，避免频繁的表结构变更。

### 13.2 开发阶段最佳实践

在数据库开发阶段，应当遵循以下最佳实践：严格按照本规范编写SQL语句，确保代码的一致性和可读性；编写完整的注释，包括表注释、字段注释和关键代码注释；进行充分的测试，验证表的创建、查询、更新和删除操作；编写数据初始化脚本和迁移脚本，便于环境部署和数据同步。

### 13.3 维护阶段最佳实践

在数据库维护阶段，应当遵循以下最佳实践：记录所有表结构变更，使用版本管理工具管理SQL脚本；定期审查索引使用情况，优化或删除无效索引；监控数据库性能，及时发现和解决性能问题；备份重要数据，确保数据安全；编写和维护数据库设计文档，保持文档与实际结构的一致性。

---

## 14 规范执行检查清单

在提交新的数据库表之前，应当按照以下清单进行检查：

- [ ] 表名是否以`tb_`开头，使用小写字母和下划线命名
- [ ] 是否包含主键字段`id`（bigint, AUTO_INCREMENT）
- [ ] 是否包含所有审计字段（create_time, update_time, create_user_id, update_user_id, is_deleted, status）
- [ ] 字段是否都添加了中文注释，注释是否清晰完整
- [ ] 字符集是否使用`utf8mb4`，排序规则是否正确选择
- [ ] 是否选择了正确的存储引擎（InnoDB）
- [ ] 是否添加了必要的索引，索引命名是否符合规范
- [ ] 是否添加了外键约束，约束命名和级联规则是否正确
- [ ] 是否添加了表注释
- [ ] SQL语句格式是否符合规范（关键字大写、正确缩进等）

---

## 15 版本信息

| 版本 | 日期 | 描述 |
|------|------|------|
| 1.0 | 2026-01-08 | 初始版本，基于现有数据库结构分析制定 |

---

本文档将随着项目的发展和经验的积累持续更新和完善。如有疑问或建议，请联系数据库管理员或技术负责人。
# MySQL数据库表新增规范与格式标准

## 1 概述

本文档旨在为项目数据库表的新增、修改和维护工作制定统一的规范与标准。通过遵循这些规范，可以确保数据库结构的一致性、可读性和可维护性，同时提高开发团队的协作效率。所有参与数据库设计与开发的人员都应严格遵守本规范中的各项要求。

本规范基于对现有数据库结构（`law_schema.sql`）的分析总结，涵盖了从表命名到SQL语句格式的各个方面。这些规范不仅考虑了数据库的性能优化需求，也兼顾了实际业务场景的复杂性，力求在规范性与灵活性之间取得平衡。

---

## 2 表命名规范

### 2.1 基本命名规则

表名是数据库对象的重要组成部分，良好的表命名能够直观地反映表的用途和数据特征。表名必须全部使用小写字母，并采用下划线分隔单词，这种命名方式具有以下优势：首先，小写字母可以避免因大小写敏感导致的跨平台兼容性问题；其次，下划线分隔的方式便于阅读和理解，尤其对于包含多个单词的表名效果更为明显。

表名的命名应当遵循以下核心原则：**表名必须以`tb_`前缀开头**，这是本项目数据库表命名的统一前缀，用于区分不同项目的表并便于识别和管理。例如，用户表命名为`tb_user`，角色表命名为`tb_role`，权限表命名为`tb_permission`。这种前缀命名方式源自现有数据库的实践，能够有效避免表名冲突，特别是在多个系统共用数据库或未来需要进行数据库合并的场景下尤为重要。

### 2.2 表名命名约定

表名的单词选择应当准确反映表的业务含义，避免使用模糊或过于简略的名称。每个单词都应具有明确的含义，不建议使用缩写形式，除非该缩写已成为业界通用的标准术语（如`info`代表`information`、`desc`代表`description`等）。表名的长度应控制在合理范围内，建议不超过30个字符，过长的表名不仅书写不便，还会影响SQL语句的可读性。

对于表示实体对象的表，应使用单数名词形式，如`tb_user`而非`tb_users`，因为一张表代表的是用户这一类实体的集合，而非多个独立的用户。这种命名方式与主流的ORM框架（如MyBatis、Hibernate）的默认映射规则相一致，可以减少配置工作量。对于关联表（表示多对多关系的中间表），应包含两个关联实体的名称，名称之间用下划线分隔，顺序按照字母排序或按照主从关系排列，例如表示用户和角色关联的表命名为`tb_user_role`。

### 2.3 特殊表命名规则

某些特殊功能的表需要遵循额外的命名约定。关联表用于存储两个实体之间的多对多关系，表名应包含两个关联实体的名称，顺序按照字母先后排列，例如用户角色关联表`tb_user_role`、角色权限关联表`tb_role_permission`。日志表用于记录系统操作历史，表名应包含`log`或`record`等后缀以表明其用途，如`tb_login_record`（登录记录表）、`tb_fund_operation_log`（资金操作日志表）。

配置表用于存储系统运行所需的配置信息，应包含`config`或`setting`等后缀，如`tb_system_config`（系统配置表）。字典表或枚举表用于存储可选值列表，应包含`dict`或`type`等后缀，如`tb_dictionary_type`（字典类型表）。统计表或报表表用于存储统计汇总数据，应包含`statistics`、`report`或`summary`等后缀，以明确区分于业务数据表。

---

## 3 字段设计规范

### 3.1 主键字段设计

每个表都必须定义一个主键，主键字段统一命名为`id`，数据类型为`bigint`，并设置自增属性`AUTO_INCREMENT`。使用`bigint`作为主键类型可以满足绝大多数业务场景的需求，即使在高并发、大数据量的环境下也能提供充足的主键空间。自增属性确保了主键值的唯一性和顺序性，同时由数据库自动管理可以避免应用层手动赋值可能带来的重复或冲突问题。

对于主键字段，添加`NOT NULL`约束是必须的，因为主键的本质要求就是非空且唯一。主键字段应当作为表的第一个字段，这不仅符合常规的阅读习惯，也有利于数据库优化器进行查询优化。主键字段必须添加详细的中文注释，说明其含义和用途，注释内容应简洁明了，如`COMMENT '主键ID'`。

### 3.2 通用字段设计

为了满足审计追踪和数据管理的需求，所有业务表都应包含以下通用字段，这些字段的设计在现有数据库中得到了充分体现。`create_time`字段用于记录数据创建时间，数据类型为`datetime`，默认值设为`CURRENT_TIMESTAMP`，表示记录创建时自动填充当前时间。`update_time`字段用于记录数据最后修改时间，数据类型为`datetime`，默认值设为`CURRENT_TIMESTAMP`，并添加`ON UPDATE CURRENT_TIMESTAMP`修饰，表示记录更新时自动更新为当前时间。

`create_user_id`字段记录创建者的用户ID，数据类型为`bigint`，允许为空，表示创建记录的操作人员。`update_user_id`字段记录最后修改者的用户ID，数据类型为`bigint`，允许为空。这两个字段的设计支持后续的业务审计需求，可以通过关联查询追溯每条记录的创建者和修改者。`is_deleted`字段用于实现软删除功能，数据类型为`tinyint(1)`，默认值设为`0`，通过该字段标记记录是否已被删除，而非物理删除数据，这种设计可以保留历史数据便于追溯和分析。

### 3.3 状态字段设计

状态字段用于表示记录的业务状态，命名应使用`status`、`state`或`is_xxx`等形式。`status`字段命名适用于表示多状态的情况，如`ACTIVE`（激活）、`INACTIVE`（停用）、`DELETED`（删除）等。数据类型建议使用`varchar(20)`，以支持状态值的扩展和描述性命名。字段注释必须详细说明所有可能的状态值及其含义，例如`COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除'`。

对于布尔类型的状态，使用`is_`前缀命名，数据类型为`tinyint(1)`，0和1分别表示两种相反的状态。注释中应明确说明0和1的具体含义，例如`COMMENT '是否删除: 0-否, 1-是'`。状态字段应当建立索引以支持状态的快速筛选和统计查询，索引命名遵循`idx_status`的形式。

### 3.4 外键字段设计

外键字段用于建立表与表之间的关联关系，命名应准确反映所关联的表名。外键字段名通常由关联表名的核心部分加`_id`后缀组成，例如关联用户表的字段命名为`user_id`，关联角色表的字段命名为`role_id`，关联案件表的字段命名为`case_id`。这种命名方式直观明了，便于理解字段的业务含义。

外键字段的数据类型必须与被关联表的主键类型一致，本项目中统一使用`bigint`类型。外键字段的注释应说明其所关联的表和字段，例如`COMMENT '用户ID'`。在定义外键约束时，应合理设置级联操作规则，`ON DELETE`和`ON UPDATE`行为需要根据具体的业务需求决定。常用的设置包括`ON DELETE CASCADE`（级联删除）和`ON UPDATE CASCADE`（级联更新），以及`ON DELETE SET NULL`（删除置空）等。

---

## 4 数据类型选择标准

### 4.1 整数类型选择

整数类型的的选择应根据字段的实际取值范围和业务需求来确定。`bigint`是本项目中使用最广泛的整数类型，适用于主键（`id`）、用户ID（`user_id`）、角色ID（`role_id`）等需要大范围取值的字段，其取值范围为-9,223,372,036,854,775,808到9,223,372,036,854,775,807，足以满足绝大多数业务场景的需求。

`int`适用于取值范围相对较小的场景，如序号（`sort_order`）、登录次数（`login_count`）、审核次数（`review_count`）等字段。`tinyint`适用于只需要小范围取值的场景，如布尔标记字段（`is_deleted`、`is_valid`）和状态标记字段，其取值范围为-128到127，对于`tinyint(1)`在MySQL中等同于布尔值，可以存储0或1。

### 4.2 字符串类型选择

字符串类型的选择需要综合考虑存储效率、字符集支持和对检索需求的影响。`varchar`是本项目中使用最广泛的字符串类型，适用于长度不固定或较短的字符串字段。选择`varchar`时需要指定合理的长度，长度设置应当基于业务需求而非随意设置。

不同长度的`varchar`字段适用于不同的场景：长度20适用于手机号（`mobile`）、电话（`phone`）等短字符串；长度50适用于用户名（`username`）、姓名（`real_name`）、角色代码（`role_code`）等中等长度字符串；长度100适用于邮箱（`email`）、简称（`short_name`）等稍长字符串；长度255适用于名称类字段（`enterprise_name`、`full_name`）和地址类字段；长度500或更长适用于描述类字段（`description`、`remarks`）和路径类字段（`file_path`）。

对于长度超过1000的长文本，应使用`text`类型，如债权事实（`claim_facts`）、证据材料（`evidence_materials`）等字段。`text`类型在存储和检索上有其特殊性，需要注意相关的性能影响。

### 4.3 日期时间类型选择

日期时间类型的选择取决于字段的精度需求和存储特性。`datetime`类型适用于需要存储精确时间的场景，如创建时间（`create_time`）、修改时间（`update_time`）、登录时间（`login_time`）等。`datetime`类型的取值范围为'1000-01-01 00:00:00'到'9999-12-31 23:59:59'，精度可达秒级，可以存储日期和时间信息。

`date`类型适用于只需要存储日期的场景，如成立日期（`establishment_date`）、受理日期（`acceptance_date`）、开户日期（`opening_date`）等。`date`类型占用存储空间更小，查询效率更高，适合只需要日期信息的场景。

对于需要存储时间戳的场景，如过期时间（`expire_time`）、登录时间（`last_login_time`）等，使用`datetime`类型可以直观地表示具体的时间点。在设置默认值时，`CURRENT_TIMESTAMP`表示使用当前时间戳，`DEFAULT NULL`表示允许为空。

### 4.4 数值类型选择

数值类型用于存储需要精确计算的数值数据。`decimal`类型是存储货币金额和需要精确计算数值的不二选择，如注册资本（`registered_capital`）、本金（`principal`）、利息（`interest`）、余额（`current_balance`）等字段。`decimal`类型的精度由长度和小数位数共同决定，格式为`decimal(precision, scale)`，其中`precision`表示总位数，`scale`表示小数位数。

本项目中金额字段统一使用`decimal(18,2)`，表示总共18位数字，其中2位小数，可以精确存储最大999,999,999,999,999.99的金额，满足破产清算等业务场景中大额资金的管理需求。使用`decimal`类型而非`float`或`double`可以避免浮点数运算中的精度丢失问题，确保财务数据的准确性。

---

## 5 字符集与排序规则

### 5.1 字符集选择

本项目统一使用`utf8mb4`字符集，这是MySQL中最完整的Unicode字符集支持。`utf8mb4`支持存储所有Unicode字符，包括表情符号（Emoji）和一些特殊符号，这对于处理用户输入的多样性至关重要。相比早期的`utf8`字符集，`utf8mb4`解决了4字节UTF-8字符的存储问题，是现代MySQL应用的首选字符集。

字符集应在表级别或数据库级别统一设置，确保整个数据库的一致性。在创建表时显式指定字符集可以避免依赖默认配置带来的不确定性。字符集的设置应在`CREATE TABLE`语句中明确声明，如`DEFAULT CHARSET=utf8mb4`。

### 5.2 排序规则选择

排序规则（Collation）决定了字符数据的比较和排序方式。本项目主要使用两种排序规则：`utf8mb4_bin`和`utf8mb4_unicode_ci`。`utf8mb4_bin`将字符串按照二进制方式比较，区分大小写，适用于需要精确匹配的字段，如用户名（`username`）、邮箱（`email`）等登录凭证相关的字段，确保用户名的唯一性和区分大小写的验证。

`utf8mb4_unicode_ci`按照Unicode规范进行不区分大小写的比较，适用于名称类字段的模糊查询，如企业名称（`enterprise_name`）、法院全称（`full_name`）等。`unicode_ci`排序规则在处理中文字符时具有良好的排序效果，按照拼音顺序排列中文字符，便于用户浏览和查找。

在选择排序规则时，需要权衡区分大小写的需求和查询便利性。对于需要精确匹配的认证字段，建议使用`bin`排序规则；对于需要进行模糊搜索的名称类字段，建议使用`unicode_ci`排序规则。在表定义中，排序规则应与字符集一起明确指定，如`COLLATE=utf8mb4_bin`。

---

## 6 索引创建原则

### 6.1 索引命名规范

索引命名应当清晰反映索引的用途和类型，便于后续的维护和优化。本项目采用统一的索引命名规范：唯一索引使用`uk_`前缀（Unique Key的缩写），普通索引使用`idx_`前缀（Index的缩写）。索引名应包含所涉及的字段名，当包含多个字段时，字段名之间用下划线分隔。

常见的索引命名示例：`uk_username`表示username字段的唯一索引，`uk_email`表示email字段的唯一索引，`idx_status`表示status字段的普通索引，`idx_user_id`表示user_id字段的普通索引。对于联合索引，索引名应包含所有涉及的字段名，如`uk_user_role`表示user_id和role_id的联合唯一索引，`idx_case_id_create_time`表示case_id和create_time的联合普通索引。

### 6.2 主键索引

主键自动创建唯一索引，索引名为`PRIMARY`。主键索引是表中最重要的索引，所有查询都应尽可能利用主键索引进行数据检索。主键应选择能够唯一标识记录的字段，本项目中统一使用自增的`id`字段作为主键，这种设计具有以下优势：主键值有序插入，避免了随机插入导致的页分裂问题；主键索引紧凑高效，占用存储空间小；自增主键的B+树结构稳定，查询性能稳定。

### 6.3 唯一索引创建

唯一索引用于保证字段或字段组合的唯一性，防止重复数据的产生。唯一索引的创建应基于业务需求，对业务上要求唯一性的字段建立唯一索引。用户相关表中的`username`、`mobile`、`email`等登录凭证字段应建立唯一索引，确保每个用户账号、手机号、邮箱的唯一性。配置表中的`config_key`字段应建立唯一索引，防止配置键重复。编号类字段如`case_number`（案号）、`plan_number`（单据号）应建立唯一索引。

### 6.4 普通索引创建

普通索引用于加速查询速度，应建立在经常用于查询条件的字段上。状态字段（`status`）应建立索引，支持状态的快速筛选。创建时间字段（`create_time`）应建立索引，支持按时间排序和范围查询。外键字段（如`user_id`、`case_id`）应建立索引，支持关联查询的性能优化。频繁出现在WHERE子句、ORDER BY子句、GROUP BY子句中的字段应考虑建立索引。

### 6.5 联合索引创建

联合索引是指包含多个字段的索引，适用于多条件组合查询的场景。创建联合索引时，应遵循最左前缀原则，将选择性高（区分度高）的字段放在前面，将等值查询的字段放在范围查询字段前面。例如，查询条件经常包含`status`和`create_time`时，可以创建联合索引`idx_status_create_time`。

关联表中的外键组合应建立联合唯一索引，如用户角色关联表的`uk_user_role(user_id, role_id)`，既保证了关联的唯一性，也加速了基于用户或角色的关联查询。在设计联合索引时，应避免创建过多的单列索引，评估查询模式后创建适当的联合索引可以减少索引数量，提高查询效率。

---

## 7 约束条件设置要求

### 7.1 主键约束

主键约束是表最基本的约束，定义了记录的唯一标识符。每个表必须有且只有一个主键，主键字段的值不能为空且必须唯一。主键字段应放在表定义的第一位，这既是阅读习惯的要求，也有利于数据库优化器进行查询优化。

主键的选择应当考虑以下因素：值不应频繁更新；值应该是稳定的；值应该尽可能简短以减少索引占用空间。本项目使用自增的`bigint`类型作为主键，符合上述所有要求，是业界推荐的通用做法。

### 7.2 唯一约束

唯一约束确保字段或字段组合的值在表中唯一，但允许有空值（NULL）。唯一约束通过唯一索引实现，与唯一索引的命名规则保持一致。创建唯一约束时，应评估业务需求确定哪些字段或字段组合需要唯一性。

常见的需要唯一约束的字段包括：用户名、手机号、邮箱等登录凭证；配置键、编号等业务编码；关联表中的外键组合用于确保关联关系的唯一性。在设置唯一约束时，应考虑空值的处理逻辑，MySQL中多个空值不会被视为重复，这在某些业务场景下需要特别注意。

### 7.3 外键约束

外键约束用于维护表之间的引用完整性，确保子表中引用的父表记录存在。外键约束的命名采用`fk_`前缀，加上关联信息，格式为`fk_表名_关联表名`或`fk_表名_字段名`。例如，`fk_user_role_user`表示`tb_user_role`表关联`tb_user`表的外键约束，`fk_fund_account_case`表示`tb_fund_account`表关联`tb_bankrupt_case`表的外键约束。

外键约束的级联行为需要根据业务需求谨慎设置。`ON DELETE CASCADE`表示当父表记录被删除时，自动删除子表中引用该记录的关联记录，适用于关联表和从属表的场景。`ON DELETE SET NULL`表示当父表记录被删除时，将子表的外键字段设为NULL，适用于外键字段允许为空的场景。`ON UPDATE CASCADE`表示当父表主键更新时，自动更新子表的外键值。

### 7.4 非空约束

非空约束（NOT NULL）用于确保字段必须有值，不允许为空。对于业务上必须有值的字段，如用户名、密码、角色代码等，应设置`NOT NULL`约束。对于允许为空的字段，如可选的联系方式、描述信息等，不应设置`NOT NULL`约束。

合理使用非空约束可以提高数据质量，减少空值判断的代码复杂度。在设计表结构时，应明确每个字段是否允许为空，并在建表时通过`NOT NULL`或`DEFAULT NULL`明确声明。默认值（DEFAULT）可以与`NOT NULL`配合使用，为字段提供默认值填充。

### 7.5 默认值约束

默认值约束为字段提供默认值，当插入新记录时如果未指定该字段的值，则使用默认值填充。默认值的使用场景包括：状态字段的默认值（如`DEFAULT 'ACTIVE'`）；标记字段的默认值（如`is_deleted`默认`0`）；时间戳字段的默认值（如`DEFAULT CURRENT_TIMESTAMP`）；数值字段的默认值（如`login_count`默认`0`）。

默认值应设置为符合业务逻辑的合理值。对于状态类字段，默认值应为最常见或最安全的值；对于时间戳字段，`CURRENT_TIMESTAMP`是最佳选择；对于数值字段，0通常是合理的默认值。默认值应与字段类型匹配，避免类型转换带来的潜在问题。

---

## 8 注释编写规范

### 8.1 表注释规范

每个表都应添加表级别的注释，说明表的用途和业务含义。表注释应在`CREATE TABLE`语句的末尾添加，格式为`COMMENT='表注释'`。表注释应当简洁明了，用一到两句话准确描述表的业务用途，例如`COMMENT='用户表'`、`COMMENT='案件信息表'`、`COMMENT='资金流水表'`。

表注释应当随着表结构的变化及时更新，确保注释始终准确反映表的当前状态和用途。在团队协作中，清晰的表注释可以帮助其他开发人员快速理解表的功能和数据结构，降低沟通成本。

### 8.2 字段注释规范

每个字段都应添加注释，说明字段的含义和取值规范。字段注释紧跟在字段定义后面，格式为`COMMENT '字段注释'`。字段注释应当详细准确，特别对于枚举类字段，应列出所有可能的取值及其含义。

示例：
```sql
`status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
`is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
```

对于外键字段，注释应说明关联的表和字段，例如`COMMENT '用户ID'`。对于金额字段，注释应说明计量单位，例如`COMMENT '金额（元）'`。对于日期时间字段，注释应说明时间的含义和格式，例如`COMMENT '创建时间'`。

### 8.3 索引注释规范

索引注释不是MySQL的标准功能，但可以在CREATE TABLE语句中通过COMMENT关键字为索引添加注释。在复杂的数据库设计中，为重要索引添加注释有助于后续的维护和优化工作。索引注释应当说明索引的用途和创建原因，例如`KEY idx_user_id (user_id) COMMENT '用于用户相关查询'`。

### 8.4 约束注释规范

外键约束是重要的数据库对象，为外键约束添加注释可以清晰地说明表之间的关联关系和业务规则。虽然外键约束的注释需要通过ALTER TABLE语句单独添加，但在设计文档中应当记录约束的业务含义和级联规则，便于开发人员理解数据关系。

---

## 9 存储引擎与表属性

### 9.1 存储引擎选择

本项目统一使用`InnoDB`存储引擎。InnoDB是MySQL的默认存储引擎，具有以下优势：支持事务（ACID特性），确保数据的一致性和可靠性；支持行级锁定，在高并发场景下提供更好的性能；支持外键约束，便于维护数据的引用完整性；支持崩溃恢复，异常情况下可以恢复到一致状态。

在CREATE TABLE语句中，应明确指定存储引擎：`ENGINE=InnoDB`。虽然InnoDB是MySQL 5.7以后的默认引擎，但显式指定可以避免因配置变更导致的不确定性。

### 9.2 表注释

表注释是表属性的一部分，用于描述表的用途和相关信息。表注释应当包含以下信息：表的业务功能描述；创建日期和创建者（可以在注释中注明）；表的用途说明和特殊注意事项。表注释应在CREATE TABLE语句中指定，格式为`COMMENT='表注释'`。

### 9.3 表的字符集和排序规则

表的字符集和排序规则应在CREATE TABLE语句中统一指定，确保表内所有字符类型字段的一致性。推荐使用以下配置：

```sql
DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
```

字符集和排序规则的选择应当与项目的整体设计保持一致，避免同一表中混用不同的字符集和排序规则，这可能导致比较和排序行为不一致的问题。

---

## 10 SQL语句格式规范

### 10.1 建表语句模板

为确保建表语句的一致性和规范性，提供以下标准模板：

```sql
-- 表结构描述注释
DROP TABLE IF EXISTS `表名`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `表名` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  -- 业务字段定义
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  -- 索引定义
  UNIQUE KEY `uk_xxx` (`xxx`),
  KEY `idx_xxx` (`xxx`),
  -- 外键约束
  CONSTRAINT `fk_xxx` FOREIGN KEY (`xxx_id`) REFERENCES `关联表` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='表注释';
/*!40101 SET character_set_client = @saved_cs_client */;
```

### 10.2 关键字大小写规范

SQL关键字应使用大写，以提高可读性和代码的规范性。常见的需要大写的关键字包括：`CREATE`、`TABLE`、`DROP`、`IF`、`EXISTS`、`NOT`、`NULL`、`AUTO_INCREMENT`、`DEFAULT`、`COMMENT`、`PRIMARY`、`KEY`、`UNIQUE`、`CONSTRAINT`、`FOREIGN`、`REFERENCES`、`ENGINE`、`CHARSET`、`COLLATE`等。

表名和字段名使用反引号（`）括起来，这是MySQL的标识符引用方式。反引号可以避免关键字冲突，同时允许使用保留字作为标识符（如`status`在某些数据库中是保留字）。

### 10.3 缩进与换行规范

良好的代码格式可以显著提高SQL语句的可读性。字段定义应每个字段占一行，缩进2个空格。字段之间用逗号分隔，逗号放在字段定义的末尾。括号内第一个字段不缩进，后续字段缩进2个空格。索引定义和约束定义各占一行，缩进2个空格。

```sql
CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '用户账号',
  `password` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '用户密码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户表';
```

### 10.4 注释规范

SQL语句中的注释用于说明代码的意图和业务规则。单行注释使用`--`，多行注释使用`/* */`。表结构开始处应添加注释说明表的用途。复杂字段或约束应添加注释说明其含义。

示例：
```sql
-- 用户表
DROP TABLE IF EXISTS `tb_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
```

### 10.5 字符集与兼容性设置

为了确保SQL脚本的兼容性和正确执行，应在脚本开头包含必要的MySQL兼容性设置。这些设置确保脚本在不同版本的MySQL之间可以正确执行，同时设置正确的字符集环境。

```sql
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
```

在脚本末尾恢复原始设置：
```sql
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
```

---

## 11 关联表设计规范

### 11.1 多对多关系关联表

多对多关系需要通过中间关联表来实现。关联表的设计应遵循以下规范：表名由两个关联实体的名称组成，顺序按字母排列或按主从关系排列；必须包含两个外键字段分别引用关联的两个主表；建立联合唯一索引确保关联关系的唯一性；包含标准的审计字段（create_time、update_time等）。

用户角色关联表示例：
```sql
CREATE TABLE `tb_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户角色关联表';
```

### 11.2 一对多关系外键设计

一对多关系在多的一方表中外键指向一的一方。外键字段允许为空时，使用`ON DELETE SET NULL`策略；外键字段不允许为空时，使用`ON DELETE CASCADE`策略。`ON UPDATE CASCADE`通常保持一致。

案件与管理人关系示例：
```sql
KEY `idx_case_id` (`case_id`),
CONSTRAINT `fk_administrator_case` FOREIGN KEY (`case_id`) REFERENCES `tb_bankrupt_case` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
```

---

## 12 审计字段规范

### 12.1 审计字段清单

所有业务表应包含以下审计字段，这些字段共同构成了完整的审计追踪体系：

| 字段名 | 数据类型 | 默认值 | 用途 |
|--------|----------|--------|------|
| `id` | bigint | AUTO_INCREMENT | 主键ID |
| `create_time` | datetime | CURRENT_TIMESTAMP | 创建时间 |
| `update_time` | datetime | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 修改时间 |
| `create_user_id` | bigint | NULL | 创建者ID |
| `update_user_id` | bigint | NULL | 修改者ID |
| `is_deleted` | tinyint(1) | 0 | 软删除标记 |
| `status` | varchar(20) | 'ACTIVE' | 状态 |

### 12.2 审计字段排列顺序

审计字段在表定义中的位置应遵循以下顺序：首先放置主键字段（`id`）；然后放置业务字段；最后放置审计字段（`create_time`、`update_time`、`create_user_id`、`update_user_id`、`is_deleted`、`status`）。这种排列顺序将核心标识字段和业务核心字段放在前面，便于阅读和理解。

### 12.3 审计字段说明

`create_time`和`update_time`字段使用`CURRENT_TIMESTAMP`作为默认值，可以自动记录数据的时间信息。`update_time`额外添加`ON UPDATE CURRENT_TIMESTAMP`，确保每次数据更新时该字段都会自动更新。这两个字段共同提供了完整的时间线信息，可以追溯每条记录的生命周期。

`create_user_id`和`update_user_id`字段记录操作人员的用户ID，便于审计追踪。这两个字段允许为空，因为历史数据可能没有这些信息。通过关联查询`tb_user`表，可以获取操作人员的详细信息。

`is_deleted`字段实现软删除功能，这是现代应用推荐的数据删除方式。相比物理删除，软删除保留了数据的历史记录，便于数据追溯和恢复。查询时需要添加`is_deleted = 0`的条件来排除已删除的记录。

`status`字段表示记录的当前状态，通过状态值的变化来管理记录的生命周期。常见的状态值包括`ACTIVE`（激活/有效）、`INACTIVE`（停用/无效）、`DELETED`（已删除）等。通过状态值的筛选可以实现数据的多状态管理。

---

## 13 最佳实践总结

### 13.1 设计阶段最佳实践

在数据库设计阶段，应当遵循以下最佳实践：首先进行充分的业务分析，明确数据实体、属性和关系；绘制ER图可视化数据模型，便于沟通和评审；为每个字段选择合适的数据类型和长度，避免过度设计；预估数据量和查询性能，提前规划索引策略；考虑未来的扩展性，避免频繁的表结构变更。

### 13.2 开发阶段最佳实践

在数据库开发阶段，应当遵循以下最佳实践：严格按照本规范编写SQL语句，确保代码的一致性和可读性；编写完整的注释，包括表注释、字段注释和关键代码注释；进行充分的测试，验证表的创建、查询、更新和删除操作；编写数据初始化脚本和迁移脚本，便于环境部署和数据同步。

### 13.3 维护阶段最佳实践

在数据库维护阶段，应当遵循以下最佳实践：记录所有表结构变更，使用版本管理工具管理SQL脚本；定期审查索引使用情况，优化或删除无效索引；监控数据库性能，及时发现和解决性能问题；备份重要数据，确保数据安全；编写和维护数据库设计文档，保持文档与实际结构的一致性。

---

## 14 规范执行检查清单

在提交新的数据库表之前，应当按照以下清单进行检查：

- [ ] 表名是否以`tb_`开头，使用小写字母和下划线命名
- [ ] 是否包含主键字段`id`（bigint, AUTO_INCREMENT）
- [ ] 是否包含所有审计字段（create_time, update_time, create_user_id, update_user_id, is_deleted, status）
- [ ] 字段是否都添加了中文注释，注释是否清晰完整
- [ ] 字符集是否使用`utf8mb4`，排序规则是否正确选择
- [ ] 是否选择了正确的存储引擎（InnoDB）
- [ ] 是否添加了必要的索引，索引命名是否符合规范
- [ ] 是否添加了外键约束，约束命名和级联规则是否正确
- [ ] 是否添加了表注释
- [ ] SQL语句格式是否符合规范（关键字大写、正确缩进等）

---

## 15 版本信息

| 版本 | 日期 | 描述 |
|------|------|------|
| 1.0 | 2026-01-08 | 初始版本，基于现有数据库结构分析制定 |

---

本文档将随着项目的发展和经验的积累持续更新和完善。如有疑问或建议，请联系数据库管理员或技术负责人。
