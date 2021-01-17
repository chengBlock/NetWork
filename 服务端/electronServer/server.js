const net = require('net');
const fs = require('fs')
const path = require('path')

//模块引入
var listenPort = 6901; //监听端口
var server = net.createServer((socket) => {
    // 创建socket服务端
    console.log('connect: ' +
        socket.remoteAddress + ':' + socket.remotePort);
    // socket.setEncoding('binary');
    const data = fs.readFileSync('./index.html')
    console.log(data.toString())

    //发送html
    socket.write(data)

    socket.on('error', function (exception) {
        console.log('socket error:' + exception);
        socket.end();
    });
    //客户端关闭事件
    socket.on('close', function (data) {
        console.log('client closed!');
        // socket.remoteAddress + ' ' + socket.remotePort);
    });
}).listen(listenPort);
//服务器监听事件
server.on('listening', function () {
    console.log("server listening:" + server.address().port);
});
//服务器错误事件
server.on("error", function (exception) {
    console.log("server error:" + exception);
});