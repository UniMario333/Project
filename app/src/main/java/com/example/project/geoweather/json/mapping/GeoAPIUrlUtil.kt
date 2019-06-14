package com.example.project.geoweather.json.mapping

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object GeoAPIUrlUtil {

    fun getServerResponse(apiUrl: String): String {

        val response = StringBuffer()

        try {

            val urlRisposta = URL(apiUrl)
            val con = urlRisposta.openConnection() as HttpURLConnection

            con.requestMethod = "GET"
            con.setRequestProperty("User-Agent", "Mario Client")

            val `in` = BufferedReader(
                InputStreamReader(con.inputStream)
            )

            var inputLine: String

            while ((inputLine = `in`.readLine()) != null) {
                response.append(inputLine)
            }

            `in`.close()

            println("APIURL: $apiUrl")
            println("APIRESP: $response")

        } catch (e: IOException) {
            response.append("{\"cod\":\"404\",\"message\":\"city not found\"}")
        }

        return response.toString()
    }
}
