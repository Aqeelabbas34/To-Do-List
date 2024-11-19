package com.aqeel.to_do_list.MVVM;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MutableLiveData<List<ModelTask>> taskLiveData = new MutableLiveData<>();

    public void signUpHandler(String mail, ModelUser modelUser,Callback callback) {
        db.collection("User")
                .whereEqualTo("email", mail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                       callback.onFailure("Email already registered");
                    } else {


                        db.collection("User")
                                .add(modelUser)
                                .addOnSuccessListener(documentReference -> {
                                   callback.onSuccess("User Registered");
                                })
                                .addOnFailureListener(e -> {
                                  callback.onFailure("Failed to Register"+e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                  callback.onFailure("Error" + e.getMessage());
                });
    }
    public void loginHandler(String enteredEmail, String enteredPassword,Callback callback){
        db.collection("User")
                .whereEqualTo("email",enteredEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot= task.getResult();
                        if (!querySnapshot.isEmpty()){
                            for(QueryDocumentSnapshot document : querySnapshot){
                                ModelUser modelUser=document.toObject(ModelUser.class);
                                if (modelUser.getPassword().equals(enteredPassword)){
                                 callback.onSuccess("Login Successful");
                                }
                                else {
                                 callback.onFailure("Incorrect Password");
                                }
                                return;
                            }
                        }
                        else {
                            callback.onFailure("No user found");
                        }
                    }
                    else {
                        callback.onFailure("Error while fetching data");
                    }

                });

    }

    public MutableLiveData<List<ModelTask>> fetchTask(String category, String ID, Callback callback){
        List<ModelTask> taskList = new ArrayList<>();
        Query taskQuery= db.collection("Task")
                .whereEqualTo("userID",ID)
                .whereEqualTo("status","pending")
                .orderBy("timeStamp", Query.Direction.DESCENDING);
        if(!category.equals("All")){
            taskQuery=taskQuery.whereEqualTo("category",category);
        }
        taskQuery .addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                callback.onFailure("snapshot error");
                return;
            }
            taskList.clear();
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    String taskID = documentSnapshot.getId();
                    ModelTask userTask = documentSnapshot.toObject(ModelTask.class);
                    userTask.setTaskID(taskID);

                    taskList.add(userTask);

                }
            }else {
                callback.onFailure("No task found");
            }
            taskLiveData.postValue(taskList);

        });
        return taskLiveData;
    }
    public void addTask(ModelTask modelTask,Callback callback){
        db.collection("Task")
                .add(modelTask)
                .addOnSuccessListener(documentReference -> {
                    // Set the document ID as taskId
                    modelTask.setTaskID(documentReference.getId());


                    documentReference.update("taskID", modelTask.getTaskID())
                            .addOnSuccessListener(aVoid -> {
                                callback.onSuccess("Task added  " );
                            })
                            .addOnFailureListener(e -> {
                                callback.onFailure("Failed to update task ID");
                            });




                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Failed to add task");
                });
    }
    public MutableLiveData<List<ModelTask>> fetchTaskOnDate (String date, String ID, Callback callback){
        List<ModelTask> taskList = new ArrayList<>();
       db.collection("Task")
                .whereEqualTo("userID",ID)
                .whereEqualTo("dueDate",date)
                .whereEqualTo("status","pending")
                .addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                callback.onFailure("snapshot error");
                return;
            }
            taskList.clear();
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    String taskID = documentSnapshot.getId();
                    ModelTask userTask = documentSnapshot.toObject(ModelTask.class);
                    userTask.setTaskID(taskID);

                    taskList.add(userTask);

                }
            }else {
                callback.onFailure("No task found");
            }
            taskLiveData.postValue(taskList);

        });
        return taskLiveData;
    }
    public MutableLiveData<List<ModelTask>>  markComplete(ModelTask task){
        List<ModelTask> taskList = taskLiveData.getValue();
        db.collection("Task")
                .document(task.getTaskID())
                .update("status","complete")
                .addOnSuccessListener(unused -> {
                      task.setStatus("complete");
//                        taskList.remove(task);

                        taskLiveData.postValue(taskList);
                })

        .addOnFailureListener(e -> Log.w("AdapterTask","Error deleting task" ));
        return taskLiveData;
    }
    public MutableLiveData<List<ModelTask>>  deleteTask(ModelTask task){
        List<ModelTask> taskList = taskLiveData.getValue();
        db.collection("Task")

                .whereEqualTo("taskID",task.getTaskID())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("DeleteTask", "Task deleted successfully");

                                        taskList.remove(task);
                                        taskLiveData.postValue(taskList);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("DeleteTask", "Error deleting task", e);
                                        taskLiveData.postValue(taskList);
                                    });
                        }
                    } else {
                        Log.w("DeleteTask", "Task not found");
                        taskLiveData.postValue(taskList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("DeleteTask", "Error querying Firestore", e);
                    taskLiveData.postValue(taskList);
                });

        return taskLiveData;
    }



    public interface Callback {
        void onSuccess(String message);

        void onFailure(String error);
    }
}

