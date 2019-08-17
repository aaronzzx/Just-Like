package com.aaron.base.base;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;

/**
 * BaseFragment 扩展功能接口，详情看 {@link BaseFragment}。
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public interface IBaseFragment {

    /**
     * 懒加载，基类空实现，子类选择实现
     */
    void lazyLoad();

    /**
     * 指定过渡动画
     *
     * @param enter    添加 Fragment 时的入场动画
     * @param exit     添加 Fragment 时的退场动画
     * @param popEnter 弹出 Fragment 时的入场动画
     * @param popExit  弹出 Fragment 时的退场动画
     */
    void setTransitionAnim(@AnimatorRes @AnimRes int enter,
                           @AnimatorRes @AnimRes int exit,
                           @AnimatorRes @AnimRes int popEnter,
                           @AnimatorRes @AnimRes int popExit);
}
