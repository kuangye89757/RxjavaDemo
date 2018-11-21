package com.kuangye.rxjavademo.operator;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * RxJava 是提供了多个ActionX形式的接口 (例如 Action2, Action3)的，它们可被用以包装不同的无返回值的方法
 *      后面的数字代表回调参数类型的个数
 * 
 * @author shijie9
 */
public class ActionOperator {
    private static final String TAG = "ActionOperator";
    private ActionOperator(){}
    
    private static class ActionOperatorHolder{
        private static final ActionOperator mInstance = new ActionOperator();
    }
    
    public static ActionOperator getInstance(){
        return ActionOperatorHolder.mInstance;
    }

    /**
     * Action系列
     */
    public void actionOperator() {
        actionMethod();
    }


    /**
     * 
     */
    private void actionMethod() {
        Action1 nextAction = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG, "nextAction:" + s);
            }
        };


        Action1 errorAction = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.d(TAG, "errorAction:" + throwable.getMessage());
            }
        };


        Action0 completeAction = new Action0() {
            @Override
            public void call() {
                Log.d(TAG, "completeAction");
                Log.d(TAG, "========================");
            }
        };

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //这里只会回调completeAction 
//                subscriber.onCompleted();
//                subscriber.onError(new IllegalAccessException());
//                subscriber.onNext("action1");


                //这里不会回调errorAction
//                subscriber.onNext("action1");
//                subscriber.onCompleted();
//                subscriber.onError(new IllegalAccessException());

                //这里不会回调completeAction
                subscriber.onNext("action1");
                subscriber.onError(new IllegalAccessException());
                subscriber.onCompleted();
            }
        }).subscribe(nextAction, errorAction, completeAction);
    }
}
