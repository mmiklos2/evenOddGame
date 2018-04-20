var usernamePage = document.getElementById('username-page');
var chatPage = document.getElementById('chat-page');
var gameArea = document.getElementById('gameArea');
var usernameForm = document.getElementById('usernameForm');
var messageForm = document.getElementById('messageForm');
var messageInput = document.getElementById('message');
var messageArea = document.getElementById('messageArea');
var connectingElement = document.getElementsByClassName('connecting')[0];
var stompClient = null;
var username = null;
var stompRoomObject = null;

function connect(event) {
    username = document.getElementById('name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');


        var socket = new SockJS('/evenOddSocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    stompClient.subscribe('/user/queue/messages', onMessageReceived);
    stompClient.subscribe('/topic/active', onMessageReceived);

    stompClient.send("/app/connectPlayer",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to the server!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function sendEvenOrOdd(event) {
    var messageContent = $($(this).find("img")).attr('alt').trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'GAME'
        };
        stompClient.send("/app/sendGameData", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    blockInput();
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');
    var messageParts;
    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined the room';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left the room';
    } else if (message.type === 'START') {
        alert('Game starting');
        enableInput();
        messageParts = message.content.split('|');
        $("#player1").text(messageParts[0]);
        $("#player2").text(messageParts[1]);
    } else if (message.type === 'GAME') {
        messageParts = message.content.split("|");
        stompRoomObject = stompClient.subscribe('/room/' + messageParts[0]);
        if (messageParts[0] === 'inactive')
            connectingElement.classList.remove('hidden');
        else {
            connectingElement.classList.add('hidden');
        }
        $('#player1').text(messageParts[1]);
        $('#player2').text(messageParts[2]);
    } else if (message.type === 'SCORE') {
        messageParts = message.content.split('|');
        $('#score').text(messageParts[0]);
        $('#player1').text(messageParts[1]);
        $('#player2').text(messageParts[2]);
        enableInput();
    } else if (message.type === 'QUIT') {
        chatPage.classList.remove('hidden');
        gameArea.classList.add('hidden');
        var navBar = $("nav");
        navBar.css('display', 'none');
        connectingElement.classList.add('hidden');
        messageParts = message.content.split('|');
        alert('Game has ended with a final score ' + messageParts[1] + ' ' + messageParts[0] + ' ' + messageParts[2]);
    } else {
        debugger;
        messageElement.classList.add('chat-message');
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);


        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
    }
}

function requestGame(event) {
    chatPage.classList.add('hidden');
    gameArea.classList.remove('hidden');
    var navBar = $("nav");
    navBar.css('display', 'block');
    connectingElement.classList.remove('hidden');
    var messageContent = "Play";
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'GAME'
        };
        stompClient.send("/app/requestGame", {"reply-to":"/"+username}, JSON.stringify(chatMessage))   ;
        messageInput.value = '';
    }
    event.preventDefault();
}

function stopPlaying(event) {
    chatPage.classList.remove('hidden');
    gameArea.classList.add('hidden');
    var navBar = $("nav");
    navBar.css('display', 'none');
    connectingElement.classList.remove('hidden');
    var messageContent = "Quit";
    if (messageContent && stompClient) {
        debugger;
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'QUIT'
        };
        stompClient.send("/app/sendGameData", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    if (stompRoomObject !== null)
        stompRoomObject.unsubscribe();
    event.preventDefault();
}

function blockInput() {
    $(".list-inline-item").off();
}

function enableInput() {
    $(".list-inline-item").on('click', sendEvenOrOdd);
}

$(usernameForm).submit(connect);
$(messageForm).submit(sendMessage);
$('#playButton').on('click', requestGame);
$('#stopPlaying').on('click', stopPlaying);