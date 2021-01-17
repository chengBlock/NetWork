# 工程汇总指南

> 本次开发涉及C语言、Java、JavaSript、HTML和XML等语言，涉及安卓开发技术、Electron开发技术和Java和Node.js简单Socket服务器开发。
>
> 开发环境：
>
> - Windows10、Ubuntu Linux18.04、CentOS7.6
> - Visual Studio2017、IDEA2019.1.2、VSCODE、Android Studio
> - JDK1.8、Node12.16.3、npm6.14.4、electron8.2.3

## 文件概述

工程汇总文件夹含有以下五个内容：

- java核心通信代码

  > 可以正常运行的IDEA工程，父工程是服务端工程，子模块是客户端工程。
  >
  > java核心的socket通信代码，包含客户端和服务端可正常运行的java源码。

- 服务端

  > 含有两个工程，都是使用源码通过JDK和Node直接运行源码文件。
  >
  > - node server.js
  > - java MyServer.java
  >
  > **命令执行程序**
  >
  > “MyServer.java"是Java服务器的主入口，提供文件和消息的“群发”功能和安卓apk的更新
  >
  > “electronServer”文件夹包含“server.js"和”index.html“，”server.js"提供electron工程更新“index.html”的功能，“index.html”是提供给客户端更新的文件。

- 客户端

  > 含有PCElectron工程和安卓工程
  >
  > - Electron 使用npm i electron --dev-save安装依赖，再使用npm start运行工程
  > - 安卓工程里包含一个可以运行Android Studio工程，使用Android Studio可以打开。另外有一个apk可以安装使用。

- 实验一C语言实现

  > 含有“TCPClient"和“TCPServer”两个Visual Studio两个工程，打开即可运行
  >
  > ”TCPClient.c"和“TCPServer.c”是工程的源代码，“TCPClient.exe"和”TCPServer.exe"是生成的exe

- README

  > 是工程汇总的介绍