package com.kuangye.rxjavademo.operator;

import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 延时、超时、错误异常、条件、包含等辅助操作
 * @author shijie9
 */
public class AssistOperator {
    private static final String TAG = "AssistOperator";
    private AssistOperator(){}
    
    private static class AssistOperatorHolder{
        private static final AssistOperator mInstance = new AssistOperator();
    }
    
    public static AssistOperator getInstance(){
        return AssistOperatorHolder.mInstance;
    }

    /**
     * 辅助操作符
     */
    public void assistOperator() {
        Log.d(TAG,"=================delayOperator=================");
        delayOperator();
        Log.d(TAG,"=================timeoutOperator=================");
        timeoutOperator();
        Log.d(TAG,"=================catchOperator=================");
        catchOperator();
        Log.d(TAG,"=================retryOperator=================");
        retryOperator();
        Log.d(TAG,"=================allOperator=================");
        allOperator();
    }




    /**
     * delay操作符 延时发射
     */
    private void delayOperator() {
        Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                Long currentTime = SystemClock.currentThreadTimeMillis() / 1000;
                subscriber.onNext(currentTime);
            }
        }).delay(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.d(TAG, "delay:" + aLong);
                    }
                });
    }

    /**
     * timeout操作符 超时发射的会执行到onError 或执行备用Observable代替
     */
    private void timeoutOperator() {
        Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                Long currentTime = SystemClock.currentThreadTimeMillis() / 1000;
                subscriber.onNext(currentTime);
            }
        }).timeout(2, TimeUnit.SECONDS)
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    Log.d(TAG, "delay:" + aLong);
                }
            });
    }



    /**
     * catch操作符 拦截Observable的onError方法
     * onErrorReturn 发生错误时返回备用的Observable发射特殊数据项并调用onCompleted 从而忽略onError调用
     * onErrorResumeNext 同上 发射备用的Observable的数据
     * onExceptionResumeNext  同上 但若onError的Throwable非Exception则调用onError  而不再使用备用Observable
     */
    private void catchOperator() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    if (i > 2) {
                        subscriber.onError(new Throwable("throwable"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).onErrorReturn(new Func1<Throwable, Integer>() {
            @Override
            public Integer call(Throwable throwable) {
                return 666;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext................." + integer);
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted.................");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError....................");
            }
        });
    }

    /**
     * retry操作符 当发生onError时尝试重新发射 会造成数据重复
     */
    private void retryOperator() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    if (i == 2) {
                        subscriber.onError(new Throwable("throwable"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).retry(2).subscribe(new Observer<Integer>() {
            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext................." + integer);
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted.................");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError....................");
            }
        });
    }


    /**
     * all操作符 全部满足时返回true 否则false
     * contains操作符 Observable发射的数据中包含该数据则返回true 否则返回false
     * isEmpty操作符 Observable没有数据发射过返回true 否则返回false
     */
    private void allOperator() {
        Observable.just(1, 2, 3, 4)
                .all(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        Log.d(TAG, "call " + integer);
                        return integer < 3;
                    }
                }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                System.out.println("onNext................." + aBoolean);
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted.................");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError....................");
            }
        });
    }
}
