package com.kuangye.rxjavademo.operator;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class DoOperator {
    private static final String TAG = "DoOperator";
    private DoOperator(){}
    
    private static class DoOperatorHolder{
        private static final DoOperator mInstance = new DoOperator();
    }
    
    public static DoOperator getInstance(){
        return DoOperatorHolder.mInstance;
    }

    /**
     * do系列操作符: 为Observable的生命周期注册一个回调 会先执行
     */
    public void doOperator() {
        Log.d(TAG,"=================doOnNextOperator=================");
        doOnNextOperator();
    }


    private void doOnNextOperator() {
        Observable.just(1, 2)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "call: " + integer);
                    }
                }).subscribe(new Subscriber<Integer>() {
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
}
