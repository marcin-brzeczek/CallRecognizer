package com.wepa.callrecognizer.call

import com.wepa.callrecognizer.model.ContactsRequest
import com.wepa.callrecognizer.network.ContactsApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import timber.log.Timber

class CallPresenter(private val viewInterface: CallContract.ViewInterface, private val contactsApi: ContactsApi) :
    CallContract.PresenterInterface {

    private val compositeDisposable = CompositeDisposable()

    private val contactObserver
        get() = object : DisposableObserver<Response<ContactsRequest>>() {
            override fun onComplete() {}

            override fun onNext(resposne: Response<ContactsRequest>) {

               Timber.d("response message: ${resposne.errorBody()}")
                viewInterface.displayContact(resposne.body())
            }
            override fun onError(error: Throwable) = viewInterface.displayError(error.message ?: "")
        }

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