const express = require('express');
const auth = require('../middleware/users_jwt');
const ToDoModel = require('../models/ToDo');

const router = express.Router();

//**************Create new ToDoTask --- Method=POST**************//

//We firstly pass the task creation request to auth i.e. JWT Middleware which checks the user's token and verifies their authority.
//Then we get back the user's value in the req field after it successfully gets verified from the auth/jwt middleware.
router.post('/', auth, async(req, res, next) => {
    try 
    {
        //Also, we don't pass task finished value here, as obviously when we create a task, it must be unfinished. Thus we use only default
        //value(which is false), and don't pass anything related to it in the body.
        const createToDoTask = await ToDoModel.create({
            title: req.body.title,
            description: req.body.description,
            user: req.user.id
        });

        if(!createToDoTask)
        {
            return res.status(400).json({
                success: false,
                msg: "Task cannot be created. Something went wrong..."
            });
        }

        res.status(200).json({
            success: true,
            msg: 'Task successfully created',
            todotask: createToDoTask
        });

        
    } catch (error) {
        next(error);
    }
});

//**************Fetch all Unfinished ToDo tasks of THE USER using it.(and not fetchig all user's tasks.) -- Method = GET**************//

//We don't need to create a new name for the path as even though both are starting with '/', but one is GET and other is POST request.
//That is why we require JWT middleware here as well, as we need to only get tasks of a particular user and return their tasks to them.
//We don't return everybody's tasks to everyone.

router.get('/', auth, async(req, res, next) => {
    try
    {

        const toDoTasks = await ToDoModel.find({
            user: req.user.id,
            finished: false
        });

        if(!toDoTasks)
        {
            return res.status(400).json({
                success: false,
                msg: 'Tasks could not be fetched. Something went wrong...'
            });
        }

        res.status(200).json({
            success: true,
            msg: "All Tasks received!",
            count: toDoTasks.length,
            allToDos: toDoTasks
        });
        
    }
    
    catch (error) {
        next(error);
    }
});

//**************Fetch all Finished ToDo tasks of THE USER using it.(and not fetchig all user's tasks.) -- Method = GET**************//

//We created a new name for the path as '/finished'.
//We require JWT middleware here as well, as we need to only get tasks of a particular user and return their tasks to them.
//We don't return everybody's tasks to everyone.

router.get('/finished', auth, async(req, res, next) => {
    try
    {

        const toDoTasks = await ToDoModel.find({
            user: req.user.id,
            finished: true
        });

        if(!toDoTasks)
        {
            return res.status(400).json({
                success: false,
                msg: 'Tasks could not be fetched. Something went wrong...'
            });
        }

        res.status(200).json({
            success: true,
            msg: "All Tasks received!",
            count: toDoTasks.length,
            allToDos: toDoTasks
        });
        
    }
    
    catch (error) {
        next(error);
    }
});

//**************Edit a ToDoTask --- Method=PUT**************//

//Here we have to pass the id of the task created, as we will firstly search if such a task exists, and if it does
//, then we will update it. We don't need auth/JWT middleware here as any task created is unique, doesn't matter the user.

//We search this task using req.params.id, as params is passed through the app, in the url.
router.put('/:id', async(req, res, next) => {
    try
    {
        let editToDoTask = await ToDoModel.findById(req.params.id);
        console.log(req.params.id);

        if(!editToDoTask)
        {
            return res.status(400).json({
                success: false,
                msg: "Task couldn't be found."
            });
        }

        //Here findByIdAndUpdate just finds the task by it's id and updates whatever came inside the body on it's own in the required fields.
        //We also mark it as new, and run validation on the fields received, so that no useless field is passes and updated.
        //Using this only we can update our task as finished (or not).
        
        editToDoTask = await ToDoModel.findByIdAndUpdate(req.params.id, req.body, {
            new: true,
            runValidators: true
        });

        // if(!editToDoTask)
        // {
        //     return res.status(400).json({
        //         success: false,
        //         msg: "Task couldn't be updated. Something went wrong..."
        //     });
        // }

        res.status(200).json({
            success: true,
            msg: "Task successfully updated!",
            updatedTask: editToDoTask
        });
    }
    
    catch (error) {
        next(error);
    }
});

//**************Delete a ToDoTask --- Method=DELETE**************//

//Here we have to pass the id of the task created, as we will firstly search if such a task exists, and if it does
//, then we will delete it. We don't need auth/JWT middleware here as any task created is unique, doesn't matter the user.

//We search this task using req.params.id, as params is passed through the app, in the url.
router.delete('/:id', async(req, res, next) => {

    try
    {
        let deleteToDoTask = await ToDoModel.findById(req.params.id);

        if(!deleteToDoTask)
        {
            return res.status(400).json({
                success: false,
                msg: "Task couldn't be found."
            });
        }

        //Here findByIdAndDelete just finds the task by it's id and deletes the task and whatever came inside the body on it's own.
        deleteToDoTask = await ToDoModel.findByIdAndDelete(req.params.id);

        res.status(200).json({
            success: true,
            msg: "Task successfully deleted!"
        });
    } 

    catch (error) {
        next(error);
    }

});

module.exports = router;