# LDLib2 1.20.1 移植版功能说明与变更标注

本文档仿照官方 LDLib2 wiki 的模块顺序整理本仓库移植版功能，并额外标注从官方 1.21.x NeoForge 版本移植到 Minecraft 1.20.1 后的 API、平台和功能差异。

官方 wiki: <https://low-drag-mc.github.io/LowDragMC-Doc/en/ldlib2/>

本文件定位为移植版总览，不替代官方逐页教程。需要学习概念、用法和示例时仍以官方 wiki 为主；需要在本仓库 1.20.1 Forge/Fabric 版本中落地时，以本文的“移植后变更”为准。

## 标注规则

| 标记 | 含义 |
| --- | --- |
| 保留 | 功能主体与官方 wiki 一致，调用方式基本不变。 |
| 移植变更 | 功能保留，但平台、包名、类型或运行入口有变化。 |
| 降级/限制 | 功能只保留部分能力，或当前移植版中不可用。 |
| 新增修复 | 为 1.20.1 移植版额外补上的兼容、资源或崩溃修复。 |

## 版本与平台

| 项目 | 官方 wiki 口径 | 本仓库移植版 |
| --- | --- | --- |
| Minecraft | 主要面向 1.21.x | 1.20.1 |
| Loader | NeoForge | Forge 47.4.20 + Fabric API 0.92.9，通过 Architectury 组织 |
| Mod 版本 | LDLib2 2.x | `2.2.27+mc1.20.1` |
| 平台源码 | NeoForge 风格 API | `common` + `forge` + `fabric` 多平台结构 |
| Java | 官方 1.21 生态 | 当前构建目标 Java 21，本机 Gradle 指向 Microsoft JDK 25 |

移植版的核心目标是尽量保留上游 LDLib2 的 UI、同步、配置器、编辑器、节点图、渲染和资源体系，同时用兼容层补齐 1.20.1 缺少的 1.21/NeoForge API。

## 功能总览

| 官方 wiki 模块 | 移植版状态 | 移植后变更 |
| --- | --- | --- |
| Java Integration | 移植变更 | 官方 Maven 坐标面向 NeoForge 1.21.x；本仓库当前应作为本地项目、子模块或构建产物使用。 |
| Data Synchronization and Persistence | 移植变更 | 注解式同步/持久化保留，网络和序列化类型改走 `com.lowdragmc.lowdraglib2.compat` 兼容层。 |
| LDLib2 UI | 移植变更 | Taffy 布局、LSS、事件、数据绑定、XML、编辑器主体保留；物品/流体/XEI/KJS 相关能力有差异。 |
| Node Graph Toolkit | 保留 | 图模型、节点、端口、变量、子图、GraphView、GraphEditorView、资源接入保留。 |
| Shaders | 移植变更 | shader 注册与纹理功能保留，补了 1.20.1 shader 构造、加载和空 shader fallback。 |
| Model Rendering | 移植变更 | renderer block、renderer model loader、场景渲染保留，NeoForge 事件由 Forge/Fabric/Architectury 适配。 |
| In-game Editor | 新增修复 | 编辑器框架保留，并新增/修复 Forge 1.20.1 客户端命令、内置资源、`mod_resources` 只读资源源。 |
| Configurable | 移植变更 | 注解式属性面板、Inspector、History 保留；1.21 DataComponent 编辑降级为不可用占位。 |
| XEI / KubeJS integrations | 降级/限制 | JEI 依赖存在，但 XEI/KJS 源码当前从 common 编译排除，REI/EMI/KJS 集成尚未完整恢复。 |

## Java Integration

### 官方功能

官方 wiki 的 Java Integration 主要说明如何在 1.21.x NeoForge 项目中通过 Maven 引入 LDLib2、安装 IDEA 辅助插件，并通过 `@LDLibPlugin` 注册插件入口。

### 移植版使用方式

| 功能 | 状态 | 说明 |
| --- | --- | --- |
| `@LDLibPlugin` / `ILDLibPlugin` | 保留 | 插件式扩展入口仍可作为注册资源、扩展和启动逻辑的入口。 |
| 官方 Maven 坐标 | 降级/限制 | README 中的 `ldlib2-neoforge-1.21.1` 坐标不适用于本 1.20.1 移植版。 |
| 多平台构建 | 移植变更 | 使用 Architectury Loom，平台为 `fabric,forge`。 |
| Yoga / Taffy | 保留 | 作为 bundled libraries 参与构建，支撑 UI 布局。 |

建议在依赖本移植版时使用以下方式之一：

1. 将本仓库作为子模块或复合构建依赖。
2. 先运行平台构建任务，再在目标整合包/开发环境中引用生成的 Forge 或 Fabric jar。
3. 若要发布 Maven，需要另行配置本移植版坐标，不能直接复用官方 NeoForge 1.21.x 坐标。

## 1.20.1 兼容层

移植版最大的 API 差异来自兼容层。官方 1.21.x/NeoForge 代码中的一部分类型在 1.20.1 中不存在或行为不同，因此本仓库提供了 LDLib2 自己的替代类型。

| 官方/上游类型或概念 | 移植版替代 | 说明 |
| --- | --- | --- |
| `net.neoforged.neoforge.fluids.FluidStack` | `com.lowdragmc.lowdraglib2.compat.FluidStack` | 流体槽、流体纹理、配置器和同步中使用。 |
| `IFluidHandler` / `IFluidTank` | `com.lowdragmc.lowdraglib2.compat.*` | 用于 1.20.1 的流体容器抽象。 |
| `IItemHandler` / `ItemStackHandler` / `InvWrapper` | `com.lowdragmc.lowdraglib2.compat.*` | 用于物品槽和库存适配。 |
| `INBTSerializable` | `com.lowdragmc.lowdraglib2.compat.INBTSerializable` | 用于移植版 NBT 序列化接口。 |
| `CustomPacketPayload` | `com.lowdragmc.lowdraglib2.compat.network.CustomPacketPayload` | 网络包接口由兼容层承载。 |
| `RegistryFriendlyByteBuf` | `com.lowdragmc.lowdraglib2.compat.network.RegistryFriendlyByteBuf` | 网络读写 buffer 兼容层。 |
| `StreamCodec` / `ByteBufCodecs` | `com.lowdragmc.lowdraglib2.compat.network.codec.*` | 兼容 1.21 网络 codec 风格。 |
| `ComponentSerialization` | `com.lowdragmc.lowdraglib2.compat.network.chat.ComponentSerialization` | 文本组件网络序列化兼容。 |
| `TickRateManager` | `com.lowdragmc.lowdraglib2.compat.world.TickRateManager` | 1.20.1 tick rate 相关兼容。 |
| NeoForge 事件/注册类 | `net.neoforged.*` shim + Architectury/Forge/Fabric 实现 | 仅为源码兼容，不代表运行时是真 NeoForge。 |

### 迁移速查

从官方 wiki 示例复制代码到本移植版时，优先检查这些 import：

```java
// Fluid
// 官方/上游:
// import net.neoforged.neoforge.fluids.FluidStack;
// 移植版:
import com.lowdragmc.lowdraglib2.compat.FluidStack;

// Item handler
import com.lowdragmc.lowdraglib2.compat.IItemHandler;
import com.lowdragmc.lowdraglib2.compat.IItemHandlerModifiable;
import com.lowdragmc.lowdraglib2.compat.ItemStackHandler;

// NBT serializable
import com.lowdragmc.lowdraglib2.compat.INBTSerializable;

// Network payload and codec
import com.lowdragmc.lowdraglib2.compat.network.CustomPacketPayload;
import com.lowdragmc.lowdraglib2.compat.network.RegistryFriendlyByteBuf;
import com.lowdragmc.lowdraglib2.compat.network.codec.StreamCodec;
```

## Data Synchronization and Persistence

### 官方功能

官方 wiki 的 Sync 模块覆盖：

| 功能 | 状态 | 说明 |
| --- | --- | --- |
| 字段托管存储 | 保留 | `FieldManagedStorage` 负责收集被管理字段。 |
| 自动持久化 | 保留 | `@Persisted` 字段可自动写入/读取 NBT。 |
| 自动描述同步 | 保留 | `@DescSynced` 字段可进行服务端到客户端同步。 |
| 懒加载/只读管理 | 保留 | `@LazyManaged`、`@ReadOnlyManaged` 保留。 |
| 更新监听 | 保留 | `@UpdateListener` 用于字段变化后的回调。 |
| 条件同步 | 保留 | `@ConditionalSynced` 可按条件控制同步。 |
| RPC 方法 | 保留 | `@RPCMethod` 用于跨端调用。 |
| 跳过默认值 | 保留 | `@SkipPersistedValue` 保留。 |
| 掉落保存 | 移植变更 | `@DropSaved` 保留，但示例中的 1.21 DataComponent 代码在 1.20.1 需要改写。 |
| 触发重渲染 | 保留 | `@RequireRerender` 保留。 |
| Codec 生成 | 移植变更 | `PersistedParser.createCodec(...)` 保留，但底层类型要使用本移植版兼容类型。 |
| BlockEntity 集成 | 移植变更 | 同步、持久化、RPC block entity 接口保留，网络发送通过 Architectury channel 包装。 |

### 移植后变更

| 项目 | 变更 |
| --- | --- |
| 网络包注册 | `LDLNetworking` 使用 Architectury `NetworkChannel` 包装注册和发送。 |
| Packet payload | 包接口为 `com.lowdragmc.lowdraglib2.compat.network.CustomPacketPayload`。 |
| Buffer/codec | `RegistryFriendlyByteBuf`、`StreamCodec`、`ByteBufCodecs` 来自 LDLib2 compat 包。 |
| 流体字段 | 需要使用 `com.lowdragmc.lowdraglib2.compat.FluidStack`。 |
| 1.21 DataComponent | Minecraft 1.20.1 没有对应 API，不能按官方 1.21 示例直接使用。 |

## LDLib2 UI

### 官方功能

UI 模块是官方 wiki 内容最多的部分，包含：

| 功能 | 状态 | 说明 |
| --- | --- | --- |
| `ModularUI` | 保留 | UI 根容器和 screen/menu 绑定入口。 |
| `UI` / `UIElement` | 保留 | 元素树、布局、样式、事件、渲染和序列化基础。 |
| Taffy 布局 | 保留 | 支持 flex、absolute、relative、margin、padding、gap、overflow 等布局能力。 |
| Screen 和 Menu | 移植变更 | 逻辑保留，注册和打开流程由 Forge/Fabric 平台代码适配。 |
| LSS stylesheet | 保留 | CSS-like 样式表系统保留，内置 `gdp.lss`、`mc.lss`、`modern.lss`、`ore.lss`。 |
| Style animation | 保留 | 样式动画、过渡相关能力保留。 |
| UI event system | 保留 | 鼠标、键盘、焦点、冒泡/捕获等事件系统保留。 |
| Data binding | 保留 | 绑定值、同步和 RPC 事件保留。 |
| XML UI | 保留 | XML 声明式 UI 和 `ldlib2-ui.xsd` 保留。 |
| In-game UI Editor | 新增修复 | 编辑器可通过客户端命令打开，资源和 shader 兼容性已在移植版补齐。 |
| HUD | 保留 | HUD 相关 UI 入口保留。 |
| Kotlin support | 保留 | common sourceSet 将 Java/Kotlin 源合并处理。 |
| KubeJS support | 降级/限制 | KJS 源码当前从 common 编译排除，不能按官方 KJS wiki 直接使用。 |
| XEI support | 降级/限制 | XEI 源码当前从 common 编译排除；JEI 依赖存在但完整 XEI 接入尚未恢复。 |
| Agent guide | 保留 | 官方面向自动化/生成 UI 的约束可参考，落地时注意本移植版 API 差异。 |

### UI 组件注册表

当前源码中注册的 UI element 如下：

| 组件 ID | 状态 | 说明/变更 |
| --- | --- | --- |
| `element` | 保留 | 基础元素。 |
| `bindable-value` | 保留 | 数据绑定辅助元素。 |
| `button` | 保留 | 按钮组件。 |
| `code-editor` | 保留 | 代码编辑器组件。 |
| `color-selector` | 保留 | 颜色选择组件。 |
| `fluid-slot` | 移植变更 | 使用 `compat.FluidStack` 和兼容流体 handler。 |
| `graph-view` | 保留 | 节点图视图组件。 |
| `inspector` | 保留 | 属性检查器组件。 |
| `inventory-slots` | 移植变更 | 使用兼容物品 handler。 |
| `item-slot` | 移植变更 | 使用兼容物品 handler；XEI 查询能力受集成限制影响。 |
| `label` | 保留 | 简单文本标签。 |
| `progress-bar` | 保留 | 进度条组件。 |
| `scene` | 保留 | 3D/场景预览组件。 |
| `scroller-horizontal` | 保留 | 横向滚动条。 |
| `scroller-vertical` | 保留 | 纵向滚动条。 |
| `scroller-view` | 保留 | 滚动视图容器。 |
| `search-component` | 保留 | 搜索选择组件。 |
| `selector` | 保留 | 候选值选择组件。 |
| `split-view-horizontal` | 保留 | 横向分割视图。 |
| `split-view-vertical` | 保留 | 纵向分割视图。 |
| `structured-tag-editor` | 保留 | 结构化 NBT/Tag 编辑组件。 |
| `switch` | 保留 | 开关组件。 |
| `tab` | 保留 | 单个 tab。 |
| `tab-view` | 保留 | Tab 容器。 |
| `tag-field` | 保留 | Tag 输入/编辑组件。 |
| `template` | 保留 | UI 模板元素。 |
| `text` | 保留 | 富文本/文本显示组件。 |
| `text-area` | 保留 | 多行文本输入。 |
| `text-field` | 保留 | 单行文本输入。 |
| `toggle` | 保留 | toggle 组件。 |
| `toggle-group` | 保留 | toggle 分组。 |
| `tree-list` | 保留 | 树形列表。 |
| `virtual-scroller-view` | 保留 | 虚拟滚动视图。 |

### GUI Texture 注册表

| Texture ID | 状态 | 说明/变更 |
| --- | --- | --- |
| `empty` | 保留 | 空纹理。 |
| `missing` | 新增修复 | 缺失纹理兜底。 |
| `animation_texture` | 保留 | 动画纹理。 |
| `color_border_texture` | 保留 | 纯色边框纹理。 |
| `color_rect_texture` | 保留 | 纯色矩形纹理。 |
| `fluid_stack_texture` | 移植变更 | 使用 `compat.FluidStack`。 |
| `group_texture` | 保留 | 纹理组。 |
| `item_stack_texture` | 保留 | 物品堆纹理。 |
| `rect_texture` | 保留 | 矩形纹理。 |
| `sdf_rect_texture` | 移植变更 | 依赖移植版 shader fallback。 |
| `shader_texture` | 新增修复 | 1.20.1 shader 加载失败时有兜底，避免编辑器透明/崩溃类问题继续扩大。 |
| `sprite_texture` | 新增修复 | 贴图渲染路径补了 1.20.1 兼容处理。 |
| `text_texture` | 保留 | 文本纹理。 |
| `ui_resource_texture` | 新增修复 | 可解析打包在模组资源中的 UI 资源。 |
| `vanilla_sprite_texture` | 保留 | 原版 atlas sprite 纹理。 |

### UI 资源与内置资源

移植版保留官方资源系统，并补充了一个只读的模组资源提供器：

| 资源源 | 状态 | 说明 |
| --- | --- | --- |
| Built-in provider | 保留 | 内存/默认资源。 |
| File provider | 保留 | 文件系统资源。 |
| `mod_resources` | 新增修复 | 从资源包或模组 jar 的 `assets/<modid>/resources/` 读取 `.ui.nbt` 等资源，只读不可编辑。 |

当前已打包示例资源：

| 路径 | 说明 |
| --- | --- |
| `assets/ldlib2/resources/examples/button.ui.nbt` | 按钮 UI 示例。 |
| `assets/ldlib2/resources/examples/example_layout.ui.nbt` | 布局 UI 示例。 |

### UI 移植注意事项

1. 官方 wiki 中涉及流体和物品 handler 的示例需要换成 `com.lowdragmc.lowdraglib2.compat` 包。
2. 官方 XEI/KJS 页面在本移植版中只能作为设计参考，当前不能视为完整可用 API。
3. 如果编辑器资源面板缺少模组自带资源，应优先检查资源是否位于 `assets/<modid>/resources/`，并确认 `mod_resources` provider 被注册和刷新。
4. 如果 UI 使用 shader 或 SDF 纹理，在 1.20.1 环境中需要依赖本移植版的 shader fallback 修复，避免空 shader 导致渲染崩溃。

## Configurable

### 官方功能

Configurable 是注解驱动的属性编辑系统，用于把 Java 对象转为 Inspector 可编辑面板，并自动接入历史记录、持久化和编辑器 UI。

| 功能 | 状态 | 说明 |
| --- | --- | --- |
| `IConfigurable` | 保留 | 对象暴露属性面板的核心接口。 |
| `ConfiguratorParser` | 保留 | 根据注解和字段类型生成配置 UI。 |
| `ConfiguratorGroup` | 保留 | 配置器分组。 |
| Inspector 集成 | 保留 | 编辑器中选中对象后显示属性。 |
| History 集成 | 保留 | 编辑变更可进入撤销/重做历史。 |
| 与持久化联动 | 保留 | `@Configurable` 默认可参与持久化，除非显式关闭。 |

### Configurable 注解

| 注解 | 状态 | 用途 |
| --- | --- | --- |
| `@Configurable` | 保留 | 暴露字段或子对象为可编辑属性。 |
| `@ConfigColor` | 保留 | 颜色字段。 |
| `@ConfigHDR` | 保留 | HDR 颜色相关配置。 |
| `@ConfigHeader` | 保留 | 配置分组标题。 |
| `@ConfigList` | 保留 | 列表/数组配置。 |
| `@ConfigNumber` | 保留 | 数值范围、类型等限制。 |
| `@ConfigRL` | 保留 | `ResourceLocation` 选择约束。 |
| `@ConfigSearch` | 保留 | 搜索型选择器。 |
| `@ConfigSelector` | 保留 | 候选值选择器。 |
| `@ConfigSetter` | 保留 | 指定 setter。 |
| `@DefaultValue` | 保留 | 默认值声明。 |

### 配置访问器注册表

当前注册的 configurator accessor：

| 类型/ID | 状态 | 说明/变更 |
| --- | --- | --- |
| `boolean` | 保留 | 布尔值。 |
| `number` | 保留 | 数字类型。 |
| `string` | 保留 | 字符串。 |
| `string_array` | 保留 | 字符串数组。 |
| `enum` | 保留 | 枚举。 |
| `component` | 保留 | Minecraft 文本组件。 |
| `resource_location` | 保留 | 资源路径。 |
| `tag` | 保留 | NBT/Tag。 |
| `item` | 保留 | 物品。 |
| `itemstack` | 移植变更 | 物品组件编辑中的 DataComponent 部分不可用。 |
| `fluid` | 保留 | 流体。 |
| `fluidstack` | 移植变更 | 使用 `compat.FluidStack`，DataComponent 部分不可用。 |
| `block` | 保留 | 方块。 |
| `block_state` | 保留 | 方块状态。 |
| `block_pos` | 保留 | 方块坐标。 |
| `entity_type` | 保留 | 实体类型。 |
| `aabb` | 保留 | 包围盒。 |
| `range` | 保留 | 范围值。 |
| `position` | 保留 | UI 位置值。 |
| `size` | 保留 | UI 尺寸值。 |
| `pivot` | 保留 | pivot。 |
| `translate2d` | 保留 | 2D 平移。 |
| `quaternion` | 保留 | 四元数。 |
| `vector2f` | 保留 | 2D float 向量。 |
| `vector2i` | 保留 | 2D int 向量。 |
| `vector3f` | 保留 | 3D float 向量。 |
| `vector3i` | 保留 | 3D int 向量。 |
| `vector4f` | 保留 | 4D float 向量。 |
| `vector4i` | 保留 | 4D int 向量。 |
| `gui_texture` | 保留 | GUI 纹理配置。 |
| `renderer` | 保留 | renderer 配置。 |
| `transform_ref` | 保留 | transform 引用。 |

### DataComponent 降级

官方 1.21.x 可以围绕 DataComponent 做物品/流体组件编辑。Minecraft 1.20.1 没有同等 API，因此本仓库的 `DataComponentConfigurator` 是占位实现：

| 功能 | 状态 | 说明 |
| --- | --- | --- |
| ItemStack/FluidStack 基础编辑 | 保留 | 物品/流体本体仍可配置。 |
| 1.21 DataComponent 面板 | 降级/限制 | 当前不生成实际组件编辑项。 |
| 旧式 NBT/Tag 编辑 | 保留 | 可通过 tag/structured tag 相关组件处理。 |

## In-game Editor Framework

### 官方功能

官方 Editor 模块描述的是一套可复用的游戏内编辑软件框架，而不只是 UI Editor。它包含项目、资源、视图、菜单、设置、Inspector、History、SceneEditor 等。

| 功能 | 状态 | 说明 |
| --- | --- | --- |
| Editor window | 新增修复 | Forge 1.20.1 客户端命令打开路径已补齐。 |
| Project system | 保留 | 项目类型、加载、保存、生命周期保留。 |
| Views | 保留 | 可停靠视图、内置 Inspector/History 等保留。 |
| Resource system | 新增修复 | 新增 `mod_resources` 只读资源源，修复模组内置资源缺失问题。 |
| Resource UI | 保留 | 资源浏览、选择、预览框架保留。 |
| Menus | 保留 | File/View 等菜单扩展保留。 |
| Settings | 保留 | 编辑器设置保存/读取保留。 |
| SceneEditor | 保留 | 场景编辑辅助保留。 |
| Source examples | 保留 | 官方示例可参考，但 1.21 API 需要改 import。 |

### 移植版新增/修复点

| 项目 | 标记 | 说明 |
| --- | --- | --- |
| 客户端编辑器命令 | 新增修复 | `ldlib2_ui_editor` 可直接打开 UI Editor。 |
| 客户端事件注册 | 新增修复 | Forge/Fabric 客户端初始化、菜单 screen、tooltip、shader 注册已适配。 |
| 内置图标/贴图打包 | 新增修复 | `assets/ldlib2/textures/gui/...` 和 icon 资源被纳入 common resources。 |
| 内置 UI 示例资源 | 新增修复 | `assets/ldlib2/resources/examples/*.ui.nbt` 可从资源面板读取。 |
| `mod_resources` provider | 新增修复 | 只读读取模组/资源包自带 `.ui.nbt` 等资源。 |
| shader fallback | 新增修复 | 降低编辑器透明或渲染阶段空 shader 崩溃风险。 |

### 编辑器使用注意事项

1. `mod_resources` 是只读资源源，不能重命名、删除、编辑或复制内部资源。
2. 需要可写资源时仍使用文件资源源。
3. 官方 wiki 中对 NeoForge 事件的描述，在本移植版中对应到 Forge/Fabric/Architectury 平台代码。
4. 如果资源视图没有出现自带资源，检查资源路径、文件扩展名和资源刷新逻辑。

## Node Graph Toolkit

### 官方功能

Node Graph Toolkit 用于构建游戏内节点图编辑器。移植版保留源码中的图模型、图视图和编辑器接入。

| 功能 | 状态 | 说明 |
| --- | --- | --- |
| `Graph` | 保留 | 用户侧图定义。 |
| `GraphModel` | 保留 | 节点、端口、线、变量、子图、便签、placemat 和脏状态。 |
| Node classes | 保留 | 节点类和注册表保留。 |
| `@NodeAttribute` | 保留 | 节点元数据声明保留。 |
| Ports and wires | 保留 | 输入/输出端口和连接线保留。 |
| Variables and Blackboard | 保留 | 图变量和黑板面板保留。 |
| TypeHandle | 移植变更 | 类型系统保留，涉及流体/物品时使用 compat 类型。 |
| Constants and default values | 保留 | 常量和默认值体系保留。 |
| `GraphView` | 保留 | 低层图画布和面板容器。 |
| `GraphEditorView` | 保留 | 带保存、脏状态、面包屑和子图钻取的编辑器视图。 |
| `GraphResource` | 保留 | 图资源接入 Editor resource system。 |
| Subgraphs | 保留 | 本地/外部子图保留。 |
| Context and block nodes | 保留 | 上下文节点和有序 block list 保留。 |
| Commands and customization | 保留 | 可撤销命令、能力开关、诊断和 UI 自定义保留。 |

### 移植后变更

1. 节点图本体基本沿用官方模型。
2. 和 UI、资源、配置器、同步系统交互时，仍需遵守本移植版 compat import。
3. 若自定义 TypeHandle 包含 1.21 DataComponent，需改成 1.20.1 可表达的数据结构。
4. 图编辑器资源可通过普通文件资源源保存；打包的示例图资源如需只读发布，可放入 `assets/<modid>/resources/`。

## Shaders, Textures and Model Rendering

### 官方功能

官方 wiki 将 Shaders 和 Model Rendering 作为核心模块之一。源码中对应 `client/shader`、`client/renderer`、`client/model`、GUI texture、Scene 等体系。

| 功能 | 状态 | 说明 |
| --- | --- | --- |
| Core shader 注册 | 移植变更 | 保留 shader 注册入口，适配 1.20.1 构造和资源加载。 |
| GUI shader texture | 新增修复 | 对 UI/编辑器中的 shader 纹理增加 fallback。 |
| SDF rect | 移植变更 | 保留，依赖 shader 注册兼容。 |
| Graph wire shader | 保留 | 节点图连线渲染使用。 |
| Particle shader | 保留 | 粒子渲染辅助。 |
| Renderer block | 移植变更 | `renderer_block` 方块、物品和 block entity 保留，注册由平台层适配。 |
| Renderer model loader | 移植变更 | Forge/Fabric 模型 loader 注册路径不同。 |
| Scene element | 保留 | UI 中可嵌入场景/模型预览。 |

当前 shader 注册中包含：

| Shader | 用途 |
| --- | --- |
| `particle` | 粒子。 |
| `fast_blit` | 快速 blit。 |
| `visual_layer` | 可视层。 |
| `sprite_blit` | sprite blit。 |
| `hsb_block` | HSB block 渲染。 |
| `graph_wire` | 节点图连线。 |
| `sdf_rect` | SDF 矩形。 |
| `gui_texture` | GUI 纹理渲染。 |

### 移植后变更

1. 1.20.1 的 shader 构造和资源加载与 1.21 不同，已在移植版中做兼容。
2. 编辑器透明、SpriteTexture 绘制和 Embeddium/Sodium 类优化路径容易暴露空 shader 问题，移植版已有 fallback 修复，但整合包内其他渲染优化 mod 仍可能影响结果。
3. 官方 NeoForge renderer/model 事件示例需要按本仓库平台层改写。

## XEI, JEI, REI, EMI and KubeJS

| 集成 | 状态 | 说明 |
| --- | --- | --- |
| JEI | 降级/限制 | common/forge 依赖中有 JEI compile/runtime 依赖，但完整 XEI 源码当前被排除。 |
| REI | 降级/限制 | 依赖声明存在，`integration/xei/rei/**` 当前从 common 编译排除。 |
| EMI | 降级/限制 | 依赖声明存在，`integration/xei/emi/**` 当前从 common 编译排除。 |
| KubeJS | 降级/限制 | `integration/kjs/**` 关键源码当前从 common 编译排除。 |
| XEI lookup in slots | 降级/限制 | `ItemSlot`、`FluidSlot` 中保留编辑器字段，但实际查询能力取决于集成是否恢复。 |

结论：官方 wiki 的 XEI/KJS 章节在本移植版里应视为设计目标和上游参考，不应标注为当前完整可用功能。

## Resource, Registry and Platform APIs

### 注册表

当前核心注册表：

| 注册表 | 状态 | 说明 |
| --- | --- | --- |
| `LDLib2Registries.UI_ELEMENTS` | 保留 | UI 元素注册。 |
| `LDLib2Registries.GUI_TEXTURES` | 保留 | GUI 纹理注册。 |
| `LDLib2Registries.CONFIGURATOR_ACCESSORS` | 保留 | 配置器访问器注册。 |
| `LDLib2Registries.RESOURCE_PROVIDER_TYPES` | 移植变更 | 包含 built-in、file，并通过移植版资源逻辑支持 `mod_resources`。 |
| `LDLib2Registries.RENDERERS` | 保留 | renderer 注册。 |

### Platform

移植版 `Platform` 通过 Architectury 和平台实现提供：

| API | 状态 | 说明 |
| --- | --- | --- |
| `platformName()` | 移植变更 | 返回当前平台名。 |
| `isForge()` | 移植变更 | Forge/Fabric 平台判断。 |
| `isModLoaded(...)` | 移植变更 | 通过平台 API 查询 mod。 |
| `getGamePath()` | 移植变更 | 游戏目录。 |
| `executeOnClient(...)` | 移植变更 | 客户端执行。 |
| `executeOnServer(...)` | 移植变更 | 服务端执行。 |

## 官方 wiki 示例迁移清单

从官方 wiki 迁移示例时按以下顺序检查：

1. Minecraft 版本：官方示例默认偏 1.21.x，本仓库是 1.20.1。
2. Loader：官方 NeoForge API 不一定能直接运行，本仓库通过 Forge/Fabric/Architectury 适配。
3. 流体：`FluidStack`、fluid handler、fluid tank 全部优先使用 `com.lowdragmc.lowdraglib2.compat`。
4. 物品 handler：使用 `com.lowdragmc.lowdraglib2.compat.IItemHandler` 等兼容类型。
5. 网络：使用 compat `CustomPacketPayload`、`RegistryFriendlyByteBuf`、`StreamCodec`。
6. DataComponent：1.20.1 不支持官方 1.21 DataComponent 示例，需要改成 NBT/Tag 或自定义数据。
7. XEI/KJS：当前移植版未完整恢复，相关示例不要作为可编译依据。
8. 编辑器资源：自带资源放入 `assets/<modid>/resources/`，通过 `mod_resources` 只读读取。
9. Shader/渲染：若在复杂整合包中崩溃，先排查 shader fallback、Embeddium/Sodium 类优化 mod 和其他渲染 mixin。

## 已知限制

| 限制 | 影响 | 建议 |
| --- | --- | --- |
| 1.21 DataComponent 不存在 | ItemStack/FluidStack 的组件编辑不可用。 | 使用 NBT/Tag 或自定义配置字段替代。 |
| XEI/KJS 编译排除 | 官方 XEI/KJS wiki 示例不能直接使用。 | 后续恢复 loader-specific source set 后再启用。 |
| NeoForge shim 不是运行时 NeoForge | 不能依赖 NeoForge-only 运行行为。 | 对外 API 尽量暴露 LDLib2 compat 或 Architectury 可表达类型。 |
| 渲染优化 mod 组合复杂 | shader、VBO、GUI 渲染可能受第三方 mixin 影响。 | 复现时先用最小 mod 集验证，再逐个加回优化 mod。 |
| 官方 Maven 坐标不适用 | 依赖管理不能直接照抄官方 README。 | 使用本地构建产物或为移植版单独发布坐标。 |

## 验证建议

文档更新本身不改变运行时代码。后续修改功能时建议按模块验证：

| 模块 | 建议验证 |
| --- | --- |
| 构建 | `./gradlew.bat :fabric:build :forge:build --stacktrace`。 |
| UI/编辑器 | 启动客户端，执行 `ldlib2_ui_editor`，检查元素面板、资源面板、Inspector 和保存。 |
| 内置资源 | 确认资源面板能看到 `examples/button`、`examples/example_layout`。 |
| Sync/RPC | 用测试 block entity 或测试 UI 触发字段同步、持久化和 RPC。 |
| Configurable | 打开带 `@Configurable` 的对象，确认 Inspector、History、复制粘贴和持久化。 |
| Node Graph | 打开 `GraphEditorView`，测试节点创建、连线、变量、子图、保存。 |
| Shader/Renderer | 测试 `shader_texture`、`sdf_rect_texture`、`scene`、renderer block/model。 |
| XEI/KJS | 当前只做限制确认，不应作为可用功能验收。 |

## 源码锚点

| 功能 | 主要源码/资源 |
| --- | --- |
| 版本与平台 | `gradle.properties`、`build.gradle`、`common/build.gradle`、`forge/build.gradle`、`fabric/build.gradle` |
| 兼容层 | `common/src/main/java/com/lowdragmc/lowdraglib2/compat/` |
| 网络 | `common/src/main/java/com/lowdragmc/lowdraglib2/networking/LDLNetworking.java` |
| 注册表 | `common/src/main/java/com/lowdragmc/lowdraglib2/LDLib2Registries.java` |
| UI | `common/src/main/java/com/lowdragmc/lowdraglib2/gui/` |
| GUI textures | `common/src/main/java/com/lowdragmc/lowdraglib2/gui/texture/` |
| Configurable | `common/src/main/java/com/lowdragmc/lowdraglib2/configurator/` |
| Sync/Persistence | `common/src/main/java/com/lowdragmc/lowdraglib2/syncdata/` |
| Editor | `common/src/main/java/com/lowdragmc/lowdraglib2/editor/` |
| Node Graph Toolkit | `common/src/main/java/com/lowdragmc/lowdraglib2/nodegraphtookit/` |
| Shader/Renderer/Model | `common/src/main/java/com/lowdragmc/lowdraglib2/client/` |
| 内置 LSS | `common/src/main/resources/assets/ldlib2/lss/` |
| 内置 UI 资源 | `common/src/main/resources/assets/ldlib2/resources/examples/` |
