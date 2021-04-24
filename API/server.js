// For doing http type get/post requests, 'http' package is directly available to use.
// We create a server here, which takes a callback for sending a 'request' or getting a 'response'.
// We have to create the Http header for them, and then use end() to either send the response to OR get the request from the client.

// This will work only after the server starts running and an actual response id sent to that url+port.
// listen() will tell on what port the server is running and what message is received when it does start. 

// We created a localhost+port at 3000 and thus can normally send a request at "http://localhost:3000" to send a request,
// upon which we get "Hello Node" as the response.

/**
 * ---------------------------------------------------------------------------------------------------------
var http = require('http');

http.createServer(function(request, response){
    //200, 400, 500

    response.writeHead(200, {
        'Content-Type': 'text/plain'
    });

    response.end("Hello Node")

}).listen(3000, console.log("Server is running on port: 3000"));
 * ---------------------------------------------------------------------------------------------------------
 */

/*
 *
 * Whenever we change anything in our request/response, the server needs to be restarted to accept those changes. To handle this situation,
 * we use "nodemon" package, which automatically restarts the server whenever a change is detected in the server.js file.
 * Use "nodemon server.js" in the terminal instead of the usual "node server.js".
 * 
 * -D means devDependencies i.e. the dependencies that are only used at the time of development and not in general.
 * -g means global package/dependency addition.
 * 
 * express = Framework to create API's and server.
 * mongoose = Used for MongoDB connections.
 * jsonWebToken(JWT) = Used for Authentication and Authorization for user sessions.
 * bcryptjs = For encrypting the passwords and crucial info while making connections/sessions.
 * dotenv = Helps provides the system variables.
 * colors = Prints console outputs in colors.
 * morgan = Shows all requests, sent to the server, in the terminal.
 * 
 */

//--------------------------------------------------------------------------------------------------------------------------------
//express provides REST API features (i.e CRUD operations), therefore we use get() here, and it works just like an http request.
//Also we send data in JSON format.

// app.get('/todo', (req, res) => {
//     res.status(200).json({
//         "name": "Krishn"
//     });
// });
//--------------------------------------------------------------------------------------------------------------------------------


// ----------------------------- // ----------------------------- // ----------------------------- // ----------------------------- //
// ----------------------------- // ----------------------------- // ----------------------------- // ----------------------------- //
// ----------------------------- // ----------------------------- // ----------------------------- // ----------------------------- //


const express = require('express')
const colors = require('colors')
const morgan = require('morgan')
const dotenv = require('dotenv')
const connectDB = require('./config/db')

const app = express()

//We can use use() function, which acts as a middleware i.e that runs everytime a request is sent/received. It can also save values that can
//be used by other CRUD ops. Also, it can only move to further ops. using the next() function.

// app.use((req, res, next) => {
//     console.log("Middleware ran.")
//     req.title = "Agarwal"
//     next();
// });

//We use morgan instead which shows all requests, sent to the server, in the terminal.
//express provides a function to read the json type data/object (here received as a request/response from a client).
app.use(morgan("dev"));
app.use(express.json({}));
app.use(express.json({
    extended: true
}));

//Here we defined which env file to configure and thus gave it's path. Now we can use those env variables/macros wherever we want here.
//We just need to use process.env.<variable> now.
dotenv.config({
    path: "./config/config.env"
})

//Here, we run the connectDB function, which connects us to our MongoDB using mongoose. We exported it from db.js file and used it here.
//We write this code only after we did our env config, as config file stores the vars/macros and connectDB() won't get those values before.
connectDB();

//Here we have to write all the routes that will be available, as they can only be accessed from the server/by running the server.
//So we gave all the routes in the users.js and todo.js files.
app.use('/api/todo/auth', require('./routes/users'));
app.use('/api/todo', require('./routes/todo'));

//Using the PORT macro from the env file. Also to use it in a string, we have to enclose that string in ` ` (tilde) rather than " " (quotes).
//We use the general ${} way to use a variable value in a string.
const PORT = process.env.PORT || 3000;

app.listen(PORT, console.log(`Server is running on port: ${PORT}`.red.underline.bold));
//using color features for console output.

//--------------------------------------------------------------------------------------------------------------------//
//--------------------------------------------------------------------------------------------------------------------//
//--------------------------------------------------------------------------------------------------------------------//

//We cannot use the API on our phone as here also we actually run it on a localhost(on port 3000). We need to deploy it on a real server
//so that we can create a url, and then these routes get appended to that url so that we can use these API ops. to Register, Auth and
//Login a user.

//For this we use a free server service - Heroku. Heroku helps us create our app (which actually runs your API on their server).
//We just need to login using our terminal and create a Heroku remote on our app for our git repo.

//Then we add these files and commit our changes to our local git repo., which we finally push to Heroku's master branch.

