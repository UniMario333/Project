package com.example.project.geoweather.json.mapping.geonames

import com.google.gson.Gson
import com.example.project.geoweather.GeoLocation
import com.example.project.geoweather.json.mapping.GeoAPIUrlUtil
import geoweather.json.mapping.geonames.pojo.GeoNames

import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.DecimalFormat
import java.util.Comparator
import java.util.NoSuchElementException
import java.util.stream.Collectors

object GeoNamesAPIUtil {

    @Throws(IllegalArgumentException::class)
    private fun getFindNearbyAPIurl(location: GeoLocation, radius: Int?): String {

        val urlBuffer = StringBuilder()
        val df = DecimalFormat()
        df.maximumFractionDigits = 2

        if (radius < 1) {
            throw IllegalArgumentException("The radius shall not be less than 1 km")
        }

        return urlBuffer.append("http://api.geonames.org/findNearbyPlaceNameJSON?").append("lat=")
            .append(df.format(location.latitudine!!.toFloat().toDouble()).replace(',', '.')).append("&lng=")
            .append(df.format(location.longitudine!!.toFloat().toDouble()).replace(',', '.'))
            .append("&username=LegendMavie").append("&radius=").append(radius).toString()
    }

    @Throws(IllegalArgumentException::class, UnsupportedEncodingException::class)
    private fun getFindMatchingAPIurl(partialName: String, maxResults: Int?): String {

        val urlBuffer = StringBuilder()

        if (maxResults < 1) {
            throw IllegalArgumentException("The number of max results shall not be less than 1 row")
        }

        return urlBuffer.append("http://api.geonames.org/searchJSON?q=").append(URLEncoder.encode(partialName, "UTF-8"))
            .append("&maxRows=").append(maxResults).append("&username=LegendMavie").toString()
    }

    @Throws(NoSuchElementException::class, IOException::class)
    fun getPlacesNear(location: GeoLocation, radius: Int?): GeoNames {

        val jsonResponse = GeoAPIUrlUtil.getServerResponse(getFindNearbyAPIurl(location, radius))

        val places = Gson().fromJson(jsonResponse, GeoNames::class.java)

        if (places.geonames.isEmpty()) {
            throw NoSuchElementException("No nearby cities were found.")
        }

        return places
    }

    @Throws(IOException::class, IllegalArgumentException::class, NoSuchElementException::class)
    fun autocompleteCityName(partialName: String): GeoNames {

        val jsonResponse = GeoAPIUrlUtil.getServerResponse(getFindMatchingAPIurl(partialName, 50))

        val places = Gson().fromJson(jsonResponse, GeoNames::class.java)

        places.geonames = places.geonames.stream()
            .filter({ p -> p.name.contains(partialName) })
            .sorted(Comparator.comparing<Any, Comparable> { o -> o.name })
            .collect(Collectors.toList<Any>())

        if (places.geonames.isEmpty()) {
            throw NoSuchElementException("No matching cities were found.")
        }

        return places
    }
}
