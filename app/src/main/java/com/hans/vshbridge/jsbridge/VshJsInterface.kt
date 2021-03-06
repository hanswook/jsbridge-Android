package com.hans.vshbridge.jsbridge;

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import com.hans.vshbridge.utils.JsonUtils

/**
 * @date: 2020/6/10 6:32 PM
 * @author: hanxu
 * @email hxxx1992@163.com
 * @description null
 */
class VshJsInterface(val context: Activity, val webview: WebView) : Object() {

    @JavascriptInterface
    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        Log.e("VshJsInterface", "msg:$msg")
    }

    @JavascriptInterface
    fun callNative(msg: String) {
        Log.e("VshJsInterface", "callNative msg:$msg")
        val data = JsonUtils.fromJsonString<JsFunctionParams>(msg, JsFunctionParams::class.java)
            ?: return
        JsNativeCenter.instance.function(
            context,
            data.handlerName,
            JsonUtils.toJsonString(data.data),
            object : JsFunctionHandler {
                override fun handler(params: String) {
                    val func = JsFunctionParams()
                    func.handlerName = data.handlerName
                    func.data = params
                    func.callbackId = data.callbackId
                    var json = JsonUtils.toJsonString(func)

                    context.runOnUiThread {
                        webview.evaluateJavascript(
                            "javascript:dispatchMessageFromNative($json)"
                        ) { }
                    }
                }
            })
        println("data:$data")
    }

    @JavascriptInterface
    fun log(msg: String) {
        Log.e("VshJsInterface", "log msg:$msg")
    }


}
