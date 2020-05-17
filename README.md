
![](http://bigdata-star.com/star)

# IG-REPORT
IG-REPORT是一个企业级别的智能通用报表平台，支持多种数据源和多种落地，任务和调度均可视化管理，报表查看可控制权限，操作简单，只需30s即可出报表。

- [项目演示地址](http://101.37.90.241:8081) 
登录账号：普通用户(liuyanling/123456) 管理员用户(admin/88888888)

- 关注【胖滚猪学编程】公众号，以漫画形式让编程so easy,so interesting
 
![](http://bigdata-star.com/wchat1)

项目详细文档可直接查看本人博客：- [项目说明文档](http://bigdata-star.com/archives/2194) 


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

1、首页总体概览、清晰知道整个公司目前一个报表的数量、调度的次数、并且有耗时统计、失败统计等，方便揪出那些异常的报表
![](http://bigdata-star.com/wp-content/uploads/2020/05/igreport-dashbord.png)

2、web界面一键化配置报表、支持多种数据源(MYSQL\TIDB\Presto\Pgxl 其他也都行 自己开发就好)、只要把sql和sql对应的元数据信息配上去，其他所有事都交给IG-REPORT去完成
![](http://bigdata-star.com/wp-content/uploads/2020/02/add-task.png)

3、如果通用报表配置不能满足您的要求、完全可以自行开发某些特定报表，比如我的需求不仅仅是写个sql跑出数据来就行，我数据来源是kafka，那么你可以自行开发一个kafkaHandler。
![](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-add-special-task.png)

3、分布式调度平台，基于quartz做了很多改造。（注:调度这块大部分是直接用的xxl-job源码，这是一个非常好的分布式调度平台）

4、统一的任务管理平台，动态修改任务参数、启动、禁用任务
![](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-my-task.png)
![](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-edit.png)

5、在线查看调度结果,可动态终止运行中任务，即时生效；并且实时的展示完整的调度日志。
![](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-scheduler-task.png)
![](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-log-error.png)
![](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-log-running.png)


6、任务失败告警、可以配置多人的邮箱。
![](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-alarm.png)

7、报表具有权限控制、创建报表的时候需指定授权用户，其他用户则无法看见。
![](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-authpeople.png)
![](http://bigdata-star.com/wp-content/uploads/2020/02/igreport-report.png)


8、管理员可以查看和操控所有的任务、可以管理用户、普通用户只可以查看自己的任务


# 一个完整配置的Demo案例

假设mysql数据源有一个表叫做user_info表,报表需求是每天统计一下总人数,每天8点执行跑批。
那么点击左侧菜单【我的任务】，再点击【新建任务】
按照要求配置相关信息，包括报表名称、报表描述、数据源、调度频率(即什么时候执行任务，cron表达式),授权用户,元数据格式,报表时间(对应报表的开始时间和结束时间，界面上可查看具体提示),SQL，如图所示:

![](http://bigdata-star.com/wp-content/uploads/2020/02/demo.png)

配置完任务后可在【我的任务】中查看到该任务，默认不启动，需要用户手动【启动】，启动之前建议先点击【执行】，这样会马上执行一次，可以查看日志任务是否成功，相当于我们先测试一遍，成功的任务再启动。
点击【执行】后可立刻在【调度日志】中查看到调度信息，点击【日志】可查看具体日志信息。
假如任务显示成功执行，则可在【我的报表】中查询报表信息，起止时间是根据配置任务时的报表时间来决定的，比如刚刚我们跑的是按天的报表，今天是3.28日，那么起止时间分别为3.27和3.28日。

![](http://bigdata-star.com/wp-content/uploads/2020/02/demo1.png)

# 开发指南
本项目非常轻量级，开箱即用。10分钟即可完成项目搭建。

- 至少需要安装以下程序:mysql\mongo，并修改application.properties中的数据库配置
- 在mysql中创建inteport数据库并执行resources/igreport.sql中的ddl脚本
- 运行IgreportCoreApplication
- 访问localhost:8081即可

# 支持作者

由于开发不易,女攻城狮更不易！因此github只公开后端源码+经编译后的前端源码，项目可运行可使用，但无法二次开发前端原始vue源码。

仅需2步即可获取完整源码：
1、在github上点个star;
2、关注【胖滚猪学编程】公众号回复igreport即可获取


