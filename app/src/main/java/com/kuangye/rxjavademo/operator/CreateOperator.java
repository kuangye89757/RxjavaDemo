package com.kuangye.rxjavademo.operator;

import android.util.Log;

import com.kuangye.rxjavademo.Student;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * RxJava基本概念：
 * 
 * 一、Observer   即观察者，  决定事件触发的时候将有怎样的行为  
 * 二、Observable 即被观察者，决定什么时候触发事件以及触发怎样的事件
 * 三、subscribe() 方法将它们联结起来 实现订阅关系
 *      observable.subscribe(observer);
 *           或者：
 *      observable.subscribe(subscriber); 用于链式写法
 *      
 *      //将传入的 Subscriber 作为 Subscription 返回。是为了方便unsubscribe().
 *      public Subscription subscribe(Subscriber subscriber) {
 *          subscriber.onStart();
 *          onSubscribe.call(subscriber);
 *          return subscriber;
 *      }
 * 
 * 从而 Observable 可以在需要的时候发出事件来通知 Observer。
 * 
 * 
 * Observer 与 Subscriber区别:
 *      1.onStart(): 这是 Subscriber 增加的方法。会在subscribe开始，事件未发送前被调用，用于做一些准备工作
 *              总是在 subscribe 所发生的线程被调用，而不能指定线程
 *              在指定的线程来做准备工作，可以使用 doOnSubscribe() 
 *      2.unsubscribe(): Subscriber实现的Subscription接口的方法，用于取消订阅    
 *              调用后，Subscriber将不再接收事件。一般在调用前使用isUnsubscribed()判断一下状态
 *              由于subscribe() 之后， Observable 会持有 Subscriber 的引用
 *              要在不再使用的时候尽快在合适的地方（如onPause() onStop() 等）调用unsubscribe()来解除引用，以避内存泄露
 *      3.Observer 在 subscribe() 过程中最终会被转换成 Subscriber 对象
 *              一般使用 Subscriber 来代替 Observer
 * 
 * @author shijie9
 */
public class CreateOperator {
    private static final String TAG = "CreateOperator";

    private static class CreateOperatorHolder {
        private final static CreateOperator mInstance = new CreateOperator();
    }

    private CreateOperator() {

    }

    public static CreateOperator getInstance() {
        return CreateOperatorHolder.mInstance;
    }

    /**
     * 创建操作符: 用于创建不同形式的Observable
     */
    public void generateOperator() {
        Log.d(TAG,"=================createOperator=================");
        createOperator();
        Log.d(TAG,"=================justOperator=================");
        justOperator();
        Log.d(TAG,"=================fromOperator=================");
        fromOperator();
        Log.d(TAG,"=================intervalOperator=================");
        intervalOperator();
        Log.d(TAG,"=================rangeOperator=================");
        rangeOperator();
        Log.d(TAG,"=================fromCallOperator=================");
        fromCallOperator();
    }

    /**
     * 创建 Observer
     */
    private void createObserver(){
        //RxJava 中的 Observer 接口的实现方式
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG,"onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"onError -- " + e.getMessage());
            }

            @Override
            public void onNext(String o) {
                Log.d(TAG,"onNext -- " + o);
            }
        };
    }

    /**
     * 使用create创建一个Observable
     */
    private void createOperator() {
        //RxJava 还内置了一个实现了 Observer 的抽象类：Subscriber
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
                Log.d(TAG, "========================");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext : " + s);
            }
        };

        /**
         * 传入了一个OnSubscribe对象作为参数，OnSubscribe会被存储在返回的Observable对象中，相当于一个计划表
         *  ***当Observable被订阅时，OnSubscribe的call()方法会自动被调用***
         *  注意: 不是在创建时就立即开始发送事件，而是被订阅时，即subscribe()方法执行时
         *  
         *  事件序列会依次触发（观察者Subscriber将会被调用二次 onNext() 和一次 onCompleted())
         *  形成被观察者向观察者的事件传递，即观察者模式
         */
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("第一个");
                subscriber.onNext("第二个");
                subscriber.onCompleted();
            }
        }).subscribe(subscriber);

    }



    /**
     * 使用just创建一个Observable [参数列表 提供1~10之间个同泛型参数]
     */
    private void justOperator() {
        Subscriber<Boolean> subscriber = new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
                Log.d(TAG, "========================");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onNext(Boolean s) {
                Log.d(TAG, "onNext : " + s);
            }
        };
        // 相当于依次调用：
        // onNext(true);
        // onNext(false);
        // onCompleted();
        Observable.just(true, false).subscribe(subscriber);
    }


    /**
     * 使用from创建一个Observable [可以存放集合或数组]
     */
    private void fromOperator() {
        Subscriber<Student> subscriber = new Subscriber<Student>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
                Log.d(TAG, "========================");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onNext(Student s) {
                Log.d(TAG, "onNext : " + s.getName());
            }
        };

        ArrayList<Student> students = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            students.add(new Student("学生" + i));
        }
        // 相当于将传入的数组或 Iterable 拆分成具体对象后，依次发送出来
        Observable.from(students).subscribe(subscriber);
    }

    /**
     * 使用interval创建一个按固定时间间隔发射Long序列的Observable [每隔3秒发射]
     */
    private void intervalOperator() {
        Observable.interval(3, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.d(TAG, "interval:" + aLong.intValue());
                    }
                });
    }

    /**
     * 使用range创建一个发射指定范围的int序列的Observable [从3开始一共10次的发射]
     * 可代替循环
     * <p>
     * 使用repeat来设置执行次数
     */
    private void rangeOperator() {
        Observable.range(3, 10)
                .repeat(2)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "range:" + integer);
                    }
                });
    }

    /**
     * 异步加载
     * 获取要发送的数据的代码只会在有Observer订阅之后执行。
     * 获取数据的代码可以在子线程中执行。
     *
     * 返回值使用Subscription接收 待生命周期结束时 使用unsubscribe销毁
     */
    private void fromCallOperator() {
        Subscription mTvShowSubscription = Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() {
                Log.d(TAG, "Observable: " + Thread.currentThread().getName());
                Log.d(TAG, "发射" + 1);
                return 1;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "observer: " + Thread.currentThread().getName());
                        Log.d(TAG, "收到" + integer);
                    }
                });

    }
}
