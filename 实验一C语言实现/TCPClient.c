#include<stdio.h>
#include<winsock2.h>
#pragma comment (lib,"ws2_32")

//运行在Visual Studio2017
//VS默认使用新函数，所以要关闭函数检查
//关闭SDL检查：项目->属性->C/C++常规->SDL检查：否

int main(int argc, char *argv[])
{

	WSADATA wsaData;
	WSAStartup(MAKEWORD(2, 2), &wsaData);

	SOCKET s = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);

	SOCKADDR_IN sockaddr;
	sockaddr.sin_family = PF_INET;
	sockaddr.sin_addr.S_un.S_addr = inet_addr("127.0.0.1");
	sockaddr.sin_port = htons(6789);

	connect(s, (SOCKADDR*)&sockaddr, sizeof(SOCKADDR));

	//标准输入
	char str[100];
	printf("请输入:");
	scanf("%s", str);

	send(s, str, strlen(str) + sizeof(char), NULL);

	closesocket(s);
	getchar();
	WSACleanup();
	return 0;

}