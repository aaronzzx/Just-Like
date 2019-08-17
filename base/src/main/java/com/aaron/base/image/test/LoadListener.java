package com.aaron.base.image.test;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public interface LoadListener {

    boolean onSuccess(Object resource);

    boolean onFailure(Throwable e);
}
