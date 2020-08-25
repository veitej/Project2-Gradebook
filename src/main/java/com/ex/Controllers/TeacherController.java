package com.ex.Controllers;


import com.ex.Dao.Dao;
import com.ex.Models.AssignmentEntity;
import com.ex.Models.ClazzEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path="/teacher")
public class TeacherController {

    Dao dao;

    @Autowired
    public TeacherController(Dao dao) {
        this.dao = dao;
    }

    @RequestMapping(path = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> classList(@PathVariable int id) {

        //This is an ArrayList of Maps -> this will be our class list where each item in the list corresponds to all the data needed about a class
        ArrayList<Map<String, Object>> arraylist1 = new ArrayList<>();
        
        ArrayList<ClazzEntity> classes = dao.getClassForTeacher(id);

        if(classes!=null){
            for (int i=0; i<classes.size(); i++) {
                //This is all the data about 1 class needed
                Map<String, Object> map = new HashMap<>();
                map.put("ClassName", classes.get(i).getClassName());
                map.put("ClassSubject", classes.get(i).getClassSubject());
                map.put("TeacherName", dao.getTeacherName(classes.get(i).getTeacherId()));
                map.put("TestWeight", classes.get(i).getTestWeight());
                map.put("QuizWeight", classes.get(i).getQuizWeight());
                map.put("HomeworkWeight", classes.get(i).getHomeworkWeight());
                map.put("ParticipationWeight", classes.get(i).getParticipationWeight());
                map.put("AssignmentList",dao.getAssignmentListByClassID(classes.get(i).getId()));
                //these should be class averages
                map.put("TestAverage", null);
                map.put("QuizAverage", null);
                map.put("HomeworkAverage", null);
                map.put("ParticipationAverage", null);
                map.put("OverAllAverage",null);
                arraylist1.add(map);
            }
        }

        if (arraylist1.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity(arraylist1, HttpStatus.OK);
        }
    }

    @PostMapping(path = "/update", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> classList(@RequestBody String data) {
        if(data==null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }else{
            System.out.println(data);
            ObjectMapper om = new ObjectMapper();
            try {
                Map<String,Object> check = om.readValue(data,Map.class);
                System.out.println(check.get("name"));
                System.out.println(check.get("type"));
                System.out.println(check.get("data"));
                String hold = check.get("data").toString();
                hold=hold.substring(1,hold.length()-1);
                System.out.println(hold);
                String[] list = hold.split(", ");
                for(String pair : list){
                    String[]a = pair.split("=");
                    dao.updateGrade(check.get("name").toString(),check.get("type").toString(),Integer.parseInt(a[0]),Integer.parseInt(a[1]));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(null,HttpStatus.OK);
        }
    }

}
