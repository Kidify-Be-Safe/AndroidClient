package com.antozstudios.myapplication.util;

import com.antozstudios.myapplication.data.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;
public class JsonParser {

    public String jsonResponse;
    public List<User> userList;
    public List<User> emailList;

    // Methode zum Parsen aufrufen, nachdem jsonResponse gesetzt wurde
    public void parse() {
        Gson gson = new Gson();
        Type userListType = new TypeToken<List<User>>() {}.getType();
        userList = gson.fromJson(jsonResponse, userListType);


    }


    public int getIndex(String email){

        for(int i =0;i<userList.size();i++){
            if(email.equals(userList.get(i).email)){
                return i;
            }
        }
        return -1;
    }

}
