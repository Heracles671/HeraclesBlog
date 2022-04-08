# 进程和线程
具象地理解，进程就是你的应用程序，线程是进城开启的的一条条流水线，用来执行具体任务，没什么可比较性，逻辑上是一个从属关系。进程和线程的运行都是由操作系统来控制。
# 线程相关 API
>1. 如果线程池中的线程数量未达到核心线程数，那么会直接启动一个核心线程来执行任务
>2. 如果线程池中的线程数量达到或者超过核心线程数，那么任务会被插入到任务队列中排队执行
>3. 如果步骤2中无法将任务插到任务队列中，这往往是由于任务队列已满，这个时候如果线程数量未达到线程池规定的最大值，那么会立即启动一个非核心线程来执行任务
>4. 如果步骤3中的线程数达到线程池规定的最大值，那么就拒绝执行任务，ThreadPoolExecutor 会调用 RejectExecutionHandler 的 rejectedException 方法来通知调用者
## Executors
```java
Executors.newCachedThreadPool();
Executors.newFixedThreadPool(5);
Executors.newScheduledThreadPool(5);
Executors.newSingleThreadExecutor();
```
## ExecutorService
```java
ExecutorService executors = new ThreadPoolExecutor(5, 5,
                60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });
```
# 线程同步
如果多个线程对同一个资源进行读写，会导致线程同步问题
## 同步性
理解线程同步问题，首先理解什么叫同步性。我们知道，当线程操作共享内存中的变量时，回先把变量拷贝到线程本地缓存中，进行相关操作后，会把最新结果写入回共享内存中，这样其他线程访问共享内存中的数据时，取到的就是有效的值，这就是同步性的定义。
## volatile
保证被修改变量的同步性，这是这种同步性仅限于变量的原子操作。
>原子操作：基本类型（long 和 double 除外）赋值；引用类型赋值
## synchronize
- 保证方法或代码块内部资源（数据）的互斥访问。即同一时间、由同一个 Monitor 监视的代码，最多只能有一个线程在访问
- 保证线程之间对监视资源的数据同步。即，任何线程在获取到 Monitor 后的第一时间，会先将共享内存中的数据复制到自己的缓存中；任何线程在释放 Monitor 的第一时间，会将缓存中的数据复制到共享内存中。
## java.util.concurrent.atomic 包
Atomic类是通过无锁（lock-free）的方式实现的线程安全（thread-safe）访问。它的主要原理是利用了CAS：Compare and Set：
```java
public int incrementAndGet(AtomicInteger var) {
    int prev, next;
    do {
        prev = var.get();
        next = prev + 1;
    } while ( ! var.compareAndSet(prev, next));
    return next;
}
```
CAS 是指，在这个操作中，如果 AtomicInteger 的当前值是 prev，那么就更新为 next，返回 true。如果 AtomicInteger 的当前值不是 prev，就什么也不干，返回 false。通过 CAS 操作并配合 do ... while 循环，即使其他线程修改了 AtomicInteger 的值，最终的结果也是正确的。
## 读写锁 ReentrantLock
```java
Lock lock = new ReentrantLock();

lock.lock();
try {
 x++;
} finally {
 lock.unlock();
}
```
```java
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    Lock readLock = lock.readLock();
    Lock writeLock = lock.writeLock();

    private int x = 0;
    
    private void count() {
        writeLock.lock();
        try {
            x++;
        }finally {
            writeLock.unlock();
        }
    }
    
    private void print(int time) {
        readLock.lock();
        try {
            for (int i = 0; i < time; i++) {
                System.out.println(x + " ");
            }
        }finally {
            readLock.unlock();
        }
    }
```
# 线程交互
1. 开启其他线程
2. 关闭其他线程
3. 等到其他线程
## interrupt 与 isInterrupt() 配合操作
线程收到关闭消息，自己停止任务执行；如果线程处于睡眠状态，则直接抛例外
```java
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 1_000_000; i++) {
                    if (isInterrupted()) {
                        return;
                    }
                    System.out.println("number: " + i );
                }
            }
        };
        
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
```
## wait notify notifyAll
由 Monitor 来控制线程的等待与唤醒状态，所以 wait notify 方法全都定义在 object 对象内
```java
    private String sharedString;

    private synchronized void initString() {
        sharedString = "test";
        notifyAll();
    }

    private synchronized void printString() {
        while (sharedString == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("String: " + sharedString);
    }

    private void test2() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initString();
            }
        });
        thread.start();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                printString();
            }
        });

        thread1.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("aa");
    }
```

join() 相当于一个简化版的 wait(), 并且会自动回到执行线程。上述代码中，主线程执行到 join() 方法后会处于等待状态，等到 thread 线程执行完后，主线程会继续往下执行，打印 “aa” 。这个方法相当于把并行执行的线程编程串行执行。同样的代码可以改写如下：
```java
    private String sharedString;

    private synchronized void initString() {
        sharedString = "test";
    }

    private synchronized void printString() {
        System.out.println("String: " + sharedString);
    }

    private void test2() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initString();
            }
        });
        thread.start();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                printString();
            }
        });
        thread1.start();
    }
```
yield() 方法会给同级别其他线程让出时间片，并不会等待，等到下次就会直接加入竞争资源队列中