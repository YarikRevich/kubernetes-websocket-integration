import SockJS from "sockjs-client"
import Stomp from "stomp-websocket"

// window.addEventListener("DOMContentLoaded", () => {

// })

document.addEventListener('DOMContentLoaded', function () {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        console.log("Connected!");
        stompClient.subscribe('/topic/pod', function (menuItem) {
            alert(menuItem);
        });
    });

    setTimeout(() => {
        stompClient.send("/app/pod", {}, "");
    }, 5000);
}, false);  