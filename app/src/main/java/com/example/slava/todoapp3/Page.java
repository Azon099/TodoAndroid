package com.example.slava.todoapp3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slava on 28.09.17.
 */

public class Page {
    List<Project> Projects = new ArrayList<Project>();

    public void clearAll(){
        for (int j = 0; j < Projects.size(); j++) {
            Projects.get(j).todos.clear();
        }
        Projects.clear();
    }

    public Integer getTodoId(String text){
        for(int i = 0; i < Projects.size(); i++){

            Project proj = Projects.get(i);
            for(int j=0; j < proj.todos.size(); j++){
                if(proj.todos.get(j).text == text){
                    return proj.todos.get(j).id;
                }
            }
        }
        return -1;
    }

    public Integer getLastTodoId(){
        Integer lastId = -1;
        for(int i = 0; i < Projects.size(); i++){
            Project proj = Projects.get(i);
            for(int j=0; j < proj.todos.size(); j++){
                if(proj.todos.get(j).id > lastId){ lastId = proj.todos.get(j).id;}
            }
        }
        return lastId;
    }

    public void addTodo(String text, int project_id, Boolean isCompleted){
        Project proj = Projects.get(project_id);
        Todo newTodo = new Todo();
        newTodo.text = text;
        newTodo.id = getLastTodoId();
        newTodo.isCompleted = isCompleted;
        proj.todos.add(newTodo);
    }
}
