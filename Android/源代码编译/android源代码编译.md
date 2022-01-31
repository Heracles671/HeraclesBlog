# 安装 JDK6
## 下载JDK6安装包
```
Oracle公司的官方下载网页链接：http://www.oracle.com/technetwork/java/javase/downloads/index.html
```

## 安装JDK6

1、为jdk-6u45-linux-x64.bin增加执行权限
```
sudo chmod u+x jdk-6u45-linux-x64.bin
```
2、执行jdk-6u45-linux-x64.bin，将JDK6的相关文件解包至jdk1.6.0_45目录下
```
./jdk-6u45-linux-x64.bin
```
3、将jdk1.6.0_45复制到/usr/lib下

```
sudo mkdir -p /usr/lib/java

sudo cp -r jdk1.6.0_45 /usr/lib/java/
```
4、安装JDK6
```
JDK6的bin文件中有许多可执行命令，根据需要，可以选择安装至/usr/bin目录下，比如，我安装了java/javac/javaws/jar四个命令。具体执行如下命令：

sudo update-alternatives --install /usr/bin/javac javac /usr/lib/java/jdk1.6.0_45/bin/javac 1

sudo update-alternatives --install /usr/bin/javah javah /usr/lib/java/jdk1.6.0_45/bin/javah 1

sudo update-alternatives --install /usr/bin/java java /usr/lib/java/jdk1.6.0_45/bin/java 1

sudo update-alternatives --install /usr/bin/javaws javaws /usr/lib/java/jdk1.6.0_45/bin/javaws 1

sudo update-alternatives --install /usr/bin/jar jar /usr/lib/java/jdk1.6.0_45/bin/jar 1

sudo update-alternatives --install /usr/bin/javadoc javadoc /usr/lib/java/jdk1.6.0_45/bin/javadoc 1

注意：如果需要安装其它命令，按照上面的格式，根据需要进行添加即可。
```
5、测试
```
执行java -version可以看到当前JDK的版本信息，表示安装成功。显示如下：

root@ubuntu:~/JDK# java -version

java version "1.6.0_33"

Java(TM) SE Runtime Environment (build 1.6.0_33-b04)

Java HotSpot(TM) Server VM (build 20.8-b03, mixed mode)
```

# 安装 ubuntu14 依赖包
```
sudo apt-get install git-core gnupg flex bison gperf build-essential zip curl zlib1g-dev gcc-multilib g++-multilib libc6-dev-i386 libncurses5 lib32ncurses5-dev x11proto-core-dev libx11-dev lib32z-dev libgl1-mesa-dev libxml2-utils xsltproc unzip libswitch-perl --fix-missing

```

# 安装 gcc4.4
## 安装
```
sudo apt-get install gcc-4.4 g++-4.4

sudo rm -rf /usr/bin/gcc

sudo rm -rf /usr/bin/g++

sudo ln -s /usr/bin/gcc-4.4 /usr/bin/gcc

sudo ln -s /usr/bin/g++-4.4 /usr/bin/g++

sudo apt-get install g++-4.4-multilib

```

# 编译安卓源代码 2.3
1). 脚本初始化环境： source build/envsetup.sh

2). 删除上一次编译的结果，初次编译可以不需要这一步： make clobber

3). 选择编译目标：lunch

4). 开始编译：make -j4

5).最终会在源码跟目录out/target/product/angler目录下生成镜像文件：
* system.img：系统镜像
* ramdisk.img：根文件系统镜像
* userdata.img：用户数据镜像
* recovery.img:recovery镜像
* boot.img:启动镜像
* vendor.img:驱动镜像

# 相关错误
1. 修改 dalvik/vm/native/dalvik_system_Zygote.c
```
在 dalvik_system_Zygote.c 中第28行添加：#include <sys/resource.h>
```

2. 移动文件 /usr/include/x86_64-linux-gnu/zconf.h
```
 sudo cp /usr/include/x86_64-linux-gnu/zconf.h /usr/include/
```

# 打包 SDK
```
make sdk
```

# 运行 Android 模拟器
```
export PATH=$PATH:~/Android/out/host/linux-x86/bin
export ANDROID_PRODUCT_OUT=~/Android/out/target/product/generic

sudo apt-add-repository "deb http://archive.canonical.com/ $(lsb_release -sc) partner"
sudo apt-get update
sudo apt-get install libsdl1.2debian:i386

emulator
```

# 编译 Android 内核源代码
```
cd ~/Android/kernel/goldfish
export PATH=$PATH:~/Android/prebuild/linux-86/toolchain/arm-eabi-4.4.3/bin
make goldfish_defconfig
make

cd ~/Android
emulator -kernel ./kernel/goldfish/arch/arm/boot/zImage &

adb shell
cd proc
cat version
```

# 单独编译 Android 应用程序模块
```
mmm ./packages/experimental/HelloAndroid/
make snod
```