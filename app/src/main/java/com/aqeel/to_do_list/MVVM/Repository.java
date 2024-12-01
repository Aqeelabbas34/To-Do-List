package com.aqeel.to_do_list.MVVM;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.DataClasses.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Repository {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    MutableLiveData<List<ModelTask>> taskLiveData = new MutableLiveData<>();

    public void signUpHandler(String mail, ModelUser modelUser, Callback callback) {
        db.collection("User")
                .whereEqualTo("email", mail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (!task.getResult().isEmpty()) {
                            callback.onFailure("Email already registered");
                            return;
                        }


                        db.collection("User")
                                .add(modelUser)
                                .addOnSuccessListener(documentReference -> {
                                    callback.onSuccess("User Registered");
                                })
                                .addOnFailureListener(e -> {
                                    callback.onFailure("Failed to Register: " + e.getMessage());
                                });

                    } else {

                        callback.onFailure("Query failed: " + task.getException().getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Error during query: " + e.getMessage());
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

    public MutableLiveData<List<ModelTask>> fetchTask(String category, String ID){
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
//                callback.onFailure("snapshot error");
                Log.e("Fetch Task","error"+error);
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
//                callback.onSuccess("task fetched");
            }else {
                Log.d("CategoryDebug", "Selected Category no task found: " + category);

//                callback.onFailure("No task found");
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
    public MutableLiveData<List<ModelTask>> fetchTaskOnDate (String date, String ID){
        List<ModelTask> taskList = new ArrayList<>();
       db.collection("Task")
                .whereEqualTo("userID",ID)
                .whereEqualTo("dueDate",date)
                .whereEqualTo("status","pending")
                .addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
//                callback.onFailure(" error");
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
            }/*else {
                callback.onFailure("No task found");
            }*/
            taskLiveData.postValue(taskList);

        });
        return taskLiveData;
    }
    public MutableLiveData<List<ModelTask>> fetchCompletedTask ( String ID){
        List<ModelTask> taskList = new ArrayList<>();
        db.collection("Task")
                .whereEqualTo("userID",ID)
                .whereEqualTo("status","complete")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
//                        callback.onFailure(" error");
                        Log.e("Fetch completed Task","error"+error);
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
                    }/*else {
//                        callback.onFailure("No task found");
                    }*/
                    taskLiveData.postValue(taskList);

                });
        return taskLiveData;
    }
    public MutableLiveData<HashMap<String,Integer>> getCompletedTaskCountForWeek(String ID) {
        MutableLiveData<HashMap<String,Integer>> completedTaskCount = new MutableLiveData<>();

        HashMap<String, Integer> taskCounts = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, -6); // Move back 6 days to the previous Monday
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Otherwise, set to the current week's Monday
        }
        Date startOfWeek = calendar.getTime();
        calendar.add(Calendar.DATE, 6); // Get the end of the week (Sunday)
        Date endOfWeek = calendar.getTime();

        // Format dates to "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = sdf.format(startOfWeek);
        String endDate = sdf.format(endOfWeek);
        Log.d("Query", "Start Date: " + startDate + ", End Date: " + endDate);
        // Initialize taskCounts with the 7 days of the week
        List<String> weekDays = new ArrayList<>();
        calendar.setTime(startOfWeek);
        for (int i = 0; i < 7; i++) {
            weekDays.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
            taskCounts.put(weekDays.get(i), 0); // Initialize with 0 task count
        }

        db.collection("Task")
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

                        Log.d("Firestore", "Number of tasks fetched: " + queryDocumentSnapshots.size());

                        // Iterate over the documents and count completed tasks per date
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String taskDate = document.getString("dateCompleted"); // Expected format: "yyyy-MM-dd"
                            String completed = document.getString("status"); // Check "complete" status

                            Log.d("TaskRepo", "Task ID: " + document.getId() + " Task Date: " + taskDate + ", Completed: " + completed);
                            if (taskDate == null || taskDate.isEmpty()) {
                                Log.w("TaskRepository", "Null or empty dateCompleted for task ID: " + document.getId());
                                continue;
                            }
                            if (completed != null && completed.equalsIgnoreCase("complete")) {

                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    Date parsedDate = dateFormat.parse(taskDate);
                                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault()); // "Mon", "Tue", etc.
                                    String dayName = dayFormat.format(parsedDate);

                                    // Update task count for this day
                                    taskCounts.put(dayName, taskCounts.getOrDefault(dayName, 0) + 1);
                                    Log.d("TaskRepository", "Task Date: " + taskDate + " is mapped to Day Name: " + dayName + " Count: " + taskCounts.get(dayName));

                                } catch (ParseException e1) {
                                    Log.e("TaskRepository", "Error parsing taskDate: " + taskDate, e1);
                                }
                            }
                        }

                        Log.d("Task Count", "Completed Task Counts: " + taskCounts);
                        completedTaskCount.postValue(taskCounts);
                    }else {
                        Log.d("Firestore", "No tasks found or error.");
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
        if (taskList == null) {
            taskList = new ArrayList<>();
        }

        List<ModelTask> finalTaskList = taskList;

        db.collection("Task")

                .whereEqualTo("taskID",task.getTaskID())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("DeleteTask", "Task deleted successfully");


                                        finalTaskList.remove(task);
                                        taskLiveData.postValue(finalTaskList);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("DeleteTask", "Error deleting task", e);
                                        taskLiveData.postValue(finalTaskList);
                                    });
                        }
                    } else {
                        Log.w("DeleteTask", "Task not found");
                        taskLiveData.postValue(finalTaskList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("DeleteTask", "Error querying Firestore", e);
                    taskLiveData.postValue(finalTaskList);
                });

        return taskLiveData;
    }

    public MutableLiveData<Integer> getPendingCount(String ID){
        MutableLiveData<Integer> taskCount = new MutableLiveData<>();
        db.collection("Task")
                .whereEqualTo("userID", ID)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                       int   pending = task.getResult().size();
                       taskCount.postValue(pending);

                    } else {
                        Log.w("count pending", "Error getting tasks", task.getException());
                    }
                });
        return taskCount;
    }
    public MutableLiveData<Integer> getCompletedCount(String ID){
        MutableLiveData<Integer> taskCount = new MutableLiveData<>();
        db.collection("Task")
                .whereEqualTo("userID", ID)
                .whereEqualTo("status", "complete")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        int   pending = task.getResult().size();
                        taskCount.postValue(pending);

                    } else {
                        Log.w("count pending", "Error getting tasks", task.getException());
                    }
                });
        return taskCount;
    }


    public interface Callback {
        void onSuccess(String message);

        void onFailure(String error);
    }
}

