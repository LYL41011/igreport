# IG-REPORT
IG-REPORT是一个企业级别的智能通用报表平台，支持多种数据源和多种落地，任务和调度均可视化管理，报表查看可控制权限，操作简单，只需30s即可出报表。
项目演示地址 101.37.90.241:8080 普通用户:liuyanling/123456 管理员用户:admin/88888888
# 传统报表方式的弊端
报表是所有企业都必要的分析决策工具，传统的展示报表的方式特别麻烦，步骤大概要经历

- 1、数据库中创建目的表,存储SQL跑批后的结果 
- 2、开发后端代码,从service到dao层都要开发,需要实现定时调度跑批 
- 3、开发前端代码,html\css\jquery\ajax等

这些步骤从技术的角度来看算简单的、但却永远在做重复的事情。大概需要花费一小时时间和500行+代码。除了繁琐的程序、无意义的重复代码和工作，还有很多痛点:

- 1、没有统一的调度平台、如果不看代码我就不知道今天要执行多少报表任务、不知道何时执行、不知道执行是否成功、失败了没有告警机制、失败了要登录服务器看日志才知道错误信息等。
- 2、不能实时的对报表任务进行管理、比如一个大sql跑了20分钟我需要kill掉，要去服务器操作、很麻烦；不能实时查看报表日志掌握第一手消息。
- 3、报表数据其实没有必要新建一个表来存储、这样未免太浪费资源和时间。。

还有很多弊端和琐事就不一一举例了，总之，你需要一个智能的报表平台！这些，在IG-REPORT中你都可以解决。

# IG-REPORT智能报表

IG-REPORT智能报表适用于任何企业、支持多种数据源、只需要30s就可以完成一个报表的配置。大概功能如下:

- 1、首页总体概览、清晰知道整个公司目前一个报表的数量、调度的次数、并且有耗时统计、失败统计等，方便揪出那些异常的报表

![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-dashbord.png)

- 2、web界面一键化配置报表、支持多种数据源(MYSQL\TIDB\Presto\Pgxl 其他也都行 自己开发就好)、只要把sql和sql对应的元数据信息配上去，其他所有事都交给IG-REPORT去完成

![image](http://bigdata-star.com/wp-content/uploads/2020/02/add-task.png)

- 3、如果通用报表配置不能满足您的要求、完全可以自行开发某些特定报表，比如我的需求不仅仅是写个sql跑出数据来就行，我数据来源是kafka，那么你可以自行开发一个kafkaHandler。

![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-add-special-task.png)


- 4、分布式调度平台，基于quartz做了很多改造。（注:调度这块大部分是直接用的xxl-job源码，这是一个非常好的分布式调度平台）
- 5、统一的任务管理平台，可动态修改任务参数、方便操控任务，比如启动、禁用任务
![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-my-task.png)
![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-edit.png)


- 6、在线查看调度状态和结果,可动态终止运行中任务,即时生效
![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-scheduler-task.png)

- 7、可在线实时查看完整的调度日志
![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-log-error.png)
![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-log-running.png)

- 8、任务失败告警、可以配置多人的邮箱。
![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-alarm.png)

- 9、报表具有权限控制、创建报表的时候需指定授权用户，其他用户则无法看见。
![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-authpeople.png)
![image](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-report.png)

- 10、管理员可以查看和操控所有的任务、可以管理用户、普通用户只可以查看自己的任务



