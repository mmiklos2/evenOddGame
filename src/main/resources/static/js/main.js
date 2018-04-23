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
var key_258_array;
var iv_258_array;

var publicKeyHARD = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdMsTjBqojBfC+2YvxHr4go4yEqfqKOVdqIkLNsGtkx7lpQYUls0aOOFtDajC/AenAAZPXskfIHpdPfgS6N9B+agwAIuVnGoYXwR+t3D5A81VHWEaG9bafObhtxowY0xkExVH+kNERvELn/TBbjK00PubZv2l79i4JKP2n3qk+gwIDAQAB";
var privateKeyHARD = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJ0yxOMGqiMF8L7Zi/EeviCjjISp+oo5V2oiQs2wa2THuWlBhSWzRo44W0NqML8B6cABk9eyR8gel09+BLo30H5qDAAi5WcahhfBH63cPkDzVUdYRob1tp85uG3GjBjTGQTFUf6Q0RG8Quf9MFuMrTQ+5tm/aXv2Lgko/afeqT6DAgMBAAECgYEAkkK3gAgmavnO+rPNCiaxfgrN5m2FB/C8Tarc/yZjddyCvdVRfjdksVTsTThTbJTqGVUearWdJiyYWkUFPwJJgK/y6Yo4zORb4GcALgHNQNq1CtMveofIUbO3hdnwed7l+h2FCr5oZePIIfkcr+bAotForaR9CNCQKG7TfbwINIkCQQDLUNfnZoHRCDSSKWTuFhcSt3ml0gBfFSgJm9beXdjerejjxArEmOVPzznHs3XfyaGJ1pH6Q/aMwlarrlJt4Jr/AkEAxe6tO+9hXgbQ7gMH6UcBei/GvD+g1++cXAzY/7kvYv1hyPKUlAunSUhXqgAqXbxvIF/Wh06REnpmUuyUMdJwfQJBAK7CUpKoXbVbJDTuN78eZb7Ezl9l3sPb34pC1bJVYHAndCMzAz5xXcn4CWZKL2uoWJ2uPdOsT2Q2N2FieZ/Qi+kCQQCBxGyxKc1lTwS1tyj9qT7vs9kQ3IqAaCjUNNwkNG478TGmBFyi/0zq/9Hxs/ASypL038CPr1PvkG37mf3C+FwlAkARNhhzUryoI3BBPg5WhS/UK63m5hf+umC6BC64YQQrg+VQ+ssdRho4D1757px90Pk9NdlDDI7V88al6vn5PgkQ";
var serverPublicKey;


// function used to encrypt a message using am AES-256 key using the pkcs7 padding
// the array parameters are used to initialize the encryption object, one is the actual, random key and the other the initialization vector
function encryptUsingAES(message) {
    var textBytes = aesjs.utils.utf8.toBytes(message);
    var aesCtr = new aesjs.ModeOfOperation.cbc(key_258_array, iv_258_array);
    var encryptedBytes = aesCtr.encrypt(aesjs.padding.pkcs7.pad(textBytes));
    return btoa(String.fromCharCode.apply(null, encryptedBytes));
}

// function used to encrypt the AES-256 key with RSA using a server supplied public key
// returns encrypted AES key
function encryptAESKey(pKey) {

    var encrypt = new JSEncrypt();
    pKey = "-----BEGIN PUBLIC KEY-----\n" + pKey + "\n-----END PUBLIC KEY-----";
    encrypt.setPublicKey(pKey);
    var stringKey1 = btoa(aesjs.utils.hex.fromBytes(key_258_array));
    return encrypt.encrypt(stringKey1);
}

function decryptUsingRSA(text) {
    var decrypt = new JSEncrypt();
    decrypt.setPrivateKey(privateKeyHARD);
    return decrypt.decrypt(text);
}

function encryptUsingRSA(pKey, text) {
    var key_256 = new Array(32);
    var iv_256 = new Array(16);
    for (var i = 0; i < key_256.length; i++) {
        key_256[i] = randomIntInc(1, 100);
        if (i % 2 === 0)
            iv_256[i / 2] = key_256[i];
    }
    key_258_array = new Uint8Array(key_256);
    iv_258_array = new Uint8Array(iv_256);
    var encrypt = new JSEncrypt();
    pKey = "-----BEGIN PUBLIC KEY-----\n" + pKey + "\n-----END PUBLIC KEY-----";
    encrypt.setPublicKey(pKey);
    return encrypt.encrypt(btoa((text)));
}

function prepareConnectPayload(pKey) {
    var encryptedPassword;

    // generating the AES key and initialization vector
    var key_256 = new Array(32);
    var iv_256 = new Array(16);
    for (var i = 0; i < key_256.length; i++) {
        key_256[i] = randomIntInc(1, 100);
        if (i % 2 === 0)
            iv_256[i / 2] = key_256[i];
    }
    key_258_array = new Uint8Array(key_256);
    iv_258_array = new Uint8Array(iv_256);

    encryptedPassword = encryptAESKey(pKey, key_258_array);
    var ivy = btoa(new TextDecoder('utf8').decode(iv_258_array));
    var encryptedIV = encryptUsingRSA(pKey, ivy);
    var payload = publicKeyHARD + "|" + encryptedPassword + "|" + ivy;
    return {
        'sender': username,
        'content': payload,
        'type': 'PKEY'
    };
}

// helper function used to generate random values, potentially unsafe
function randomIntInc(low, high) {
    return Math.floor(Math.random() * (high - low + 1) + low);
}


function connect(event) {
    username = document.getElementById('name').value.trim();
    //username = sanitizeString(username);
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
    //messageContent = sanitizeString(messageContent);
    if (messageContent && stompClient) {
        var encryptedContent = encryptUsingRSA(serverPublicKey, messageInput.value);
        var chatMessage = {
            sender: username,
            content: encryptedContent,
            type: 'CHAT'
        };
        stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function sendEvenOrOdd(event) {
    var messageContent = $($(this).find("img")).attr('alt').trim();
    //messageContent = sanitizeString(messageContent);
    if (messageContent && stompClient) {
        var encryptedContent = encryptUsingRSA(serverPublicKey, messageContent);
        var chatMessage = {
            sender: username,
            content: encryptedContent,
            type: 'GAME'
        };
        stompClient.send("/app/sendGameData", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    $('#opponent').attr('src', 'images/'+$($(this).find("img")).attr('alt') + '_big.svg');
    blockInput();
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');
    var messageParts;
    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.sender = decryptUsingRSA(message.sender);
        message.content = decryptUsingRSA(message.content);
        message.content = message.sender + ' joined the room';
    } else if (message.type === 'LEAVE') {
        message.sender = decryptUsingRSA(message.sender);
        message.content = decryptUsingRSA(message.content);
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left the room';
    } else if (message.type === 'START') {
        alert('Game starting');
        enableInput();
        message.content = decryptUsingRSA(message.content);
        messageParts = message.content.split('|');
        $("#player1").text(messageParts[0]);
        $("#player2").text(messageParts[1]);
    } else if (message.type === 'PKEY') {
        serverPublicKey = message.content;
        username = encryptUsingRSA(serverPublicKey, username);
        stompClient.send("/app/sendKeys",
            {},
            JSON.stringify(prepareConnectPayload(serverPublicKey))
        );
    } else if (message.type === 'GAME') {
        message.content = decryptUsingRSA(message.content);
        messageParts = message.content.split("|");
        if (messageParts[0] === 'inactive')
            connectingElement.classList.remove('hidden');
        else {
            connectingElement.classList.add('hidden');
        }
        $('#player1').text(messageParts[1]);
        $('#player2').text(messageParts[2]);
    } else if (message.type === 'SCORE') {
        message.content = decryptUsingRSA(message.content);
        messageParts = message.content.split('|');
        $('#score').text(messageParts[0]);
        $('#player1').text(messageParts[1]);
        $('#player2').text(messageParts[2]);
        alert(messageParts[1] + ' picked ' + messageParts[3] + ' while ' + messageParts[2] + ' picked ' + messageParts[4]+ '. New score is ' + messageParts[0]);
        enableInput();
    } else if (message.type === 'QUIT') {
        chatPage.classList.remove('hidden');
        gameArea.classList.add('hidden');
        var navBar = $("nav");
        navBar.css('display', 'none');
        connectingElement.classList.add('hidden');
        message.content = decryptUsingRSA(message.content);
        messageParts = message.content.split('|');
        alert('Game has ended with a final score ' + messageParts[1] + ' ' + messageParts[0] + ' ' + messageParts[2]);
    } else {
        messageElement.classList.add('chat-message');
        var usernameElement = document.createElement('span');
        message.sender = decryptUsingRSA(message.sender);
        message.content = decryptUsingRSA(message.content);
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
    //messageContent = sanitizeString(messageContent);
    messageContent = encryptUsingRSA(serverPublicKey, messageContent);
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'GAME'
        };
        stompClient.send("/app/requestGame", {"reply-to": "/" + username}, JSON.stringify(chatMessage));
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
    // messageContent = sanitizeString(messageContent);
    messageContent = encryptUsingRSA(serverPublicKey, messageContent);
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
    event.preventDefault();
}

function blockInput() {
    $(".list-inline-item").off();
    $(".buttonImage").css('filter', 'brightness(50%)');
}

function enableInput() {
    $(".list-inline-item").on('click', sendEvenOrOdd);
    $(".buttonImage").css('filter', 'brightness(100%)');
    $('#opponent').attr('src', 'images/opponent.svg');
}

function sanitizeString(str){
    str = str.replace(/[^a-z0-9 \.,_-|]/gim,"");
    return str.trim();
}

$(usernameForm).submit(connect);
$(messageForm).submit(sendMessage);
$('#playButton').on('click', requestGame);
$('#stopPlaying').on('click', stopPlaying);