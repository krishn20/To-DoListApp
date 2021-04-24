//mongoose is used for connecting our app to DB.

const mongoose = require('mongoose');

const connectDB = async() => {

    const conn = await mongoose.connect(process.env.MONGO_URI, {
        useNewUrlParser: true,
        useCreateIndex: true,
        useUnifiedTopology: true,
        useFindAndModify: false
    });

    console.log(`MongoDB connected: ${conn.connection.host}`.cyan.bold);

}

//We need to use this connectDB variable in server.js file. So we have to export it.
module.exports = connectDB