# ShopX 多门店O2O商城系统 - 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现支持多门店管理、虚拟支付、核销码生成/核销、门店代付的H5商城系统MVP

**Architecture:** Spring Boot 3.x 单体应用 + 模块化分层，Vue3 + Vant4 前端H5，MySQL + Redis 数据存储。后端按业务域分模块（user/store/product/order/payment/verification），前端按页面分视图。

**Tech Stack:** Java 17 / Spring Boot 3.x / MyBatis-Plus / MySQL 8.x / Redis 7.x / JWT / Vue3 / Vant4 / Pinia / Vite

---

## 文件结构总览

### 后端文件结构

```
shop-server/
├── pom.xml
├── shop-common/
│   ├── pom.xml
│   └── src/main/java/com/shopx/common/
│       ├── result/Result.java
│       ├── result/ResultCode.java
│       ├── exception/BizException.java
│       ├── exception/GlobalExceptionHandler.java
│       ├── utils/JwtUtil.java
│       ├── utils/CodeGeneratorUtil.java
│       ├── utils/RedisLockUtil.java
│       └── base/BaseEntity.java
├── shop-model/
│   ├── pom.xml
│   └── src/main/java/com/shopx/model/
│       ├── entity/
│       │   ├── User.java
│       │   ├── Store.java
│       │   ├── Product.java
│       │   ├── ProductStore.java
│       │   ├── Order.java
│       │   ├── OrderItem.java
│       │   ├── VerificationCode.java
│       │   ├── VirtualPaymentConfig.java
│       │   ├── StorePaymentQuota.java
│       │   ├── Admin.java
│       │   └── OperationLog.java
│       ├── dto/
│       │   ├── OrderCreateDTO.java
│       │   ├── PaymentDTO.java
│       │   ├── VerifyDTO.java
│       │   ├── StoreDTO.java
│       │   └── ProductDTO.java
│       ├── vo/
│       │   ├── OrderVO.java
│       │   ├── VerifyCodeVO.java
│       │   ├── StoreVO.java
│       │   └── ProductVO.java
│       └── enums/
│           ├── PayType.java
│           ├── OrderStatus.java
│           ├── PayStatus.java
│           └── VerifyStatus.java
├── shop-dao/
│   ├── pom.xml
│   └── src/main/java/com/shopx/dao/
│       ├── mapper/
│       │   ├── UserMapper.java
│       │   ├── StoreMapper.java
│       │   ├── ProductMapper.java
│       │   ├── ProductStoreMapper.java
│       │   ├── OrderMapper.java
│       │   ├── OrderItemMapper.java
│       │   ├── VerificationCodeMapper.java
│       │   ├── VirtualPaymentConfigMapper.java
│       │   ├── StorePaymentQuotaMapper.java
│       │   ├── AdminMapper.java
│       │   └── OperationLogMapper.java
│       └── redis/
│           └── RedisService.java
├── shop-service/
│   ├── pom.xml
│   └── src/main/java/com/shopx/service/
│       ├── user/
│       │   ├── UserService.java
│       │   └── impl/UserServiceImpl.java
│       ├── store/
│       │   ├── StoreService.java
│       │   └── impl/StoreServiceImpl.java
│       ├── product/
│       │   ├── ProductService.java
│       │   └── impl/ProductServiceImpl.java
│       ├── order/
│       │   ├── OrderService.java
│       │   └── impl/OrderServiceImpl.java
│       ├── payment/
│       │   ├── PaymentService.java
│       │   └── impl/PaymentServiceImpl.java
│       └── verification/
│           ├── VerificationService.java
│           └── impl/VerificationServiceImpl.java
├── shop-admin/
│   ├── pom.xml
│   └── src/main/java/com/shopx/admin/
│       ├── controller/
│       │   ├── AdminAuthController.java
│       │   ├── AdminStoreController.java
│       │   ├── AdminProductController.java
│       │   ├── AdminVirtualPaymentController.java
│       │   ├── AdminVerifyController.java
│       │   └── AdminOrderController.java
│       └── config/
│           └── AdminSecurityConfig.java
├── shop-api/
│   ├── pom.xml
│   └── src/main/java/com/shopx/api/
│       ├── controller/
│       │   ├── UserAuthController.java
│       │   ├── StoreController.java
│       │   ├── ProductController.java
│       │   ├── OrderController.java
│       │   ├── PaymentController.java
│       │   └── VerifyController.java
│       └── config/
│           └── ApiSecurityConfig.java
└── shop-job/
    ├── pom.xml
    └── src/main/java/com/shopx/job/
        └── OrderExpireJob.java
```

### 前端文件结构

```
shop-h5/
├── package.json
├── vite.config.js
├── index.html
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── router/index.js
│   ├── stores/
│   │   ├── user.js
│   │   └── cart.js
│   ├── api/
│   │   ├── request.js
│   │   ├── user.js
│   │   ├── store.js
│   │   ├── product.js
│   │   ├── order.js
│   │   ├── payment.js
│   │   └── verify.js
│   ├── utils/
│   │   └── qrcode.js
│   ├── components/
│   │   ├── NavBar.vue
│   │   └── ProductCard.vue
│   └── views/
│       ├── home/
│       │   └── Index.vue
│       ├── product/
│       │   ├── List.vue
│       │   └── Detail.vue
│       ├── store/
│       │   ├── List.vue
│       │   └── Detail.vue
│       ├── order/
│       │   ├── Create.vue
│       │   ├── List.vue
│       │   └── Detail.vue
│       ├── payment/
│       │   └── Index.vue
│       ├── verification/
│       │   └── Code.vue
│       └── user/
│           ├── Login.vue
│           ├── Register.vue
│           └── Center.vue
```

---

## Task 1: 后端项目脚手架搭建

**Files:**
- Create: `shop-server/pom.xml`
- Create: `shop-server/shop-common/pom.xml`
- Create: `shop-server/shop-model/pom.xml`
- Create: `shop-server/shop-dao/pom.xml`
- Create: `shop-server/shop-service/pom.xml`
- Create: `shop-server/shop-admin/pom.xml`
- Create: `shop-server/shop-api/pom.xml`
- Create: `shop-server/shop-job/pom.xml`
- Create: `shop-server/shop-admin/src/main/resources/application.yml`
- Create: `shop-server/shop-api/src/main/resources/application.yml`

- [ ] **Step 1: 创建Maven父POM**

创建 `shop-server/pom.xml`，定义Spring Boot 3.2.x父工程，声明所有子模块，统一管理依赖版本：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
    </parent>

    <groupId>com.shopx</groupId>
    <artifactId>shop-server</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>shop-common</module>
        <module>shop-model</module>
        <module>shop-dao</module>
        <module>shop-service</module>
        <module>shop-admin</module>
        <module>shop-api</module>
        <module>shop-job</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.6</mybatis-plus.version>
        <jjwt.version>0.12.5</jjwt.version>
        <knife4j.version>4.4.0</knife4j.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
                <version>${knife4j.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

- [ ] **Step 2: 创建shop-common子模块POM**

创建 `shop-server/shop-common/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.shopx</groupId>
        <artifactId>shop-server</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>shop-common</artifactId>

    <dependencies>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.26</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 3: 创建shop-model子模块POM**

创建 `shop-server/shop-model/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.shopx</groupId>
        <artifactId>shop-server</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>shop-model</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.shopx</groupId>
            <artifactId>shop-common</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 4: 创建shop-dao子模块POM**

创建 `shop-server/shop-dao/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.shopx</groupId>
        <artifactId>shop-server</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>shop-dao</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.shopx</groupId>
            <artifactId>shop-model</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 5: 创建shop-service子模块POM**

创建 `shop-server/shop-service/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.shopx</groupId>
        <artifactId>shop-server</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>shop-service</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.shopx</groupId>
            <artifactId>shop-dao</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.zxing</groupId>
            <artifactId>core</artifactId>
            <version>3.5.3</version>
        </dependency>
        <dependency>
            <groupId>com.google.zxing</groupId>
            <artifactId>javase</artifactId>
            <version>3.5.3</version>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 6: 创建shop-admin子模块POM和启动类**

创建 `shop-server/shop-admin/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.shopx</groupId>
        <artifactId>shop-server</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>shop-admin</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.shopx</groupId>
            <artifactId>shop-service</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

创建 `shop-server/shop-admin/src/main/java/com/shopx/admin/ShopAdminApplication.java`：

```java
package com.shopx.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.shopx")
public class ShopAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopAdminApplication.class, args);
    }
}
```

- [ ] **Step 7: 创建shop-api子模块POM和启动类**

创建 `shop-server/shop-api/pom.xml`（与shop-admin类似，artifactId为shop-api）。

创建 `shop-server/shop-api/src/main/java/com/shopx/api/ShopApiApplication.java`：

```java
package com.shopx.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.shopx")
public class ShopApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApiApplication.class, args);
    }
}
```

- [ ] **Step 8: 创建shop-job子模块POM**

创建 `shop-server/shop-job/pom.xml`（依赖shop-service，含spring-boot-starter-quartz）。

- [ ] **Step 9: 创建application.yml配置文件**

创建 `shop-server/shop-api/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopx?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: localhost
      port: 6379
      database: 0

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

shopx:
  jwt:
    secret: ShopX2026SecretKeyForJwtTokenGenerationAndValidation
    access-token-expire: 7200
    refresh-token-expire: 604800
  verify-code:
    prefix: VP
    expire-hours: 168
```

- [ ] **Step 10: 验证Maven构建**

Run: `cd shop-server && mvn clean compile -DskipTests`
Expected: BUILD SUCCESS

---

## Task 2: 数据库Schema初始化

**Files:**
- Create: `shop-server/sql/schema.sql`
- Create: `shop-server/sql/init-data.sql`

- [ ] **Step 1: 创建数据库Schema SQL**

创建 `shop-server/sql/schema.sql`，包含所有11张表的DDL：

```sql
CREATE DATABASE IF NOT EXISTS shopx DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopx;

CREATE TABLE t_store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_name VARCHAR(100) NOT NULL,
    store_code VARCHAR(32) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20),
    business_hours VARCHAR(50),
    status TINYINT NOT NULL DEFAULT 1,
    longitude DECIMAL(10,6),
    latitude DECIMAL(10,6),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_store_code (store_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(200) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    stock INT NOT NULL DEFAULT 0,
    description TEXT,
    images JSON,
    store_scope TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_product_store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    UNIQUE KEY uk_product_store (product_id, store_id),
    KEY idx_product_id (product_id),
    KEY idx_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    pay_amount DECIMAL(10,2) NOT NULL,
    pay_type TINYINT NOT NULL,
    pay_status TINYINT NOT NULL DEFAULT 0,
    order_status TINYINT NOT NULL DEFAULT 0,
    proxy_store_id BIGINT,
    remark VARCHAR(500),
    expire_time DATETIME,
    pay_time DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_status (user_id, order_status),
    KEY idx_store_status (store_id, order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_verification_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(32) NOT NULL,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    used_time DATETIME,
    verified_by BIGINT,
    expire_time DATETIME NOT NULL,
    batch_no VARCHAR(32),
    version INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code),
    KEY idx_order_id (order_id),
    KEY idx_user_id (user_id),
    KEY idx_store_status (store_id, status),
    KEY idx_batch_no (batch_no),
    KEY idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_virtual_payment_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_name VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    used_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    store_id BIGINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    expire_time DATETIME,
    version INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_store_payment_quota (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    total_quota DECIMAL(10,2) NOT NULL,
    used_quota DECIMAL(10,2) NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    version INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    role TINYINT NOT NULL DEFAULT 2,
    store_id BIGINT,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator_id BIGINT NOT NULL,
    operator_type TINYINT NOT NULL,
    module VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    detail TEXT,
    ip VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_module_action (module, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

- [ ] **Step 2: 创建初始化数据SQL**

创建 `shop-server/sql/init-data.sql`：

```sql
USE shopx;

INSERT INTO t_admin (username, password, real_name, role, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 1, 1);

INSERT INTO t_store (store_name, store_code, address, contact_phone, business_hours, status)
VALUES
    ('旗舰店', 'STORE001', '上海市浦东新区陆家嘴环路1000号', '021-12345678', '09:00-21:00', 1),
    ('城西店', 'STORE002', '上海市徐汇区漕溪北路500号', '021-87654321', '09:00-21:00', 1);
```

---

## Task 3: shop-common 公共模块实现

**Files:**
- Create: `shop-server/shop-common/src/main/java/com/shopx/common/result/ResultCode.java`
- Create: `shop-server/shop-common/src/main/java/com/shopx/common/result/Result.java`
- Create: `shop-server/shop-common/src/main/java/com/shopx/common/exception/BizException.java`
- Create: `shop-server/shop-common/src/main/java/com/shopx/common/exception/GlobalExceptionHandler.java`
- Create: `shop-server/shop-common/src/main/java/com/shopx/common/base/BaseEntity.java`
- Create: `shop-server/shop-common/src/main/java/com/shopx/common/utils/JwtUtil.java`
- Create: `shop-server/shop-common/src/main/java/com/shopx/common/utils/CodeGeneratorUtil.java`
- Create: `shop-server/shop-common/src/main/java/com/shopx/common/utils/RedisLockUtil.java`

- [ ] **Step 1: 实现ResultCode枚举**

创建 `ResultCode.java`：

```java
package com.shopx.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数错误"),
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_PHONE_EXISTS(1003, "手机号已注册"),
    STORE_NOT_FOUND(2001, "门店不存在"),
    STORE_DISABLED(2002, "门店已禁用"),
    PRODUCT_NOT_FOUND(3001, "商品不存在"),
    PRODUCT_OFF_SHELF(3002, "商品已下架"),
    PRODUCT_STOCK_INSUFFICIENT(3003, "库存不足"),
    ORDER_NOT_FOUND(4001, "订单不存在"),
    ORDER_STATUS_ERROR(4002, "订单状态异常"),
    PAY_AMOUNT_ERROR(5001, "支付金额异常"),
    VIRTUAL_CONFIG_NOT_FOUND(5002, "虚拟支付配置不存在"),
    VIRTUAL_CONFIG_DISABLED(5003, "虚拟支付配置已禁用"),
    VIRTUAL_AMOUNT_INSUFFICIENT(5004, "虚拟支付额度不足"),
    STORE_QUOTA_INSUFFICIENT(5005, "门店代付额度不足"),
    VERIFY_CODE_NOT_FOUND(6001, "核销码不存在"),
    VERIFY_CODE_USED(6002, "核销码已使用"),
    VERIFY_CODE_EXPIRED(6003, "核销码已过期"),
    VERIFY_CODE_STORE_MISMATCH(6004, "核销码需在指定门店使用"),
    VERIFY_LOCK_FAILED(6005, "核销操作中，请稍后"),
    VERIFY_CODE_VOIDED(6006, "核销码已作废");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
```

- [ ] **Step 2: 实现Result统一响应**

创建 `Result.java`：

```java
package com.shopx.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result() {}

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        return result;
    }

    public static <T> Result<T> fail(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
```

- [ ] **Step 3: 实现BizException业务异常**

创建 `BizException.java`：

```java
package com.shopx.common.exception;

import com.shopx.common.result.ResultCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final ResultCode resultCode;

    public BizException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
```

- [ ] **Step 4: 实现GlobalExceptionHandler**

创建 `GlobalExceptionHandler.java`：

```java
package com.shopx.common.exception;

import com.shopx.common.result.Result;
import com.shopx.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getResultCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst()
                .orElse("参数校验失败");
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst()
                .orElse("参数绑定失败");
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ResultCode.FAIL);
    }
}
```

- [ ] **Step 5: 实现BaseEntity基础实体**

创建 `BaseEntity.java`：

```java
package com.shopx.common.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 6: 实现JwtUtil**

创建 `JwtUtil.java`，提供generateToken、parseToken、isTokenExpired方法，使用HMAC-SHA256签名。

- [ ] **Step 7: 实现CodeGeneratorUtil核销码生成工具**

创建 `CodeGeneratorUtil.java`：

```java
package com.shopx.common.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CodeGeneratorUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");

    public static String generateVerifyCode(String prefix) {
        String datePart = LocalDateTime.now().format(DATE_FMT);
        StringBuilder randomPart = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            randomPart.append(CHARS.charAt(SECURE_RANDOM.nextInt(CHARS.length())));
        }
        return prefix + datePart + randomPart;
    }

    public static String generateOrderNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        StringBuilder randomPart = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            randomPart.append(SECURE_RANDOM.nextInt(10));
        }
        return "ORD" + timestamp + randomPart;
    }

    public static String generateBatchNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        StringBuilder randomPart = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            randomPart.append(CHARS.charAt(SECURE_RANDOM.nextInt(CHARS.length())));
        }
        return "BATCH" + timestamp + randomPart;
    }
}
```

- [ ] **Step 8: 实现RedisLockUtil分布式锁工具**

创建 `RedisLockUtil.java`，提供tryLock(key, expireSeconds)和unlock(key)方法，基于Redis SETNX实现。

---

## Task 4: shop-model 数据模型实现

**Files:**
- Create: `shop-server/shop-model/src/main/java/com/shopx/model/entity/` 下所有Entity
- Create: `shop-server/shop-model/src/main/java/com/shopx/model/enums/` 下所有枚举
- Create: `shop-server/shop-model/src/main/java/com/shopx/model/dto/` 下所有DTO
- Create: `shop-server/shop-model/src/main/java/com/shopx/model/vo/` 下所有VO

- [ ] **Step 1: 实现枚举类**

创建 `PayType.java`、`OrderStatus.java`、`PayStatus.java`、`VerifyStatus.java` 枚举。

- [ ] **Step 2: 实现所有Entity类**

创建所有11个Entity类，继承BaseEntity，使用MyBatis-Plus注解（@TableName、@TableField、@TableLogic等）。关键字段：
- `Order.java`：含payType(OrderStatus枚举)、orderStatus、payStatus、proxyStoreId
- `VerificationCode.java`：含version字段（@Version乐观锁注解）
- `VirtualPaymentConfig.java`：含version字段
- `StorePaymentQuota.java`：含version字段

- [ ] **Step 3: 实现DTO类**

创建 `OrderCreateDTO.java`（含productId、quantity、storeId、payType、proxyStoreId、remark字段，加@Valid注解）、`PaymentDTO.java`、`VerifyDTO.java`、`StoreDTO.java`、`ProductDTO.java`。

- [ ] **Step 4: 实现VO类**

创建 `OrderVO.java`（含订单详情+核销码信息）、`VerifyCodeVO.java`（含code、qrcodeUrl、storeName、expireTime、status）、`StoreVO.java`、`ProductVO.java`。

---

## Task 5: shop-dao 数据访问层实现

**Files:**
- Create: `shop-server/shop-dao/src/main/java/com/shopx/dao/mapper/` 下所有Mapper
- Create: `shop-server/shop-dao/src/main/java/com/shopx/dao/redis/RedisService.java`

- [ ] **Step 1: 实现所有Mapper接口**

创建所有11个Mapper接口，继承BaseMapper，添加自定义查询方法：
- `VerificationCodeMapper.java`：添加selectByCode、selectByBatchNo方法
- `OrderMapper.java`：添加selectByOrderNo、selectExpiredOrders方法

- [ ] **Step 2: 实现RedisService**

创建 `RedisService.java`，封装Redis操作：
- set(key, value, timeout)
- get(key)
- delete(key)
- setIfAbsent(key, value, timeout) - 用于分布式锁
- hashSet / hashGet - 用于核销码缓存
- pipelineSet - 用于批量缓存

---

## Task 6: shop-service 用户模块实现

**Files:**
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/user/UserService.java`
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/user/impl/UserServiceImpl.java`

- [ ] **Step 1: 实现UserService接口**

定义register、login、getUserInfo、updateUser方法。

- [ ] **Step 2: 实现UserServiceImpl**

- register：检查手机号是否已注册 → BCrypt加密密码 → 插入用户
- login：根据手机号查用户 → BCrypt校验密码 → 生成JWT Token
- getUserInfo：根据ID查用户，手机号脱敏
- updateUser：更新昵称、头像

---

## Task 7: shop-service 门店模块实现

**Files:**
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/store/StoreService.java`
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/store/impl/StoreServiceImpl.java`

- [ ] **Step 1: 实现StoreService接口和实现类**

CRUD方法 + listStores（支持LBS排序）+ getStoreDetail。
创建门店时自动生成storeCode，设置代付额度时操作t_store_payment_quota表。

---

## Task 8: shop-service 商品模块实现

**Files:**
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/product/ProductService.java`
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/product/impl/ProductServiceImpl.java`

- [ ] **Step 1: 实现ProductService接口和实现类**

CRUD方法 + listProducts（分页）+ getProductDetail + bindStore/unbindStore。
创建商品时处理store_scope和t_product_store关联。查询商品详情时附带适用门店列表。

---

## Task 9: shop-service 订单模块实现

**Files:**
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/order/OrderService.java`
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/order/impl/OrderServiceImpl.java`

- [ ] **Step 1: 实现OrderService接口和实现类**

核心方法：
- createOrder：校验商品+门店 → 扣减库存 → 创建订单+明细 → 返回订单信息
- getOrderList：分页查询用户订单
- getOrderDetail：订单详情+核销码信息
- cancelOrder：仅待付状态可取消，恢复库存

---

## Task 10: shop-service 支付模块实现（核心）

**Files:**
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/payment/PaymentService.java`
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/payment/impl/PaymentServiceImpl.java`

- [ ] **Step 1: 实现PaymentService接口**

定义pay、virtualPay、storeProxyPay方法。

- [ ] **Step 2: 实现virtualPay虚拟支付**

```
1. 查询虚拟支付配置（store_id=0 或 store_id=订单门店ID）
2. 校验配置状态和过期时间
3. 乐观锁扣减额度：UPDATE t_virtual_payment_config SET used_amount=used_amount+#{payAmount}, version=version+1 WHERE id=#{id} AND version=#{version} AND used_amount+#{payAmount}<=total_amount
4. 扣减失败抛出VIRTUAL_AMOUNT_INSUFFICIENT异常
5. 扣减成功 → 调用VerificationService生成核销码
6. 更新订单状态为待核销
7. 返回核销码信息
```

- [ ] **Step 3: 实现storeProxyPay门店代付**

```
1. 查询门店代付额度（Redis缓存优先）
2. 乐观锁扣减额度：UPDATE t_store_payment_quota SET used_quota=used_quota+#{amount}, version=version+1 WHERE store_id=#{storeId} AND version=#{version} AND used_quota+#{amount}<=total_quota
3. 扣减失败抛出STORE_QUOTA_INSUFFICIENT异常
4. 扣减成功 → 调用VerificationService生成核销码（绑定代付门店）
5. 更新订单状态为待核销，设置proxy_store_id
6. 返回核销码信息
```

- [ ] **Step 4: 实现pay统一支付入口**

根据payType分发到不同支付方式（MVP仅实现虚拟支付和门店代付，微信/支付宝预留接口）。

---

## Task 11: shop-service 核销模块实现（核心）

**Files:**
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/verification/VerificationService.java`
- Create: `shop-server/shop-service/src/main/java/com/shopx/service/verification/impl/VerificationServiceImpl.java`

- [ ] **Step 1: 实现VerificationService接口**

定义generateCode、verifyByScan、verifyManual、batchGenerate、voidCode方法。

- [ ] **Step 2: 实现generateCode核销码生成**

```
1. 调用CodeGeneratorUtil.generateVerifyCode("VP")生成码
2. 创建VerificationCode记录（绑定orderId、storeId、expireTime）
3. 插入数据库（唯一索引防重复，重复则重新生成）
4. 缓存到Redis（verify:code:{code}，Hash结构存status/storeId/expireTime）
5. 生成二维码图片（ZXing库，内容为核销码字符串）
6. 返回VerifyCodeVO
```

- [ ] **Step 3: 实现verifyByScan扫码核销（核心并发安全逻辑）**

```java
public VerifyCodeVO verifyByScan(String code, Long storeId, Long operatorId) {
    String lockKey = "verify:lock:" + code;
    boolean locked = redisLockUtil.tryLock(lockKey, 30);
    if (!locked) {
        throw new BizException(ResultCode.VERIFY_LOCK_FAILED);
    }
    try {
        VerificationCode vc = getVerificationCodeWithCache(code);
        validateVerifyCode(vc, storeId);
        int updated = verificationCodeMapper.update(null,
            new LambdaUpdateWrapper<VerificationCode>()
                .eq(VerificationCode::getCode, code)
                .eq(VerificationCode::getVersion, vc.getVersion())
                .eq(VerificationCode::getStatus, VerifyStatus.UNUSED.getCode())
                .set(VerificationCode::getStatus, VerifyStatus.USED.getCode())
                .set(VerificationCode::getUsedTime, LocalDateTime.now())
                .set(VerificationCode::getVerifiedBy, operatorId)
                .set(VerificationCode::getVersion, vc.getVersion() + 1)
        );
        if (updated == 0) {
            throw new BizException(ResultCode.VERIFY_CODE_USED);
        }
        orderService.updateOrderStatus(vc.getOrderId(), OrderStatus.VERIFIED);
        redisService.delete("verify:code:" + code);
        logOperation(operatorId, 1, "verification", "scan_verify", code);
        return buildVerifyCodeVO(vc);
    } finally {
        redisLockUtil.unlock(lockKey);
    }
}
```

- [ ] **Step 4: 实现batchGenerate批量生成**

```
1. 生成batchNo
2. 分批循环（每批1000条）：
   a. 生成1000个核销码
   b. 批量INSERT（mybatis-plus saveBatch）
   c. Redis Pipeline缓存
3. 返回批次统计
```

- [ ] **Step 5: 实现voidCode作废核销码**

仅管理员可操作，更新status=3（已作废），记录操作日志。

---

## Task 12: shop-service 定时任务实现

**Files:**
- Create: `shop-server/shop-job/src/main/java/com/shopx/job/OrderExpireJob.java`

- [ ] **Step 1: 实现OrderExpireJob**

每分钟扫描过期订单（order_status=1 AND expire_time<NOW()），批量更新订单和核销码状态，回退虚拟支付/代付额度，清理Redis缓存。

---

## Task 13: Spring Security + JWT认证配置

**Files:**
- Create: `shop-server/shop-api/src/main/java/com/shopx/api/config/ApiSecurityConfig.java`
- Create: `shop-server/shop-api/src/main/java/com/shopx/api/filter/JwtAuthenticationFilter.java`
- Create: `shop-server/shop-admin/src/main/java/com/shopx/admin/config/AdminSecurityConfig.java`

- [ ] **Step 1: 实现JwtAuthenticationFilter**

从请求Header提取Token → JwtUtil解析 → 设置SecurityContext。

- [ ] **Step 2: 实现ApiSecurityConfig**

配置H5端安全策略：公开路径（/api/user/register、/api/user/login、/api/store/**、/api/product/**）+ 认证路径 + JWT Filter。

- [ ] **Step 3: 实现AdminSecurityConfig**

配置管理端安全策略：公开路径（/admin/auth/login）+ 认证路径 + 角色权限（超级管理员/门店管理员）。

---

## Task 14: shop-api 用户端Controller实现

**Files:**
- Create: `shop-server/shop-api/src/main/java/com/shopx/api/controller/UserAuthController.java`
- Create: `shop-server/shop-api/src/main/java/com/shopx/api/controller/StoreController.java`
- Create: `shop-server/shop-api/src/main/java/com/shopx/api/controller/ProductController.java`
- Create: `shop-server/shop-api/src/main/java/com/shopx/api/controller/OrderController.java`
- Create: `shop-server/shop-api/src/main/java/com/shopx/api/controller/PaymentController.java`
- Create: `shop-server/shop-api/src/main/java/com/shopx/api/controller/VerifyController.java`

- [ ] **Step 1: 实现UserAuthController**

register、login、getUserInfo、updateUser接口。

- [ ] **Step 2: 实现StoreController**

list（支持经纬度排序）、detail接口。

- [ ] **Step 3: 实现ProductController**

list（分页）、detail接口。

- [ ] **Step 4: 实现OrderController**

create、list、detail、cancel接口。

- [ ] **Step 5: 实现PaymentController**

pay接口（统一支付入口，根据payType分发）。

- [ ] **Step 6: 实现VerifyController**

getCode（获取核销码）、getQrcode（获取二维码图片）接口。

---

## Task 15: shop-admin 管理端Controller实现

**Files:**
- Create: `shop-server/shop-admin/src/main/java/com/shopx/admin/controller/AdminAuthController.java`
- Create: `shop-server/shop-admin/src/main/java/com/shopx/admin/controller/AdminStoreController.java`
- Create: `shop-server/shop-admin/src/main/java/com/shopx/admin/controller/AdminProductController.java`
- Create: `shop-server/shop-admin/src/main/java/com/shopx/admin/controller/AdminVirtualPaymentController.java`
- Create: `shop-server/shop-admin/src/main/java/com/shopx/admin/controller/AdminVerifyController.java`
- Create: `shop-server/shop-admin/src/main/java/com/shopx/admin/controller/AdminOrderController.java`

- [ ] **Step 1: 实现AdminAuthController**

login、info接口。

- [ ] **Step 2: 实现AdminStoreController**

list、create、update、delete、getQuota、setQuota接口。

- [ ] **Step 3: 实现AdminProductController**

list、create、update、bindStore、unbindStore接口。

- [ ] **Step 4: 实现AdminVirtualPaymentController**

list、create、update、bindStore接口。核心：创建虚拟支付配置时指定store_id，控制虚拟支付可用门店。

- [ ] **Step 5: 实现AdminVerifyController**

batchGenerate、list、scan、manual、void接口。核心：扫码核销和手动核销均走VerificationService.verifyByScan/verifyManual。

- [ ] **Step 6: 实现AdminOrderController**

list、detail、refund接口。

---

## Task 16: 前端H5项目脚手架搭建

**Files:**
- Create: `shop-h5/` 整个项目结构

- [ ] **Step 1: 初始化Vue3项目**

Run: `cd /workspace && npm create vite@latest shop-h5 -- --template vue`
Then: `cd shop-h5 && npm install vant@4 pinia vue-router@4 axios`

- [ ] **Step 2: 配置vite.config.js**

配置代理、路径别名。

- [ ] **Step 3: 配置main.js**

引入Vant组件（按需引入）、Pinia、Router。

- [ ] **Step 4: 配置router/index.js**

定义所有页面路由，含路由守卫（登录校验）。

---

## Task 17: 前端API层和状态管理实现

**Files:**
- Create: `shop-h5/src/api/request.js`
- Create: `shop-h5/src/api/user.js`
- Create: `shop-h5/src/api/store.js`
- Create: `shop-h5/src/api/product.js`
- Create: `shop-h5/src/api/order.js`
- Create: `shop-h5/src/api/payment.js`
- Create: `shop-h5/src/api/verify.js`
- Create: `shop-h5/src/stores/user.js`

- [ ] **Step 1: 实现request.js**

Axios实例配置：baseURL、请求拦截器（添加JWT Token）、响应拦截器（统一错误处理、401跳转登录）。

- [ ] **Step 2: 实现所有API模块**

封装所有后端接口调用。

- [ ] **Step 3: 实现user store**

Pinia store管理用户登录状态、Token、用户信息。

---

## Task 18: 前端公共组件实现

**Files:**
- Create: `shop-h5/src/components/NavBar.vue`
- Create: `shop-h5/src/components/ProductCard.vue`

- [ ] **Step 1: 实现NavBar组件**

通用导航栏，支持标题、返回按钮。

- [ ] **Step 2: 实现ProductCard组件**

商品卡片组件，展示商品图片、名称、价格。

---

## Task 19: 前端首页和商品页面实现

**Files:**
- Create: `shop-h5/src/views/home/Index.vue`
- Create: `shop-h5/src/views/product/List.vue`
- Create: `shop-h5/src/views/product/Detail.vue`

- [ ] **Step 1: 实现首页**

商品推荐列表 + 门店入口 + 搜索框。简约风格，白色背景，圆角卡片。

- [ ] **Step 2: 实现商品列表页**

分页加载商品列表，下拉刷新，商品卡片网格布局。

- [ ] **Step 3: 实现商品详情页**

商品图片轮播 + 名称/价格 + 适用门店列表 + 立即购买按钮。

---

## Task 20: 前端门店和订单页面实现

**Files:**
- Create: `shop-h5/src/views/store/List.vue`
- Create: `shop-h5/src/views/store/Detail.vue`
- Create: `shop-h5/src/views/order/Create.vue`
- Create: `shop-h5/src/views/order/List.vue`
- Create: `shop-h5/src/views/order/Detail.vue`

- [ ] **Step 1: 实现门店列表和详情页**

门店列表（卡片布局，显示名称/地址/营业时间）+ 门店详情（地址/电话/营业时间/可核销商品）。

- [ ] **Step 2: 实现下单页**

选择商品数量 → 选择核销门店 → 选择支付方式（虚拟支付/门店代付）→ 确认下单。

- [ ] **Step 3: 实现订单列表和详情页**

订单列表（按状态Tab分类：全部/待付/待核销/已核销）+ 订单详情（商品信息+核销码入口）。

---

## Task 21: 前端支付和核销码页面实现

**Files:**
- Create: `shop-h5/src/views/payment/Index.vue`
- Create: `shop-h5/src/views/verification/Code.vue`

- [ ] **Step 1: 实现支付页**

展示订单金额 → 选择支付方式 → 虚拟支付直接完成 → 门店代付选择门店 → 支付成功跳转核销码页。

- [ ] **Step 2: 实现核销码展示页**

展示核销码数字（大字体）+ 二维码图片 + 指定门店名称 + 过期时间 + 使用状态。二维码使用qrcode.js库生成。

---

## Task 22: 前端用户中心页面实现

**Files:**
- Create: `shop-h5/src/views/user/Login.vue`
- Create: `shop-h5/src/views/user/Register.vue`
- Create: `shop-h5/src/views/user/Center.vue`

- [ ] **Step 1: 实现登录和注册页**

手机号+密码登录/注册，Vant Form表单校验。

- [ ] **Step 2: 实现个人中心页**

用户头像/昵称 + 我的订单入口 + 退出登录。

---

## Task 23: 集成测试和构建验证

- [ ] **Step 1: 后端编译验证**

Run: `cd shop-server && mvn clean package -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 2: 前端构建验证**

Run: `cd shop-h5 && npm run build`
Expected: 构建成功，无错误

- [ ] **Step 3: 启动后端服务验证**

启动shop-api服务，访问Knife4j文档页面验证接口注册。

- [ ] **Step 4: 启动前端开发服务器验证**

Run: `cd shop-h5 && npm run dev`
验证页面可正常访问。
