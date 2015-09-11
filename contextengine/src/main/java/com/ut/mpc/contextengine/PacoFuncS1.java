package com.ut.mpc.contextengine;

import rx.functions.Function;

/**
 * Created by nathanielwendt on 8/20/15.
 */
public interface PacoFuncS1<T, R> extends Function {
    R call(T t);
}


