// Handle REST requests (POST and GET) for departments
var express = require('express') //npm install express
  , bodyParser = require('body-parser') // npm install body-parser
  , fs = require('fs')
  , https = require('https')
  , http = require('http')
  , request = require('request');
var jwt = require('jsonwebtoken');
var PORT = process.env.PORT || 8123;


const app = express()
  .use(bodyParser.urlencoded({ extended: true }))
  ;

const server = http.createServer(app);


//CORS middleware - taken from http://stackoverflow.com/questions/7067966/how-to-allow-cors-in-express-node-js
var allowCrossDomain = function (req, res, next) {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
  res.header('Access-Control-Allow-Headers', 'Content-Type');
  res.header('Access-Control-Allow-Credentials', true);
  res.header("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Authorization, Access-Control-Request-Method, Access-Control-Request-Headers");
  next();
}

app.use(allowCrossDomain);

server.listen(PORT, function listening() {
  console.log('Listening on %d', server.address().port);
});

app.get('/about', function (req, res) {
  res.writeHead(200, { 'Content-Type': 'text/html' });
  res.write("About External REST API:");
  res.write("/api/things?token=... , /api/things/{id}?token=... ");
  res.write("incoming headers" + JSON.stringify(req.headers));
  res.end();
});


app.get('/api/things', function (req, res) {
  // did we receive a token?
  //https://scotch.io/tutorials/authenticate-a-node-js-api-with-json-web-tokens
  // check header or url parameters or post parameters for token
  var token = req.body.token || req.query.token || req.headers['x-access-token'];
  if (req.headers && req.headers.authorization) {
    var parts = req.headers.authorization.split(' ');
    if (parts.length === 2 && parts[0] === 'Bearer') {
      // two tokens sent in the request
      if (token) {
        error = true;
      }
      token = parts[1];
    }
  }
  console.log("token received: " + token)
  var decoded = jwt.decode(token);

  // get the decoded payload and header
  var decoded = jwt.decode(token, { complete: true });
  console.log(decoded.header);
  console.log(decoded.payload);

  var subject = decoded.payload.sub;
  var issuer = decoded.payload.iss;

  // verify key
  var myKey = "myKey";
  var rejectionMotivation;
  var tokenValid = false;

  jwt.verify(token, myKey, function (err, decoded) {
    if (err) {
      console.log(JSON.stringify(err))
      rejectionMotivation = err.name + " - " + err.message;
    } else {
      console.log("Verified successfully against key and for non-expiration and general validity")
      tokenValid = true;
    }
  });


  if (!tokenValid) {
    res.status(403);
    res.header("Refusal-Motivation", rejectionMotivation);
    res.end();
  } else {
    // using the token, retrieve data from the cache via the REST API on the Cache




    // call to order service
    route_options = {
      method: 'GET',
      url: 'http://127.0.0.1:7101/ADF_JET_REST-ViewController-context-root/app-rest/cache/' + token
    };
    var userMap;
    console.log("Call REST Cache with token: " + JSON.stringify(route_options));
    request(route_options, function (error, rawResponse, body) {
      console.log("In callback after Cache request");
      if (error) {
        console.log(JSON.stringify(error));
      } else {
        console.log(rawResponse.statusCode);
        console.log("BODY:" + JSON.stringify(body));
        userMap = JSON.parse(body);
      }

      // do the thing the REST API is supposed to do
      var things = { "collection": [{ "name": "bicycle" }, { "name": "table" }, { "name": "car" }] }

      res.status(200);
      res.header('Content-Type', 'application/json');
      things.extra = 'Principal From usermap (from session cache): '+userMap.principal;
      res.end(JSON.stringify(things));
    })




  }
});
