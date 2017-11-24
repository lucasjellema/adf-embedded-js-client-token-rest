// Handle REST requests (POST and GET) for departments
var express = require('express') //npm install express
, bodyParser = require('body-parser') // npm install body-parser
, fs = require('fs')
, https = require('https')
, http = require('http')
, request = require('request');


const app = express()
.use(bodyParser.urlencoded({ extended: true }))
//configure sseMW.sseMiddleware as function to get a stab at incoming requests, in this case by adding a Connection property to the request
.use(express.static(__dirname + '/client-web-app'))
;

const server = http.createServer(app);

server.listen(3300, function listening() {
    console.log('Listening on %d', server.address().port);
  });
  