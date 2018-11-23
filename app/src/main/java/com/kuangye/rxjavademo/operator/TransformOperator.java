package com.kuangye.rxjavademo.operator;

import android.util.Log;

import com.kuangye.rxjavademo.Student;
import com.kuangye.rxjavademo.Student.Course;
import com.kuangye.rxjavademo.Swordsman;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

/**
 * 变换，将事件序列中的对象或整个序列进行加工，转换成不同的事件或事件序列
 *          对Observable发射的数据按一定规则变换
 *          
 *          
 * Func1 和 Action 的区别:
 *      FuncX   包装的是有返回值的方法 (参数列表1,..参数列表9,返回值类型)
 *      ActionX 包装的是无返回值的方法
 * 
 * 原理:针对事件序列的处理和再发送
 *      public <R> Observable<R> lift(Operator<? extends R, ? super T> operator) {
 *          return Observable.create(new OnSubscribe<R>() {
 *              @Override
 *              public void call(Subscriber subscriber) {
 *                  Subscriber newSubscriber = operator.call(subscriber);
 *                  newSubscriber.onStart();
 *                  onSubscribe.call(newSubscriber);
 *              }
 *          });
 *       }
 *      
 *      //结合subscribe()一起看
 *      public Subscription subscribe(Subscriber subscriber) {
 *            subscriber.onStart();
 *            onSubscribe.call(subscriber);
 *            return subscriber;
 *      }
 *      
 *      建议尽量使用已有的 lift() 包装方法（如 map() flatMap() 等）进行组合来实现需求
 * 
 * @author shijie9
 */
public class TransformOperator {
    private static final String TAG = "TransformOperator";

    private TransformOperator() {
    }

    private static class TransformOperatorHolder {
        private static final TransformOperator mInstance = new TransformOperator();
    }

    public static TransformOperator getInstance() {
        return TransformOperatorHolder.mInstance;
    }


    /**变换操作符: 对Observable发射的数据按一定规则变换*/
    public void transformOperator(ArrayList<Student> data) {
        Log.d(TAG,"=================mapOperator=================");
        mapOperator(data);
        Log.d(TAG,"=================flatMapOperator=================");
        flatMapOperator(data);
        Log.d(TAG,"=================flatMapIterableOperator=================");
        flatMapIterableOperator();
        Log.d(TAG,"=================bufferOperator=================");
        bufferOperator();
        Log.d(TAG,"=================groupByOperator=================");
        groupByOperator();
        Log.d(TAG,"=================singleOperator=================");
        singleOperator();
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
     * 
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
     *      flatMap的合并允许交叉 最终结果可能顺序不是发射顺序  可使用concatMap
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
     * 
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
     * single操作符 只发射一个值会正常执行
     */
    private void singleOperator() {
        Integer[] items = {6};
        Observable.from(items).single().subscribe(new Subscriber<Integer>() {
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
