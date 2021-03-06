#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"

int main(int argc, char *argv[])
{

  // 函数for调用
  // int pid = fork();

  // if (pid > 0)
  // {
  //   printf("parent: child=%d\n", pid);
  //   pid = wait((int *)0);
  //   printf("child %d is done\n", pid);
  // }
  // else if (pid == 0)
  // {
  //   printf("child: exiting\n");
  //   exit(0);
  // }
  // else
  // {
  //   printf("fork error\n");
  // }

  // exec函数调用示例
  // char *args[3];
  // args[0] = "echo";
  // args[1] = "hello";
  // args[2] = 0;
  // int a = exec("/echo", args);
  // printf("exec error: %d\n", a);

  // 读写函数示例
  // char buf[512];
  // int n;

  // for (;;)
  // {
  //   n = read(0, buf, sizeof buf);
  //   if (n == 0)
  //   {
  //     break;
  //   }
  //   if (n < 0)
  //   {
  //     fprintf(2, "read error\n");
  //     exit(1);
  //   }
  //   if (write(1, buf, n) != n)
  //   {
  //     fprintf(2, "write error\n");
  //     exit(1);
  //   }
  // }

  if (argc != 2)
  {
    printf("error: usage -> sleep 100 \n");
    exit(1);
  }

  int duration = atoi(argv[1]);
  sleep(duration);
  exit(0);
}
