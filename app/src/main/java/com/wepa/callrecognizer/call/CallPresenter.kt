package com.wepa.callrecognizer.call

import com.wepa.callrecognizer.model.ContactsRequest
import com.wepa.callrecognizer.network.ContactsApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class CallPresenter(private val viewInterface: CallContract.ViewInterface, private val contactsApi: ContactsApi) :
    CallContract.PresenterInterface {

    private val compositeDisposable = CompositeDisposable()

//    private val contactsObserver
//        get() = object : DisposableObserver<ContactsRequest>() {
//
//            override fun onComplete() {}
//
//            override fun onNext(contactsRequest: ContactsRequest) {
//                viewInterface.displayContacts(contactsRequest.data)
//            }
//            override fun onError(error: Throwable) = viewInterface.displayError(error.message ?: "")
//        }

    private val contactObserver
        get() = object : DisposableObserver<ContactsRequest>() {

            override fun onComplete() {}

            override fun onNext(contactRequest: ContactsRequest) {
                viewInterface.displayContact(contactRequest)
            }
            override fun onError(error: Throwable) = viewInterface.displayError(error.message ?: "")
        }


//    override fun getContacts() {
//        val getContactsObservable = contactsApi.getContacts().toObservable()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(contactsObserver)
//
//        compositeDisposable.add(getContactsObservable)
//    }

    override fun getContactbyPhoneNumber(phoneNumber:String) {
        val getContactsObservable = contactsApi.getContactByPhoneNumber(phoneNumber).toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(contactObserver)

        compositeDisposable.add(getContactsObservable)
    }

    override fun stop() {
        compositeDisposable.clear()
    }
}