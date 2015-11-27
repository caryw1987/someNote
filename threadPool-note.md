ThreadPoolExector
===========================

## 用法

一般来说不会通过直接调用构造函数ThreadPoolExector(....)的用法去实例化一个线程池。
而是通过调用不同的 Executors静态方法去创建不同功能的线程池。

### 常用构造方法
```java
public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue)
```
#### 构造方法参数描述
  - corePoolSize
  	 - 线程池中长期存在的线程的个数, 即使这些线程处于idle状态。
  	 - 当成员变量allowCoreThreadTimeOut 设为 true的时候， corePoolSize会失效
  - maximumPoolSize
     - 线程池中允许存在线程的最大数目
  - keepAliveTime
     - 线程池中当线程处于idel状态后多久会被回收
  - workQueue
  	 - 用于存储Runnable task的阻塞队列。
  	 - 线程池中的works 会不断的在队列中区出task并执行
  	 - 长度为Integer.MAX_VALUE， 当task 不停的被push到这个队列中并超出队列长度时会抛出异常

### Executors 提供创建线程池的静态方法：
  - newFixThreadPool(int threadNum)
     - 调用 ThreadPoolExecutor(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>)
     - 线程池中最大允许线程和 常驻线程设置为常量
     - keepAliveTime 设置为0， 不会影响线程生命周期， 因为线程池中getTask() 方法会有算法判断.
       当前worker线程数目大于corePoolSize时， keepAliveTime才会生效， keepAliveTime会作为workQueue.poll()的超时时间。
       当worker线程数目小于corePoolSize时， worker线程会调用workQueue.take() 方法没有task时会一直阻塞。
     - workQueue： worker会循环调用线程池中getTask() 方法获取要执行的task， 当前worker线程数目大于corePoolSize时会通过
       workQueue.poll()方式取task。 超过keepAliveTime 时间以后会返回，并销毁该线程。 当worker线程数目 < corePoolSize时
       会通过workQueue.take() 获取task， queue为空的话会一直阻塞。
  - newCachedThreadPool()
     - 调用 ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>)
  - newSingleThreadExector()
     - 调用 ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>)

### ThreadPoolExecutor中的Worker
  - 线程池中处理task的线程 被封装成为ThreadPoolExecutor 的内部类 Worker。 Worker是实现了 Runnable接口的类。 并且包含一个
    Thread 类型实例， 该thread实例以自身Runnable对象初始化。
  - Worker 的线程方法run(), 主要作用是开启一个循环， 不断在workQueue 中取task执行。
  	  - 当queue中存在task时会一直循环获取task并执行。
  	  - 当queue中不存在task时
  	      - 假如当前worker数量 <= corePoolSize， 会调用workQueue.take()， 此时没有task， 此方法会被阻塞， 知道有task执行时
  	        继续循环。
  	      - 当前worker 数量 > corePoolSize 时， 会调用workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS)， 阻塞一段时间，有
  	        task的话会继续执行， 超过keepAliveTime 时间还没有task 的话会走向消亡(走向消亡的过程中还会做一次检查 比较当前线程
  	        数量 和corePoolSize， 如果num < 和corePoolSize 的话会执行addWorker() 来添加一个新的worker， 但是之前的已经消亡)。