package com.aqeel.to_do_list.MVVM;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.DataClasses.ModelUser;

import java.util.HashMap;
import java.util.List;

public class MyViewModel extends ViewModel {
    private final MutableLiveData<String> _message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private MutableLiveData<List<ModelTask>> _taskLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<ModelTask>> _DateTaskLiveData = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, Integer>> _taskForWeek = new MutableLiveData<>();
    private final Repository repository;
    public MyViewModel() {
        this.repository = new Repository();
    }

    public LiveData<Integer> getPending(String ID){
        return repository.getPendingCount(ID);
    }
    public LiveData<Integer> getComplete(String ID){
        return repository.getCompletedCount(ID);
    }

    public LiveData<HashMap<String, Integer>> getCompletedTasksForWeek() {
        return _taskForWeek;
    }

    public void fetchCompletedTasksForWeek(String userID) {
        _taskForWeek = repository.getCompletedTaskCountForWeek(userID);

    }
    public void fetchUserTask(String category, String ID,String date) {
        _taskLiveData = repository.fetchTask(category, ID,date);
    }

    public void fetchTaskOnDate(String date, String ID) {
        Log.d("fetch task calender","View Model called repo function  ");
        _taskLiveData = repository.fetchTaskOnDate(date, ID, new Repository.Callback() {
            @Override
            public void onSuccess(String message) {

            }

            @Override
            public void onFailure(String error) {

            }
        });

    }

    public LiveData<List<ModelTask>> getTaskLiveData() {
        Log.d("View Model","Task received in view model");
        return _taskLiveData;
    }
    public LiveData<List<ModelTask>> getOnDateTaskLiveData() {
        Log.d("fetch task calender","View Model return live data ");
        return _DateTaskLiveData;
    }





    public void deleteTask(ModelTask modelTask) {
        _taskLiveData = repository.deleteTask(modelTask);
    }

    public void addTask(ModelTask modelTask) {
        repository.addTask(modelTask, new Repository.Callback() {
            @Override
            public void onSuccess(String message) {
                _message.postValue(message);
                success.postValue(true);
            }

            @Override
            public void onFailure(String error) {
                _message.postValue(error);
                success.postValue(false);
            }
        });
    }



    public void fetchCompletedTask(String ID) {
        _taskLiveData = repository.fetchCompletedTask(ID);

    }

    public void markComplete(ModelTask modelTask) {
        repository.markComplete(modelTask);

    }

    public MutableLiveData<String> getMessage() {
        return _message;
    }

    public MutableLiveData<Boolean> getSuccess() {
        return success;
    }


    public void signUpHandler(String email,ModelUser modelUser) {
        repository.signUpHandler(email,modelUser, new Repository.Callback() {
            @Override
            public void onSuccess(String message) {
                _message.postValue(message);
                success.postValue(true);
            }

            @Override
            public void onFailure(String error) {
                _message.postValue(error);
                success.postValue(false);
            }
        });
    }

    public void login(String email, String pass) {
        repository.loginHandler(email, pass, new Repository.Callback() {
            @Override
            public void onSuccess(String message) {
                _message.postValue(message);
                success.postValue(true);
            }

            @Override
            public void onFailure(String error) {
                _message.postValue(error);
                success.postValue(false);
            }
        });
    }
}
