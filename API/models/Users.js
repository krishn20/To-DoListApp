const mongoose = require('mongoose')

//Here we create the schema of the User type JSON object that will be passed using the API. We define all it's attributes.
//Also we exported it to use in other files.

const userSchema = new mongoose.Schema({

    username: {
        type: String,
        required: true
    },

    avatar: {
        type: String
    },

    email: {
        type: String,
        required: true
    },

    password: {
        type: String,
        required: true
    }

});

module.exports = mongoose.model('Users', userSchema); 