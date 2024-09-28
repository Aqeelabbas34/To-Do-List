package com.aqeel.to_do_list;

public class ModelUser {
    String name,email,password;
    ModelUser(String Name, String Email,String pass){
        this.name=Name;
        this.email=Email;

        this.password=pass;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

