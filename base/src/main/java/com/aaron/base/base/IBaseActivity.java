package com.aaron.base.base;

/**
 * BaseActivity 扩展功能接口，详情看 {@link BaseActivity}。
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public interface IBaseActivity {

    /**
     * 是否禁止响应系统字体变化，子类调用
     *
     * @param yes 是否禁止系统可以修改 App 字体大小
     */
    void forbidScaleTextSize(boolean yes);
}
