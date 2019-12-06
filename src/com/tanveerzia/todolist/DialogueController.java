package com.tanveerzia.todolist;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogueController {
    @FXML
    private TextField shortDescriptionPane;

    @FXML
    private TextArea detailsPane;

    @FXML
    private DatePicker deadLinePane;

    public TodoItem processData(){
        String shortDescription = shortDescriptionPane.getText().trim();
        String details = detailsPane.getText().trim();
        LocalDate deadline = deadLinePane.getValue();


        TodoItem item = new TodoItem(shortDescription,details,deadline);
        TodoData.getInstance().addItem(item);
        return item;
    }

    public void editTodoItem(String shortDescription,String details,LocalDate deadline){
        this.shortDescriptionPane.setText(shortDescription);
        this.detailsPane.setText(details);
        this.deadLinePane.setValue(deadline);



        //
//        String shortDescription1 = shortDescriptionPane.getText().trim();
//        String details1 = detailsPane.getText().trim();
//        LocalDate deadline1 = deadLinePane.getValue();
//
//        TodoItem item = new TodoItem(shortDescription1,details1,deadline1);
//
//        TodoData.getInstance().addItem(item);
//
//        return item;



    }
}
