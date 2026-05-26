<imgwidth="2559" height="1421" alt="db489c561956f050dfd19baff082d2f8" src="https://github.com/user-attachments/assets/befcbabe-e62e-47ec-9acb-5595333bcc5c" /><imgwidth="1890" height="753" alt=“b403a9d52d32184e989aab0c0ea33042” src=“https://github.com/user-attachments/assets/1acb55a8-b374-4957-92e3-1f2f85fcfa41” /><imgwidth="2559" height="1421" alt=“a7a02e4592dca0f62779975f109ea530” src=“https://github.com/user-attachments/assets/41bd590e-17dc-4ad6-a80c-1cd0ae6fc2aa” /><imgwidth="2559" height="1421" alt=“1e4989b1816ee88ab324b659397cc546” src=“https://github.com/user-attachments/assets/9053f9a0-59e3-4cec-a2b4-3232728d017e” /><imgwidth="1152" height="1035" alt=“53084b04b4518fec2444d712b3511647” src=“https://github.com/user-attachments/assets/1b24c38a-b742-437e-adab-b98235930448” /># 超市商品管理系统

> **一个前后端分离的超市商品管理平台**，支持收银结账、商品采购、库存管理、员工管理、销售统计等功能。  
> 🎓 本项目为大学《应用软件开发》课程设计作品，已完成从 H2 内存数据库向 MySQL 的持久化迁移。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.x-blue)](https://react.dev/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-orange)](https://www.mysql.com/)
[![许可证](https://img.shields.io/badge/license-MIT-green)](许可证)

---

## 🛠 技术栈

| 层级 | 技术 |
|------|------|
| **前端** | React 18 + React Router + Axios |
| **后端** | Spring Boot 4.0.6 + Spring Security + JWT |
| **数据库** | MySQL 8.0+ （已完成从 H2 的迁移，数据持久化） |
| **ORM** |Spring Data JPA（Hibernate 实现）|
| **构建工具** | Maven（后端）、npm（前端） |
| **开发工具** | IntelliJ IDEA、VS Code |

---

## ✨ 功能模块

| 角色 | 功能 |
|------|------|
| 🔹 **收银员** | 商品浏览、购物车管理、结账找零 |
| 🔹 **采购员** | 供货商增删改查、创建采购单 |
| 🔹 **库存管理员** | 入库确认、库存查询、库存预警、分类管理 |
| 🔹 **经理** | 员工账户管理、销售记录与销售额统计 |

---

## 📸 项目截图

> 

![正在上传 53084b04b4518fec2444d712b3511647.png…]()

---![正在上传 1e4989b1816ee88ab324b659397cc546.png…]()

![正在上传a7a02e4592dca0f62779975f109ea530.png…]()

![正在上传 b403a9d52d32184e989aab0c0ea33042.png…]()

![正在上传 db489c561956f050dfd19baff082d2f8.png…]()



## 🚀 快速开始
商品缓存功能（第二次更新）README
📌 功能介绍
针对超市收银系统商品查询模块，重构缓存方案，采用 Redis逻辑过期 + 分布式锁架构，替代固定过期策略。专门解决高并发下缓存雪崩、缓存击穿、并发重复更新等问题，适用于读多写少的商品查询场景。
🔧 技术栈
SpringBoot + Redis + Jackson + Spring Data JPA
🗝️ Redis Key结构
-收银台：商品：存储全部商品JSON，物理永不过期
- cashier:products:expire：独立存放缓存逻辑过期时间
- lock:products:update：缓存更新分布式锁
⚙️ 核心优化点（第二次更新重点）
1. 逻辑过期：取消统一TTL，防止大量Key同时过期引发缓存雪崩
2. 数据与过期时间拆分：不再绑定实体类，降低代码耦合
3. 加锁二次校验（核心）：解决多线程同时判定过期，造成重复更新数据库问题
4. 精细化异常捕获：单独处理JSON序列化、时间格式化异常，自动清理脏缓存
5. 分布式锁兜底：同一时刻仅允许单个线程更新缓存，防止缓存击穿
📚 执行流程
1. 优先读取Redis缓存，缓存为空直接加锁查询数据库并初始化缓存
2. 缓存存在，判断逻辑过期时间
3. 未过期：直接返回缓存数据
4. 已过期：竞争分布式锁；抢锁失败直接返回旧缓存
5. 抢锁成功：二次校验缓存，避免并发重复更新，确认过期后刷新缓存
💡 使用方法
- 查询商品：调用 getAllProducts()，自动走缓存逻辑
- 数据更新/新增/删除/库存变更：调用 evict() 主动清空缓存，保证数据一致性
📈 后续可扩展方向
- 替换原生锁为Redisson，实现锁自动续期
- 拆分全量缓存，实现分页缓存
- 搭建Caffeine + Redis 二级缓存

### 环境要求

- **JDK**：17 或以上
- **Node.js**：16 或以上
- **MySQL**：8.0 或以上（需提前安装并启动服务）
- **IDE**：IntelliJ IDEA（后端）、VS Code（前端）

### 1. 克隆仓库

```bash
git clone https://github.com/xh486/supermarket.git
cd supermarket
