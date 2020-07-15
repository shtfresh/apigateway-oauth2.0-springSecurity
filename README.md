# 一个简单的微服务
包含鉴权服务 auth-service，网关服务 gateway-service，业务层 api-service

所有服务基于 spring boot 2.2.0.RELEASE & spring cloud Hoxton.M3 进行开发。

- api-service 是业务服务，提供业务接口，没有 token 验证；
- 通过 gateway-service 可以访问 api-service 的业务接口，并在 gateway 层面实现了统一的用户认证；
- auth-service 提供用户认证和用户鉴权能力。

### auth-service
- 使用 spring cloud oauth2，实现一个简单的基本的 oauth2 provider
- 使用 jwt token，使用自定义 JwtTokenStore
- 提供 `/.well-known/jwks.json` 端点

### gateway-service
- 使用 spring cloud gateway 实现简单路由
- 作为 oauth2 resource server 接入 auth-service

### api-service
- 提供简单的 Restful API，通过 gateway-service 调用

## 运行
依次运行 auth-service，gateway-service，api-service

###### 获取 access token
```shell script
curl -X POST \
  http://localhost:8081/oauth/token \
  -d grant_type=password \
  -d client_id=test-client \
  -d client_secret=test-secret \
  -d username=my-username \
  -d password=my-password

# 将接口返回的 access_token 赋值给 shell 变量，方便后续引用
access_token_here=eyJhb...
```

###### 不带 token 访问接口，返回 401 Unauthorized
```shell script
curl -X GET http://localhost:8082/api/hello -sI
```

###### 带 token 访问接口
```shell script
curl -X GET \
  http://localhost:8082/api/hello \
  -H "Authorization: Bearer ${access_token_here}"
```

###### 查询用户详情
```shell script
# 直接访问 auth-service 接口
curl -X GET \
  http://localhost:8081/users/me \
  -H "Authorization: Bearer ${access_token_here}"

# 或者通过 gateway 访问 auth-service 接口
curl -X GET \
  http://localhost:8082/auth/users/me \
  -H "Authorization: Bearer ${access_token_here}"

```

## 备注
1. 追加了一个不需要认证的微服务，证明只有需要校验token的时候才回去调用认证服务校验jwttoken
2. webflux和传统servlet在集成spring security oauth2.0的方式上很不一样，很多地方还不是很懂
3. spring api gateway 拿不到 springsecurity的上下文，只能手动解析jwt token，token解析是通过认证服务器做的，此操作在网关的filter（通过httpHeader 拿到token 手动解析出用户信息）执行前操作
4. 这个例子中网关需要配置哪uri些是放行（此例中，认证服务器，不需要登陆的微服务两个服务不需要拦截）的，哪些是需要认证的，通过配置认证服务器地址可以校验token的正确性。
5. 在网关filter手动拿token这种方法，不是特别好，没有完全实现自动化，目前位置webflux和spring security oauth2.0 集成不是太完美，相比组zuul，apigateway集成spring security oauth2.0要复杂很多
## 实现步骤
[Spring Cloud Gateway 基于 OAuth2.0 的身份认证](https://mp.weixin.qq.com/s/4v_wwX0SS7jvOwtO8uiDAw)
