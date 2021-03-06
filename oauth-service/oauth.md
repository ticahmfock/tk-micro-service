## oauth认证中心 【oauth-service】
### 功能设想
```text
1、支持多种模式的实现并使用，如正常密码登陆由UserNamePassword进行校验，包括但不限于短信登录，微信登录；登陆第三方授权授权模式；邮件发送jwt

2、支持自定义选中是否单点登录

3、针对不同平台来源来进行不同的权限颗粒控制
```
### 一、原理流程
#### ①理解Security组件
```text
1、SecurityContextHolder:是SecurityContext的存放容器，默认使用ThreadLocal 存储，意味SecurityContext在相同线程中的方法都可用,生命周期是request

2、Authentication:是Spring Security中最高级别的身份/认证的抽象。在进入Security前得把信息封装成Authentication;Authentication是没有失效时间的

3、AuthenticationManager:是用来校验Authentication。常见的实现类有ProviderManager.

  但ProviderManager将校验工作交给了另外一个组件AuthenticationProvider来完成.ProviderManager中维护一个List<AuthenticationProvider>,
  
  通过遍历找到支持当前Authentication认证的AuthenticationProvider,交给其进行认证

4、AuthenticationProvider:对Authentication进行校验的组件.常见的有:
   
  4.1、DaoAuthenticationProvider:Dao式认证Provider,从数据库中取出信息与提交信息进行对比,完成认证
  
  4.2、AnonymousAuthenticationProvider:匿名认证Provider

  4.3、继承AbstractUserDetailsAuthenticationProvider来实现自定义的AuthenticationProvider来进行认证authenticate()

5、UserDetails:与Authentication进行理解对比.Authentication是来自用户提交的数据封装,UserDetails是从数据库获取的用户信息封装

6、UserDetailsService:DaoAuthenticationProvider认证器从数据库层取数据是通过UserDetailsService 完成的，取到的是UserDetails.常见有:
  
  6.1、JdbcDaoImpl:数据库查询

  6.2、ClientDetailsUserDetailsService:查询[clientId,clientSecret ]形式的UserDetails
    
  6.3、通过继承UserDetailsService来实现自定义获取用户信息UserDetails方式.

```
#### ②Security中的Filter
```text
1、请求都是经过一系列的filter链到达Servlet的.类似 Filter1-->Filter2-->Filter3-->...-->servlet

2、Spring Security 在Web应用中,是通过filter介入的,为了介入到主体ApplicationFilterChain中,这里介绍一个特殊的Filter【DelegatingFilterProxy】

```

#### DelegatingFilterProxy:授权过滤代理
```text
1、本质是一个Filter,内部存在一个Filter delegate属性,用来代理另外一个Filter.当请求执行到DelegatingFilterProxy时,会调用delegate这个filter.
    
    DelegatingFilterProxy可以看做一个可以让Filter链拐弯的Filter

Filter-->DelegatingFilterProxy-->Filter-->Servlet
          ↓          ↑
         Filter    Filter
          ↓         ↑
         Filter-->Filter 

  
```
#### FilterChainProxy
```text
1、本质是一个Filter,Security就是通过把它设置到DelegatingFilterProxy.delegate属性上来介入了主体FilterChain.是一个代理性质的Filter

2、它内部维护了一个List<SecurityFilterChain> filterChains来表示不同权限的url对应的不同的过滤器链,但是一次请求最多只有一个SpringSecurityFilterChain链
```
#### SpringSecurityFilterChain
```text
1、FilterChainProxy遍历List<SecurityFilterChain> filterChains匹配到一个适用于当前请求的SecurityFilterChain然后就是链式调用
```
#### 常见的Filter
```text
1、SecurityContextPersistenceFilter:位于SecurityFilterChain的顶端.以前我们使用session来存储用户信息，用了Security框架后，用户登录一次，后续通过sessionId 来识别，

   用户信息存放到SecurityContextHolder中，这个放入的过程就是SecurityContextPersistenceFilter完成的,
  
   SecurityContextPersistenceFilter的主要工作创建 SecurityContext 安全上下文信息和请求结束时清空 SecurityContextHolder

2、UsernamePasswordAuthenticationFilter:表单认证是最常用的一个认证方式，允许表单输入用户名和密码进行登录。
   
   它会先将 [username,password]封装成Authentication然后交给authenticationManager 认证,authenticationManager 会选择一个provider 
  
   通过UserDetailsService从redis获取mysql等数据层面获得存储用户信息的数据的UserDetail与Authentication 进行对比。认证成功

3、ExceptionTranslationFilter:异常转换过滤器位于整个 springSecurityFilterChain 的后方,主要处理两大异常:

   AccessDeniedException 访问异常和 AuthenticationException 认证异常.根据配置和异常类型，会选择跳转到登录页面，或者404 ，405页面.
      
``` 
#### 核心思想
```text
1、用户授权:认证访问系统的用户,调用authenticationManager.authenticate()方法来获得证书authentication;authentication的实现类就是UsernamePasswordAuthentication。

2、资源认证:授权用户可以访问的资源,一般在WebSecurityConfigurerAdapter的继承类中编写.
```
#### Spring Security核心对象
```text
1、Spring Security核心对象大致分为两类；一类是用户对象，一类是认证对象

2、用户对象:用来描述用户并完成对用户信息管理；涉及UserDetails、GrantedAuthority、UserDetailsService、UserManager四个核心对象

    2.1、UserDetails:描述Spring Security中的用户

    2.2、GrantedAuthority:定义用户的操作权限

    2.3、UserDetailsService:定义对UserDetails的查询操作

    2.4、UserDetailsManager:扩展UserDetailsService,添加了创建用户、修改用户密码等功能
```
#### 授权
```text
1、access()方法来进行更加细粒度的权限控制;http.authorizeRequests().anyRequest().access("hasAuthority('CREATE')");

2、
```

#### ③@EnableWebSecurity注解
```code
Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Import({ WebSecurityConfiguration.class,
		SpringWebMvcImportSelector.class })
@EnableGlobalAuthentication
@Configuration
public @interface EnableWebSecurity {
	boolean debug() default false;
}

=====================================================

@Import(AuthenticationConfiguration.class)
@Configuration
public @interface EnableGlobalAuthentication {
}
```
```text
1、@EnableWebSecurity注解的工作就是激活三个类:

  1.1、SpringWebMvcImportSelector:判断当前的环境是否包含 springmvc

  1.2、WebSecurityConfiguration:是用来配置 web 安全的

  1.3、AuthenticationConfiguration: 配置认证相关的核心类，主要负责生成全局的身份认证管理者 AuthenticationManager
```
#### 阅读Spring相关框架的源码技巧
```text
1、xxConfiguration: 这种格式的配置文件，我们可以看做是一个bean.xml文件，对容器输出Bean

2、xxConfigurer: 这种格式的配置文件，通常是要被xxConfiguration获取到。xxConfiguration文件从xxConfigurer中提取配置，统一到xxConfiguration中处理

总结:xxConfiguration会搜集N个相关的xxConfigurer到本类中解析他们，统一成一个xxConfiguration配置文件对容器输出Bean.
```
#### WebSecurityConfiguration
```code

public class WebSecurityConfiguration implements ImportAware, BeanClassLoaderAware {

	//搜集SecurityConfigurer到本类中，做集中解析。
	private List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers
    
     //输出springSecurityFilterChain bean
    @Bean(name = "springSecurityFilterChain";)
	public Filter springSecurityFilterChain() throws Exception {
		boolean hasConfigurers = webSecurityConfigurers != null
				&& !webSecurityConfigurers.isEmpty();
		if (!hasConfigurers) {
			WebSecurityConfigurerAdapter adapter = objectObjectPostProcessor
					.postProcess(new WebSecurityConfigurerAdapter() {
					});
			webSecurity.apply(adapter);
		}
		return webSecurity.build();//构建FilterChainProxy
	}
}
```
```text
1、搜集相关的SecurityConfigurer:我们集成security时，通常会继承WebSecurityConfigurerAdapter做安全配置,
   
   WebSecurityConfigurerAdapter本身实现了SecurityConfigurer,这样们的配置信息会被解析到WebSecurityConfiguration配置类中
   
   作用到security中。所以这就是为啥我们要实现一个WebSecurityConfigurerAdapter来配置安全策略的原因

2、输出（FilterChainProxy）springSecurityFilterChain bean:这样就算是在代码层面与主题FilterChain对接上了

3、WebSecurityConfigurerAdapter适配器模式的运用，使我们可以选择性的实现部分配置
```

#### ⑤security oauth2原理
```text
1、Security Oauth2 如何架设在Security框架之上

2、Security Oauth2 又发生了哪些变化呢

3、Oauth2的四种认证方式是如何实现的
```
#### Oauth2组件
```text
1、TokenEndpoint:理解为一个Controller ，/oauth/token接口就是在这里,是Oauth逻辑处理的地方

2、TokenGranter:令牌授予者,Oauth2规范的实现就是此组件实现的
   
   2.1、AuthorizationCodeTokenGranter:授权码模式

   2.2、ClientCredentialsTokenGranter:客户端模式
   
   2.3、ImplicitTokenGranter:implicit简化模式
  
   2.4、RefreshTokenGranter:刷新token模式

   2.5、ResourceOwnerPasswordTokenGranter:密码模式

3、TokenServices:定义了对Token的一些操作，创建，获取，刷新,我们把他理解为Service层

   3.1、AuthorizationServerTokenServices:授权服务器端用到的tokenServices

   3.2、ResourceServerTokenServices:资源服务器端的tokenServices

4、这三个组件总体关系就是;请求到TokenEndPoint(Controller),交给TokenGranter进行授权;
   
   在TokenGranter授权过程中调用TokenServices生成Token
```
#### Oauth2相关注解
```code
//权限服务器配置
@Configuration
@EnableAuthorizationServer
protected static class MyAuthorizationServerConfiguration extends  AuthorizationServerConfigurerAdapter {
}

//@EnableAuthorizationServer注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AuthorizationServerEndpointsConfiguration.class, AuthorizationServerSecurityConfiguration.class})
public @interface EnableAuthorizationServer {

}


//资源服务器配置
@Configuration
@EnableResourceServer
protected static class MyResourceServerConfiguration extends
    ResourceServerConfigurerAdapter {
    
}

//@EnableResourceServer注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ResourceServerConfiguration.class)
public @interface EnableResourceServer {

}
```
#### @EnableAuthorizationServer注解
```text
1、此注解主要是激活两个配置类:
 
    1.1、AuthorizationServerEndpointsConfiguration:配置TokenEndpoint 等Controller类，也就是注册Controller式Bean，例如/oauth/token接口
 
    1.2、AuthorizationServerSecurityConfiguration:间接实现了SecurityConfigurer接口.
 
        SecurityConfigurer接口在上面说过,会在启用Security时,被WebSecurityConfiguration配置类搜集解析.这样Oauth2的配置与Security配置体系关联起来了

    1.3、自定义类AuthorizationServerConfig:继承AuthorizationServerConfigurerAdapter,间接继承了AuthorizationServerConfigurer.
        
        而AuthorizationServerConfigurer会被AuthorizationServerSecurityConfiguration搜集解析的;

        而AuthorizationServerSecurityConfiguration会被WebSecurityConfiguration配置类搜集;

        这样我们自定义的配置就这样跟Security配置体系关联起来了
```
```code
public class AuthorizationServerSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private List<AuthorizationServerConfigurer> configurers = Collections.emptyList();
} 
```
#### @EnableResourceServer注解
```text
1、主要是激活ResourceServerConfiguration类

2、ResourceServerConfiguration类:也间接的实现了SecurityConfigurer接口;也就是说它也会被WebSecurityConfiguration配置类搜集解析

3、自定义类ResourceServerConfig:实现了ResourceServerConfigurer 接口,他会被ResourceServerConfiguration配置类搜集解析,
 
   最终也是会进入WebSecurityConfiguration配置类
```
* #### Oauth2通过多出两个配置类,间接配置了security,最终都会在Security框架体系内生效,也就是说Oauth2框架就是架设在Security框架上的
* #### Spring security 在web应用中是基于Filter的
* #### Spring security Oauth2 基于 Security 框架添加认证模式的逻辑       

#### Security Oauth2流程图
* #### Token获取流程图
![流程图](./流程图.png "流程图")

### 二、代码层面
#### ①导入spring-cloud-starter-oauth2依赖
```text
1、spring-cloud-starter-oauth2:是对spring-cloud-starter-security、spring-security-oauth2、spring-security-jwt这3个依赖的整合。
```
```
 <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-oauth2</artifactId>
 </dependency>
```
#### 1、spring-cloud-starter-security
* 核心概念
```text
1、AuthenticationManager：用户认证的管理类,所有的认证请求（比如login）都会通过提交一个token给AuthenticationManager的实现类的authenticate()方法来实现。
2、AuthenticationProvider：认证的具体实现类，一个provider是一种认证方式的实现
3、UserDetailService：Provider通过UserDetailService拿到认证信息。
4、AuthenticationToken：认证token
5、SecurityContext：用户通过认证之后，就会为这个用户生成一个唯一的SecurityContext，里面包含用户的认证信息Authentication。
```
* ### spring-security-oauth2
```text

```
* ### spring-security-jwt:主要提供jwt服务
```text

```






#### 问题
```text
1、ProviderManager 内部会维护一个List<AuthenticationProvider>列表，存放多种认证方式。什么时候存入到这个列表的
```






### 管理令牌
```
该AuthorizationServerTokenServices接口定义了管理 OAuth 2.0 令牌所需的操作

创建访问令牌时，必须存储身份验证，以便接受访问令牌的资源可以稍后引用它。

访问令牌用于加载用于授权其创建的身份验证。

在创建的AuthorizationServerTokenServices实现类，使用DefaultTokenServices具有许多可插入的策略来更改访问令牌的格式和存储的 。
默认情况下，它通过随机值创建令牌并处理除它委托给TokenStore.
```

### oauth2请求路径
```
1、经过dispatchServlet分发请求到TokenEndpoint类下的postAccessToken的方法。

2、获取客户端ID。

3、获取项目启动时配置的客户端数据信息，可以使用redis存储也可以使用数据存储。

4、获取TokenRequest也就是最后返给我们的token。

5、校验作用域scope,检验scope是否一致

6、校验授权权限和模式，implicit模式不允许。

7、校验是否是授权码模式，校验是否是请求刷新token

8、获取token store的数据。

9、获取授权的token并响应客户端
```





