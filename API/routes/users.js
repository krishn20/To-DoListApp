//This is the route file for the users. Here we write all the possible routes, where the API allows some user to go
//to and perform all the route actions.
//That's why we have to export the "router" as well; so that it can be used in the server.js file.

const express = require('express');
const router = express.Router();
const Users = require('../models/Users');
const bcrypt = require('bcryptjs');
const users_jwt = require('../middleware/users_jwt');
const jwt = require('jsonwebtoken');            
const { JsonWebTokenError } = require('jsonwebtoken');
const { exists } = require('../models/Users');

//*******************Login route for any user.*******************//
//***************************************************************//

router.post('/login', async (req, res, next) => {

    const email = req.body.email;
    const password = req.body.password;

    //Here, user is the same that we get after comparing email with the email saved in the DB.

    try {

        //Finding a user with the same email as provided. A part of Authentication.
        let user = await Users.findOne({
            email: email
        });

        if(!user){
            res.status(400).json({
                success: false,
                msg: 'User does not exist. Please register first, then continue.' 
            });
        }

        else{

            //Once email is verified, we get a user related with that email. Then we check their password now. A part of Authentication.

            const isMatch = await bcrypt.compare(password, user.password);

            if(!isMatch){
                res.status(400).json({
                    success: false,
                    msg: 'Invalid Password! Please check password and email once more.'
                });
            }

            //if password also matches, then now we have to generate token(using user.id as payload) and do Authorization of the user.
            //We use JWT for this again, just like we do in Register Route.

            else{

                const payload = {
                    user: {
                        id: user.id
                    }
                }

                jwt.sign(payload, process.env.jwtUserSecretToken, {
                    expiresIn: 360000
                }, (err, token) => {

                    if(err) throw err;

                    else{
                        res.status(200).json({
                            success: true,
                            msg: 'User is logged in!',
                            token: token,
                            user: user
                        });
                    }

                });

            }

        }
        
    } catch (error) {
        res.status(500).json({
            success: false,
            msg: 'Server Error!'
        });
    }

});



//*******************Auth route for any user.*******************//
//**************************************************************//

//Here we get the user's details in "req" field. We firstly pass it to the users_jwt function/middleware, which does the Token Auth.
//If done correctly, the user's id gets added/appended in the req field. 
//We then check for the same id if present in our Users DB/models. If yes, then we send a success message i.e. Auth successful.
//Then we can use this user's value for further work (as it has now been authorized).

//This will/should go hand-in-hand with both the Login route as well as Register route, as we authenticate and auth user there.

router.get('/', users_jwt, async (req, res, next) => {
    try {

        console.log(req.user.id);
        const user = await Users.findById(req.user.id).select('-password');
        console.log(user.id);

        if(user){
            res.status(200).json({
                success: true,
                user: user
            });
        }
        
    } catch (error) {
        console.log(error.message);
        res.status(500).json({
            success: false,
            msg: "Server Error!"
        });
        next();
    }
});

//*******************Register method for any user.*******************//
//*******************************************************************//
router.post('/register', async (req, res, next) => {

    //"req" is the request that we get from a user (for registeration here). It needs to have some headers and a body.
    //Body is mostly of JSON type. We will firstly Authenticate user on these values, and then we will Authorize the user with their data.
    //Server must save user values after receiving them.
    //"res" is what the server will give to the user, which again needs to be given back in a JSON format.
    //Because both our DB and API are NoSQL based.

    //We are going to perform user check (if exists or not) obviously from the DB. That is why we use async and await.
    //Also we have to compare using findOne(), which takes a body{} only to compare as again it is NoSQL type.
    //Finally, we also encrypt the user password and then save it in our DB. We use bcrypt and salt+hash to do so.

    //Therefore, we need to do Authentication and then Auth to get the user's values and start their session.

    //"user" keyword is the DB instance of user here.

    //

    //******req received from user******//

    console.log(req.body);

    const {username, email, password} = req.body

    try {

        //Authentication
        let user_exist = await Users.findOne({email: email});

        //if exists
        if(user_exist){

            //******res given back to user******//

            res.json({
                success: false,
                msg: 'User already exists!'
            });

            console.log("User exists already!")

        }

        else{

            //if new user
            let user = new Users();

            user.username = username;
            user.email = email;

            const salt = await bcrypt.genSalt(10);
            user.password = await bcrypt.hash(password, salt);

            let size = 200;
            user.avatar = "https://gravatar.com/avatar/?s=" + size + "&d=retro";
            //gravatar is a website that provides random avatars. We give a fixed size to it although.

            await user.save();
            console.log("User data saved!");
            console.log(user);
            //saving user to MongoDB


            //******res given back to user******//

            // Here we make a payload of the user's id as this will be useful for token generation and then later for user's authorization.
            //Therefore, we sign() this payload into the env file as "jwtUserSecretToken". And send a msg if done correctly, along with
            //the token itself.
            //This payload gets saved as a String literal in the jwtUserSecretToken in our env file. This string also passes through some
            //Hash Algos, so it is completely different. The string created after this conversion is unique for each user(as id is also
            // unique), and thus it is called a user token.

            const payload = {
                user: {
                    id: user.id
                }
            }

            jwt.sign(payload, process.env.jwtUserSecretToken, {
                expiresIn: 360000
            }, (err, token) => {

                if(err) throw err;
                else{
                    res.status(200).json({
                        success: true,
                        token: token
                    });
                }

            });

            // res.json({
            //     success: true,
            //     msg: 'User registered!',
            //     user: user
            // });

        }
        
    } catch (error) {
        console.log(error);
        res.status(500).json({
            success: false,
            msg: "Server Error!"
        });
    }

});

module.exports = router;