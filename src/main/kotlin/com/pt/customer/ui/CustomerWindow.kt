package com.pt.customer.ui

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import com.vaadin.data.Binder
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.ui.Button
import com.vaadin.ui.DateField
import com.vaadin.ui.FormLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Panel
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import org.slf4j.LoggerFactory
import com.vaadin.data.ValueProvider
import com.vaadin.server.Setter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.vaadin.ui.Notification

class CustomerWindow(private val customer:Customer, private val saveOperation: (c:Customer)->Unit) : Window() {
    var log = LoggerFactory.getLogger(CustomerWindow::class.java)

    companion object {
        const val WINDOW_WIDTH = "500px"
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

    private var customerTo : CustomerTO? = null
    private val binder = Binder<CustomerTO>()

    init{
        log.info(">>> Running CustomerWindow initialize method")
        this.customerTo = CustomerTO(customer)

        isModal = true
        setWidth(WINDOW_WIDTH)
        center()

        val form = createFormAndBinder()
        val buttons = createSaveAndCloseButton()

        val layout = VerticalLayout()
        layout.addComponent(form)
        layout.addComponent(buttons)

        val panel = Panel(resolveCaption(customer))
        panel.content = layout
        this.content = panel
    }

    private fun resolveCaption(c:Customer) = if (c.id == null) "Add customer" else "Edit customer"

    private fun createSaveAndCloseButton():HorizontalLayout{
        val saveButton = Button("Save")  { _ -> saveCustomer() }
        saveButton.setClickShortcut(KeyCode.ENTER)
        val closeButton = Button("Close") { _ -> closeDialog() }

        val buttons = HorizontalLayout()
        buttons.addComponent(saveButton)
        buttons.addComponent(closeButton)
        return buttons
    }

    private fun createFormAndBinder():FormLayout{
        val form = FormLayout()
        form.setMargin(true)
        val firstName = newTextField("First name")
        form.addComponent(firstName)

        val lastName = newTextField("Last name")
        form.addComponent(lastName)

        val email = newTextField("Email", false)
        form.addComponent(email)

        val birthDate = DateField("Birth date")
        birthDate.dateFormat = DATE_FORMAT
        birthDate.placeholder = DATE_FORMAT
        birthDate.rangeStart = dateFromString("1950-01-01")
        birthDate.rangeEnd = yesterday()
        form.addComponent(birthDate)

        binder.forField(firstName)
                .asRequired("First name is required")
                .bind(ValueProvider<CustomerTO, String> { it.firstName }, Setter<CustomerTO, String> { obj, s -> obj.firstName = s })
        binder.forField(lastName)
                .asRequired("Last name is required")
                .bind(ValueProvider<CustomerTO, String> { it.lastName }, Setter<CustomerTO, String> { obj, s -> obj.lastName = s })
        binder.forField(email)
                .withValidator({ emailAddress -> isValidEmail(emailAddress) }, "This doesn't look like a valid email address")
                .bind(ValueProvider<CustomerTO, String> { it.emailAddress }, Setter<CustomerTO, String> { obj, s -> obj.emailAddress = s })
        binder.forField<LocalDate>(birthDate).bind(ValueProvider<CustomerTO, LocalDate>{ it.birthDate }, Setter<CustomerTO, LocalDate> { obj, s -> obj.birthDate = s })

        binder.readBean(customerTo)
        return form
    }

    private fun newTextField(caption:String, isRequired:Boolean = true):TextField{
        val text = TextField(caption)
        text.isRequiredIndicatorVisible = isRequired
        text.maxLength = 40
        return text
    }

    private fun isValidEmail(email:String) : Boolean{
       return EmailValidator().isValid(email, null)
    }

    private fun saveCustomer(){
        log.info(">>> Save customer button clicked")
        try {
            if (copyChangesToCustomer()) callSaveCustomerCallback()
            closeDialog()
        }catch(e : Exception){
            log.error("Failed to save customer ${e.message}", e)
            Notification.show("Save failed", e.message, Notification.Type.ERROR_MESSAGE)
        }
    }

    private fun callSaveCustomerCallback(){
        log.info(">>> Call save method")
        saveOperation(this.customer)
    }

    private fun copyChangesToCustomer():Boolean{
        log.info(">>> copyChangesToCustomer")
        if (binder.hasChanges()){
            binder.writeBean(customerTo)
            customer.readProperties(customerTo)
            log.info(">>> Customer has changed. New values: $customer")
            return true
        }
        log.info("No changes to customer")
        return false
    }

    private fun dateFromString(s:String):LocalDate = LocalDate.parse(s, DateTimeFormatter.ISO_DATE)

    private fun yesterday() = LocalDate.now().minusDays(1)

    private fun closeDialog(){
        close()
    }
}