package com.kuangye.rxjavademo;

import java.util.ArrayList;

/**
 * Created by wangshijie on 2016/8/29.
 */
public class Student {
    private String name;
    private ArrayList<Course> courses;

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                '}';
    }

    public static class Course {
        private String courseName;

        public Course(String courseName) {
            this.courseName = courseName;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }
    }
}
