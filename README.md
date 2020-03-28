# IG-REPORT
IG-REPORT是一个企业级别的智能通用报表平台，支持多种数据源和多种落地，任务和调度均可视化管理，报表查看可控制权限，操作简单，只需30s即可出报表。

# 传统报表方式的弊端
报表是所有企业都必要的分析决策工具，传统的展示报表的方式特别麻烦，步骤大概要经历
1、数据库中创建一个目的表 存储SQL跑批后的结果 2、开发后端代码 从service到dao层都要开发 还要实现定时调度 3、开发前端代码 html\css\jquery\ajax等
这些步骤从技术的角度来看算简单的、但却永远在做重复的事情。大概需要花费一小时时间和500行+代码。
除了繁琐的程序、无意义的重复代码和工作，还有很多痛点:
1、没有统一的调度平台、如果不看代码我就不知道今天要执行多少报表任务、不知道何时执行、不知道执行是否成功、失败了没有告警机制、失败了要登录服务器看日志才知道错误信息等。
2、不能实时的对报表任务进行管理、比如一个大sql跑了20分钟我需要kill掉，要去服务器操作、很麻烦；不能实时查看报表日志掌握第一手消息。
3、报表数据其实没有必要新建一个表来存储、这样未免太浪费资源和时间。。
等等。。。
而这些，在IG-REPORT中你都可以解决。

# IG-REPORT智能报表

IG-REPORT智能报表适用于任何企业、支持多种数据源、只需要30s就可以完成一个报表的配置。大概功能如下:

1、首页总体概览、清晰知道整个公司目前一个报表的数量、调度的次数、并且有耗时统计、失败统计等，方便揪出那些异常的报表

<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-dashbord.png" alt="" width="1918" height="943" class="alignnone size-full wp-image-2199" />

2、web界面一键化配置报表、支持多种数据源(MYSQL\TIDB\Presto\Pgxl 其他也都行 自己开发就好)、只要把sql和sql对应的元数据信息配上去，其他所有事都交给IG-REPORT去完成
<img src="http://bigdata-star.com/wp-content/uploads/2020/02/add-task.png" alt="" width="1447" height="849" class="alignnone size-full wp-image-2209" />

3、如果通用报表配置不能满足您的要求、完全可以自行开发某些特定报表，比如我的需求不仅仅是写个sql跑出数据来就行，我数据来源是kafka，那么你可以自行开发一个kafkaHandler。
<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-add-special-task.png" alt="" width="1573" height="708" class="alignnone size-full wp-image-2206" />

3、分布式调度平台，基于quartz做了很多改造。（注:调度这块大部分是直接用的xxl-job源码，这是一个非常好的分布式调度平台）
4、统一的任务管理平台，动态修改任务参数、启动、禁用任务
<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-my-task.png" alt="" width="1865" height="884" class="alignnone size-full wp-image-2197" />


<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-edit.png" alt="" width="1655" height="908" class="alignnone size-full wp-image-2202" />

5、在线查看调度结果,可动态终止运行中任务，即时生效；并且实时的展示完整的调度日志。
<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-scheduler-task.png" alt="" width="1897" height="664" class="alignnone size-full wp-image-2196" />

<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-log-error.png" alt="" width="1763" height="864" class="alignnone size-full wp-image-2200" />

<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-log-running.png" alt="" width="1622" height="658" class="alignnone size-full wp-image-2201" />

6、任务失败告警、可以配置多人的邮箱。

<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-alarm.png" alt="" width="1535" height="589" class="alignnone size-full wp-image-2207" />
7、报表具有权限控制、创建报表的时候需指定授权用户，其他用户则无法看见。

<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-authpeople.png" alt="" width="959" height="75" class="alignnone size-full wp-image-2205" />

<img src="http://bigdata-star.com/wp-content/uploads/2020/02/igreport-report.png" alt="" width="1891" height="758" class="alignnone size-full wp-image-2204" />


8、管理员可以查看和操控所有的任务、可以管理用户、普通用户只可以查看自己的任务
