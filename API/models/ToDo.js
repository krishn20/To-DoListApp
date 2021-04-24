const mongoose = require('mongoose');

const ToDoSchema = new mongoose.Schema({
    title:{
        type: String,
        required: true
    },

    description:{
        type: String
    },

    //Here we are saving the user's id as well, so tat we can uniqueli identify each user's items.
    //Also, the type of it will be default = mongoose.Schema.Types.ObjectId, which is provided by MongoDB itself.
    
    //We give reference as well using the 'ref' keyword and give the same value of the Users DB as we mentioned when we exported it in the
    //Users.js file.

    user:{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Users'
    },

    finished:{
        type: Boolean,
        default: false
    },

    createdAt:{
        type: Date,
        default: Date.now
    }

});

module.exports = ToDo = mongoose.model('todo', ToDoSchema);
