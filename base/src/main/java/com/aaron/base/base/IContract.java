package com.aaron.base.base;

/**
 * MVP 架构简单契约接口。
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public interface IContract {

    /**
     * Model 层，非必需
     */
    interface M {

    }

    /**
     * View 层，一般都是要的
     */
    interface V {
        void attachP();
    }

    /**
     * Presenter 层
     * @param <V> View 层
     * @param <M> Model 层
     */
    abstract class P<V extends IContract.V, M extends IContract.M> {
        protected V mV;
        protected M mM;

        public P(V v) {
            mV = v;
            mM = createM();
        }

        public abstract M createM();

        public abstract void detach();
    }
}
