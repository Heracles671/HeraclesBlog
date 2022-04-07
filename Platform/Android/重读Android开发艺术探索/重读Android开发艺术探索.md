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
## 基本工具
1. MotionEvent：获取滑动事件
2. TouchSlop：获取滑动最小距离
3. VelocityTracker：获取滑动速度
4. GestureDetector：监听手势
5. Scroller：实现弹性滑动
## View 的滑动
滑动的方式：
1. View 本身提供的 scrollTo、scrollBy 方法：改变的是 View 内容的位置，而不是 View 在布局中的位置
2. 通过动画给 View 施加平移效果：操作 View 的 translationX 和 translationY 属性。View 动画只是对 View 的影像做操作，并不真正改变 View 的位置参数，而属性动画并不会这样
3. 改变 View 的 LayoutParams 使得 View 重新布局：通过 MarginLayout 改变 margin 值也可以实现
## 弹性滑动
核心思想：将一次大的滑动分成若干次小的滑动并在一个时间段内完成。
1. Scroller：配合 View 的 computeScroll 方法实现动画；也可以使用属性动画来实现；这两个方法底层都是调用 View 的 ScrollTo 方法来实现滑动
2. handler#postDelay
3. Thread#sleep
## View 的事件分发机制
1. dispatchTouchEvent(MotionEvent ev)
2. onInterceptTouchEvent(MotionEvent ev)
3. onTouchEvent(MotionEvent ev)
```java
public boolean dispatchTouchEvent(MotionEvent ev) {
  boolean consume = false;
  if (onInterceptTouchEvent(ev)) {
    comsume = onTouchEvent(ev);
  } else {
    comsume = child.dispatchTouchEvent(ev);
  }
  return comsume;
}
```
当一个 View 需要处理事件时，如果它设置了 OnTouchListener，那么 OnTouchListener 的 onTouch 方法会被调用。如果 onTouch 返回 false，则当前 View 的 onTouchEvent 方法会被调用，如果返回 true，则不会被调用。在 onTouchEvent 方法中，如果当前 View 设置有 OnClickListener，那么它的 onClick 方法会被调用。
## 滑动冲突
1. 外部滑动方向和内部滑动方向不一致：判断水平或竖直方向上哪个值大来判断滑动方向
2. 外部滑动方向和内部滑动方向一致：根据业务需求判断由内部还是外部滑动
3. 上面两种情况嵌套的情况：根据业务需求判断由内部还是外部滑动

解决方法：
1. 内部拦截法：重写父容器的 onInterceptTouchEvent 方法
2. 外部拦截法：重写子元素的 dispatchTouchEvent 方法，配合 requestDisallowInterceptTouchEvent 方法在子元素中实现拦截；重写父容器的 onInterceptTouchEvent 方法，拦截除 ACTION_DOWN 以外的事件
## MeasureSpec
在测量过程中，系统会将 View 的 LayoutParams 根据父容器所施加的规则转换成对应的 MeasureSpec，然后再根据这个 MeasureSpec 来测量出 View 的宽高，这里的宽高是测量值，并不一定等于最终值。
`MeasureSpec = SpecMode + SpecSize`
## View 的工作流程
view 的工作流程主要指 measure、layout、draw三个流程，及测量、布局和绘制；其中测量确定 View 的测量宽高，layout 确定 View 的最终宽高和四个顶点的位置，而 draw 则将 View 绘制到屏幕上。
### measure
1. view：直接继承 View 的自定义控件需要重写 onMeasure 方法并设置 wrap_content 时的自身大小，否则在布局中使用 wrap_content 就相当于使用 match_parent 时的自身大小。在代码中，只需要给 View 制定一个默认的内部宽高，并在 wrap_content 时设置次宽高即可。
2. viewGroup：在具体实现类里进行 onMeasure 调用，并且负责测量子 View

在 Activity 中获取 View 的宽高：
1. Activity/View#onWindowFocusChanged
2. view.post(rnnnable)
3. ViewTreeObserver
4. view.measure(int widthMeasureSpec, int heightMeasureSpec)
### layout
首先会通过 setFrame 方法来设定View的四个顶点的位置，即初始化 mLeft, mRight, mTop, mBottom 四个值。除非你自己手动修改layout方法的参数，在几乎所有情况下，测量宽高和最终宽高都是相等的。
### draw
draw 的过程就是将 View 绘制到屏幕上，步骤如下：
1. 绘制背景 background.draw(canvas);
2. 绘制自己（onDraw）
3. 绘制 children（dispatchDraw）
4. 绘制装饰（onDrawScrollBars）
## 自定义 View
几种自定义方法分类：
1. 继承 View 重写 onDraw 方法
2. 继承 ViewGroup 派生特殊的 Layout 方法
3. 继承特定的 View（比如 TextView）
4. 继承特定的 ViewGroup（比如 LinearLayout）

注意事项：
1. 让 View 支持 wrap_content
2. 如果有必要，让 View 支持 padding
3. 尽量不要在 View 中使用 handler，没必要
4. View 中如果有线程或者动画，需要及时停止，参考 View#onDetachedFromWindow
5. View 带有滑动嵌套时，需要处理好滑动冲突

自定义属性：
1. 在 values 目录下常见自定义属性的 XML，比如 attrs.xml
2. 在 View 的构造方法中解析自定义属性的值并作相应处理
3. 在布局文件中使用自定义属性
# window 和 WindowManager
WindowManager 是外界访问 window 的入口，window 的具体实现位于 WindowManagerService中，WindowManager 和 WindowManagerService 的交互是一次 IPC 过程。WindowManager 所提供的功能很简单：
1. addView
2. updateViewLayout
3. removeView
## window 的内部机制
每一个 window 都对应着一个 view 和一个 viewRootImpl，window 和 view 通过 viewRootImpl 来建立联系，因此 window 并不是实际存在的，它是以 view 的形式存在。
### 添加 window
添加 window：通过 windowManager 的 addView 来实现，windowManager 是一个接口，它的真正实现是 windowManagerImpl。windowManagerImpl 通过桥接模式把任务传递给 wimdowManagerGlobal 来实现。
1. 检查参数是否合法，如果是子 window 那么还需要调整一次布局参数
2. 创建 viewRootImpl 并将 view 添加在列表中
3. 通过 viewRootImpl 来更新界面，并通过 ipc 拿到 windowManagerService，从而完成 window 的添加
### 删除 window
和添加过程类似，都是先通过 winddowManagerImpl 后，再进一步通过 wimdowManagerGlobal 来实现的
### 更新 window
和添加过程类似，都是先通过 winddowManagerImpl 后，再进一步通过 wimdowManagerGlobal 来实现的。在此过程中，不仅会重新对 View 进行重绘，还会进行一次 IPC 更新 window
## window 的创建过程
### activity 的 window 创建过程
在 setContentView 方法中，通过 window 设置视图内容，window 的具体实现是 phoneWindow：
1. 如果没有 decorView，就创建它
2. 将 view 添加到 decorView 的 mContentParent 中
3. 回调 activity 的 onContentChanged 方法通知 activity 视图已经发生改变
### dialog 的 window 创建过程
1. 创建 window
2. 初始化 decorView，并将 dialog 的视图添加到 decorView 中
3. 将 decorView 添加到 window 中并显示
### toast 的 window 创建过程
toast 和 dialog 不同，由于 toast 具有定时取消功能，所以系统采用了 handler。在 toast 内部有两类 IPC 过程：
1. toast 访问 notificationManagerService
2. notificationManagerService 回调 toast 里的 TN 接口
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