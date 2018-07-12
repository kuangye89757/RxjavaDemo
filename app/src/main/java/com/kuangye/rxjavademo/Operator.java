package com.kuangye.rxjavademo;

import android.os.SystemClock;
import android.util.Log;

import com.kuangye.rxjavademo.Student.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;


/**
 * 操作符
 * <p>
 * subscribeOn操作符: 用于指定Observable所运行的线程
 * ObserverOn操作符: 用于指定Observer所运行的线程 发射出的数据在哪个线程使用 一般为UI线程
 *
 * @author shijie9
 */
public class Operator {
    private static final String TAG = "Operator";

    /**
     * 创建操作符: 用于创建不同形式的Observable
     */
    public void generateOperator() {
//        createOperator();
//        justOperator();
//        fromOperator();
//        intervalOperator();
//        rangeOperator();
        fromCallOperator();
    }


    /**
     * Action系列
     */
    public void actionOperator() {
        actionMethod();
    }


    /**变换操作符: 对Observable发射的数据按一定规则变换*/
    /**
     * Func系列
     */
    public void transformOperator(ArrayList<Student> data) {
        mapOperator(data);
        flatMapOperator(data);
        flatMapIterableOperator();
        bufferOperator();
        groupByOperator();

    }

    /**
     * 过滤操作符: 过滤和选择Observable发射满足条件的数据
     */
    public void filterOperator(ArrayList<Student> data) {
        filterOperator();
        elementAtOperator(data);
        distinctOperator();
        skipAndTakeOperator();
        throttleFirstOperator();
        throttleWithTimeoutOperator();
    }


    /**
     * 组合操作符: 同时处理多个Observable来创建新的Observable
     */
    public void combinationOperator() {
        startWithOperator();
        mergeAndConcatOperator();
        singleOperator();
        zipOperator();
    }

    /**
     * 辅助操作符
     */
    public void assistOperator() {
        delayOperator();
        timeoutOperator();
    }

    /**
     * do系列操作符: 为Observable的生命周期注册一个回调
     */
    public void doOperator() {
        doOnNextOperator();
    }

    /**
     * 内置的Scheduler调度器
     * 不指定线程,默认是在subscribe方法的线程上进行回调
     * 1.Schedulers.immediate() -- 当前线程运行 timeout/timeInterval,timestamp默认调度器
     * 2.Schedulers.newThread() -- 新线程运行
     * 3.Schedulers.io() -- 读写文件/数据库/网络请求所使用的调度器
     * 不同于newThread()的是使用了线程池
     * 4.Schedulers.computation() -- 使用固定线程池 大小CPU核数  不要将IO操作用于CPU
     * buffer/debounce/delay/interval/sample/skip默认调度器
     * 5.Schedulers.trampoline() -- 在当前线程中以队列方式执行任务    trampoline()入队操作
     * retry/repeat默认调度器
     * 6.AndroidSchedulers.mainThread() -- RxAndroid中提供用于主线程
     */
    public void threadOperator() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.d(TAG, "Observable: " + Thread.currentThread().getName());
                subscriber.onNext(1);
                Log.d(TAG, "发射" + 1);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "observer: " + Thread.currentThread().getName());
                        Log.d(TAG, "收到" + integer);
                    }
                });
    }


    /**
     * 错误处理操作符: 出现错误时会执行Subscriber订阅者的onError方法 为了避免多个订阅者处理同一错误
     */
    public void errorOperator() {
        catchOperator();
        retryOperator();
    }

    /**
     * 布尔操作符
     */
    public void booleOperator() {
        allOperator();
    }

    /**
     * 条件操作符
     */
    public void conditionsOperator() {
        //amb操作符 多个Observable中只发射首先需要发射的Observable的数据
        //defaultIfEmpty操作符 若Observable没有数据发射则使用默认数据
    }


    /**
     * 转换操作符
     */
    public void conversionOperator() {
        //toList操作符 将发射的多项且每项都执行onNext的数据 组合成一个List
        //toSortedList操作符 同上 默认升序 若数据源未实现Comparable接口会抛出异常
        //              未实现Comparable接口可通过toSortedList(Func2)进行比较  
        //toMap操作符  同上 默认转化为Hashmap
        toMapOperator();
    }


    private void toMapOperator() {
        Swordsman s1 = new Swordsman("韦一笑", "A");
        Swordsman s2 = new Swordsman("张三丰", "SS");
        Swordsman s3 = new Swordsman("周芷若", "S");

        Observable.just(s1, s2, s3).toMap(new Func1<Swordsman, String>() {
            @Override
            public String call(Swordsman swordsman) {
                //作为key
                return swordsman.getLevel();
            }
        }).subscribe(new Action1<Map<String, Swordsman>>() {
            @Override
            public void call(Map<String, Swordsman> stringSwordsmanMap) {
                Log.d(TAG, stringSwordsmanMap.get("SS").getName());
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


    /**
     * do操作符 会先执行
     */
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
     * 不同线程会导致数据交错
     * <p>
     * 使用concat操作符 可以保证顺序
     */
    private void mergeAndConcatOperator() {
        Observable<Integer> obs1 = Observable.just(3, 4, 5).subscribeOn(Schedulers.io());
        Observable<Integer> obs2 = Observable.just(4, 5, 6);
        Observable.merge(obs1, obs2)
//        Observable.concat(obs1,obs2)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "merge:" + integer);
                    }
                });
    }


    /**
     * single操作符 只发射一个值会正常执行
     */
    private void singleOperator() {
        Integer[] items = {6};
        Observable myObservable = Observable.from(items)
                .single();
        Subscriber<Integer> mySubscriber = new Subscriber<Integer>() {
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
        };
        myObservable.subscribe(mySubscriber);
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
        })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String integer) {
                        Log.d(TAG, "zip:" + integer);
                    }
                });
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
     * elementAtOrDefault操作符
     * 索引值大于数据项数，会发射一个默认值而不是异常 但若是负数索引
     * 会抛出一个IndexOutOfBoundsException异常
     * https://blog.csdn.net/nicolelili1/article/details/52116931
     */
    private void elementAtOperator(ArrayList<Student> data) {
        Observable.from(data)
//                .elementAt(2)
                .elementAtOrDefault(11, data.get(0))
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        Log.d(TAG, "elementAt为" + student);
                    }
                });
    }

    /**
     * distinct操作符用于去重 只允许没有发射过的数据发射 内部使用set去重
     * distinctUntilChanged操作符过滤掉连续重复数据
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


    /**
     * 指定一个Func对象,将Observable转换成一个新的Observable并发射,观察者将接收到新的Observable的处理
     */
    private void mapOperator(ArrayList<Student> data) {
        Observable.from(data)
                .map(new Func1<Student, List<Course>>() {
                    @Override
                    public List<Course> call(Student student) {
                        Log.d(TAG, "call为:" + student.getName());
                        return student.getCourses();
                    }
                }).subscribe(new Action1<List<Course>>() {
            @Override
            public void call(List<Course> courseList) {
                for (Course course : courseList) {
                    Log.d(TAG, "map为:" + course.getCourseName());
                }
            }
        });

    }

    /**
     * 1. 使用传入的事件对象创建一个 Observable 对象；
     * 2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
     * 3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，
     * 而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法
     * <p>
     * 和 map() 相同的是， 都依赖FuncX(入参，返回值)进行转换 后直接被subscribe
     * 和 map() 不同的是，
     * 1. map返回的是结果集，(map返回结果集不能直接使用from/just再次进行事件分发)
     * flatmap返回的是包含结果集的Observable
     * <p>
     * 2. map被订阅时每传递一个事件执行一次onNext方法，只能单一转换，单一只的是只能一对一进行转换
     * flatmap多用于多对多，一对多，再被转化为多个时，一般利用from/just进行一一分发，
     * 被订阅时将所有数据传递完毕汇总到一个Observable后  再一一执行onNext方法（执行顺序不同）
     * <p>
     * 如单纯用于一对一转换则和map相同
     * <p>
     * <p>
     * flatMap的合并允许交叉 最终结果可能顺序不是发射顺序  可使用concatMap
     */
    private void flatMapOperator(ArrayList<Student> data) {
        Observable.from(data)
                .flatMap(new Func1<Student, Observable<Course>>() {
                    @Override
                    public Observable<Course> call(Student student) {
                        Log.d(TAG, "call为:" + student.getName());
                        return Observable.from(student.getCourses());
                    }
                }).subscribe(new Action1<Course>() {
            @Override
            public void call(Course course) {
                Log.d(TAG, "flatMap为:" + course.getCourseName());
            }
        });
    }

    /**
     * flatMapIterable可以将数据包装成Iterable发射
     */
    private void flatMapIterableOperator() {
        Observable.just(1, 2, 3)
                .flatMapIterable(new Func1<Integer, Iterable<Student>>() {
                    @Override
                    public Iterable<Student> call(Integer integer) {
                        List<Student> mList = new ArrayList<>();

                        Student student = new Student("男学生" + integer);
                        ArrayList<Course> courses = new ArrayList<>();
                        courses.add(new Course("课程1"));
                        courses.add(new Course("课程2"));
                        student.setCourses(courses);

                        Student student1 = new Student("女学生" + integer);
                        ArrayList<Course> courses1 = new ArrayList<>();
                        courses1.add(new Course("课程3"));
                        courses1.add(new Course("课程4"));
                        student1.setCourses(courses1);

                        mList.add(student);
                        mList.add(student1);
                        return mList;
                    }
                }).subscribe(new Action1<Student>() {
            @Override
            public void call(Student stu) {
                Log.d(TAG, "flatMapIterable为:" + stu.toString());
            }
        });
    }

    /**
     * buffer将新转换的Observable以缓存数按批次发射
     * 能一次性集齐多个结果到列表中，订阅后自动清空相应结果,直到完全清除
     * https://blog.csdn.net/axuanqq/article/details/50698532
     */
    private void bufferOperator() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .buffer(3)
                .subscribe(new Observer<List<Integer>>() {
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
                    public void onNext(List<Integer> integers) {
                        Log.d(TAG, "onNext " + integers);
                    }
                });
    }

    /**
     * groupBy操作符可以对key进行分组(这里key是swordsman.getLevel())
     * concat操作符是接收若干个Observables，发射数据是有序的，不会交叉
     */
    private void groupByOperator() {
        Swordsman s1 = new Swordsman("韦一笑", "A");
        Swordsman s2 = new Swordsman("张三丰", "SS");
        Swordsman s3 = new Swordsman("周芷若", "S");
        Swordsman s4 = new Swordsman("宋远桥", "S");
        Swordsman s5 = new Swordsman("殷梨亭", "A");
        Swordsman s6 = new Swordsman("张无忌", "SS");
        Swordsman s7 = new Swordsman("鹤笔翁", "S");
        Swordsman s8 = new Swordsman("宋青书", "A");

        Observable<GroupedObservable<String, Swordsman>> groupedObservableObservable =
                Observable.just(s1, s2, s3, s4, s5, s6, s7, s8)
                        .groupBy(new Func1<Swordsman, String>() {
                            @Override
                            public String call(Swordsman swordsman) {
                                return swordsman.getLevel();
                            }
                        });
        Observable.concat(groupedObservableObservable).subscribe(new Action1<Swordsman>() {
            @Override
            public void call(Swordsman swordsman) {
                Log.d(TAG, "groupBy:" + swordsman.getName() + "===" + swordsman.getLevel());
            }
        });
    }


    /**
     * 后面的数字代表回调参数类型的个数
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

        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
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
        });
        observable.subscribe(nextAction, errorAction, completeAction);
    }


    /**
     * 使用create创建一个Observable
     */
    private void createOperator() {
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
     * 使用just创建一个Observable [提供1~10之间个同泛型参数]
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
