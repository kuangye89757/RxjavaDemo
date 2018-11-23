package com.kuangye.rxjavademo.operator;

import android.os.SystemClock;
import android.util.Log;

import com.kuangye.rxjavademo.Student;
import com.kuangye.rxjavademo.Swordsman;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 
 * @author shijie9
 */
public class FilterOperator {
    private static final String TAG = "FilterOperator";
    
    private FilterOperator(){}
    
    private static class FilterOperatorHolder{
        private static final FilterOperator mInstance = new FilterOperator();
    }
    
    public static FilterOperator getInstance(){
        return FilterOperatorHolder.mInstance;
    }


    /**
     * 过滤操作符: 过滤和选择Observable发射满足条件的数据
     */
    public void filterOperator(ArrayList<Student> data) {
        Log.d(TAG,"=================filterOperator=================");
        filterOperator();
        Log.d(TAG,"=================elementAtOperator=================");
        elementAtOperator(data);
        Log.d(TAG,"=================distinctOperator=================");
        distinctOperator();
        Log.d(TAG,"=================skipAndTakeOperator=================");
        skipAndTakeOperator();
        Log.d(TAG,"=================throttleFirstOperator=================");
        throttleFirstOperator();
        Log.d(TAG,"=================throttleWithTimeoutOperator=================");
//        throttleWithTimeoutOperator();
    }



    /**
     * filter操作符是对Observable结果进行过滤后 满足条件的发射给订阅者
     */
    private void filterOperator() {
        Observable.just(1, 2, 3, 4)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 2;
                    }
                }).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "filter为" + integer);
                    }
                });
    }

    /**
     * elementAt操作符返回指定位置的数据
     * 
     * elementAtOrDefault操作符
     * 索引值大于数据项数，会发射一个默认值而不是异常 但若是负数索引
     * 会抛出一个IndexOutOfBoundsException异常
     * https://blog.csdn.net/nicolelili1/article/details/52116931   
     */
    private void elementAtOperator(ArrayList<Student> data) {
        Observable.from(data)
                //                .elementAt(2)
                .elementAtOrDefault(data.size(), data.get(0))
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        Log.d(TAG, "elementAt为" + student);
                    }
                });
    }

    /**
     * distinct操作符用于去重 只允许没有发射过的数据发射 内部使用set去重
     * 
     * distinctUntilChanged操作符过滤掉连续重复数据
     * 
     * https://blog.csdn.net/hjjdehao/article/details/69382118
     */
    private void distinctOperator() {
        Swordsman s1 = new Swordsman("name", "A");
        Swordsman s3 = new Swordsman("name1", "A");
        Swordsman s2 = new Swordsman("name", "A");

        Observable.just(s1, s2, s3)
                //                .distinct() 
                .distinct(new Func1<Swordsman, Boolean>() {
                    @Override
                    public Boolean call(Swordsman swordsman) {
                        //返回值为key用于去重比较
                        return "A".equals(swordsman.getLevel());
                    }
                })
                .subscribe(new Action1<Swordsman>() {
                    @Override
                    public void call(Swordsman swordsman) {
                        Log.d(TAG, "distinct为" + swordsman);
                    }
                });
    }

    /**
     * skip操作符 忽略
     * take操作符 只取
     * IgnoreElements操作符 抑制原始Observable发射的所有数据，只允许它的终止通知（onError或onCompleted）通过
     * 如果你不关心一个Observable发射的数据，但是希望在它完成时或遇到错误终止时收到通知
     */
    private void skipAndTakeOperator() {
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .skip(3) //忽略前3个
                .take(2) //只取2个
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        return Observable.just(integer);
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d(TAG, "call为" + integer);
            }
        });

    }

    /**
     * 抖动过滤，例如按钮的点击监听器
     * 
     * throttleFirst操作符定期发射这个时间段的第一个数据
     * throwttleLast操作符定期发射这个时间段的最后一项数据
     */
    private void throttleFirstOperator() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "completeAction");
                        Log.d(TAG, "========================");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                    }


                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "throttleFirst " + aLong.intValue());
                    }
                });
    }


    /**
     * throttleWithTimeout根据指定的时间间隔进行限流
     */
    private void throttleWithTimeoutOperator() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 10; i++) {
                    int sleep = 100;
                    subscriber.onNext(i);
                    if (i % 3 == 0) {
                        sleep = 300;
                    }
                    SystemClock.sleep(sleep);
                }
                subscriber.onCompleted();
            }
        })
                //小于200ms的数据项舍弃
                .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "completeAction");
                        Log.d(TAG, "========================");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "throttleFirst " + integer);
                    }
                });
    }
}
