package com.pt.customer.ui


import com.vaadin.annotations.Theme
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewDisplay
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.spring.annotation.SpringViewDisplay
import com.vaadin.ui.Component
import com.vaadin.ui.Panel
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout

@Theme("valo")
@SpringUI
@SpringViewDisplay
class MyVaadinUi : UI(), ViewDisplay {

    private var springViewDisplay: Panel? = null

    override fun init(vaadinRequest: VaadinRequest) {
        val root = VerticalLayout()
        root.setSizeFull()
        content = root

        springViewDisplay = Panel("Customers")
        springViewDisplay!!.setSizeFull()
        root.addComponent(springViewDisplay)
        root.setExpandRatio(springViewDisplay, 1.0f)
    }

    override fun showView(view: View) {
        springViewDisplay!!.content = view as Component
    }
}
