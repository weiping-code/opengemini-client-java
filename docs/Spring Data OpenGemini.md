# Spring Data OpenGemini

该项目的主要目的是在SpringBoot项目中更好的集成和使用OpenGemini SDK，基于opengemini-client-java的能力，实现对接OpenGemini数据库并提供便捷的操作方法。

# 项目坐标

Maven

```xml
<dependency>
  <groupId>io.opengemini</groupId>
  <artifactId>opengemini-spring-boot-starter</artifactId>
  <version>${latest.version}</version>
</dependency>
```

## OpenGemini SDK多种实现适配

默认使用opengemini-client-jdk实现，如需切换为其他实现，可修改依赖

```xml
<dependency>
  <groupId>io.opengemini</groupId>
  <artifactId>opengemini-spring-boot-starter</artifactId>
  <version>${latest.version}</version>
  <exclusions>
      <exclusion>
          <groupId>io.opengemini</groupId>
          <artifactId>opengemini-spring-boot-starter-jdk</artifactId>
      </exclusion>
  </exclusions>
</dependency>
<dependency>
  <groupId>io.opengemini</groupId>
  <artifactId>opengemini-spring-boot-starter-okhttp</artifactId>
  <version>${latest.version}</version>
</dependency>
```



## Springboot多版本适配

opengemini-spring-boot-starter保持代码向前兼容。

opengemini-spring-data-xx为适配对应springboot的版本，如opengemini-spring-data-33为适配springboot 3.3.x的版本。

```xml
<dependency>
  <groupId>io.opengemini</groupId>
  <artifactId>opengemini-spring-data-33</artifactId>
  <version>${latest.version}</version>
</dependency>
```

```xml
<dependency>
  <groupId>io.opengemini</groupId>
  <artifactId>opengemini-spring-data-27</artifactId>
  <version>${latest.version}</version>
</dependency>
```

opengemini-spring-boot-starter默认依赖适配完成的springboot新版本，如需运行在低版本的springboot项目中，可以采用如下方式替换

```xml
<dependency>
  <groupId>io.opengemini</groupId>
  <artifactId>opengemini-spring-boot-starter</artifactId>
  <version>${latest.version}</version>
  <exclusions>
      <exclusion>
          <groupId>io.opengemini</groupId>
          <artifactId>opengemini-spring-data-33</artifactId>
      </exclusion>
  </exclusions>
</dependency>
<dependency>
  <groupId>io.opengemini</groupId>
  <artifactId>opengemini-spring-data-27</artifactId>
  <version>${latest.version}</version>
</dependency>
```

# 使用方式

## 配置文件

```yaml
spring:
  opengemini:
    url: server_1:8086,server_2:8086,server_3:8086
    connectTimeout: 10s
    timeout: 15s
    authConfig:
    	authType: PASSWORD
      username: user
      password: ~
    tlsConfig:
    	keyStorePath: xxx
    	keyStorePassword: xxx
    	trustStorePath: xxx
    	trustStorePassword: xxx
    	tlsVerifyDisabled: true
    	tlsHostnameVerifyDisabled: true
```

## 代码集成

### 特性

- 可通过注解支持数据库记录与POJO之间的自动转换。
- 可通过注解自动创建数据库、RP。
- 自动声明OpenGeminiTemplate Bean，通过OpenGeminiTemplate可便捷操作POJO类对应的表。
- 自动声明OpenGeminiClient Bean，部分通过OpenGeminiTemplate不能满足的操作，可通过sdk提供的OpenGeminiClient对象完成。

### 集成示例

#### 方式一：数据库、保留策略、数据表名称均固定，可开启自动创建配置。

- 声明实体类

  ```java
  @Database(name="testdb", create=true)
  @RetentionPolicy(name="testrp", create=true, duration="365d", shardGroupDuration="30d")
  @Measurement(name="testms")
  public class Weather {
  
    @Column(name="Location", tag=true)
    private String location;
  
    @Column(name="Temperature")
    private Double temperature;
  
    @TimeColumn(name="time", precision=Precision.PRECISIONMILLISECOND)
    private Long time;
  
  }
  ```

- 与数据库交互

  ```java
  @Service
  public class WeatherService {
  
    private final OpenGeminiTemplate<Weather> template;
  
    public WeatherService(OpenGeminiTemplate<Weather> template) {
      this.template = template;
    }
  
    public void doWork() {
      Weather weather = new Weather();
      weather.setLocation("shenzhen");
      weather.setTemperature(30.5D);
      weather.setTime(System.currentTimeMillis());
      
      template.write(weather);
      
      Query query = ...;
      List<Weather> results = template.query(query);
   }
  }
  ```

#### 方式二：数据库、保留策略、数据表名称不固定（例如数据库名、保留策略存在变量），不开启自动创建配置。

- 声明实体类

  ```java
  @Measurement
  public class Weather {
  
    @Column(name="Location", tag=true)
    private String location;
  
    @Column(name="Temperature")
    private Double temperature;
  
    @TimeColumn(precision=Precision.PRECISIONMILLISECOND)
    private Long time;
  
  }
  ```

- 与数据库交互

  ```java
  @Service
  public class WeatherService {
  
    private final OpenGeminiTemplate<Weather> template;
  
    public WeatherService(OpenGeminiTemplate<Weather> template) {
      this.template = template;
    }
  
    public void doWork(String businessId) {
      String database = "testdb_" + businessId;
      String retentionPolicy = "testrp_" + businessId;
      String measurement = "testms_" + businessId;
  
      if (!template.isDatabaseExist(database)) {
        template.createDatabase(database);
      }
  
      if (!template.isRetentionPolicyExist(retentionPolicy)) {
        RpConfig rpConfig = new RpConfig(retentionPolicy, "365d", "30d");
        template.createRetentionPolicy(rpConfig);
      }
      
      Weather weather = new Weather();
      weather.setLocation("shenzhen");
      weather.setTemperature(30.5D);
      weather.setTime(System.currentTimeMillis());
      
      template.write(database, retentionPolicy, measurement, weather);
      
      Query query = ...;
      List<Weather> results = template.query(database, retentionPolicy, measurement, query);
   }
  }
  ```

# 关键设计

## 注解类

| 类名            | 说明                                               |
| --------------- | -------------------------------------------------- |
| Database        | 支持指定数据库名、支持自动创建                     |
| RetentionPolicy | 支持指定保留策略名，支持自动创建和指定保留策略参数 |
| Measurement     | 支持指定表名                                       |
| Column          | 支持指定列名，指定该列是否为标签                   |
| TimeColumn      | 支持指定时间精度                                   |

根据注解扫描相关POJO类，在SpringBoot启动过程中，SDK的Client对象初始化完成后，自动创建对应的数据库和保留策略。

## 操作类

```java
public interface OpenGeminiTemplate {

  boolean isDatabaseExist(String database);
  void createDatabase(String database);

  boolean isRetentionPolicyExist(String retentionPolicy);
  void createRetentionPolicy(RpConfig rpConfig);

  <T> MeasurementOperations<T> opsForMeasurement(Class<T> clazz);
}
```

```java
public interface MeasurementOperations<T> {

  void write(T t);

  void batchWrite(List<T> list);

  List<T> query(Query query);

  void write(String database, String retentionPolicy, String measurement, T t);

  void batchWrite(String database, String retentionPolicy, String measurement, List<T> list);

  List<T> query(String database, String retentionPolicy, String measurement, Query query);

}
```

说明：

- 全局共用一个OpenGeminiTemplate Bean。
- opsForMeasurement方法返回对`@Measurement`注解实体类的操作类`MeasurementOperations<T>`。
- MeasurementOperations提供的操作方法可自动完成类型转换。

## 其他类

| 类名                        | 说明                                                         |
| --------------------------- | ------------------------------------------------------------ |
| OpenGeminiAutoConfiguration | 自动配置类                                                   |
| OpenGeminiProperties        | 配置类，与SpringBoot配置对接                                 |
| OpenGeminiClientFactory     | 根据配置生成OpenGeminiClient                                 |
| OpenGeminiTemplate          | high-level操作模板类，复用OpenGeminiClientFactory所产生的OpenGeminiClient |
| OpenGeminiPointSerializer   | 根据注解在Point对象与POJO之间转换                            |

