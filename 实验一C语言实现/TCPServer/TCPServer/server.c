#include<winsock2.h>
#include <stdio.h>
#include <stdlib.h>
#pragma comment (lib,"ws2_32")
int main(int argc, char *argv[])
{
	WSADATA wsaData;
	WSAStartup(MAKEWORD(2, 2), &wsaData);

	SOCKET s = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);

	SOCKADDR_IN sockaddr;
	sockaddr.sin_family = PF_INET;
	sockaddr.sin_addr.S_un.S_addr = inet_addr("127.0.0.1");
	sockaddr.sin_port = htons(6789);
	
	bind(s, (SOCKADDR*)&sockaddr, sizeof(SOCKADDR));
	listen(s, 1);
	SOCKADDR clientAddr;
	int nSize = sizeof(SOCKADDR);
	SOCKET clientSock;
	printf("Socket等待连接...\n");
	clientSock = accept(s, (SOCKADDR*)&clientAddr, &nSize);
	printf("Socket已连接!\n");

	char Buffer[MAXBYTE] = { 0 };
	recv(clientSock, Buffer, MAXBYTE, NULL);
	printf("接收到客户端数据 : %s \r\n", Buffer);

	system("pause");
	closesocket(clientSock);
	closesocket(s);

	WSACleanup();

	
	return 0;
}