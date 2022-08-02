import SockJS from "sockjs-client"
import Stomp from "stomp-websocket"

// window.addEventListener("DOMContentLoaded", () => {

// })

document.addEventListener('DOMContentLoaded', function () {
    console.log("initializing sockJs");
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    console.log("sockJs is initialized");
    console.log(socket, stompClient);
}, false);