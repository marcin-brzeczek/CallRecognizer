package com.wepa.callrecognizer.call


interface ICallManager {
    fun endCall(): Boolean
    fun answerRingingCall()
    fun silenceRinger()
}