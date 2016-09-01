package com.kuangye.rxjavademo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.kuangye.rxjavademo.Student.Course;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * RxJava 的观察者模式
 *      Observable (可观察者，即被观察者)
 *      Observer (观察者)
 *      subscribe (订阅)、事件
 *      Observable 和 Observer 通过 subscribe() 方法实现订阅关系
 *      从而 Observable 可以在需要的时候发出事件来通知 Observer。
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listView;
    private List<Student> mData;
    private ArrayAdapter<Student> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mData = new ArrayList<>();
        for(int i = 0; i < 10;i++) {
            Student item = new Student("呵呵"+i);

            ArrayList<Course> courses = new ArrayList<>();
            for(int j = 0;j < 2;j++) {
                Course course = new Course("呵呵"+i+",course"+j);
                courses.add(course);
            }
            item.setCourses(courses);
            mData.add(item);
        }

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1);
        listView.setAdapter(adapter);

        /**创建 Observer --观察者*/
//        rxJavaForObserver();

        /**一个实现了 Observer 的抽象类：Subscriber*/
//        rxJavaForSubscriber();

        /**后台线程取数据，主线程显示*/
//        rxJavaForThread();

        /**快捷创建事件队列 just(T...): 将传入的参数依次发送出来*/
//        rxJavaForJust();

        /**使用RxJava实现ListView*/
//        rxJavaForListView();

        /**Func系列方法和Action系列方法的运用 之map方法*/
//        rxJavaForMap();

        /**Func系列方法和Action系列方法的运用 之flatMap方法*/
        rxJavaForFlatMap();


    }

    private void rxJavaForFlatMap() {
        Action1<Course> courseSubscriber = new Action1<Course>() {

            @Override
            public void call(Course course) {
                Toast.makeText(MainActivity.this, "course --> " + course.getCourseName(), Toast.LENGTH_SHORT).show();
            }
        };

        /***
         * flatMap() 的原理是这样的：【平铺】
         *      1. 使用传入的事件对象创建一个 Observable 对象；
         *      2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
         *      3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，
         * 而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法
         */
        Observable.from(mData).flatMap(new Func1<Student, Observable<Course>>() {
            @Override
            public Observable<Course> call(Student student) {
                return Observable.from(student.getCourses());
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(courseSubscriber);
    }

    private void rxJavaForMap() {
        Observable.just(new Student("小明")).map(new Func1<Student, String>() {

            @Override
            public String call(Student student) {
                return student.getName();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<String>() {
            @Override
            public void call(String name) {
                Toast.makeText(MainActivity.this, "name -->" + name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rxJavaForListView() {
        Observable.create(new OnSubscribe<List<Student>>() {
            @Override
            public void call(Subscriber<? super List<Student>> subscriber) {
                /**当 subscribe() 方法执行的时候调用*/
                subscriber.onNext(mData);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io())// 指定 subscribe() 发生在 IO 线程
        .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在主线程
        .subscribe(new Subscriber<List<Student>>() {
            @Override
            public void onCompleted() {
                Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "出错", Toast.LENGTH_SHORT).show();
                Log.e(TAG,e.getMessage());
            }

            @Override
            public void onNext(List<Student> data) {
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void rxJavaForObserver() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.i(TAG,"observer -->onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"observer -->onError");
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG,"observer -->onNext");
            }
        };
    }

    private void rxJavaForSubscriber() {
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.i(TAG,"subscriber -->onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"subscriber -->onError");
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG,"subscriber -->onNext");
            }

            /**
             * 会在 subscribe 刚开始，而事件还未发送之前被调用，可以用于做一些准备工作
             *      总是在 subscribe 所发生的线程被调用，而不能指定线程。要在指定的线程来做准备工作
             *      所以类似弹出一个显示进度的对话框不能在该方法中执行
             *      可以使用Observable.doOnSubscribe()
             */
            @Override
            public void onStart() {
                super.onStart();
                Log.i(TAG,"subscriber -->onStart");
            }
        };
    }

    private void rxJavaForThread() {
        final ImageView imageView = new ImageView(this);
        Observable.create(new OnSubscribe<Drawable>() {

            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_launcher);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Drawable>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Drawable drawable) {
                imageView.setImageDrawable(drawable);
            }
        });
    }

    private void rxJavaForJust() {
        Observable.just(1,2,3,4)
                .subscribeOn(Schedulers.io())// 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG,"number = " + integer);
                    }
                });
    }
}
