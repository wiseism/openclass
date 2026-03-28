# 狂神说 Java 学习项目代码知识点整理

## 一、项目概述

本项目是 Bilibili UP 主「遇见狂神说」的 Java 学习资料仓库，包含多个实战项目的源代码。

### 项目结构
```
openclass/
├── 【遇见狂神说】贪吃蛇代码/          # Java GUI 游戏开发
├── 【遇见狂神说】JUC视频笔记/          # Java 并发编程 (JUC)
├── 【遇见狂神说】MyBatisPlus视频笔记/  # MyBatis-Plus 框架
├── 【遇见狂神说】POI视频笔记/          # Excel 处理 (POI/EasyExcel)
└── ... 其他学习资料
```

---

## 二、构建工具选择

### 2.1 Maven vs Gradle

本项目中的子项目主要使用 **Maven** 作为构建工具：

| 特性 | Maven | Gradle |
|------|-------|--------|
| 配置方式 | XML (pom.xml) | Groovy/Kotlin DSL (build.gradle) |
| 学习曲线 | 平缓 | 较陡 |
| 构建速度 | 较慢 | 较快 (支持增量构建) |
| 灵活性 | 较低 | 较高 |
| 社区支持 | 广泛 | 增长中 |

### 2.2 为什么选择 Maven？

1. **约定优于配置**：标准化的项目结构
2. **依赖管理**：自动处理传递依赖
3. **插件生态**：丰富的官方和社区插件
4. **IDE 支持**：IntelliJ IDEA、Eclipse 完美支持
5. **企业级应用**：大多数企业项目使用 Maven

### 2.3 Maven 核心配置示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <!-- 项目坐标 -->
    <groupId>com.kuang</groupId>
    <artifactId>mybatis_plus</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <!-- 父项目继承 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.5.RELEASE</version>
    </parent>
    
    <!-- 依赖管理 -->
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## 三、贪吃蛇游戏项目 (Java GUI)

### 3.1 核心技术点

- **Swing GUI 编程**：JPanel、JFrame、Timer
- **事件监听**：KeyListener、ActionListener
- **游戏循环**：定时器驱动画面刷新
- **碰撞检测**：坐标比较实现

### 3.2 关键代码分析

```java
public class GamePanel extends JPanel implements KeyListener, ActionListener {
    // 游戏状态管理
    int lenth;                          // 蛇的长度
    int[] snakeX = new int[600];        // 蛇的X坐标数组
    int[] snakeY = new int[500];        // 蛇的Y坐标数组
    String fx = "R";                    // 方向: R右 L左 U上 D下
    boolean isStart = false;            // 游戏状态
    Timer timer = new Timer(100, this); // 100ms定时器
    
    // 游戏初始化
    public void init() {
        lenth = 3;
        snakeX[0] = 100; snakeY[0] = 100;  // 蛇头
        snakeX[1] = 75;  snakeY[1] = 100;  // 第一节身体
        snakeX[2] = 50;  snakeY[2] = 100;  // 第二节身体
    }
    
    // 绘制游戏画面 (双缓冲)
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // 清屏
        // 绘制游戏元素...
    }
}
```

### 3.3 设计模式应用

- **单例模式**：Data 类管理游戏资源
- **观察者模式**：键盘事件监听

---

## 四、JUC 并发编程项目

### 4.1 核心技术点

- **线程创建**：Thread、Runnable、Callable
- **线程同步**：synchronized、Lock、ReadWriteLock
- **并发工具类**：CountDownLatch、CyclicBarrier、Semaphore
- **线程池**：ExecutorService、ThreadPoolExecutor
- **CAS 操作**：Atomic 原子类
- **JMM 内存模型**：volatile、happens-before

### 4.2 单例模式实现

#### 饿汉式 (线程安全)
```java
public class Hungry {
    // 类加载时就创建实例
    private static final Hungry HUNGRY = new Hungry();
    
    private Hungry() {}  // 私有构造方法
    
    public static Hungry getInstance() {
        return HUNGRY;
    }
}
```

#### 懒汉式 (DCL 双重检查锁)
```java
public class LazyMan {
    // volatile 禁止指令重排序
    private volatile static LazyMan lazyMan;
    
    private LazyMan() {
        // 防止反射破坏
        synchronized (LazyMan.class) {
            if (lazyMan != null) {
                throw new RuntimeException("不要试图使用反射破坏");
            }
        }
    }
    
    // DCL 双重检测锁
    public static LazyMan getInstance() {
        if (lazyMan == null) {                    // 第一次检查
            synchronized (LazyMan.class) {        // 同步块
                if (lazyMan == null) {            // 第二次检查
                    lazyMan = new LazyMan();      // 创建对象
                }
            }
        }
        return lazyMan;
    }
}
```

#### 枚举单例 (最佳实践)
```java
public enum EnumSingle {
    INSTANCE;
    
    public EnumSingle getInstance() {
        return INSTANCE;
    }
}
// 天然防止反射和序列化破坏
```

### 4.3 生产者消费者模型

```java
// 使用 Lock 和 Condition 实现
class Data {
    private String message;
    private boolean flag = false;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    
    // 生产者
    public void produce(String msg) {
        lock.lock();
        try {
            while (flag) {
                condition.await();  // 等待消费
            }
            this.message = msg;
            flag = true;
            condition.signalAll();  // 通知消费者
        } finally {
            lock.unlock();
        }
    }
    
    // 消费者
    public String consume() {
        lock.lock();
        try {
            while (!flag) {
                condition.await();  // 等待生产
            }
            flag = false;
            condition.signalAll();  // 通知生产者
            return message;
        } finally {
            lock.unlock();
        }
    }
}
```

### 4.4 线程池使用

```java
// 创建线程池
ExecutorService threadPool = new ThreadPoolExecutor(
    2,                              // 核心线程数
    5,                              // 最大线程数
    3,                              // 空闲线程存活时间
    TimeUnit.SECONDS,               // 时间单位
    new LinkedBlockingQueue<>(3),   // 任务队列
    Executors.defaultThreadFactory(), // 线程工厂
    new ThreadPoolExecutor.DiscardOldestPolicy() // 拒绝策略
);

// 提交任务
threadPool.execute(() -> {
    // 任务逻辑
});

// 关闭线程池
threadPool.shutdown();
```

---

## 五、MyBatis-Plus 项目

### 5.1 核心技术点

- **Spring Boot**：快速构建 Web 应用
- **MyBatis-Plus**：简化数据库操作
- **Lombok**：减少样板代码
- **MySQL**：关系型数据库

### 5.2 项目依赖

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.0.5</version>
    </dependency>
    
    <!-- MySQL 驱动 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

### 5.3 实体类设计

```java
@Data  // Lombok: 自动生成 getter/setter/toString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;
    
    private String name;
    private Integer age;
    private String email;
    
    @TableField(fill = FieldFill.INSERT)  // 自动填充
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
```

### 5.4 Mapper 接口

```java
// 继承 BaseMapper 获得 CRUD 能力
@Repository
public interface UserMapper extends BaseMapper<User> {
    // 自定义 SQL
    @Select("SELECT * FROM user WHERE age > #{age}")
    List<User> selectByAge(@Param("age") Integer age);
}
```

### 5.5 条件构造器 Wrapper

```java
// QueryWrapper 构建查询条件
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper
    .like("name", "狂神")           // LIKE 模糊查询
    .between("age", 20, 30)        // BETWEEN 范围查询
    .orderByDesc("create_time");    // ORDER BY 排序

List<User> users = userMapper.selectList(wrapper);
```

---

## 六、POI/EasyExcel 项目

### 6.1 核心技术点

- **Apache POI**：Java 操作 Office 文档
- **EasyExcel**：阿里巴巴开源的 Excel 处理框架
- **大数据读写**：内存优化、流式处理

### 6.2 POI vs EasyExcel

| 特性 | Apache POI | EasyExcel |
|------|------------|-----------|
| 内存占用 | 高 (全量加载) | 低 (流式读写) |
| 大数据处理 | 容易 OOM | 支持百万级数据 |
| 学习成本 | 较高 | 较低 |
| 性能 | 一般 | 优秀 |
| 推荐使用 | 小文件 | 大文件 |

### 6.3 EasyExcel 使用示例

```java
// 写入 Excel
String fileName = "user_data.xlsx";
EasyExcel.write(fileName, UserData.class)
    .sheet("用户数据")
    .doWrite(userList);

// 读取 Excel (监听模式)
EasyExcel.read(fileName, UserData.class, new UserDataListener())
    .sheet()
    .doRead();

// 数据监听器
public class UserDataListener extends AnalysisEventListener<UserData> {
    private static final int BATCH_COUNT = 100;
    private List<UserData> cachedDataList = new ArrayList<>();
    
    @Override
    public void invoke(UserData data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();  // 批量保存
            cachedDataList.clear();
        }
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();  // 保存剩余数据
    }
}
```

### 6.4 实体类注解

```java
@Data
public class UserData {
    @ExcelProperty("用户ID")      // 列标题
    private Integer userId;
    
    @ExcelProperty("用户名")
    private String userName;
    
    @ExcelProperty("生日")
    @DateTimeFormat("yyyy-MM-dd")  // 日期格式
    private Date birthday;
    
    @ExcelIgnore  // 忽略该字段
    private String password;
}
```

---

## 七、RAR 解压工具实现

### 7.1 技术选型

由于仓库中的 RAR 文件都是 **RAR5 格式**，需要特殊处理：

| 库 | 支持格式 | 特点 |
|----|---------|------|
| junrar | RAR4 | 纯 Java，不支持 RAR5 |
| SevenZipJBinding | RAR4/RAR5/7z | 基于 7-Zip，功能强大 |
| Apache Commons Compress | ZIP/TAR/GZIP | 不支持 RAR |

### 7.2 核心实现代码

```java
public class RarExtractor {
    
    // 检测 RAR 版本 (魔数识别)
    public RarVersion detectRarVersion(String rarPath) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(rarPath, "r")) {
            byte[] magic = new byte[8];
            raf.readFully(magic);
            
            // RAR4 魔数: 52 61 72 21 1A 07 00
            // RAR5 魔数: 52 61 72 21 1A 07 01 00
            if (magic[0] == 0x52 && magic[1] == 0x61 && 
                magic[2] == 0x72 && magic[3] == 0x21) {
                if (magic[4] == 0x1A && magic[5] == 0x07) {
                    if (magic[6] == 0x00) return RarVersion.RAR4;
                    if (magic[6] == 0x01 && magic[7] == 0x00) return RarVersion.RAR5;
                }
            }
            return RarVersion.UNKNOWN;
        }
    }
    
    // 使用 SevenZipJBinding 解压 RAR5
    public boolean extractRar5(String rarPath) {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        
        try {
            randomAccessFile = new RandomAccessFile(rarFile, "r");
            inArchive = SevenZip.openInArchive(
                null, 
                new RandomAccessFileInStream(randomAccessFile)
            );
            
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            
            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                final String filePath = item.getPath();
                final File outputFile = new File(extractDir, filePath);
                
                if (item.isFolder()) {
                    outputFile.mkdirs();
                } else {
                    // 回调方式写入文件
                    ExtractOperationResult result = item.extractSlow(data -> {
                        try (FileOutputStream fos = new FileOutputStream(outputFile, true)) {
                            fos.write(data);
                            return data.length;
                        }
                    });
                }
            }
            return true;
        } finally {
            // 资源清理
            if (inArchive != null) inArchive.close();
            if (randomAccessFile != null) randomAccessFile.close();
        }
    }
}
```

### 7.3 Maven 依赖

```xml
<dependencies>
    <!-- junrar - RAR4 支持 -->
    <dependency>
        <groupId>com.github.junrar</groupId>
        <artifactId>junrar</artifactId>
        <version>7.5.5</version>
    </dependency>
    
    <!-- SevenZipJBinding - RAR5 支持 -->
    <dependency>
        <groupId>net.sf.sevenzipjbinding</groupId>
        <artifactId>sevenzipjbinding</artifactId>
        <version>16.02-2.01</version>
    </dependency>
    <dependency>
        <groupId>net.sf.sevenzipjbinding</groupId>
        <artifactId>sevenzipjbinding-all-platforms</artifactId>
        <version>16.02-2.01</version>
    </dependency>
</dependencies>
```

---

## 八、编译运行指南

### 8.1 环境要求

- **JDK**: 1.8 或更高版本 (推荐 JDK 17)
- **Maven**: 3.6.0 或更高版本
- **IDE**: IntelliJ IDEA (推荐) 或 Eclipse

### 8.2 编译步骤

```bash
# 1. 进入项目目录
cd /Users/ibqo/Develop/git/github/java/openclass

# 2. 编译项目
mvn clean compile

# 3. 运行主类
mvn exec:java -Dexec.mainClass="com.kuangstudy.RarExtractor"

# 4. 打包 JAR
mvn clean package

# 5. 运行 JAR
java -jar target/openclass-extractor-1.0-SNAPSHOT.jar
```

### 8.3 子项目运行

```bash
# 贪吃蛇游戏
cd 【遇见狂神说】贪吃蛇代码/kuangshen
javac -d out src/com/kuang/snake/*.java
java -cp out com.kuang.snake.StartGame

# JUC 项目
cd 【遇见狂神说】JUC视频笔记/代码
mvn clean compile exec:java -Dexec.mainClass="com.kuang.single.LazyMan"

# MyBatis-Plus 项目
cd 【遇见狂神说】MyBatisPlus视频笔记/mybatis_plus
mvn spring-boot:run

# POI 项目
cd 【遇见狂神说】POI视频笔记/Bilibili-狂神说java/kuang-poi
mvn clean compile exec:java -Dexec.mainClass="com.kuang.EasyExcelTest"
```

---

## 九、总结

### 9.1 学习路线建议

1. **Java 基础** → 贪吃蛇游戏 (GUI 编程)
2. **并发编程** → JUC 项目 (多线程、锁、线程池)
3. **Web 开发** → MyBatis-Plus (Spring Boot、数据库)
4. **数据处理** → POI/EasyExcel (Excel 操作)

### 9.2 核心知识点回顾

| 项目 | 关键技术 | 难度 |
|------|---------|------|
| 贪吃蛇 | Swing、事件监听、游戏循环 | ⭐⭐ |
| JUC | 线程同步、并发工具、线程池 | ⭐⭐⭐⭐ |
| MyBatis-Plus | ORM、Spring Boot、数据库 | ⭐⭐⭐ |
| POI | Excel 读写、大数据处理 | ⭐⭐⭐ |

### 9.3 扩展学习资源

- **官方文档**：
  - [Maven 官方文档](https://maven.apache.org/guides/)
  - [Spring Boot 文档](https://spring.io/projects/spring-boot)
  - [MyBatis-Plus 文档](https://baomidou.com/)
  - [EasyExcel 文档](https://easyexcel.opensource.alibaba.com/)

- **狂神说视频**：
  - Bilibili: https://space.bilibili.com/95256449
  - 微信公众号: 狂神说

---

*文档生成时间: 2026-03-28*
*基于 Maven 构建工具 + Java 17*
