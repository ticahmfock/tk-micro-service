# gateway:
### Spring Cloud Gateway作为所有请求流量的入口，在实际生产环境中为了保证高可靠和高可用，尽量避免重启, 需要实现Spring Cloud Gateway动态路由配置。实现动态路由其实很简单, 重点在于 RouteDefinitionRepository 这个接口. 这个接口继承自两个接口, 其中 RouteDefinitionLocator 是用来加载路由的. 它有很多实现类, 其中的 PropertiesRouteDefinitionLocator 就用来实现从yml中加载路由. 另一个 RouteDefinitionWriter 用来实现路由的添加与删除
#线上项目发布一般有以下几种方案:
```
 1、停机发布
 2、蓝绿部署
 3、滚动部署
 4、灰度发布


 停机发布 这种发布一般在夜里或者进行大版本升级的时候发布，因为需要停机，所以现在大家都在研究 Devops 方案。
 
 蓝绿部署 需要准备两个相同的环境。一个环境新版本，一个环境旧版本，通过负载均衡进行切换与回滚，目的是为了减少服务停止时间。
 
 滚动部署 就是在升级过程中，并不一下子启动所有新版本，是先启动一台新版本，再停止一台老版本，然后再启动一台新版本，再停止一台老版本，直到升级完成。基于 k8s 的升级方案默认就是滚动部署。
 
 灰度发布 也叫金丝雀发布，灰度发布中，常常按照用户设置路由权重，例如 90%的用户维持使用老版本，10%的用户尝鲜新版本。不同版本应用共存，经常与 A/B 测试一起使用，用于测试选择多种方案。

网关核心功能是路由转发，因此不要有耗时操作在网关上处理，让请求快速转发到后端服务上

网关还能做统一的熔断、限流、认证、日志监控等

```
https://blog.csdn.net/qq_38380025/article/details/102968559
https://blog.csdn.net/X5fnncxzq4/article/details/80221488