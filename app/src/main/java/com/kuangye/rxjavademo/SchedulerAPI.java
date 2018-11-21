package com.kuangye.rxjavademo;


import android.util.Log;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * RxJava中，Scheduler相当于线程控制器 
 *      指定每一段代码应该运行在什么样的线程
 * 
 * 在不指定线程的情况下， RxJava遵循线程不变原则:
 *      哪个线程调用subscribe()，就在哪个线程生产事件
 *      哪个线程生产事件，就在哪个线程消费事件
 *      
 * 需要切换线程，就要用到Scheduler调度器
 * RxJava内置Scheduler:
 * 
 *      Schedulers.immediate(): 直接在当前线程运行，默认Scheduler
 *      Schedulers.newThread(): 总是启用新线程，并在新线程执行操作
 *      Schedulers.io(): I/O 操作（读写文件、数据库、网络交互等）
 *             同newThread()区别在于io()的内部实现了一个无数量上限的线程池，可重用空闲线程，
 *             多数情况下io() 比 newThread() 更有效率。
 *             注意: 不要把计算工作放在io()中，避免创建不必要的线程
 *             
 *      Schedulers.computation(): 计算所使用的 Scheduler。即不会被I/O等操作限制性能的操作，例如图形计算。使用的固定的线程池，大小为CPU核数
 *              注意: 不要把 I/O 操作放在computation()中，否则I/O操作的等待时间会浪费CPU
 *      AndroidSchedulers.mainThread()，指定的操作在Android主线程运行
 * 
 * 
 * 
 * subscribeOn(): 指定subscribe()所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程 (事件生产线程)
 * observeOn():   指定Subscriber所运行在的线程。(事件消费线程)
 * 
 * @author shijie9
 */
public enum SchedulerAPI {
    /**
     * 单例
     */
    INSTANCE;
    private static final String TAG = "SchedulerAPI";

    /**
     *  1、2、3、4 将会在 IO 线程发出
     *  数字的打印将发生在主线程 
     */
    public void scheduler(){
        Observable.just(1,2,3,4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG,"number = " + integer);     
                    }
                });
    }
}
