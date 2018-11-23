package com.kuangye.rxjavademo.operator;

import android.util.Log;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * lift()    针对事件项和事件序列的
 * compose() 针对 Observable 自身进行变换
 *      多个 Observable 应用一组相同的 lift() 变换
 * 
 * 
 *      public class LiftAllTransformer implements Observable.Transformer<Integer, String> {
 *          @Override
 *          public Observable<String> call(Observable<Integer> observable) {
 *              return observable
 *                  .lift1()
 *                  .lift2()
 *                  .lift3()
 *                  .lift4();
 *              }
 *          }
 *      ...
 *      
 *      Transformer liftAll = new LiftAllTransformer();
 *      observable1.compose(liftAll).subscribe(subscriber1);
 *      observable2.compose(liftAll).subscribe(subscriber2);
 *      observable3.compose(liftAll).subscribe(subscriber3);
 *      observable4.compose(liftAll).subscribe(subscriber4);
 * 
 * @author shijie9
 */
public class CombinationOperator {
    private static final String TAG = "CombinationOperator";
    private CombinationOperator(){}
    
    private static class CombinationOperatorHolder{
        private static final CombinationOperator mInstance = new CombinationOperator();
    }
    
    public static CombinationOperator getInstance(){
        return CombinationOperatorHolder.mInstance;
    }



    /**
     * 组合操作符: 同时处理多个Observable来创建新的Observable
     */
    public void combinationOperator() {
        Log.d(TAG,"=================startWithOperator=================");
        startWithOperator();
        Log.d(TAG,"=================mergeAndConcat=================");
        mergeAndConcatOperator();
        Log.d(TAG,"=================zipOperator=================");
        zipOperator();
    }


    /**
     * startWith操作符会在源Observable发射的数据前插入一些数据
     */
    private void startWithOperator() {
        Observable.just(3, 4, 5)
                .startWith(1, 2)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "startWith:" + integer);
                    }
                });

    }

    /**
     * merge操作符将多个Observable合并到一个Observable中进行发射
     * 
     * 不同线程会导致数据交错
     * <p>
     * 使用concat操作符 可以保证顺序
     */
    private void mergeAndConcatOperator() {
        Observable<Integer> obs1 = Observable.just(3, 4, 5).subscribeOn(Schedulers.io());
        Observable<Integer> obs2 = Observable.just(4, 5, 6);
        Observable.merge(obs1, obs2)
//      Observable.concat(obs1,obs2)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "merge:" + integer);
                    }
                });
    }


    

    /**
     * zip操作符将多个Observable合并到一个Observable中进行发射
     * 根据给定函数进行变换
     */
    private void zipOperator() {
        Observable<Integer> obs1 = Observable.just(3, 4, 5).subscribeOn(Schedulers.io());
        Observable<String> obs2 = Observable.just("4", "5", "6");
        Observable.zip(obs1, obs2, new Func2<Integer, String, String>() {
            @Override
            public String call(Integer integer, String s) {
                return integer + "##" + s;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String integer) {
                Log.d(TAG, "zip:" + integer);
            }
        });
    }
}
