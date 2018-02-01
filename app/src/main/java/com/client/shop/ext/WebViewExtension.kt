package com.client.shop.ext

import android.webkit.WebView
import org.jsoup.Jsoup

fun WebView.fitHtmlImages(html: String): String {
    val document = Jsoup.parse(html)
    document.select("img").forEach {
        if (it.hasAttr("style")) {
            var styleValue = it.attr("style")
            styleValue = removeStyleAttr(styleValue, "max-width")
            styleValue = removeStyleAttr(styleValue, "width")
            styleValue = removeStyleAttr(styleValue, "height")
            styleValue += "display: inline; width:auto; height: auto; max-width: 100%;"
            it.attr("style", styleValue)
        } else {
            it.removeAttr("max-width")
            it.removeAttr("width")
            it.removeAttr("height")
            it.attr("style", "display: inline; width:auto; height: auto; max-width: 100%;")
        }
    }
    return document.outerHtml()
}

fun WebView.fitHtmlFrames(html: String, height: Int): String {
    val document = Jsoup.parse(html)
    document.select("iframe").forEach {
        if (it.hasAttr("style")) {
            var styleValue = it.attr("style")
            styleValue = removeStyleAttr(styleValue, "width")
            styleValue = removeStyleAttr(styleValue, "height")
            styleValue += "display: inline; width:100%; height: $height;"
            it.attr("style", styleValue)
        } else {
            it.attr("width", "100%")
            it.attr("height", "$height")
        }
    }
    return document.outerHtml()
}

private fun removeStyleAttr(styleValue: String, styleAttr: String): String {
    var styleValue1 = styleValue
    val startIndex = styleValue1.indexOf(styleAttr)
    if (startIndex >= 0) {
        var endIndex = styleValue1.indexOf(";", startIndex, true)
        //if it is last style item
        if (endIndex == 0) {
            endIndex = styleValue1.length - 1
        } else {
            endIndex++
        }
        styleValue1 = styleValue1.removeRange(startIndex, endIndex)
    }
    return styleValue1
}