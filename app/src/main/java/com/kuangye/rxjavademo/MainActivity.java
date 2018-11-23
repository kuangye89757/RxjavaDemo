package com.kuangye.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kuangye.rxjavademo.Student.Course;
import com.kuangye.rxjavademo.operator.AssistOperator;
import com.kuangye.rxjavademo.operator.CombinationOperator;
import com.kuangye.rxjavademo.operator.ConversionOperator;
import com.kuangye.rxjavademo.operator.DoOperator;
import com.kuangye.rxjavademo.operator.FilterOperator;

import java.util.ArrayList;

/**
 * 响应式编程中一切皆流 (想象成流水线)
 *      RxJava就是为了剔除这样的嵌套结构，使得整体的逻辑性更强 (不要出现for循环)
 *
 * @author shijie9
 */
public class MainActivity extends AppCompatActivity {
   
    private ListView listView;
    private ArrayList<Student> mData;
    private ArrayAdapter<Student> adapter;

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

//        CreateOperator createOperator = CreateOperator.getInstance();
//        createOperator.generateOperator();
//
//        ActionOperator actionOperator = ActionOperator.getInstance();
//        actionOperator.actionOperator();
//
//        SchedulerAPI.INSTANCE.scheduler();
//
//        TransformOperator transformOperator = TransformOperator.getInstance();
//        transformOperator.transformOperator(mData);

        FilterOperator filterOperator = FilterOperator.getInstance();
        filterOperator.filterOperator(mData);

        CombinationOperator combinationOperator = CombinationOperator.getInstance();
        combinationOperator.combinationOperator();

        DoOperator doOperator = DoOperator.getInstance();
        doOperator.doOperator();

        ConversionOperator conversionOperator = ConversionOperator.getInstance();
        conversionOperator.conversionOperator();

        AssistOperator assistOperator = AssistOperator.getInstance();
        assistOperator.assistOperator();
    }

}
