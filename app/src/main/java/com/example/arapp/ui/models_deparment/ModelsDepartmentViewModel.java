package com.example.arapp.ui.models_deparment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ModelsDepartmentViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ModelsDepartmentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is models department fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
