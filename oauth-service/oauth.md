## oauth认证中心 【oauth-service】

### 一、原理流程
#### ①理解Security组件
```text
1、SecurityContextHolder:用来存储安全上下文信息,Spring Security 校验之后,验证信息存储在SecurityContext

2、Authentication:是Spring Security中最高级别的身份/认证的抽象。在进入Security前得把信息封装成Authentication

3、AuthenticationManager:是用来校验Authentication。常见的实现类有ProviderManager.

  但ProviderManager将校验工作交给了另外一个组件AuthenticationProvider来完成.ProviderManager中维护一个List<AuthenticationProvider>,
  
  通过遍历找到支持当前Authentication认证的AuthenticationProvider,交给其进行认证

4、AuthenticationProvider:对Authentication进行校验的组件.常见的有:
   
  4.1、DaoAuthenticationProvider:Dao式认证Provider,从数据库中取出信息与提交信息进行对比,完成认证
  
  4.2、AnonymousAuthenticationProvider:匿名认证Provider

5、UserDetails:与Authentication进行理解对比.Authentication是来自用户提交的数据封装,UserDetails是从数据库获取的用户信息封装

6、UserDetailsService:DaoAuthenticationProvider认证器从数据库层取数据是通过UserDetailsService 完成的，取到的是UserDetails.常见有:
  
  6.1、JdbcDaoImpl:数据库查询

  6.2、ClientDetailsUserDetailsService:查询[clientId,clientSecret ]形式的UserDetails
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
#### ④流程图
```text

```

### 二、代码层面


```
 <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-oauth2</artifactId>
 </dependency>
```
### 一、spring-cloud-starter-oauth2 是对spring-cloud-starter-security、spring-security-oauth2、spring-security-jwt这3个依赖的整合。
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





### spring security 权限管理框架
#### 核心功能 【认证：登录】 和【授权：权限鉴别】
#### PasswordEncoder类：密码加密
```
Spring Security 采用BCryptPasswordEncoder进行加密。BCryptPasswordEncoder 就是 PasswordEncoder 接口的实现类。
```
#### WebSecurityConfigurerAdapter类：配置spring security相关配置
#### SecurityContextHolder 是 SecurityContext的存放容器，默认使用ThreadLocal 存储，意味SecurityContext在相同线程中的方法都可用。
```
SecurityContextHolder, 用来访问 SecurityContext.
SecurityContext, 用来存储Authentication .
Authentication, 代表凭证.
GrantedAuthority, 代表权限.
UserDetails, 用户信息.
UserDetailsService,获取用户信息.
```
#### AuthenticationManager :实现认证主要是通过AuthenticationManager接口
#### AuthenticationProvider: 不同的认证






### 采用spring security + Oauth2
##授权服务器配置
```
@EnableAuthorizationServer注释用于配置的OAuth 2.0授权服务器机制

ClientDetailsServiceConfigurer：定义客户端详细信息服务的配置器。可以初始化客户端详细信息;

AuthorizationServerSecurityConfigurer：定义令牌端点上的安全约束;

AuthorizationServerEndpointsConfigurer：定义授权和令牌端点以及令牌服务;
```
### 配置客户端详细信息
```
可以通过实现JdbcClientDetailsService来自定义客户端信息信息;客户端的重要熟悉是

clientId：（必需）客户端ID。

secret：（受信任的客户端需要）客户端机密，如果有的话。

scope：客户端受限的范围。如果范围未定义或为空（默认），则客户端不受范围限制。

authorizedGrantTypes：授权客户端使用的授权类型。默认值为空。

authorities：授予客户端的权限（常规 Spring Security 权限）。
```

### 管理令牌
```
该AuthorizationServerTokenServices接口定义了管理 OAuth 2.0 令牌所需的操作

创建访问令牌时，必须存储身份验证，以便接受访问令牌的资源可以稍后引用它。

访问令牌用于加载用于授权其创建的身份验证。

在创建的AuthorizationServerTokenServices实现类，使用DefaultTokenServices具有许多可插入的策略来更改访问令牌的格式和存储的 。
默认情况下，它通过随机值创建令牌并处理除它委托给TokenStore.
```

### 步骤
```
用户编写WebSecurityConfigurerApdater的继承类，配置HttpSecurity，包括formLogin，antMatcher，hasRole等等。

项目启动自动装配FilterChain，访问不同uri对应不同的Filter Chain。

用户输入账号、密码点击登录，FilterChainProxy中的UsernamePasswordAuthenticationFilter获取request中的用户名、密码，验证身份信息

doFilter()过程中会执行ProviderManager.authenticate()，即遍历所有AuthenticationProvider执行authenticate()方法。

authenticate()方法中会调用userDetailService，用户自定义类继承UserDetailService，并重写其中的方法loadUserByUsername()，从数据库中获取用户信息进行比对

比对成功后将用户信息和角色信息整合成Authentication，并存入SecurityContext中，同时将SecurityContext也存入session中，跳转到主页面。

比对失败，SecurityContext中没有Authentication，FilterChain进行到最后一步FilterSecurityInterceptor，判断用户角色是否能访问request中的访问地址即资源。如果不行则报错跳转到指定页面；如果成功则进入request调用的资源。
```
### 思想
```
Spring Security的核心思想是用户授权和资源认证。认证访问系统的用户，而授权则是用户可以访问的资源

认证是调用authenticationManager.authenticate()方法来获得证书authentication，一般我们采用用户名、密码方式认证，那么authentication的实现类就是UsernamePasswordAuthentication。

授权是让用户可以访问哪些资源，一般在WebSecurityConfigurerApdater的继承类中编写。

authorizeRequests().antMatchers("/static/**","/webjars/**","/resources/**").permitAll()

一句话概括，就是按顺序依次获得authentication ---> Authorization code  ----> access_token。

@FrameworkEndpoint与@Controller的同义词，但仅用于框架提供的端点
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
# spring security 
## 核心组件
### SecurityContextHolder
```
用户存储安全上下文的信息,当前操作的用户是谁,该用户是否已经被认证,他拥有哪些角色权限等等，这些都保存在SecurityContextHolder中。

SecurityContextHolder默认使用ThreadLocal 策略来存储认证信息。看到ThreadLocal 也就意味着，这是一种与线程绑定的策略。

Spring Security在用户登录时自动绑定认证信息到当前线程，在用户退出时，自动清除当前线程的认证信息

身份信息的存放容器SecurityContextHolder，身份信息的抽象Authentication，身份认证器AuthenticationManager

在Spring Security中。提交的用户名和密码，被封装成了UsernamePasswordAuthenticationToken，而根据用户名加载用户的任务则是交给了UserDetailsService
```

### DaoAuthenticationProvider
```
DaoAuthenticationProvider：它获取用户提交的用户名和密码，比对其正确性，如果正确，返回一个数据库中的用户信息（假设用户信息被保存在数据库中）
```

### 原理
```text
通过拦截器获取拦截，将请求参数进行包装，之后通过认证管理器来分配对应的认证提供者进行认证，
通过自定义的用户详情获取用户的信息，进行认证
```
