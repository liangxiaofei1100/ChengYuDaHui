package com.zhaoyan.juyou.game.chengyudahui.view;


public enum  Effectstype {

    SlideBottom(SlideBottom.class);

    private Class<BaseEffects> effectsClass;
//
    private Effectstype(Class mclass) {
    	effectsClass = mclass;
    }

    public BaseEffects getAnimator() {
        try {
            return (BaseEffects) effectsClass.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}
