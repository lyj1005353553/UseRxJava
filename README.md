**RxJava的学习与使用**

**一：添加依赖库**
     
    compile 'io.reactivex.rxjava2:rxjava:2.0.6'
    compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
    
**二：RxJava与Retrofit集成**
    
    compile 'com.squareup.retrofit2:retrofit:2.2.0'
    compile 'com.squareup.retrofit2:converter-gson:2.2.0'
    //compile 'com.squareup.retrofit2:adapter-rxjava:2.1.0' 官方adapter仅支持rxjava1.0
    compile 'com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0'
    
**三：RxJava的优点**
    
    1,RxJava是一个在 Java VM 上使用可观测的序列来组成异步的、 基于事件的程序的库；使用RxJava一个词就是异步，
      然后代码简洁逻辑清晰,它是通过一种扩展的观察者模式来实现的
    2，Rx扩展了观察者模式用于支持数据和事件序列，通过一些的操作符号，统一风格，
      用户无需关注底层的实现：如线程、同步、线程安全、并发数据结构和非阻塞IO；
    
    **RxJava1  vs  RxJava2的差异**：
        1，RxJava 2x 不再支持 null 值，如果传入一个null会抛出 NullPointerException
        2，RxJava2 所有的函数接口(Function/Action/Consumer)均设计为可抛出Exception，解决编译异常需要转换问题。
        3，RxJava1 中Observable不能很好支持背压，在RxJava2中将Oberservable实现成不支持背压，而新增Flowable 来支持背压。
        
        
**四：基本概念**

    1.0：
        Observable(可观察者，即被观察者)，Observer(观察者)，subscribe(订阅)，事件
        Observable和Observer通过subscribe()方法实现订阅关系，从而 Observable 可以在需要的时候发出事件来通知 Observer。
    1.1：**有关于背压**：
        背压是指在异步过程中，由于被观察者发射数据过快,而观察者处理数据不及时，
        导致内存里堆积了太多数据，从而OOM，可以选择不同的策略处理该问题。 
    1.2：**Flowable & Observable**  
        Observable: **不支持背压；**
        Flowable : Observable新的实现，**支持背压**，同时实现Reactive Streams 的 Publisher 接口。
        
        _什么时候用 Observable_:
        一般处理最大不超过1000条数据，并且几乎不会出现内存溢出；
        如果式GUI 鼠标事件，频率不超过1000 Hz,基本上不会背压（可以结合 sampling/debouncing 操作）；
        如果处理的式同步流而你的Java平台又不支持Java Stream（如果有异常处理，Observable 比Stream也更适合）;
        
        _什么时候用 Flowable_:
        处理以某种方式产生超过**10K**的元素；
        文件读取与分析，例如 读取指定行数的请求；
        通过JDBC 读取数据库记录， 也是一个阻塞的和基于拉取模式，并且由ResultSet.next() 控制；
        网络IO流;
        有很多的阻塞和/或 基于拉取的数据源，但是又想得到一个响应式非阻塞接口的。
    1.3：**Single & Completable & Maybe**
        Single: 可以发射一个单独onSuccess 或 onError消息
        Completable: 可以发送一个单独的成功或异常的信号
        Maybe：从概念上来说，它是Single 和 Completable 的结合体，它可以发射0个或1个通知或错误的信号
    
    _**RxJava2的主要操作_**
    1.1：**fromArray & fromIterable & just**,直接从数组或迭代器中产生；
    1.2：**fromFuture & fromCallable**：
         fromFuture, 事件从**非主线程**中产生； fromCallable, 事件从**主线程**中产生， 在需要消费时生产；       
    1.3：**fromPublisher** ，从标准(Reactive Streams)的发布者中产生；
    1.4：**自定义创建(generate & create)**，直接.create()或.generate()
    1.4：**zip** ：每个Flowable中的元素都按顺序结合变换，直到元素最少Flowable的已经发射完毕； 
    1.5：**combineLatest**: 每个Flowable中的发射的元素都与其他Flowable最近发射的元素*结合变换*，
        知道所有的Flowable的元素发射完毕；
    1.6：**amb**: 给定两个或多个Flowable，只发射最先发射数据的Flowable，如下面示例中的f1被发射；
    1.7：**concat**: 给定多个Flowable， 按照Flowable数组顺序,依次发射数据，不会交错，下面示例中f1,f2中数据依次发射;
    1.8：**merge**: 给定多个Flowable， 按照Flowable数组中数据发射的顺序组合成新的Flowable，
        各Flowable数据可能会交错(等价于转换操作中的flatMap)；
    1.9：**switchOnNext**：给定能发射多个Flowable的Flowable,顺序发射各子Flowable,
        最新发射的子Flowable覆盖当前子Flowable中还未发射的元素(由switchMap实现)。
    
    **//转换，过滤与聚合操作**
    1.0：**buffer & groupBy & window**
        buffer 和 window 都可以按时间或者元素数量窗口，buffer是直接转换成元素集，window是将元素集转换成另一个Flowable， 
        groupBy,按照key 来分组，需要元素发射完成才能消费，如果只是对数据处理使用Java8 groupBy更方便；
    1.1：**debounce & throttleFirst & sample** 按照时间区间采集数据
    1.2：**debounce & throttleFirst & sample** 按照时间区间采集数据
    
        **debounce 防抖动**，元素发射后在设定的超时时间内没有其它元素发射，则将此元素用于后续处理，
        在前端APP应用较多。如果是空间上的防抖动可以利用distinctUntilChanged操作符。   
        **throttle 限流操作**，对于 throttleFirst是 取发射后元素，经过间隔时间后的第一个元素进行发射。  
        **sample 数据采样**, 对于源数据，发射间隔时间内的最后出现的元素。 
        
    1.3：take & skip & first & emlmentAt,精确获取数据(集)
    
**五：实现**

    1，创建Observer,Observer即观察者，它决定事件触发的时候将有怎样的行为
    2，创建Observable,即被观察者，它决定什么时候触发事件以及触发怎样的事件。
    3，订阅Subscribe，创建了 Observable 和 Observer 之后，再用 subscribe()
    方法将它们联结起来，整条链子就可以工作了。
    4，自定义创建(generate & create)，直接.create()或.generate()
    
    异步：1,单个数据Futrue<T> getData() ; 2,多个数据Obervable<T> getData()
    
    ****异步与并发（Asynchronized & Concurrency）****
        RxJava 通过一些操作统一了 同步和异步，阻塞与非阻塞，并行与并发编程。
    **observeOn & subscribeOn & Scheduler**
        subscribeOn 和 observeOn 都是用来切换线程用的,都需要参数 Scheduler.
        Scheduler ,调度器, 是RxJava 对线程控制器 的 一个抽象,RxJava 已经内置了几个 Scheduler ，它们已经适合大多数的使用场景：
        
        trampoline, 直接在当前线程运行（继续上一个操作中，最后处理完成的数据源所处线程，并不一定是主线程），相当于不指定线程;
        computation, 这个 Scheduler 使用的固定的线程池(FixedSchedulerPool)，大小为 CPU 核数, 适用于CPU 密集型计算。
        io,I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率;
        newThread, 总是启用新线程，并在新线程中执行操作；
        single， 使用定长为1 的线程池（newScheduledThreadPool(1)），重复利用这个线程;
        Schedulers.from， 将java.util.concurrent.Executor 转换成一个调度器实例。
     1.1 subscribeOn 将Flowable 的数据发射 切换到 Scheduler 所定义的线程， 只有第一个 subscribeOn 操作有效 ；
     1.2 observeOn 指定 observeOn 后续操作所在线程，可以联合多个 observeOn 将切换多次 线程 ；
     
     
**参考文案**

     1，响应式编程介绍：https://zouzhberk.github.io/rxjava-study/#rxjava-%E5%9F%BA%E7%A1%80
