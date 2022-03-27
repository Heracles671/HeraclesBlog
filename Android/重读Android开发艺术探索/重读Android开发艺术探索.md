# Android 生命周期和启动模式
## 生命周期
### 正常情况
流程：
```
onCreate -> onStart -> onResume -> onPause -> onStop -> onDestroy
    |          |         |<-onNewIntent-|        |           |
    |          |<-----------onRestart------------|           |
    |<-------------------------------------------------------|
```
页面跳转：
```
MainActivity -> onPause
SecindActivity -> onCreate
SecindActivity -> onStart
SecindActivity -> onResume
MainActivity -> onStop
```
### 意外情况
旋转屏幕重建：
```
     Activity                            Activity
        |                                    | 
     意外情况                                 ｜
        |                                    |
onSaveInstanceState     --重新创建-->      onCreate
        |                                    |
     onDestroy                       onRestoreInstanceState

onSaveInstanceState调用时机在onStop之前，onRestoreInstanceState调用时机在onStart之后

MainActivity -> onPause
MainActivity -> onSaveInstanceState
MainActivity -> onStop
MainActivity -> onDestroy
MainActivity -> onCreate
MainActivity -> onRestoreInstanceState
```
内存不足销毁：流程和上一种情况一致
## 启动模式
1. standard：每次调用都会实例化，生命周期走正常流程
2. singleTop：位于栈顶就复用，否则重新创建
3. singleTask：位于栈内就复用，同时清除之上的活动，否则就新建任务栈并创建活动
4. singleInstance：加强版的singleTask模式，并且只能单独位于一个任务栈中
## Activity的标识位
1. 设定activity启动模式
```
FLAG_ACTIVITY_NEW_TASK：类似于singleTask模式
FLAG_ACTIVITY_SINGLE_TOP：等同于singleTop模式
```
2. 影响activity运行状态
```
FLAG_ACTIVITY_CLAER_TOP：
清除之上活动，和FLAG_ACTIVITY_NEW_TASK配合等同于singleTask模式
如果被启动活动是standard模式，则会连同自己一起被清除后重新创建并置于栈顶

FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS：
在任务历史列表中去除，类似于android:excludeFromRecents="true"属性
```
## IntentFilter 匹配
1. 隐式匹配指标：action、category、data
2. 必须满足所有指标才算匹配成功
3. 一个activity可以有多个intent-filter，只要匹配上一个就可以启动
4. 可以用intent的resolveActivity函数事先检查是否存在对应界面，第二个参数传递MATCH_DEFAULT_ONLY类型
### action
action是一个字符串，要求intent中的action存在且必须和过滤规则中的其中一个action相同；另外，action区分大小写
### category
category是一个字符串，可以没有（系统会自动为intent添加Default类型的），如果有则必须是intent-filter中category规则集中的定义的。所以为了activity能够被隐式调用，intent-filter中必须添加default类型的category

ps：由此可见，launcher启动应用采用的是隐式启动方案
### data
如果匹配规则中出现了，则要求intent中一定要定义，它由两部分构成：URI + mimeType
#### mimeType
指代媒体类型：image/jpeg、audio/mpeg4-generic、video/*、text/plain。此属性在intent中必须配合uri才能使用
#### URI
```
<scheme>://<host>:<port>/[<path>|<pathPrefix>|<pathPattern>]
```
类似category，URI有默认值：content和file
# Android IPC
## 开启多进程
1. android:process属性：只能用来控制四大组件，无法指定线程或类实例的运行进程；`:`方式指定的是私有进程
2. JNI方式fork一个进程：不常用
### **带来的问题**
每一个进程对应一个虚拟机，每个虚拟机之间相互独立：
1. 静态成员和单例模式完全失效
2. 线程同步机制完全失效
3. sharedPreference机制可靠性降低
4. Application会创建多次

解决办法：多进程间通信IPC
## IPC 
### serializable
1. 写上serialVersionUID有助于防止反序列化失败
2. 静态变量不参与序列化
3. transient修饰的变量不参与序列化
### parcelable
1. 在反序列化时，如果对象是其他可序列化对象，需要提供当前线程的列加载器上下文
2. parcelable主要用在内存序列化上，serializable主要用在io序列化上
### Binder
Book.java
```java
package com.heracles.androidstudiouserguide.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private String name;

    public Book(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "book name：" + name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public void readFromParcel(Parcel dest) {
        name = dest.readString();
    }

    protected Book(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}

```
Book.aidl
```
// Book.aidl
package com.heracles.androidstudiouserguide.aidl;

parcelable Book;
```
IManagerBook.aidl
```
// IBookManager.aidl
package com.heracles.androidstudiouserguide.aidl;
import com.heracles.androidstudiouserguide.aidl.Book;

interface IBookManager {
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    List<Book> getBookList();
    void addBookInOut(inout Book book);
}
```
IManagerBook.java
```java
/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.heracles.androidstudiouserguide.aidl;
// Declare any non-default types here with import statements

public interface IBookManager extends android.os.IInterface
{
  /** Default implementation for IBookManager. */
  public static class Default implements com.heracles.androidstudiouserguide.aidl.IBookManager
  {
    /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         */
    @Override public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, java.lang.String aString) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<com.heracles.androidstudiouserguide.aidl.Book> getBookList() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void addBookInOut(com.heracles.androidstudiouserguide.aidl.Book book) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.heracles.androidstudiouserguide.aidl.IBookManager
  {
    private static final java.lang.String DESCRIPTOR = "com.heracles.androidstudiouserguide.aidl.IBookManager";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.heracles.androidstudiouserguide.aidl.IBookManager interface,
     * generating a proxy if needed.
     */
    public static com.heracles.androidstudiouserguide.aidl.IBookManager asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.heracles.androidstudiouserguide.aidl.IBookManager))) {
        return ((com.heracles.androidstudiouserguide.aidl.IBookManager)iin);
      }
      return new com.heracles.androidstudiouserguide.aidl.IBookManager.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_basicTypes:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          long _arg1;
          _arg1 = data.readLong();
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          float _arg3;
          _arg3 = data.readFloat();
          double _arg4;
          _arg4 = data.readDouble();
          java.lang.String _arg5;
          _arg5 = data.readString();
          this.basicTypes(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getBookList:
        {
          data.enforceInterface(descriptor);
          java.util.List<com.heracles.androidstudiouserguide.aidl.Book> _result = this.getBookList();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_addBookInOut:
        {
          data.enforceInterface(descriptor);
          com.heracles.androidstudiouserguide.aidl.Book _arg0;
          if ((0!=data.readInt())) {
            _arg0 = com.heracles.androidstudiouserguide.aidl.Book.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addBookInOut(_arg0);
          reply.writeNoException();
          if ((_arg0!=null)) {
            reply.writeInt(1);
            _arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.heracles.androidstudiouserguide.aidl.IBookManager
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      /**
           * Demonstrates some basic types that you can use as parameters
           * and return values in AIDL.
           */
      @Override public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, java.lang.String aString) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(anInt);
          _data.writeLong(aLong);
          _data.writeInt(((aBoolean)?(1):(0)));
          _data.writeFloat(aFloat);
          _data.writeDouble(aDouble);
          _data.writeString(aString);
          boolean _status = mRemote.transact(Stub.TRANSACTION_basicTypes, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().basicTypes(anInt, aLong, aBoolean, aFloat, aDouble, aString);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<com.heracles.androidstudiouserguide.aidl.Book> getBookList() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<com.heracles.androidstudiouserguide.aidl.Book> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getBookList();
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(com.heracles.androidstudiouserguide.aidl.Book.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void addBookInOut(com.heracles.androidstudiouserguide.aidl.Book book) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((book!=null)) {
            _data.writeInt(1);
            book.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addBookInOut, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addBookInOut(book);
            return;
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            book.readFromParcel(_reply);
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.heracles.androidstudiouserguide.aidl.IBookManager sDefaultImpl;
    }
    static final int TRANSACTION_basicTypes = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_addBookInOut = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    public static boolean setDefaultImpl(com.heracles.androidstudiouserguide.aidl.IBookManager impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.heracles.androidstudiouserguide.aidl.IBookManager getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  /**
       * Demonstrates some basic types that you can use as parameters
       * and return values in AIDL.
       */
  public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, java.lang.String aString) throws android.os.RemoteException;
  public java.util.List<com.heracles.androidstudiouserguide.aidl.Book> getBookList() throws android.os.RemoteException;
  public void addBookInOut(com.heracles.androidstudiouserguide.aidl.Book book) throws android.os.RemoteException;
}

```
```
   |<-yield-|      |<-----------------------------------------------------------------------------|
client--远程请求-->binder--写入参数-->data--transact-->service--onTransact-->线程池--写入结果-->reply--|
   |<----返回数据----|
```
## android ipc 中的几种实现
### bundle
在intent中传递bundle，因为bundle实现了parcelable接口
### 共享文件
注意并法问题，不建议把sharedPreference作为共享文件载体，因为它存在缓存，数据可能不准确
### messager
底层还是基于aidl来实现的，并且是串行处理消息的：首先，客户端拿到服务端的 messager，然后发送消息，replyTo 字段中带上客户端的 messager；服务端拿到客户端的 messager 后，通过它向客户端发送消息，客户端就可以收到了
### aidl
支持的数据类型：
1. 基本数据类型: int, long, char, boolean, double
2. String, CharSequence
3. List: 只支持 ArrayList
4. Map: 只支持 HashMap
5. Parcelable: 所有实现了 Parcelable 接口的对象
6. AIDL: 所有的 AIDL 接口本身也可以在 AIDL 文件中使用
>其中，自定义的 Parcelable 对象和 AIDL 接口必须显示 import 进来

RemoteCallbackList 是专门用于删除跨进程 listener 的接口

Binder 可能会死亡，用 DeathRecipient 监听

在 AIDL 中使用权限验证：判读 permission，通过 onBinder 或 onTransact 方法控制
### contentProvider
底层使用 binder 实现，系统做了封装，没有 aidl 那么复杂，无需了解底层细节就可以实现 IPC
### socket
客户端使用：Socket 类；服务端使用：ServerSocket 类

# 四大组件的工作过程
## Activity
整个启动过程涉及 binder IPC 、H Handler 和类加载器
## Service
整个启动过程涉及 binder IPC 、H Handler 和类加载器

# View 的世界

# Android 消息机制
 Android 的消息机制主要是指 Handler 的运行机制，Handler 的运行需要底层 MessageQueue 和 Looper 的支撑。整个 Handler 干的活其实就是线程切换。

 MessageQueue 是一个单链表结构，用来存储消息列表。Looper 是一个消息循环，以无限循环的形式来检查 MQ 是否有新消息。Hanlder 使用当前线程的 Lopper 来创建消息循环系统。

 Handler 通过 ThreadLocal 获取当前线程的 Lopper；线程默认没有 Lopper，所以需要使用 Handler 就必须为线程创建 Lopper。

 ## Handler 的使用
 发送消息：
 1. sendMessage(Message)
 2. post(Runnable)

 处理消息：
 1. 派生子类实现 handleMessage 方法
 2. Handler 构造方法添加 Calback 参数
 # Android 的线程和线程池
## Android 中的线程
1. AsyncTask：子线程中访问 UI，封装了 线程池（AT 有两个线程池） 和 Handler
2. HandlerThread：带有 Handler 的线程
3. IntentService：优先级很高的后台线程
## Android 中的线程池
ThreadPoolExecutor：
1. FixedThreadPool：只有固定核心线程，任务队列没有大小限制，无超机制时，适合执行快速响应界面的请求
2. CachedThreadPool：只有任意多非核心线程，60s超时机制，适合执行大量的耗时较少的任务
3. ScheduledThreadPool：固定核心线程，任意多非核心线程，0s超时机制，适合执行固定周期的重复任务
4. SingleThreadPool：一个核心线程，确保所有任务在一个线程中执行，避免线程同步问题