package com.kristianskokars.shotsandbeer.common

import timber.log.Timber

class LineNumberDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement) =
        "$TAG_NAME: (${element.fileName}:${element.lineNumber}) #${element.methodName}"
}