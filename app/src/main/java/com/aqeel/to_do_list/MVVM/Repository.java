package com.aqeel.to_do_list.MVVM;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
                    Log.d("CategoryDebug", "number of tasks: " + taskList.size());

                }
                callback.onSuccess("task fetched");
            }else {
                Log.d("CategoryDebug", "Selected Category no task found: " + category);

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
    public MutableLiveData<HashMap<String,Integer>> getCompletedTaskCountForWeek(String ID) {
        MutableLiveData<HashMap<String,Integer>> completedTaskCount = new MutableLiveData<>();



        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Start from Monday
        Date startOfWeek = calendar.getTime();
        calendar.add(Calendar.DATE, 6); // Get the end of the week (Sunday)
        Date endOfWeek = calendar.getTime();

        // Format dates to "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = sdf.format(startOfWeek);
        String endDate = sdf.format(endOfWeek);

        db.collection("tasks")
                .whereEqualTo("userID", ID) // Filter by userID
                .whereGreaterThanOrEqualTo("dateCompleted",startDate)
                .whereLessThanOrEqualTo("dateCompleted", endDate)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore Error", "SnapshotListener failed", e);
                        completedTaskCount.setValue(null); // Handle error state
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        HashMap<String, Integer> taskCounts = new HashMap<>();
                        Log.d("Firestore", "Number of tasks fetched: " + queryDocumentSnapshots.size());

                        // Iterate over the documents and count completed tasks per date
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String taskDate = document.getString("date"); // Expected format: "yyyy-MM-dd"
                            String completed = document.getString("complete"); // Check "complete" status

                            Log.d("TaskRepo", "Task ID: " + document.getId() + " Task Date: " + taskDate + ", Completed: " + completed);

                            if (completed != null && completed.equals("complete")) {
                                if (taskDate != null) {
                                    taskCounts.put(taskDate, taskCounts.getOrDefault(taskDate, 0) + 1);
                                } else {
                                    // Log or handle the case where taskDate is null
                                    Log.w("TaskRepository", "Null taskDate encountered for task ID: " + document.getId());
                                }
                            }
                        }

                        Log.d("Task Count", "Completed Task Counts: " + taskCounts);
                        completedTaskCount.postValue(taskCounts);
                    } else {
                        Log.w("Firestore", "No tasks found for the given query");
                        completedTaskCount.postValue(null);
                    }
                });

        return completedTaskCount;
    }
    public void markComplete(ModelTask task){
        List<ModelTask> taskList = taskLiveData.getValue();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.collection("Task")
                .document(task.getTaskID())
                .update("dateCompleted", todayDate,
                        "status", "complete" )
                .addOnSuccessListener(unused -> {
                      task.setStatus("complete");
                      task.setDateCompleted(todayDate);
//                        taskList.remove(task);

                        taskLiveData.postValue(taskList);
                })

        .addOnFailureListener(e -> Log.w("AdapterTask","Error deleting task" ));
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

                                        assert taskList != null;
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

