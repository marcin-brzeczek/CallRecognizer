package com.wepa.callrecognizer.call

import com.wepa.callrecognizer.model.ContactModel
import com.wepa.callrecognizer.model.ShortContactModel

class CallContract {

    interface PresenterInterface {
        fun getContacts()
        fun getContactbyPhoneNumber(phoneNumber:String)
        fun stop()
    }

    interface ViewInterface {
        fun displayContacts(contacts: ArrayList<ContactModel>)
        fun displayContact(contact: ShortContactModel)
        fun displayError(message: String)
    }
}