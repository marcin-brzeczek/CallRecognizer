package com.wepa.callrecognizer.call

import com.wepa.callrecognizer.model.ContactsRequest

class CallContract {

    interface PresenterInterface {
        fun getContactbyPhoneNumber(phoneNumber:String)
        fun stop()
    }

    interface ViewInterface {
        fun displayContact(contact: ContactsRequest?)
        fun displayError(message: String)
    }
}