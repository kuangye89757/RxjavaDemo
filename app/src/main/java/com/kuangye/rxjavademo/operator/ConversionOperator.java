package com.kuangye.rxjavademo.operator;

import android.util.Log;

import com.kuangye.rxjavademo.Swordsman;

import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ConversionOperator {
    private static final String TAG = "ConversionOperator";
    private ConversionOperator(){}
    
    private static class ConversionOperatorHolder{
        private static final ConversionOperator mInstance = new ConversionOperator();
    }
    
    public static ConversionOperator getInstance(){
        return ConversionOperatorHolder.mInstance;
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
}
