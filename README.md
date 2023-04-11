## 介绍

用于解决rcptt本身和运行的程序内存不释放问题的jar包

## 使用
使用示例
`-javaagent:E:/testPlace/result/rcpttAgent.jar=intervalTime=120,initialDelay=300,hasGcLog=true`

**属性值**
 - intervalTime: 默认值120, 单位s. 执行任务间隔事件
 - initialDelay: 默认值300, 单位s. 启动后多少时间后开始执行任务
 - hasGcLog: 默认值true. 是否有执行gc的日志

## 联系

如果有什么问题，可以发邮件到 liang.tang.cx@gmail.com
