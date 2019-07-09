package com.wepa.callrecognizer.main


interface ICallManager {
    fun endCall(): Boolean
    fun answerRingingCall()
    fun silenceRinger()
}