package com.wepa.callrecognizer.main

import com.wepa.callrecognizer.model.ContactsRequest
import com.wepa.callrecognizer.network.ContactsApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class MainPresenter(private val viewInterface: MainContract.ViewInterface, private val contactsApi: ContactsApi) :
    MainContract.PresenterInterface {

    private val compositeDisposable = CompositeDisposable()

    private val contactsObserver
        get() = object : DisposableObserver<ContactsRequest>() {

            override fun onComplete() {}

            override fun onNext(contactsRequest: ContactsRequest) {
                viewInterface.displayResult(contactsRequest.data)
            }
            override fun onError(error: Throwable) = viewInterface.displayError(error.message ?: "")
        }

    override fun getContacts() {
        val getContactsObservable = contactsApi.getContacts().toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(contactsObserver)

        compositeDisposable.add(getContactsObservable)
    }

    override fun stop() {
        compositeDisposable.clear()
    }
}