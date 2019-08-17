package com.aaron.base.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 所有 Fragment 的基类，理论上应该都继承于它，统一管理。
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class BaseFragment extends Fragment implements View.OnTouchListener, IBaseFragment {

    @AnimRes @AnimatorRes private static int sEnter    = -1;
    @AnimRes @AnimatorRes private static int sExit     = -1;
    @AnimRes @AnimatorRes private static int sPopEnter = -1;
    @AnimRes @AnimatorRes private static int sPopExit  = -1;

    @Nullable protected BaseActivity mActivity;
    protected FragmentManager mFragmentManager;
    protected FragmentManager mChildFragmentManager;

    /**
     * 添加 Fragment ，原 Fragment 不被销毁并且伴随动画效果，有返回栈
     *
     * @param manager     FragmentManager
     * @param fragment    要添加的 Fragment
     * @param containerId Fragment 所属父布局 ID
     */
    public static void addFragment(FragmentManager manager, Fragment fragment, int containerId) {
        FragmentTransaction transaction = manager.beginTransaction();
        if (sEnter != -1 || sExit != -1 || sPopEnter != -1 || sPopExit != -1) {
            transaction.setCustomAnimations(sEnter, sExit, sPopEnter, sPopExit);
        }
        transaction.add(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * 添加 Fragment ，原 Fragment 不被销毁并且伴随动画效果，有返回栈
     *
     * @param manager     FragmentManager
     * @param fragment    要添加的 Fragment
     * @param containerId Fragment 所属父布局 ID
     * @param anim        动画数组，顺序：enter, exit, popEnter, popExit
     */
    public static void addFragment(FragmentManager manager, Fragment fragment, int containerId, int[] anim) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3])
                .add(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * 添加 Fragment ，原 Fragment 被销毁并且无动画效果，无返回栈
     *
     * @param manager       FragmentManager
     * @param fragment      要添加的 Fragment
     * @param containerId   Fragment 所属父布局 ID
     */
    public static void replaceFragment(FragmentManager manager, Fragment fragment, int containerId) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerId, fragment).commit();
    }

    /**
     * 弹出 Fragment ，无动画效果
     *
     * @param manager  FragmentManager
     * @param fragment 要移除的 Fragment
     */
    public static void popFragment(FragmentManager manager, Fragment fragment) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    public BaseFragment() {

    }

    @Override
    public void lazyLoad() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible()) lazyLoad(); // 实现懒加载
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) mActivity = (BaseActivity) context;
        mFragmentManager = getFragmentManager();
        mChildFragmentManager = getChildFragmentManager();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) lazyLoad(); // 实现懒加载
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(this); // 防止因多个 Fragment 叠加导致点击穿透
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true; // 防止因多个 Fragment 叠加导致点击穿透
    }

    @Override
    public void startActivity(Intent intent) {
        // 如果 BaseActivity 不为空则会使用 BaseActivity 中指定的过渡动画
        if (mActivity != null) mActivity.startActivity(intent);
    }

    @Override
    public void setTransitionAnim(int enter, int exit, int popEnter, int popExit) {
        sEnter = enter;
        sExit = exit;
        sPopEnter = popEnter;
        sPopExit = popExit;
    }
}
