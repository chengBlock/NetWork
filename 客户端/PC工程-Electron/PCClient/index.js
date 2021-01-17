const net = require("net");
const path = require("path");
const fs = require("fs");
const {
  ipcRenderer
} = require("electron");

//创建socket客户端
const client = new net.Socket();

//更新路径
let updatePath = path.join("./tmp/index.html");
let currentPath = path.join("./index.html");

const inputIp = document.getElementById("input_ip");
const inputPort = document.getElementById("input_port");
const buttonConnect = document.getElementById("button_connect");

const divRecvMsg = document.getElementById("div_recv_msg");
const inputSendMsg = document.getElementById("input_send_msg");
const buttonSend = document.getElementById("button_send");

const buttonSendFile = document.getElementById("button_send_file");
const buttonRecvFile = document.getElementById("button_recv_file");

const divUpdate = document.getElementById("div_update");
const buttonUpdate = document.getElementById("button_update");
const divVersion = document.getElementById("div_version");

buttonConnect.addEventListener("click", () => {
  let ipIn = inputIp.value;
  let portIn = inputPort.value;

  client.connect(portIn, ipIn, () => {
    client.write("Hello, I am Windows!\r\n");
  });
  buttonConnect.innerHTML = "已连接";
  console.log("ip:" + ipIn);
  console.log("port:" + portIn);
});

buttonSend.addEventListener("click", () => {
  let content = inputSendMsg.value;
  client.write(content + "\r\n");
  console.log(content);
});

buttonUpdate.addEventListener("click", () => {
  const newHtml = fs.readFileSync("./tmp/index.html");

  fs.writeFileSync("index.html", newHtml.toString());
  ipcRenderer.send("update", "update");
});

client.on("data", function (data) {
  console.log("from server:" + data);
  console.log(data.length);
  //得到服务端返回来的数据
  if (data.length > 1) {
    divRecvMsg.innerHTML = data;
  }
});
client.on("error", function (error) {
  //错误出现之后关闭连接
  console.log("error:" + error);
  client.destory();
});
client.on("close", function () {
  //正常关闭连接
  console.log("Connection closed");
});

//开机下载
downloadUpdateFile();

//返回文件修改时间
function getMTimeStamp(path) {
  let stat;
  try {
    stat = fs.statSync(path);
    return stat.mtime.getTime();
  } catch (err) {
    console.log(err);
    return null;
  }
}

//返回文件大小
function getSizeFile(path) {
  let stat;
  try {
    stat = fs.statSync(path);
    return stat.size;
  } catch (error) {
    console.log("getSize Error");
  }
}

//返回是否可更新
function isUpdate() {
  // const updateTimeStamp = getMTimeStamp(updatePath)
  // const currentTimeStamp = getMTimeStamp(currentPath)

  updateTimeStamp = getSizeFile(updatePath);
  currentTimeStamp = getSizeFile(currentPath);

  console.log("updateSize : " + updateTimeStamp);
  console.log("currentSize : " + currentTimeStamp);

  if (updateTimeStamp === undefined) {
    divUpdate.innerHTML = "不可更新";
    return false;
  } else if (updateTimeStamp !== currentTimeStamp) {
    divUpdate.innerHTML = "可更新";
    return true;
  } else {
    divUpdate.innerHTML = "不可更新";
    return false;
  }
}

function downloadUpdateFile() {
  try {
    let updateSocket = new net.Socket();
    let ip = "47.95.39.148";
    let port = 6901;

    updateSocket.connect(port, ip, () => {
      console.log("已连接");
    });

    let data;

    updateSocket.on("data", function (data) {
      console.log("from server:" + data);
      console.log(data.length);
      //得到服务端返回来的数据
      if (data.length > 1) {
        fs.writeFileSync("./tmp/index.html", data);
        updateSocket.end();
      }
    });
    updateSocket.on("error", function (error) {
      //错误出现之后关闭连接
      console.log("error:" + error);
      updateSocket.destory();
    });
    updateSocket.on("close", function () {
      //正常关闭连接
      console.log("Connection closed");
      isUpdate();
    });
  } catch (error) {
    console.log("下载更新失败");
  }
}

//--------------------------------------
buttonRecvFile.addEventListener("click", () => {
  let socket = new net.Socket();
  let ip = "47.95.39.148";
  let port = 6788;
  socket.connect(port, ip, () => {
    console.log("接收文件连接成功");
  });

  let i = 1;
  let fileName;
  let fileLen = 0;
  let recvFile;

  socket.on("data", (data) => {
    console.log(data);
    if (i === 1) {
      fileName = data.slice(2).toString();
      console.log("文件名:" + fileName);
    } else {
      fs.writeFileSync("File-Recive/" + fileName, data.slice(8));
      console.log(data.slice(8));
    }
    i++;
  });
});

buttonSendFile.addEventListener("click", () => {
  console.log("clicksend");
  let socket = new net.Socket();
  let ip = "47.95.39.148";
  let port = 6788;
  socket.connect(port, ip, () => {
    console.log("发送文件连接成功");
  });

  let bufName = Buffer.from([0, 5, 97, 46, 116, 120, 116]);
  let bufFile = fs.readFileSync("./File-Send/bb.txt");

  let bufLen = Buffer.from([0, 0, 0, 0, 0, 0, 0, bufFile.length]);

  socket.write(bufName);
  socket.write(bufLen);
  socket.write(bufFile);
  socket.end()

  socket.on("error", (e) => {
    console.log(e);
  });
});