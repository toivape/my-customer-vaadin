package com.pt.customer.ui

import com.vaadin.ui.*
import org.slf4j.LoggerFactory

class ConfirmDialog<T> (private val msg:String, private val attribute:T, private val confirmedOperation: (t:T) -> Unit) : Window() {
    var log = LoggerFactory.getLogger(ConfirmDialog::class.java)

    init{
        isModal = true
        setWidth("300px")
        center()

        val layout = VerticalLayout()
        val label = Label(msg)
        layout.addComponent(label)
        layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER)

        val buttons = HorizontalLayout(createYesButton(), createNoButton())
        layout.addComponent(buttons)
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER)

        this.content = Panel("Confirm operation", layout)
    }

    private fun createYesButton() = Button("Yes") { _ -> executeOperation() }

    private fun createNoButton() = Button("No") { _ -> closeDialog() }

    private fun executeOperation(){
        try {
            confirmedOperation(attribute)
            closeDialog()
        }catch(e:Exception){
            log.error("Operation failed + ${e.message}", e)
            Notification.show("Operation failed", e.message, Notification.Type.ERROR_MESSAGE)
        }
    }

    private fun closeDialog(){
        close()
    }
}