package com.project;

import com.project.OrderForm;
import org.statefulj.framework.core.annotations.StatefulController;
import org.statefulj.framework.core.annotations.Transition;
import org.statefulj.framework.core.annotations.Transitions;

import java.util.Date;

@StatefulController(startState = OrderForm.CREATED, clazz = OrderForm.class)
public class OrderFormStateController {
    public static final String STOP = "STOP";
    public static final String RESUME = "RESUME";
    public static final String FINISH = "FINISH";


    @Transition(from = OrderForm.CREATED, event = OrderFormStateController.STOP, to = OrderForm.STOPPED)
    public void onPause(OrderForm OrderForm) {

    }

    @Transition(from = OrderForm.STOPPED, event = OrderFormStateController.RESUME, to = OrderForm.CREATED)
    public void onResume(OrderForm OrderForm) {

    }

    @Transitions({
        @Transition(from = OrderForm.CREATED, event = OrderFormStateController.FINISH, to = OrderForm.FINISHED),
        @Transition(from = OrderForm.STOPPED, event = OrderFormStateController.FINISH, to = OrderForm.FINISHED)
    })
    public void onFinish(OrderForm OrderForm) {
        OrderForm.setFinished(new Date());
    }

}
