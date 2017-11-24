var portalToken = "";
var jsessionId = "";

function callServlet() {
    var targetURL = 'http://127.0.0.1:7101/ADF_JET_REST-ViewController-context-root/restproxy/rest-api/person';
    var xhr = new XMLHttpRequest();
    xhr.open('GET', targetURL)
    xhr.setRequestHeader("Authorization", "Bearer " +  portalToken);
    xhr.onload = function () {
        if (xhr.status === 200) {
            alert('Response ' + xhr.responseText);
        }
        else {
            alert('Request failed.  Returned status of ' + xhr.status);
        }
    };
    xhr.send();
}


function callNodeAPI() {
    var targetURL = 'http://127.0.0.1:8123/api/things';
    var xhr = new XMLHttpRequest();
    xhr.open('GET', targetURL)
    xhr.setRequestHeader("Authorization", "Bearer " +  portalToken);
    xhr.onload = function () {
        if (xhr.status === 200) {
            alert('Response ' + xhr.responseText);
        }
        else {
            alert('Request failed.  Returned status of ' + xhr.status);
        }
    };
    xhr.send();
}

function getQueryParams() {
    token = getParameterByName('token');
    if (token) {
        document.getElementById('txt').value += 'Token was received and saved in the client for future REST calls';
        document.getElementById('txt').value += 'Token:' + token;
        document.getElementById('txt').value += 'Decoded Token:' + JSON.stringify(parseJwt(token));
    }
    else 
        document.getElementById('txt').value += 'Token was NOT received; you will not be able to use this web application in a meaningful way';
    portalToken = token;

    // get session cookie
    jsessionId = getCookie("JSESSIONID");
    document.getElementById('txt').value += 'JSESSIONID Cookie was read: ' + jsessionId;

    if (!jsessionId)
        jsessionId = getParameterByName('sessionid');
    if (!jsessionId)
        document.getElementById('txt').value += '<br>Session Id was received and saved in the client for future REST calls';
    else 
        document.getElementById('txt').value += '<br>Session Id was NOT received; you will not be able to use this web application in a meaningful way';

}

function getParameterByName(name, url) {
    if (!url)
        url = window.location.href;
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex.exec(url);
    if (!results)
        return null;
    if (!results[2])
        return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

// https://www.w3schools.com/js/js_cookies.asp 
function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

// https://stackoverflow.com/questions/38552003/how-to-decode-jwt-token-in-javascript
function parseJwt(token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace('-', '+').replace('_', '/');
    return JSON.parse(window.atob(base64));
};