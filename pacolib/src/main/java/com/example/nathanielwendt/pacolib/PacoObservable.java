package com.example.nathanielwendt.pacolib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Adaptation from Rx Observable to implement serializable nature
 * Note that only a few functions are supported at this time.
 * Also, the subscribe function is stored for later retrieval.
 */
public class PacoObservable<T> implements Serializable {

    private List<Func1<? super T, Boolean>> filterFuncs = new ArrayList<>();
    private List<Func1<? super T, ? extends R>> mapFuncs = new ArrayList<>();
    private Action1<? super T> subscribeFunc;

    public List<Func1<? super T, ? extends R>> getMapFuncs() {
        return mapFuncs;
    }

    public List<Func1<? super T, Boolean>> getFilterFuncs() {
        return filterFuncs;
    }

    public PacoObservable<T> filter(Func1<? super T, Boolean> func){
        filterFuncs.add(func);
        return this;
    }

    public PacoObservable<T> map(Func1<? super T, ? extends R> func) {
        mapFuncs.add(func);
        return this;
    }

    public final PacoObservable<T> subscribe(final Action1<? super T> onNext) {
        subscribeFunc = onNext;
        return this;
    }

    public Action1<? super T> getSubscribeFunc(){
        return subscribeFunc;
    }
}
