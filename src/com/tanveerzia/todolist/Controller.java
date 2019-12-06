package com.tanveerzia.todolist;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {
    private List<TodoItem> todoItems;
    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea textArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Toggle filterToggleButton;
    private ContextMenu contextMenu;
    private FilteredList<TodoItem> filteredList;
    private Predicate<TodoItem> allItems;
    private Predicate<TodoItem> todaysItems;


    public void initialize(){
        contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem editItem = new MenuItem("Edit");

        editItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                editTodoItem(item);
            }
        });

        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteitem(item);
            }
        });
        contextMenu.getItems().addAll(deleteMenuItem,editItem);
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem todoItem, TodoItem t1) {
                if(t1 != null){
                    TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                    textArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });
      allItems= new Predicate<TodoItem>() {
          @Override
          public boolean test(TodoItem item) {
              return true;
          }
      };

      todaysItems = new Predicate<TodoItem>() {
          @Override
          public boolean test(TodoItem item) {
              return (item.getDeadline().equals(LocalDate.now()));
          }
      };

        filteredList = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(),
              allItems);

        SortedList<TodoItem> sortedList = new SortedList<TodoItem>(filteredList,
                new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem o1, TodoItem o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });


        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();
        todoListView.setContextMenu(contextMenu);
        todoListView.setVisible(true);

        todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {

            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
                ListCell<TodoItem> listCell = new ListCell<TodoItem>(){
                    @Override
                    protected void updateItem(TodoItem todoItem, boolean b) {
                        super.updateItem(todoItem, b);

                        if(b){
                            setText(null);
                        }
                        else{
                            setText(todoItem.getShortDescription());
                            if(todoItem.getDeadline().equals(LocalDate.now())){
                                setTextFill(Color.RED);
                            }
                            else if(todoItem.getDeadline().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.GREEN);
                            }
                        }
                    }
                };

                listCell.emptyProperty().addListener(
                        (obs,wasEmpty,isNowEmpty)->{
                            if(isNowEmpty){
                                listCell.setContextMenu(null);
                            }
                            else{
                                listCell.setContextMenu(contextMenu);
                            }
                }
                );
                return listCell;



            }
        });

    }
    @FXML
    public void showNewItemDialogue(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setHeaderText("this is used to add new item");
        dialog.setTitle("Add new Todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialogue.fxml"));
        try{
           System.out.println("couldnt load the dialogue");
            dialog.getDialogPane().setContent(fxmlLoader.load());

        }
        catch(IOException e){
            System.out.println(e.getStackTrace());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            DialogueController dialogueController = fxmlLoader.getController();
            TodoItem newItem = dialogueController.processData();
            todoListView.getSelectionModel().select(newItem);



        }
        else
            {
            System.out.println("cancel");
        }

    }

     public void handleKeyPressed(KeyEvent keyEvent ){
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        if(item != null){
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteitem(item);
            }

        }
     }

  public void deleteitem(TodoItem item){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(item.getShortDescription());
        alert.setContentText("Do you really want to delete the item from the list");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() ==ButtonType.OK){
            TodoData.getInstance().deleteTodoItem(item);
        }

  }
  public void editTodoItem(TodoItem item){
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.initOwner(mainBorderPane.getScene().getWindow());
      dialog.setHeaderText("this is used to edit item");
      dialog.setTitle("Edit Todo item");
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("todoItemDialogue.fxml"));
      try{
          dialog.getDialogPane().setContent(fxmlLoader.load());

      }
      catch(IOException e){
          System.out.println("couldnt load the dialogue");
          System.out.println(e.getStackTrace());
          return;
      }

      DialogueController dialogueController = fxmlLoader.getController();
      dialogueController.editTodoItem(item.getShortDescription(),item.getDetails(),item.getDeadline());



      dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
      dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
      Optional<ButtonType> result = dialog.showAndWait();
      if(result.isPresent() && result.get() == ButtonType.OK){
          TodoData.getInstance().deleteTodoItem(item);
          TodoItem newItem;
          newItem=dialogueController.processData();
          todoListView.getSelectionModel().select(newItem);


      }
      else
      {
          System.out.println("cancel");
      }

  }

  @FXML
  public void handleFilterButton(){
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        if(filterToggleButton.isSelected()){
           filteredList.setPredicate(todaysItems);
           if(filteredList.isEmpty()) {
               textArea.clear();

           }
           else if(filteredList.contains(item)){
               todoListView.getSelectionModel().select(item);
           }


        }

        else{
          filteredList.setPredicate(allItems);
          todoListView.getSelectionModel().select(item);
        }

  }

  @FXML
    public void exit(){
      Platform.exit();
  }

}
