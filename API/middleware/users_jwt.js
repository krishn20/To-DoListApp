//After Login or Register, we can directly work upon the user, but for that we should have a unique way of identifying a user as well as
//using it, we need to do auth of user to get their data and restore their session.

//This file acts as the middleware as it works upon authorization of a user.

//We check if firstly it is an Auth request, and if it is then we check if the tokens, received and saved, match or not.
//And then we send res accordingly.
//We don't go to the try catchblock if we don't even have a token for a user i.e. a new user. So we directly use return in this case.

const jwt = require('jsonwebtoken');

module.exports = async function(req, res, next) {

    const token = req.header('Authorization');

    if(!token){
        return res.status(401).json({
            msg: "No token generated for this user. Authorization Denied!"
        });
    }

    try {

        //jwt.verify() receives a user token (present in the Authorization Header). We compare this token to our saved jwtToken in
        //the env file. If verified, we receive the decoded value of the token i.e our payload. (which has payload.user.id in our case).

        //We give this id to the user's currently sent params i.e. the "req" params.
        //(req.user = decoded.user --> This gives the decoded.user value, which is nothing but id: user.id, to the req.user object).

        //req.user is currently not defined here, as it is provided by the user itself. We will actually make this user object here, as
        //json object, when we receive this from our app. Then we will make those details into a "user "JSON object and send it here.
        
        //Finally, since it's a callback, this will be received by the login route again, for further work.

        await jwt.verify(token, process.env.jwtUserSecretToken, (err, decoded) => {

            if(err){
                res.status(401).json({
                    msg: "Token is invalid!"
                });
            }

            else{
                req.user = decoded.user;
                next();
            }

        });
        
    } catch (error) {

        console.log("Something went wrong with the middleware: " + error);

        res.status(500).json({
            msg: "Server error!"
        });

    }

}