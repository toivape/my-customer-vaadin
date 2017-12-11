package com.pt.customer.ui

import java.text.SimpleDateFormat

import javax.annotation.PostConstruct

import org.springframework.beans.factory.annotation.Autowired
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.navigator.View
import com.vaadin.server.FontAwesome
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.renderers.ButtonRenderer
import com.vaadin.ui.renderers.DateRenderer
import com.vaadin.ui.Window

import org.slf4j.LoggerFactory

@SpringView(name = "")
class DefaultView : VerticalLayout(), View{

    var log = LoggerFactory.getLogger(DefaultView::class.java)

    companion object {
        const val GRID_WIDTH = "900px"
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

    @Autowired
    lateinit private var customerService:CustomerService

    private var grid = Grid<Customer>()

    private val searchField: TextField = TextField()

    @PostConstruct
    fun init(){
        log.info(">>> Initializing DefaultView")
        val searchLayout = HorizontalLayout()
        searchField.placeholder = "Last name"
        searchLayout.addComponent(searchField)

        // Create Search -button
        val searchButton = Button("Search")
        searchButton.icon = FontAwesome.SEARCH
        searchButton.isEnabled = false
        searchButton.setClickShortcut(KeyCode.ENTER)
        searchField.addValueChangeListener({ e -> searchButton.isEnabled = !e.value.isEmpty() })
        searchLayout.addComponent(searchButton)

        // Create Add new -button
        val topLayout = HorizontalLayout()
        topLayout.setWidth(GRID_WIDTH)
        topLayout.addComponent(searchLayout)
        topLayout.setComponentAlignment(searchLayout, Alignment.BOTTOM_LEFT)
        val addCustomerButton = Button("Add customer") { _ -> addCustomer() }
        topLayout.addComponent(addCustomerButton)
        topLayout.setComponentAlignment(addCustomerButton, Alignment.BOTTOM_RIGHT)

        addComponent(topLayout)

        // Setup customer table
        grid.setWidth(GRID_WIDTH)
        grid.setItems(listOf<Customer>())
        grid.addColumn({it.lastName}).caption = "Last name"
        grid.addColumn({it.firstName}).caption = "First name"
        grid.addColumn({it.emailAddress}).caption = "Email"
        grid.addColumn({it.birthDate}, getDateRenderer() ).caption = "Birth Date"

        grid.addColumn({ _ -> "Edit" },
                ButtonRenderer { clickEvent -> editCustomer(clickEvent.item as Customer) })
        grid.addColumn({ _ -> "Del" },
                ButtonRenderer { clickEvent -> deleteCustomer(clickEvent.item as Customer) })
        addComponent(grid)

        searchButton.addClickListener { _ -> findByName(searchField, grid) }
    }

    private fun getDateRenderer() = DateRenderer(SimpleDateFormat(DATE_FORMAT))

    private fun findByName(searchField:TextField, grid : Grid<Customer>){
        val name = searchField.value.trim()
        if (name.isNotEmpty()){
            val customerList = customerService.findByName(name)
            log.info(">>> Found ${customerList.size} customers with name [$name]")
            grid.setItems(customerList)
        }
    }

    private fun deleteCustomer(c:Customer){
        log.info(">>> Delete customer clicked $c")
        val msg = "Delete customer ${c.firstName} ${c.lastName}?"
        showWindow(ConfirmDialog<Customer>(msg, c, this::deleteCustomerCallback));
    }

    private fun addCustomer() {
        log.info(">>> Add customer clicked")
        showCustomerWindow(Customer())
    }

    private fun editCustomer(c:Customer){
        log.info(">>> Edit customer clicked $c")
        val customer = customerService.getCustomer(c.id)
        showCustomerWindow(customer?:c)
    }

    private fun showCustomerWindow(c: Customer) {
        log.info(">>> showCustomerWindow $c")
        showWindow(CustomerWindow(c, this::saveCustomerCallback))
    }

    private fun deleteCustomerCallback(c:Customer){
        customerService.deleteCustomer(c.id!!)
        refreshCustomerTable()
    }

    private fun saveCustomerCallback(c:Customer){
        log.info(">>> Saving customer $c")
        customerService.saveCustomer(c)
        refreshCustomerTable()
    }

    private fun refreshCustomerTable() = findByName(searchField, grid)

    private fun showWindow(w:Window) = UI.getCurrent().addWindow(w)
}
