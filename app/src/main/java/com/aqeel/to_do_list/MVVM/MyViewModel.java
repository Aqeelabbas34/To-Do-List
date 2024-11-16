package com.aqeel.to_do_list.MVVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aqeel.to_do_list.DataClasses.ModelTask;
import com.aqeel.to_do_list.DataClasses.ModelUser;

import java.util.List;

public class MyViewModel extends ViewModel {
    private final MutableLiveData<String> _message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private MutableLiveData<List<ModelTask>> _taskLiveData = new MutableLiveData<>();
    public LiveData<List<ModelTask>> getTaskLiveData(){
        return _taskLiveData;
    }
    private final Repository repository;

    public MyViewModel() {
        this.repository = new Repository();

    }
    public void addTask(ModelTask modelTask){
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
    public void fetchUserTask(String category,String ID){
        _taskLiveData=repository.fetchTask(category, ID, new Repository.Callback() {
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
    } public void fetchTaskOnDate(String date,String ID){
        _taskLiveData=repository.fetchTaskOnDate(date, ID, new Repository.Callback() {
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

    public MutableLiveData<String> getMessage() {
        return _message;
    } public MutableLiveData<Boolean> getSuccess() {
        return success;
    }

   public void signUpHandler(String email, ModelUser modelUser){
        repository.signUpHandler(email, modelUser, new Repository.Callback() {
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
   public void login(String email,String pass){
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
