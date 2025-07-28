package com.cloudstuff.tictactoe.dagger;

import com.cloudstuff.tictactoe.activity.BaseActivity;
import com.cloudstuff.tictactoe.fragment.BaseFragment;

import dagger.Component;

@ViewScope
@Component(dependencies = AppComponent.class, modules = ViewModule.class)
public interface ViewComponent {

    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);
}
