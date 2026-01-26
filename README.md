# ClinicSystem

#### 介绍
口腔诊所系统

#### 软件架构
软件架构说明


#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  cd "C:\Program Files\Redis-3.0.504"; .\redis-server.exe
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request 

curl https://loca.lt/mytunnelpassword

lt --port 4567 --local-host localhost


#### 说明
Actuator 端点：
            访问 http://localhost:4300/swagger-ui/index.html#/ 查看swagger 文档    
            访问 http://localhost:4300/actuator/loggers/com.dental.clinic 可以查看 com.dental.clinic 包的日志级别。
            访问 http://localhost:4300/actuator/loggers 可以查看当前所有 Logger 的名称和日志级别。     

```
com.dental.clinic
├─ common
├─ config
├─ module
│    └─ user
│         ├─ controller
│         │     └─ UserController.java
│         ├─ service
│         │     ├─ UserService.java
│         │     └─ impl
│         │           └─ UserServiceImpl.java
│         ├─ mapper
│         │     └─ UserMapper.java
│         ├─ entity
│         │     └─ User.java
│         ├─ dto
│         │     ├─ UserCreateDTO.java
│         │     ├─ UserUpdateDTO.java
│         │     ├─ UserQueryDTO.java
│         │     └─ UpdateProfileDTO.java
│         ├─ vo
│         │     └─ UserDetailVO.java
│         ├─ convert
│         │     └─ UserConvert.java
│         └─ enums
│               └─ UserStatusEnum.java
└─ ClinicSystemApplication.java
```