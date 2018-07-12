package com.kuangye.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kuangye.rxjavademo.Student.Course;

import java.util.ArrayList;

/**
 * 响应式编程中一切皆流
 * 想象成流水线
 * <p>
 * RxJava 的观察者模式
 * Observable (可观察者，即被观察者)
 * Observer (观察者)
 * subscribe (订阅)、事件
 * Observable 和 Observer 通过 subscribe() 方法实现订阅关系
 * 从而 Observable 可以在需要的时候发出事件来通知 Observer。
 * <p>
 * RxJava就是为了剔除这样的嵌套结构，使得整体的逻辑性更强 (不要出现for循环)
 */
public class MainActivity extends AppCompatActivity {

   
    private ListView listView;
    private ArrayList<Student> mData;
    private ArrayAdapter<Student> adapter;
    private Operator mOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Student item = new Student("学生" + i);

            ArrayList<Course> courses = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                Course course = new Course("学生" + i + "的课程" + j);
                courses.add(course);
            }
            item.setCourses(courses);
            mData.add(item);
        }

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        listView.setAdapter(adapter);

        mOperator = new Operator();
        mOperator.generateOperator();
//        mOperator.actionOperator();
//        mOperator.transformOperator(mData);
//        mOperator.filterOperator(mData);
//        mOperator.combinationOperator();
//        mOperator.assistOperator();
//        mOperator.doOperator();
//        mOperator.threadOperator();
//        mOperator.errorOperator();
//        mOperator.booleOperator();
//        mOperator.conditionsOperator();
//        mOperator.conversionOperator();

    }

}
