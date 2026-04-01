# bean-merge

`bean-merge` 是一个轻量级 Java Bean 合并工具，支持把 `source` 对象的值递归合并到 `target` 对象，并返回本次合并是否发生变更。

项目适用于以下场景：

- DTO -> Domain 对象同步
- 局部更新（Patch）对象
- 深层嵌套对象合并
- 合并后按字段路径触发业务通知

## 功能特性

- 同名属性自动合并（基于 getter/setter）
- 支持父类字段合并
- 支持嵌套对象递归合并
- 支持 `List` / `Set` 集合元素合并
- 支持基础类型自动类型转换（例如 `String` -> `Integer` / `BigDecimal`）
- 支持忽略 `null` 源值（默认开启）
- 支持自定义类型复制逻辑
- 支持配置不可变类型（按引用替换）
- 支持按字段路径或全局的更新通知回调

## 环境要求

- JDK 8+
- Gradle（项目已包含 `gradlew`）

## 安装与构建

克隆后直接执行：

```bash
./gradlew clean test
```

如果你已发布到私服/仓库，可按如下方式依赖（坐标来自项目配置）：

```gradle
dependencies {
    implementation 'io.github.beanmerge:bean-merge:1.0.0'
}
```

## 快速开始

### 1) 默认合并（忽略 null）

```java
import io.github.beanmerge.Merge;

UserPatch source = new UserPatch();
source.setName("Alice");
source.setAge(null); // 默认忽略 null，不会覆盖 target 的 age

UserEntity target = new UserEntity();
target.setName("Bob");
target.setAge(18);

boolean changed = Merge.merge(source, target);
// changed = true
// target.name -> "Alice"
// target.age  -> 18
```

### 2) 关闭忽略 null

```java
boolean changed = Merge.merge(source, target, false);
// source.age == null 时会把 target.age 覆盖为 null
```

### 3) 使用配置对象

```java
import io.github.beanmerge.Merge;
import io.github.beanmerge.MergeConfiguration;

MergeConfiguration<UserPatch, UserEntity> configuration = new MergeConfiguration<UserPatch, UserEntity>()
        .ignoreNullValue(true);

boolean changed = Merge.withConfiguration(configuration).merge(source, target);
```

## 高级配置

### 1) 自定义复制逻辑

当某个目标类型需要特殊构造方式时可配置 `custom`：

```java
MergeConfiguration<Source, Target> configuration = new MergeConfiguration<Source, Target>()
        .custom(AddressDTO.class, AddressVO.class, dto -> {
            AddressVO vo = new AddressVO();
            vo.setCode(dto.getCityCode());
            vo.setName(dto.getCityName());
            return vo;
        });
```

也可以只按目标类型兜底：

```java
configuration.custom(AddressVO.class, from -> convertAnyToAddress(from));
```

### 2) 自定义不可变类型

默认不可变类型包含常见时间与数值类型（如 `LocalDateTime` / `OffsetDateTime` / `ZonedDateTime` / `BigDecimal`）。
你也可以扩展：

```java
MergeConfiguration<Source, Target> configuration = new MergeConfiguration<Source, Target>()
        .immutableTypes(Money.class, Version.class);
```

被标记为不可变类型的字段会直接使用源值（必要时做类型转换），不进行递归合并。

### 3) 变更通知（Notifier）

#### 按单个路径通知

```java
MergeConfiguration<Source, Target> configuration = new MergeConfiguration<Source, Target>()
        .notifyUpdate("profile.nickname", (path, oldValue, newValue) -> {
            System.out.println(path + " changed: " + oldValue + " -> " + newValue);
        });
```

#### 按多个路径同时满足时通知

```java
MergeConfiguration<Source, Target> configuration = new MergeConfiguration<Source, Target>()
        .notifyUpdate(Arrays.asList("profile.nickname", "profile.avatar"), (source, target, fields) -> {
            // fields 包含本次触发的更新字段
        });
```

#### 按多个路径任意一个满足时通知

```java
MergeConfiguration<Source, Target> configuration = new MergeConfiguration<Source, Target>()
        .notifyUpdateAny(Arrays.asList("profile.nickname", "profile.avatar"), (source, target, fields) -> {
            // nickname 或 avatar 任一发生更新即触发
        });
```

#### 全局通知

```java
MergeConfiguration<Source, Target> configuration = new MergeConfiguration<Source, Target>()
        .notifyUpdate((source, target) -> {
            // 只要有配置监听路径发生更新，就会触发
        });
```

## 字段路径规则

- 普通字段：`parent.child`
- 集合元素：`orders{1}.amount`（索引从 1 开始）
- 无序集合元素可能用占位索引：`items{?}.name`

> 说明：通知路径需要与实际合并路径完全匹配，否则不会触发。

## 行为说明

- 只会处理 **target 可写** 且 **source 可读** 的同名属性。
- 返回值 `boolean` 表示本次是否有字段值发生变化。
- 比较逻辑优先使用 `Comparable`，否则走 `Objects.equals`。
- 对象合并依赖无参构造（用于需要新建目标子对象的场景）。

## 运行测试

```bash
./gradlew test
```

测试覆盖了：

- 基础字段合并
- 父类字段合并
- 嵌套对象/集合合并
- 自定义复制器
- 更新通知
- 类型转换与不可变类型

## 许可证

当前仓库未声明许可证文件。若用于生产或分发，建议先补充 `LICENSE`。
