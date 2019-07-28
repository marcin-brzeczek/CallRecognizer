package com.wepa.callrecognizer.main

import com.wepa.callrecognizer.model.ContactModel

class MainContract {

    interface PresenterInterface {
        fun getContacts()
        fun stop()
    }

    interface ViewInterface {
        fun displayResult(contacts: ArrayList<ContactModel>)
        fun displayError(message: String)
    }
}